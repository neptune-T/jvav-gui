package code.utils;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

public class MD5 {

    public static String encrypt(String password) {
        // 1.生成随机盐值
        // 通过UUID生成唯一的数字作为随机盐值
        String salt = UUID.randomUUID().toString().replace("-","");// 顺便去掉 -
        // 2.根据初始密码 和 随机盐值 通过md5生成 加盐加密的密码
        // StandardCharsets.UTF_8  设置编码格式
        String finalPassword = DigestUtils.md5Hex((salt + password).getBytes(StandardCharsets.UTF_8));
        // 3.将盐值 和 加盐加密得到的密码一起返回(合并盐值和加盐密码)
        return salt +"$"+finalPassword;
    }

    public static String encrypt(String password,String salt) {

        // 2.根据初始密码 和 随机盐值 通过md5生成 加盐加密的密码
        String finalPassword = DigestUtils.md5Hex((salt + password).getBytes(StandardCharsets.UTF_8));
        // 3.将盐值 和 加盐加密得到的密码一起返回(合并盐值和加盐密码)
        // 这里的"$"是自定义的拼接规则，用于区分随机盐值和明文密码
        return salt +"$"+finalPassword;
    }

    public static boolean decrypt(String password,String dbPassword) {
        // 判端长度是否为65是因为：随机盐值是32位，通过md5生成的密码也是32位 $ 长度1位
        if (password != null && !password.isEmpty() && dbPassword != null && !dbPassword.isEmpty()
                && dbPassword.length() == 65 && dbPassword.contains("$")) {
            // 分割数据库中的密码
            String[] arr = dbPassword.split("\\$");
            // 得到盐值
            String salt = arr[0];
            // 将盐值和初始密码传入重载的加盐加密的方法，生成新的密码
            String finalPassword = encrypt(password,salt);

            if (finalPassword.equals(dbPassword)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(encrypt("123"));
        String str = "123";
        String finalPassword = encrypt(str);
        Scanner sc = new Scanner(System.in);
        String str1 = sc.next();
        System.out.println(decrypt(str1, finalPassword));
    }
}
