package root.iv.androidacademy.activity.listener;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import root.iv.androidacademy.app.App;
import root.iv.androidacademy.activity.NewsDetailsActivity;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsItem;

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
        NewsItem item = adapter.getItem(pos);
        int id = App.getDatabase().getNewsDAO().getId(item.getTitle(), item.getPreviewText(), item.getPublishDateString());
        NewsDetailsActivity.start(recyclerView.getContext(), id);
    }

    public void unsubscribe() {
        recyclerView = null;
        adapter = null;
    }
}
