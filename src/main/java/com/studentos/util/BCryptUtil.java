package com.studentos.util;
import org.mindrot.jbcrypt.BCrypt;
public class BCryptUtil {
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }
    public static boolean check(String plain, String hash) {
        return BCrypt.checkpw(plain, hash);
    }
}
