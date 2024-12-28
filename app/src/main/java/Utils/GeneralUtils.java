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

    /**
     * 判断文本是中文还是英文
     * @param text 输入的文本
     * @return 如果是中文返回 1，如果是英文返回 0
     */
    public static int detectLanguage(String text) {
        // 中文字符范围：\u4e00-\u9fa5
        for (char c : text.toCharArray()) {
            if (Character.toString(c).matches("[\\u4e00-\\u9fa5]")) {
                return 1; // 检测到中文字符
            }
        }
        return 0; // 没有检测到中文字符，默认为英文
    }

}
