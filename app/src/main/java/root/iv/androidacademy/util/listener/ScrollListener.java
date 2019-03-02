package root.iv.androidacademy.util.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import root.iv.androidacademy.app.App;
import root.iv.androidacademy.util.Action1;

public class ScrollListener extends RecyclerView.OnScrollListener implements Subscribed<Action1<Integer>> {
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
