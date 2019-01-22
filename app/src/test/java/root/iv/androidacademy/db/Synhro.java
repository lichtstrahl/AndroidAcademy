package root.iv.androidacademy.db;

// Класс, переключатель
public class Synhro {
    private int countThread;
    private int finishedThread;

    public Synhro(int count) {
        countThread = count;
        finishedThread = 0;
    }

    synchronized public void threadFinished() {
        finishedThread++;
    }

    public boolean allIsFinished() {
        return countThread == finishedThread;
    }

    public void reset() {
        finishedThread = 0;
    }

    public int getFinishedThread() {
        return finishedThread;
    }
}
