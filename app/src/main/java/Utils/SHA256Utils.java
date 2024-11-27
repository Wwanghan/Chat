package Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Utils {

    /**
     * 对字符串进行 SHA-256 加密
     * @param input 需要加密的字符串
     * @return 加密后的 SHA-256 哈希值（64位十六进制字符串）
     */
    public static String encrypt(String input) {
        try {
            // 获取 SHA-256 加密算法实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 将输入字符串转换为字节并加密
            byte[] hash = digest.digest(input.getBytes());
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // 补齐为两位
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // 如果加密失败，返回 null
        }
    }
}
