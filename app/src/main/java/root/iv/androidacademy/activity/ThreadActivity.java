package root.iv.androidacademy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.R;

public class ThreadActivity extends AppCompatActivity {
    private ivThread th1, th2;
    public final static String TAG = "ThreadActivity";
    private THREAD curThread;
    private final Object lock = new Object();
    @BindView(R.id.textView)
    TextView viewThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        th1 = new ivThread(new LeftLeg());
        th1.start();
        th2 = new ivThread(new RightLeg());
        th2.start();
    }


    private class LeftLeg implements StopRunnable {
        private boolean isRunning = true;
        private final String name = "LEFT";
        @Override
        public void run() {
            while (isRunning) {
                synchronized (lock) {
                    if (curThread == THREAD.LEFT)
                        continue;
                    curThread = THREAD.LEFT;
                    runOnUiThread(()->viewThread.setText(name));
                    // Без паузы какая-то дичь
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        @Override
        public void finish() {
            isRunning = false;
        }
    }
    private class RightLeg implements StopRunnable {
        private boolean isRunning = true;
        private final String name = "RIGHT";
        @Override
        public void run() {
            while (isRunning) {
                synchronized (lock) {
                    if (curThread == THREAD.RIGHT)
                        continue;
                    curThread = THREAD.RIGHT;
                    runOnUiThread(()->viewThread.setText(name));
                    // Без паузы какая-то дичь
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
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
        th1.finish();
        th2.finish();
    }
}

enum THREAD {
    LEFT,
    RIGHT
}

class ivThread extends Thread {
    private StopRunnable action;
    public ivThread(StopRunnable a) {
        super(a);
        action = a;
    }

    public void finish() {
        action.finish();
    }
}

interface StopRunnable extends Runnable {
    void finish();
}