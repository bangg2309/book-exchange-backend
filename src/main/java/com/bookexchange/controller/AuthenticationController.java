package com.bookexchange.controller;

import com.bookexchange.dto.request.*;
import com.bookexchange.dto.response.AuthenticationResponse;
import com.bookexchange.dto.response.IntrospectResponse;
import com.bookexchange.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

import java.text.ParseException;

@Tag(name = "Authentication API", description = "Endpoints for handling authentication and token management")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

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

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid AuthenticationRequest request) throws ApiException {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().build());
    }

    @GetMapping("/verify-email")
    public ResponseEntity verify(@RequestParam String token,
                                 @RequestParam(defaultValue = "", required = false) String action) {
        VerificationEmailRequest request = new VerificationEmailRequest();
        request.setToken(token);
        request.setAction(action);
        authenticationService.verifyEmail(request);
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
