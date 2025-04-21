package com.bookexchange.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND(1009, "Post not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1010, "Category not found", HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND(1011, "Tag not found", HttpStatus.NOT_FOUND),
    SUBJECT_NOT_FOUND(1012, "Subject not found", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED(1013, "Token expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1014, "Invalid token", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1015, "Email existed", HttpStatus.BAD_REQUEST), VERIFICATION_TOKEN_NOT_FOUND(1016, "Verification token not found", HttpStatus.NOT_FOUND),
    VERIFICATION_TOKEN_EXPIRED(1017, "Verification token expired", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_VERIFIED(1018, "User already verified", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1019, "Invalid email", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(1020, "User not verified", HttpStatus.UNAUTHORIZED),
    USER_BANNED(1021, "User banned", HttpStatus.UNAUTHORIZED),
    USERNAME_REQUIRED(1022, "Username is required", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1023, "Email is required", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1024, "Password is required", HttpStatus.BAD_REQUEST),;
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
