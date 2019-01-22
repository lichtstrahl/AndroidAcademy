package root.iv.androidacademy.app;

public class RobolectricApp extends App {

    @Override
    public void onCreate() {
        super.onCreate();
        database = getDatabaseBuilder(false).build();
    }

    public static void logE(String e) {
        System.err.println(e);
    }

    public static void logW(String msg) {
        System.out.println("W: " + msg);

    }

    public static void logI(String msg) {
        System.out.println(msg);
    }


}
