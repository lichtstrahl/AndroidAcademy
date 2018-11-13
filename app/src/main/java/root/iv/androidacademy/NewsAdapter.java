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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> listNews;
    private List<NewsItem> deletedNews;
    private LayoutInflater inflater;
    private View.OnClickListener listener;
    private String curSection = Section.SECTIONS[0].getName();
    private String filter = "";

    private NewsAdapter(Builder builder){
        listNews = builder.listNews;
        inflater = builder.inflater;
        listener = builder.listener;
        deletedNews = new LinkedList<>();
    }

    public static Builder getBuilderNewsAdapter() {
        return new Builder();
    }

    public static class Builder {
        private List<NewsItem> listNews = null;
        private LayoutInflater inflater = null;
        private View.OnClickListener listener = null;

        public Builder buildListNews(List<NewsItem> items) {
            listNews = items;
            return this;
        }

        public Builder buildInflater(LayoutInflater inf) {
            inflater = inf;
            return this;
        }

        public Builder buildListener(View.OnClickListener l) {
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

    public void setNewSection(String section) {
        curSection = section;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_news, viewGroup, false);
        if (i != 0) view.setVisibility(View.GONE);
        return new NewsViewHolder(view);
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

    public void clear() {
        listNews.clear();
        notifyItemRangeRemoved(0, listNews.size());
    }

    public void append(NewsItem item) {
        listNews.add(item);
        notifyItemInserted(listNews.size()-1);
    }

    public void remove(NewsItem item) {
        int index = listNews.indexOf(item);
        if (index != -1) {
            listNews.remove(item);
            notifyItemRemoved(index);
        }
    }

    public void setFilter(String newFilter) {
        filter = newFilter;

        for (NewsItem item : listNews) {
            String fullText = item.getSection().getName() + " " + item.getTitle() + " " + item.getPreviewText();
            if (!fullText.toLowerCase().contains(filter.toLowerCase()))
                deletedNews.add(item);
        }

        LinkedList<NewsItem> reload = new LinkedList<>();
        for (NewsItem item : deletedNews) {
            String fullText = item.getSection().getName() + " " + item.getTitle() + " " + item.getPreviewText();
            if (fullText.toLowerCase().contains(filter.toLowerCase())) {
                append(item);
                reload.add(item);
            }
        }
        deletedNews.removeAll(reload);


        for (NewsItem item : deletedNews)
            remove(item);
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
            viewCategory.setText(newsItem.getSection().getName());
            viewCategory.setVisibility(newsItem.getSection().getName().isEmpty() ? View.GONE : View.VISIBLE);
            viewTitle.setText(newsItem.getTitle());
            viewPreview.setText(newsItem.getPreviewText());
            viewDate.setText(newsItem.getPublishDateString());
            GlideApp.with(imageView.getContext())
                    .load(newsItem.getImageUrl())
                    .into(imageView);
            imageView.setVisibility(newsItem.getImageUrl().isEmpty() ? View.GONE : View.VISIBLE);
            int colorID = Section.getColorForSection(curSection);
            int color = layout.getContext().getResources().getColor(colorID);
            layout.setBackgroundColor(color);
        }
    }
}
