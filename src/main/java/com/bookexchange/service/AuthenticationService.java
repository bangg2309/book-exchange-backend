package com.bookexchange.service;

import com.bookexchange.constant.PredefinedRole;
import com.bookexchange.dto.request.*;
import com.bookexchange.dto.response.AuthenticationResponse;
import com.bookexchange.dto.response.IntrospectResponse;
import com.bookexchange.entity.InvalidatedToken;
import com.bookexchange.entity.Role;
import com.bookexchange.entity.User;
import com.bookexchange.entity.VerificationToken;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.repository.InvalidatedTokenRepository;
import com.bookexchange.repository.RoleRepository;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.repository.VerificationTokenRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sendinblue.ApiException;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
  UserRepository userRepository;
  InvalidatedTokenRepository invalidatedTokenRepository;
  RoleRepository roleRepository;
  VerificationTokenRepository verificationTokenRepository;
  EmailService emailService;
  PasswordEncoder passwordEncoder;

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${jwt.valid-duration}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  protected long REFRESHABLE_DURATION;

  public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
    var token = request.getToken();
    log.info(token);
    boolean isValid = true;

    try {
      verifyToken(token);
    } catch (AppException e) {
      isValid = false;
    }

    return IntrospectResponse.builder().valid(isValid).build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    var user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    if (user.getStatus() == 0) {
      throw new AppException(ErrorCode.USER_NOT_VERIFIED);
    }

    if (user.getStatus() == 2) {
      throw new AppException(ErrorCode.USER_BANNED);
    }

    boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

    if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

    var accessToken = generateToken(user, false);
    var refreshToken = generateToken(user, true);

    return AuthenticationResponse.builder().accessToken(accessToken)
            .refreshToken(refreshToken)
            .authenticated(true).build();
  }

  public void register(RegisterRequest request) throws ApiException {
    log.info("Registering user: {}", request.getUsername());

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new AppException(ErrorCode.EMAIL_EXISTED);
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setStatus(0);
    HashSet<Role> roles = new HashSet<>();
    roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

    user.setRoles(roles);
    userRepository.save(user);

    //Create verification token
    String token = UUID.randomUUID().toString();
    //Save token to database
    verificationTokenRepository.save(VerificationToken.builder().token(token)
            .user(user)
            .expiryDate(LocalDateTime.now().plusDays(1))
            .build());

    //Send email
    emailService.sendVerificationEmailApi(
            request.getEmail(),
            "Verification Email",
            "?token=" + token
    );
  }

  public void verifyEmail(VerificationEmailRequest request) {
    VerificationToken token = verificationTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND));

    if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new AppException(ErrorCode.VERIFICATION_TOKEN_EXPIRED);
    }
    if (token.getUser().getStatus() != 0) {
      throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
    }

    User user = token.getUser();
    user.setStatus(1);
    userRepository.save(user);

    verificationTokenRepository.delete(token);
  }

  public void logout(LogoutRequest request) throws ParseException, JOSEException {
    try {
      var signToken = verifyToken(request.getToken());

      String jit = signToken.getJWTClaimsSet().getJWTID();
      Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

      InvalidatedToken invalidatedToken =
              InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

      invalidatedTokenRepository.save(invalidatedToken);
    } catch (AppException exception) {
      log.info("Token already expired");
    }
  }

  public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
    if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    var signedJWT = verifyToken(request.getRefreshToken());

    var jwtClaimsSet = signedJWT.getJWTClaimsSet();
    var expiryTime = jwtClaimsSet.getExpirationTime();
    var username = jwtClaimsSet.getSubject();
    var type = jwtClaimsSet.getStringClaim("type");

    if (!type.equals("refresh")) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    if (expiryTime.before(new Date())) {
      throw new AppException(ErrorCode.TOKEN_EXPIRED);
    }

    var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    var newAccessToken = generateToken(user, false);

    return AuthenticationResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(request.getRefreshToken())
            .authenticated(true)
            .build();
  }

  private String generateToken(User user, boolean isRefresh) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issuer("bangdev.blog")
            .issueTime(new Date())
            .expirationTime(isRefresh ? Date.from(Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)) :
                    Date.from(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS)))
            .jwtID(UUID.randomUUID().toString())
            .claim("type", isRefresh ? "refresh" : "access")
            .claim("scope", buildScope(user))
            .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      log.error("Cannot create token", e);
      throw new RuntimeException(e);
    }
  }

  private SignedJWT verifyToken(String token) throws JOSEException, ParseException {


    JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

    SignedJWT signedJWT = SignedJWT.parse(token);

    Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

    var verified = signedJWT.verify(verifier);

    if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

    if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
      throw new AppException(ErrorCode.UNAUTHENTICATED);

    return signedJWT;
  }

  private String buildScope(User user) {
    StringJoiner stringJoiner = new StringJoiner(" ");

    if (!CollectionUtils.isEmpty(user.getRoles()))
      user.getRoles().forEach(role -> {
        stringJoiner.add("ROLE_" + role.getName());
        if (!CollectionUtils.isEmpty(role.getPermissions()))
          role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
      });

    return stringJoiner.toString();
  }

  public User loginWithGoogle(Map<String, Object> attributes) {
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String picture = (String) attributes.get("picture");
    String sub = (String) attributes.get("sub");

    // Check if user exists, or save new one
    return userRepository.findByEmail(email)
            .orElseGet(() -> {
              User newUser = new User();
              newUser.setEmail(email);
              newUser.setUsername(email);
              newUser.setFullName(name);
              newUser.setAvatar(picture);
              newUser.setGoogleId(sub);
              return userRepository.save(newUser);
            });
  }
}
