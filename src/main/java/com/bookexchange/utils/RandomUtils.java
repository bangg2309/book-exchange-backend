package com.bookexchange.utils;


/**
 * @author Nguyen Toan
 * @version RandomUtils.java v0.1, 2025-06-23
 */

public class RandomUtils {

    public static String generateRandomToken(int length) {
        StringBuilder token = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            token.append(characters.charAt(index));
        }

        return token.toString();
    }

}
