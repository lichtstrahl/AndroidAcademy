package root.iv.androidacademy.util;

import root.iv.androidacademy.news.NewsAdapter;

public class NotifyWrapper {
    public void wrapNotifyItemInserted(NewsAdapter adapter, int pos) {
        adapter.notifyItemInserted(pos);
    }


    public void wrapNotifyItemRemoved(NewsAdapter adapter, int pos, int count) {
        adapter.notifyItemRangeRemoved(pos, count);
    }

    public void wrapNotifyDataSetChanged(NewsAdapter adapter) {
        adapter.notifyDataSetChanged();
    }
}