package root.iv.androidacademy;

public class Stopper {
    private Stopper() {}
    public static void pause(int mlsec) {
        try {Thread.sleep(mlsec);}
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
