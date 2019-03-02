package root.iv.androidacademy.util.listener;

import androidx.annotation.Nullable;
import android.view.View;

import root.iv.androidacademy.util.Action1;

public class NewsItemClickListener implements ClickListener<Action1<View>> {
    @Nullable
    private Action1<View> action;

    @Override
    public void onClick(View v) {
        if (action != null) action.run(v);
    }

    public void unsubscribe() {
        action = null;
    }

    @Override
    public void subscribe(Action1<View> a) {
        action = a;
    }
}
