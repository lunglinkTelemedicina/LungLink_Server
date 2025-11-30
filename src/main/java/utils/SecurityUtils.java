package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class providing simple security-related operations,
 * such as hashing passwords using MD5. Used for storing user
 * passwords in a non-plain-text format.
 */
public class SecurityUtils {

    /**
     * Generates an MD5 hash for the given password and returns it as a
     * hexadecimal string. If the hashing algorithm is not available,
     * the method returns null.
     * @param password the plain-text password to hash
     * @return the hashed password in hexadecimal format, or null on error
     */
    public static String hashPassword(String password) {

        try{
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Add password bytes to digest
            md.update(password.getBytes());
            // Get the hash's bytes
            byte[] bytes = md.digest();
            // This bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
