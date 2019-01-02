package root.iv.androidacademy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import root.iv.androidacademy.R;

public class IntoActivity extends AppCompatActivity {
    private static final String KEY_INTO = "KEY_INTO";

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null)
            disposable.dispose();
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean into = getPreferences(MODE_PRIVATE).getBoolean(KEY_INTO, true);

        if (into) {
            setContentView(R.layout.activity_into);

            disposable = Completable.complete()
                    .delay(1, TimeUnit.SECONDS)
                    .subscribe(this::startNewsListActivity);
        } else {
            startNewsListActivity();
        }
        getPreferences(MODE_PRIVATE).edit()
                .putBoolean(KEY_INTO, !into)
                .commit();
    }

    private void startNewsListActivity() {
        Intent intent = new Intent(this, NewsListActivity.class);
        startActivity(intent);
        finish();
    }
}
