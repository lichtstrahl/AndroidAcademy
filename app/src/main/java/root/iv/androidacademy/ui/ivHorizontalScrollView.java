package root.iv.androidacademy.ui;

import android.content.Context;
import android.drm.DrmStore;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import javax.annotation.Nullable;

import root.iv.androidacademy.util.Action1;
import root.iv.androidacademy.util.listener.Subscribed;

public class ivHorizontalScrollView extends HorizontalScrollView implements Subscribed<Action1<Integer>> {
    @Nullable
    Action1<Integer> action;

    public ivHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (action != null) action.run(l-oldl);
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
