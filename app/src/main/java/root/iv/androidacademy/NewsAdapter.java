package root.iv.androidacademy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import static root.iv.androidacademy.Stopper.pause;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> listNews;
    private LayoutInflater inflater;
    private View.OnClickListener listener;
    private NewsAdapter(BuilderNewsAdapter builder){
        listNews = builder.listNews;
        inflater = builder.inflater;
        listener = builder.listener;
    }

    public static BuilderNewsAdapter getBuilderNewsAdapter() {
        return new NewsAdapter.BuilderNewsAdapter();
    }

    public static class BuilderNewsAdapter {
        private List<NewsItem> listNews = new LinkedList<>();
        private LayoutInflater inflater;
        private View.OnClickListener listener;

        public BuilderNewsAdapter buildListNews(List<NewsItem> items) {
            listNews = items;
            return this;
        }

        public BuilderNewsAdapter buildInflater(LayoutInflater inf) {
            inflater = inf;
            return this;
        }

        public BuilderNewsAdapter buildListener(View.OnClickListener l) {
            listener = l;
            return  this;
        }

        @Nullable
        public NewsAdapter build() {
            if (listNews != null && inflater != null  && listener != null) {
                return new NewsAdapter(this);
            } else {
                return null;
            }
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewsViewHolder(inflater.inflate(R.layout.item_news, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder viewHolder, int i) {
        viewHolder.bindNewsItemView(i);
    }

    @Override
    public int getItemCount() {
        return listNews.size();
    }

    public NewsItem getItem(int pos) {
        return listNews.get(pos);
    }

    public void appendWhichPause(NewsItem item) {
        listNews.add(item);
        pause(200);
    }

    public void append(NewsItem item) {
        listNews.add(item);
        notifyItemInserted(listNews.size()-1);
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView  viewCategory;
        private final TextView  viewTitle;
        private final TextView viewPreview;
        private final TextView viewDate;
        private final ViewGroup layout;

        NewsViewHolder(View item) {
            super(item);
            layout = item.findViewById(R.id.layoutBG);
            imageView = item.findViewById(R.id.imageView);
            viewCategory = item.findViewById(R.id.viewCategory);
            viewTitle = item.findViewById(R.id.viewTitle);
            viewPreview = item.findViewById(R.id.viewPreview);
            viewDate = item.findViewById(R.id.viewDate);
            item.setOnClickListener(listener);
        }

        public void bindNewsItemView(int pos) {
            NewsItem newsItem = listNews.get(pos);
            viewCategory.setText(newsItem.getCategory().getName());
            viewCategory.setVisibility(newsItem.getCategory().getName().isEmpty() ? View.GONE : View.VISIBLE);
            viewTitle.setText(newsItem.getTitle());
            viewPreview.setText(newsItem.getPreviewText());
            viewDate.setText(newsItem.getPublishDateString());
            GlideApp.with(imageView.getContext()).load(newsItem.getImageUrl()).into(imageView);
            imageView.setVisibility(newsItem.getImageUrl().isEmpty() ? View.GONE : View.VISIBLE);
            int color = layout.getContext().getResources().getColor(newsItem.getCategory().getColor());
            layout.setBackgroundColor(color);
        }
    }
}
