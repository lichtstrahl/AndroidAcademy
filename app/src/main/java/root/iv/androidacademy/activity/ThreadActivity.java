package root.iv.androidacademy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.R;

public class ThreadActivity extends AppCompatActivity {
    private ivThread threadLeft;
    private ivThread threadRight;
    public static final String TAG = "ThreadActivity";
    private THREAD curThread = null;
    private final Object lock = new Object();
    @BindView(R.id.textView)
    TextView viewThread;
    private long msPause = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        threadLeft = new ivThread(new LeftLeg());
        threadRight = new ivThread(new RightLeg());
        threadRight.start();
        threadLeft.start();
    }

    private class LeftLeg implements StopRunnable {
        private boolean isRunning = true;
        private static final String NAME = "LEFT";
        @Override
        public void run() {
            try {
                while (isRunning) {
                    synchronized (lock) {
                        while (curThread == THREAD.LEFT) {
                            lock.wait();
                        }
                        curThread = THREAD.LEFT;
                        lock.wait(msPause);
                        runOnUiThread(()->viewThread.setText(NAME));
                        Log.i(TAG, NAME);
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void finish() {
            isRunning = false;
        }
    }
    private class RightLeg implements StopRunnable {
        private boolean isRunning = true;
        private static final String NAME = "RIGHT";
        @Override
        public void run() {
            try {
                while (isRunning) {
                    synchronized (lock) {
                        while (curThread == THREAD.RIGHT) {
                            lock.wait();
                        }
                        curThread = THREAD.RIGHT;
                        lock.wait(msPause);
                        runOnUiThread(()->viewThread.setText(NAME));
                        Log.i(TAG, NAME);
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void finish() {
            isRunning = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        threadLeft.finish();
        threadRight.finish();
    }
}

enum THREAD {
    LEFT, RIGHT
}

class ivThread extends Thread {
    private StopRunnable action;
    ivThread(StopRunnable a) {
        super(a);
        action = a;
    }

    void finish() {
        action.finish();
    }
}

interface StopRunnable extends Runnable {
    void finish();
}