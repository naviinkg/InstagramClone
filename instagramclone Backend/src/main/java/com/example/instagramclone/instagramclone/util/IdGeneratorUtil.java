package com.example.instagramclone.instagramclone.util;

import java.security.SecureRandom;
import java.util.Base64;

public class IdGeneratorUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateUniqueId(String email, String name) {
        // Use a base from email and name
        String base = email.substring(0, Math.min(3, email.length())) + name.substring(0, Math.min(3, name.length()));
        // Add random characters
        String randomChars = generateRandomString(5); // Adjust length as needed

        // Combine base and random characters
        String id = base + randomChars;

        // Ensure the ID length is less than 10 characters
        return id.length() > 10 ? id.substring(0, 10) : id;
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
