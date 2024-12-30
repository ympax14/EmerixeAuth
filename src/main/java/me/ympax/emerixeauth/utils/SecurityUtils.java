package me.ympax.emerixeauth.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public interface SecurityUtils {
    static final String CHARSET = "!?%$ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?@#$%^&*+=";
    static final int KEY_LENGTH = 10;
    
    public static String generateRecoveryKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder recoveryKey = new StringBuilder(KEY_LENGTH);

        for (int i = 0; i < KEY_LENGTH; i++) {
            int index = random.nextInt(CHARSET.length());
            recoveryKey.append(CHARSET.charAt(index));
        }

        return recoveryKey.toString();
    }

    // Hashes a password with salt using PBKDF2WithHmacSHA256
    public static String hashPassword(String password) throws Exception {
        // Generate a salt
        byte[] salt = generateSalt();

        // Create the hash
        String hashedPassword = hashWithSalt(password, salt);

        // Combine the salt and the hashed password and return it
        return Base64.getEncoder().encodeToString(salt) + ":" + hashedPassword;
    }

    public static String hashWithSalt(String password, byte[] salt) throws Exception {
        // Create the PBKDF2WithHmacSHA256 hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hashedPasswordBytes = digest.digest(password.getBytes("UTF-8"));
        
        // Convert the byte array to a base64 string
        return Base64.getEncoder().encodeToString(hashedPasswordBytes);
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // Use a 16-byte salt (128 bits)
        random.nextBytes(salt);
        return salt;
    }

    public static boolean verifyPassword(String password, String storedPassword) throws Exception {
        // Extract the salt and hashed password from the stored value
        String[] parts = storedPassword.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String storedHash = parts[1];

        // Hash the input password with the extracted salt
        String hashedInputPassword = hashWithSalt(password, salt);

        // Compare the two hashes (input hash and stored hash)
        return storedHash.equals(hashedInputPassword);
    }

}
