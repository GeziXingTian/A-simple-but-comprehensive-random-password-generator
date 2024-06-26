import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;


public class PasswordGenerator {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate(int length) {
        return generateCustomPassword(length, true, true, true, false);
    }

    public String generateCustomPassword(int length,
                                         boolean includeLowercase,
                                         boolean includeUppercase,
                                         boolean includeNumbers,
                                         boolean includeSpecialChars) {
        if (length < 1) throw new IllegalArgumentException("密码长度至少为1");

        if (!(includeLowercase || includeUppercase || includeNumbers || includeSpecialChars)) {
            throw new IllegalArgumentException("至少需要选择一种字符类型");
        }

        StringBuilder allowedCharsBuilder = new StringBuilder();

        if (includeLowercase) allowedCharsBuilder.append(CHAR_LOWER);
        if (includeUppercase) allowedCharsBuilder.append(CHAR_UPPER);
        if (includeNumbers) allowedCharsBuilder.append(NUMBER);
        if (includeSpecialChars) allowedCharsBuilder.append(OTHER_CHAR);

        String allowedChars = allowedCharsBuilder.toString();
        int allowedCharsLength = allowedChars.length();

        StringBuilder passwordBuilder = new StringBuilder(length);

        if (length <= 3) {
            for (int i = 0; i < length; i++) {
                passwordBuilder.append(allowedChars.charAt(secureRandom.nextInt(allowedCharsLength)));
            }
        } else {
            if (includeLowercase) passwordBuilder.append(CHAR_LOWER.charAt(secureRandom.nextInt(CHAR_LOWER.length())));
            if (includeUppercase) passwordBuilder.append(CHAR_UPPER.charAt(secureRandom.nextInt(CHAR_UPPER.length())));
            if (includeNumbers) passwordBuilder.append(NUMBER.charAt(secureRandom.nextInt(NUMBER.length())));
            if (includeSpecialChars) passwordBuilder.append(OTHER_CHAR.charAt(secureRandom.nextInt(OTHER_CHAR.length())));

            for (int i = passwordBuilder.length(); i < length; i++) {
                passwordBuilder.append(allowedChars.charAt(secureRandom.nextInt(allowedCharsLength)));
            }
        }

        String password = shuffleString(passwordBuilder.toString());
        String strength = assessPasswordStrength(password);
        System.out.println("生成的密码强度: " + strength);

        return password;
    }

    private String shuffleString(String string) {
        List<Character> characters = string.chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        Collections.shuffle(characters, secureRandom);
        return characters.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private StringBuilder ensureCharacterTypes(int length, String allowedChars, int allowedCharsLength) {
        StringBuilder passwordBuilder = new StringBuilder();

        passwordBuilder.append(CHAR_LOWER.charAt(secureRandom.nextInt(CHAR_LOWER.length())));
        passwordBuilder.append(CHAR_UPPER.charAt(secureRandom.nextInt(CHAR_UPPER.length())));
        passwordBuilder.append(NUMBER.charAt(secureRandom.nextInt(NUMBER.length())));

        int remainingLength = length - 3;
        if (remainingLength > 0) {
            for (int i = 0; i < remainingLength; i++) {
                passwordBuilder.append(allowedChars.charAt(secureRandom.nextInt(allowedCharsLength)));
            }
        }

        return passwordBuilder;
    }
    private String assessPasswordStrength(String password) {
        int lengthScore = password.length() >= 12 ? 3 : password.length() >= 8 ? 2 : 1;
        boolean hasLower = !password.matches("[^a-z]");
        boolean hasUpper = !password.matches("[^A-Z]");
        boolean hasNumber = !password.matches("[^0-9]");
        boolean hasSpecial = !password.matches("[A-Za-z0-9 ]");

        int complexityScore = (hasLower ? 1 : 0) + (hasUpper ? 1 : 0) + (hasNumber ? 1 : 0) + (hasSpecial ? 1 : 0);

        String strength;
        if (lengthScore + complexityScore >= 5) {
            strength = "好";
        } else if (lengthScore + complexityScore >= 3) {
            strength = "一般";
        } else {
            strength = "差";
        }

        return strength;
    }
}
