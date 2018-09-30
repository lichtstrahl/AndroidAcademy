package root.iv.androidacademy;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> listNews = null;
    private Resources res = null;
    private LayoutInflater inflater = null;
    private RequestManager glide = null;
    private View.OnClickListener listener = null;
    private NewsAdapter(){}
    public static BuilderNewsAdapter getBuilderNewsAdapter() {
        return new NewsAdapter().new BuilderNewsAdapter();
    }

    public class BuilderNewsAdapter {
        public BuilderNewsAdapter buildListNews(List<NewsItem> items) {
            listNews = items;
            return this;
        }
        public BuilderNewsAdapter buildResources(Resources r) {
            res = r;
            return this;
        }
        public BuilderNewsAdapter buildInflater(LayoutInflater inf) {
            inflater = inf;
            return this;
        }
        public BuilderNewsAdapter buildRequestManager(RequestManager manager) {
            glide = manager;
            return this;
        }
        public BuilderNewsAdapter buildListener(View.OnClickListener l) {
            listener = l;
            return  this;
        }
        public NewsAdapter build() {
            if (listNews != null && res != null && inflater != null && glide != null && listener != null)
                return NewsAdapter.this;
            else
                return null;
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewsViewHolder(inflater.inflate(R.layout.item_news, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder viewHolder, int i) {
        viewHolder.bindNewsItemView(glide, i);
    }

    @Override
    public int getItemCount() {
        return listNews.size();
    }

    public NewsItem getItem(int pos) {
        return listNews.get(pos);
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

        public void bindNewsItemView(RequestManager glide, int pos) {
            NewsItem newsItem = listNews.get(pos);
            viewCategory.setText(newsItem.getCategory().getName());
            viewTitle.setText(newsItem.getTitle());
            viewPreview.setText(newsItem.getPreviewText());
            viewDate.setText(newsItem.getPublishDateString());
            glide.load(newsItem.getImageUrl()).into(imageView);
            layout.setBackgroundColor(res.getColor(newsItem.getCategory().getColor()));
        }
    }
}
