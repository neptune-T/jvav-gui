package code.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneRegex {
    private static String regex = "^1\\d{10}$";
    public static boolean check(String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
