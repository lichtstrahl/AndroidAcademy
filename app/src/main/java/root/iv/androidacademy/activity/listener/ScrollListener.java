package root.iv.androidacademy.activity.listener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import root.iv.androidacademy.app.App;
import root.iv.androidacademy.util.Action1;

public class ScrollListener extends RecyclerView.OnScrollListener implements Signed<Action1<Integer>> {
    @Nullable
    private Action1<Integer> action;

    /**
     * Вызываем с 0, чтобы показать кнопку
     * @param recyclerView
     * @param newState
     */
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        try {
            if (action != null) action.run(newState);
        } catch (Exception e) {
            App.logI(e.getMessage());
        }
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
