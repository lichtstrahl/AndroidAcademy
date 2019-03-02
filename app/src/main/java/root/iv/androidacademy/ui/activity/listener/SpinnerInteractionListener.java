package root.iv.androidacademy.ui.activity.listener;

import androidx.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import root.iv.androidacademy.util.Action1;

public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener, Signed<Action1<Integer>> {
    private boolean user = false;
    @Nullable
    private Action1<Integer> action;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        user = true;
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

    @Override
    public void unsubscribe() {
        action = null;
    }
}
