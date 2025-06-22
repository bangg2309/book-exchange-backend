package com.bookexchange.configuration;

import com.bookexchange.constant.PredefinedRole;
import com.bookexchange.entity.Role;
import com.bookexchange.entity.User;
import com.bookexchange.repository.RoleRepository;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    @Value("${client.url}")
    private String clientUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            String provider = oauthToken.getAuthorizedClientRegistrationId();
            
            log.info("OAuth2 login successful for provider: {}", provider);
            log.info("Request URI: {}", request.getRequestURI());
            log.info("OAuth2 user attributes: {}", oAuth2User.getAttributes());
            
            // Xử lý thông tin người dùng từ OAuth2
            String email = extractEmail(oAuth2User, provider);
            String name = extractName(oAuth2User, provider);
            
            // Tìm hoặc tạo người dùng
            User user = processOAuth2User(email, name, provider);
            
            // Tạo token cho người dùng
            var authResponse = authenticationService.authenticateOAuth2User(user);
            
            // Xây dựng URL chuyển hướng với token
            String redirectUrl = UriComponentsBuilder.fromUriString(clientUrl + "/auth/callback")
                    .queryParam("token", authResponse.getAccessToken())
                    .queryParam("refreshToken", authResponse.getRefreshToken())
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
    
    private String extractEmail(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        }
        
        return null;
    }
    
    private String extractName(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        }
        
        return null;
    }
    
    @Transactional
    protected User processOAuth2User(String email, String name, String provider) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(name != null ? name : email);
            String randomPassword = UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(randomPassword));
            user.setStatus(1);
            
            // Gán vai trò USER
            HashSet<Role> roles = new HashSet<>();
            roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
            user.setRoles(roles);
            
            userRepository.save(user);
        }
        
        return user;
    }
} 