package root.iv.androidacademy.db;

import java.util.concurrent.ThreadFactory;

public class TestThreadFactory implements ThreadFactory {
    private Thread lastT;
    private int newThreadCounter = 0;

    @Override
    public Thread newThread(Runnable r) {
        newThreadCounter++;
        lastT = new Thread(r);
        return lastT;
    }

    public Thread getLastT() {
        return lastT;
    }

    public int getNewThreadCounter() {
        return newThreadCounter;
    }
}
