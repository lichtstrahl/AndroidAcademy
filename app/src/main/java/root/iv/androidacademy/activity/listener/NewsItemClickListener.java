package root.iv.androidacademy.activity.listener;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import root.iv.androidacademy.activity.NewsDetailsActivity;
import root.iv.androidacademy.news.NewsAdapter;

public class NewsItemClickListener implements View.OnClickListener, Listener{
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    public NewsItemClickListener(RecyclerView r) {
        recyclerView = r;
        adapter = (NewsAdapter)r.getAdapter();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildAdapterPosition(v);
        NewsDetailsActivity.start(recyclerView.getContext(), adapter.getItem(pos));
    }

    public void unsubscribe() {
        recyclerView = null;
        adapter = null;
    }
}
