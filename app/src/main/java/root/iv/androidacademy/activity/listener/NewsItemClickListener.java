package root.iv.androidacademy.activity.listener;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import root.iv.androidacademy.app.App;
import root.iv.androidacademy.activity.NewsDetailsActivity;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.util.Action1;

public class NewsItemClickListener implements View.OnClickListener, Listener<Action1<View>> {
    @Nullable
    private Action1<View> action;

    @Override
    public void onClick(View v) {
        try {
            if (action != null) action.run(v);
        } catch (Exception e) {
            App.logE(e.getMessage());
        }
    }

    public void unsubscribe() {
        action = null;
    }

    @Override
    public void subscribe(Action1<View> a) {
        action = a;
    }
}
