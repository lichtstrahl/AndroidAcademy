package root.iv.androidacademy.app;

public class RobolectricApp extends App {

    @Override
    public void onCreate() {
        super.onCreate();
        database = getDatabaseBuilder(true).build();
    }
}
