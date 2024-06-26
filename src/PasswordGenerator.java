import java.security.SecureRandom;

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
        if (length < 1) throw new IllegalArgumentException("长度必须至少为1");

        StringBuilder allowedCharsBuilder = new StringBuilder();

        if (includeLowercase) allowedCharsBuilder.append(CHAR_LOWER);
        if (includeUppercase) allowedCharsBuilder.append(CHAR_UPPER);
        if (includeNumbers) allowedCharsBuilder.append(NUMBER);
        if (includeSpecialChars) allowedCharsBuilder.append(OTHER_CHAR);

        if (allowedCharsBuilder.length() == 0) {
            throw new IllegalArgumentException("至少需要选择一种字符类型");
        }

        String allowedChars = allowedCharsBuilder.toString();

        StringBuilder passwordBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(allowedChars.length());
            passwordBuilder.append(allowedChars.charAt(randomIndex));
        }

        return passwordBuilder.toString();
    }
}
