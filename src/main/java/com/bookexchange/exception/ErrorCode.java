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
    PASSWORD_REQUIRED(1024, "Password is required", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1025, "User not found", HttpStatus.NOT_FOUND),
    SCHOOL_NOT_FOUND(1026, "School not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1027, "Role not existed", HttpStatus.NOT_FOUND),
    LISTED_BOOK_NOT_FOUND(1028, "Listed book not found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1029, "Shopping cart not found", HttpStatus.NOT_FOUND),
    LISTED_BOOK_NOT_AVAILABLE(1030, "Listed book not available", HttpStatus.BAD_REQUEST),
    ITEM_ALREADY_IN_CART(1031, "Item already in cart", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(1032, "Cart item not found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_BELONG_TO_USER(1033, "Cart item does not belong to user", HttpStatus.FORBIDDEN),
    SHIPPING_ADDRESS_NOT_FOUND(1034, "Shipping address not found", HttpStatus.NOT_FOUND),
    SHIPPING_ADDRESS_NOT_BELONG_TO_USER(1035, "Shipping address does not belong to user", HttpStatus.FORBIDDEN),
    VOUCHER_NOT_FOUND(1036, "Voucher not found", HttpStatus.NOT_FOUND),
    VOUCHER_EXPIRED(1037, "Voucher has expired", HttpStatus.BAD_REQUEST),
    VOUCHER_USAGE_EXCEEDED(1038, "Voucher usage limit exceeded", HttpStatus.BAD_REQUEST),
    VOUCHER_MIN_ORDER_NOT_MET(1039, "Order does not meet minimum amount for voucher", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1040, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_ITEM_NOT_FOUND(1041, "Order item not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_BELONG_TO_USER(1042, "Order does not belong to user", HttpStatus.FORBIDDEN),
    VOUCHER_CODE_ALREADY_EXISTS(1043, "Voucher code already exists", HttpStatus.BAD_REQUEST),
    VOUCHER_MAX_USES_REACHED(1044, "Voucher has reached maximum number of uses", HttpStatus.BAD_REQUEST),
    ORDER_VALUE_TOO_LOW(1045, "Order value is too low for this voucher", HttpStatus.BAD_REQUEST),
    INVALID_VOUCHER(1046, "Invalid voucher configuration", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND(1047, "Address not found", HttpStatus.NOT_FOUND),
    OLD_PASSWORD_INCORRECT(1048, "Old password is incorrect", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_ITEM_STATUS(1049, "Invalid order item status", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(1050, "Review not found", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS(1051, "You have already reviewed this book", HttpStatus.BAD_REQUEST),
    CANNOT_REVIEW_UNDELIVERED_BOOK(1052, "You can only review books that have been delivered", HttpStatus.BAD_REQUEST)
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
