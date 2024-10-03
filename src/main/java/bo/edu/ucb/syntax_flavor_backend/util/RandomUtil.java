package bo.edu.ucb.syntax_flavor_backend.util;

import java.security.SecureRandom;

public class RandomUtil {
    private static final String NUMBER = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomNumber(int length) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(NUMBER.length());
            char rndChar = NUMBER.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();
    }
}
