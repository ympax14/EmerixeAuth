package me.ympax.emerixeauth.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public interface SecurityUtils {
    static final String CHARSET = "!?%$ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?@#$%^&*+=";
    static final int KEY_LENGTH = 10;
    
    // Nombre d'itérations pour PBKDF2
    static final int ITERATIONS = 100000;  // Ajuster ce nombre pour renforcer la sécurité
    static final int SALT_LENGTH = 16; // 16 octets pour un salt (128 bits)
    static final int HASH_LENGTH = 256;  // Longueur du hash (en bits)

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

        // Create the hash with salt and the specified number of iterations
        String hashedPassword = hashWithSalt(password, salt);

        // Combine the salt and the hashed password and return it
        return Base64.getEncoder().encodeToString(salt) + ":" + hashedPassword;
    }

    public static String hashWithSalt(String password, byte[] salt) throws Exception {
        // Create the PBKDF2WithHmacSHA256 hash
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        // Convert the byte array to a base64 string
        return Base64.getEncoder().encodeToString(hash);
    }

    // Generate a random salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];  // Use a 16-byte salt (128 bits)
        random.nextBytes(salt);
        return salt;
    }

    // Verify a password against the stored password (which includes the salt)
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