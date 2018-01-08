package c.zju.jianshu.activity;

/**
 * Created by c on 2017/12/26.
 */

public class Data{
    private static String username;
    private static String password;

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setUsername(String username) {
        Data.username = username;
    }

    public static void setPassword(String password) {
        Data.password = password;
    }
}