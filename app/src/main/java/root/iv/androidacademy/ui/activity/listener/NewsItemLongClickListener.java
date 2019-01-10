package root.iv.androidacademy.ui.activity.listener;

import android.support.annotation.Nullable;
import android.view.View;

import root.iv.androidacademy.util.Action1;

public class NewsItemLongClickListener implements Signed<Action1<View>>, View.OnLongClickListener {
    @Nullable
    private Action1<View> action;

    @Override
    public boolean onLongClick(View v) {
        if (action != null) action.run(v);
        return false;
    }

    @Override
    public void subscribe(Action1<View> a) {
        action = a;
    }

    @Override
    public void unsubscribe() {
        action = null;
    }
}
