package Utils;

import java.util.regex.Pattern;

public class GeneralUtils {

    private static final String SPECIAL_CHARACTERS_REGEX = "[()\\[\\],.<>@#$%^&*~+=!?:;'\"{}|\\\\/]";

    /**
     * 正则匹配用户名是否包含特殊字符
     * @param name
     * @return
     */
    public static boolean containsSpecialCharacters(String name) {
        Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX);
        return pattern.matcher(name).find();
    }

}
