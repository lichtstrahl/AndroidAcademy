package root.iv.androidacademy.util.listener;

import android.support.v4.widget.SwipeRefreshLayout;

import root.iv.androidacademy.app.App;

public class ListOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout layout;

    public ListOnRefreshListener(SwipeRefreshLayout l) {
        layout = l;
        layout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        layout.setRefreshing(true);
        App.logI("Загрузка началась");
    }
}
