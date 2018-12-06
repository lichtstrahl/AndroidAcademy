package root.iv.androidacademy.news;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import root.iv.androidacademy.R;
import root.iv.androidacademy.util.GlideApp;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> listNews;    // То что в данный момент показывается и с чем идет работа
    private List<NewsItem> originNews;  // Что изначально пришло с сервера
    private LayoutInflater inflater;
    private View.OnClickListener listener;
    private String curSection = Section.SECTIONS[0].getName();

    public NewsAdapter(List<NewsItem> list, LayoutInflater inf){
        this.listNews = list;
        this.inflater = inf;
        this.listener = null;
        this.originNews = new LinkedList<>();
    }

    public void setNewSection(String section) {
        curSection = section;
    }

    public void addOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void delOnClickListener() {
        this.listener = null;
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
        int count = listNews.size();
        listNews.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void append(NewsItem item) {
        listNews.add(item);
        notifyItemInserted(listNews.size()-1);
    }

    public void notifyOriginNews() {
        originNews.clear();
        originNews.addAll(listNews);
    }

    public void appendAll(List<NewsItem> items) {
        for (NewsItem item : items)
            append(item);
    }

    /**
     * Вызывается каждый раз, когда происходит изменение текста для поиска
     * @param filter - текст для поиска
     */
    public void setFilter(String filter) {
        clear();
        for (NewsItem item : originNews) {
            String fullText = item.getTitle() + " " + item.getPreviewText() + " " + item.getFullText();
            if (fullText.toLowerCase().contains(filter.toLowerCase()))
                append(item);
        }
        sort();
    }

    public void sort() {
        Collections.sort(listNews, new NewsItem.Comparator());
        notifyDataSetChanged();
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
            viewCategory.setText(newsItem.getSubSection());
            viewCategory.setVisibility(newsItem.getSubSection().isEmpty() ? View.GONE : View.VISIBLE);
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
