package com.college.admission.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2-SHA256";
    private static final String KDF_ALGORITHM =
            "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;
    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {

        validatePassword(password);

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] derivedKey = deriveKey(password, salt, ITERATIONS);

        return ALGORITHM
                + "$"
                + ITERATIONS
                + "$"
                + encode(salt)
                + "$"
                + encode(derivedKey);
    }

    public static boolean verifyPassword(
            String password,
            String storedPasswordHash
    ) {

        if (password == null
                || password.isBlank()
                || storedPasswordHash == null
                || storedPasswordHash.isBlank()) {

            return false;
        }

        if (isLegacySha256Hash(storedPasswordHash)) {
            return verifyLegacySha256(password, storedPasswordHash);
        }

        String[] parts = storedPasswordHash.split("\\$", -1);

        if (parts.length != 4
                || !ALGORITHM.equals(parts[0])) {

            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = decode(parts[2]);
            byte[] expectedKey = decode(parts[3]);
            byte[] actualKey =
                    deriveKey(password, salt, iterations);

            return MessageDigest.isEqual(
                    actualKey,
                    expectedKey
            );
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isLegacyHash(
            String storedPasswordHash
    ) {
        return storedPasswordHash != null
                && isLegacySha256Hash(storedPasswordHash);
    }

    private static boolean isLegacySha256Hash(
            String storedPasswordHash
    ) {
        return storedPasswordHash.length() == 64
                && storedPasswordHash.matches("[0-9a-fA-F]{64}");
    }

    private static boolean verifyLegacySha256(
            String password,
            String storedPasswordHash
    ) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");
            byte[] actualHash =
                    digest.digest(
                            password.getBytes(StandardCharsets.UTF_8)
                    );

            return MessageDigest.isEqual(
                    encodeHex(actualHash)
                            .getBytes(StandardCharsets.US_ASCII),
                    storedPasswordHash
                            .toLowerCase()
                            .getBytes(StandardCharsets.US_ASCII)
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    e
            );
        }
    }

    private static byte[] deriveKey(
            String password,
            byte[] salt,
            int iterations
    ) {
        if (iterations <= 0) {
            throw new IllegalArgumentException(
                    "Password hash iteration count must be positive."
            );
        }

        PBEKeySpec keySpec =
                new PBEKeySpec(
                        password.toCharArray(),
                        salt,
                        iterations,
                        KEY_LENGTH_BITS
                );

        try {
            return SecretKeyFactory
                    .getInstance(KDF_ALGORITHM)
                    .generateSecret(keySpec)
                    .getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Password KDF is not available.",
                    e
            );
        } finally {
            keySpec.clearPassword();
        }
    }

    private static String encode(byte[] value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value);
    }

    private static byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    private static String encodeHex(byte[] value) {
        StringBuilder result =
                new StringBuilder(value.length * 2);

        for (byte current : value) {
            result.append(
                    String.format("%02x", current)
            );
        }

        return result.toString();
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }
    }
}
