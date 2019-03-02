package root.iv.androidacademy.util.listener;

import androidx.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import io.reactivex.functions.Action;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.util.Action1;

public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener, Subscribed<Action1<Integer>> {
    private boolean user = false;
    @Nullable
    private Action1<Integer> action;
    @Nullable
    private Action release;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        user = true;
        try {
            if (release != null) release.run();
        }
        catch (Exception e) {
            App.logE(e.getMessage());
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (user) {
            if (action != null) action.run(position);
            user = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Не используется
    }

    @Override
    public void subscribe(Action1<Integer> a) {
        action = a;
    }

    public void subscribe(Action1<Integer> a, Action r) {
        action = a;
        release = r;
    }

    @Override
    public void unsubscribe() {
        action = null;
        release = null;
    }
}
