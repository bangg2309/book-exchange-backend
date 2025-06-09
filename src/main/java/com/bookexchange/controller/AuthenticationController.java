package com.bookexchange.controller;

import com.bookexchange.dto.request.*;
import com.bookexchange.dto.response.AuthenticationResponse;
import com.bookexchange.dto.response.IntrospectResponse;
import com.bookexchange.entity.User;
import com.bookexchange.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication API", description = "Endpoints for handling authentication and token management")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @NonFinal
    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String googleAuthUri;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String scope;


    @GetMapping("/google/authorize")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl() {
        String authUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s",
                googleAuthUri, clientId, redirectUri, scope.replace(",", "%20"));

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", authUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/google/success")
    public ResponseEntity<User> handleGoogleCallback(@AuthenticationPrincipal OAuth2User principal) {
        User user = authenticationService.loginWithGoogle(principal.getAttributes());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/google/failure")
    public ResponseEntity<Map<String, String>> handleAuthFailure() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Google authentication failed: invalid_request. Please check configuration.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @Operation(summary = "Authenticate and obtain an access token", description = "This endpoint allows users to log in and receive an access token and a refresh token.")
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder().result(result).build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest request) throws ApiException {
        authenticationService.register(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().build());
    }

    @GetMapping("/verify-email")
    public ResponseEntity verify(@RequestParam VerificationEmailRequest token) {
        authenticationService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }



    @Operation(summary = "Introspect an access token", description = "Verify if the provided access token is valid and retrieve its metadata.")
    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ResponseEntity.ok(ApiResponse.<IntrospectResponse>builder().result(result).build());
    }

    @Operation(summary = "Refresh the access token", description = "Use the refresh token to obtain a new access token.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder().result(result).build());
    }

    @Operation(summary = "Log out and invalidate the access token", description = "This endpoint invalidates the current access token to log the user out.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().build());
    }
}
