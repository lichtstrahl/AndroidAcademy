package root.iv.androidacademy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import root.iv.androidacademy.activity.NewsDetailsActivity;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<NewsItem> listNews;
    private final Context context;
    private final LayoutInflater inflater;
    public NewsAdapter(Context c, List<NewsItem> list) {
        listNews = list;
        context = c;
        inflater = LayoutInflater.from(c);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewsViewHolder(inflater.inflate(R.layout.item_news, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        NewsItem newsItem = listNews.get(i);
        viewHolder.viewCategory.setText(newsItem.getCategory().getName());
        viewHolder.viewTitle.setText(newsItem.getTitle());
        viewHolder.viewPreview.setText(newsItem.getPreviewText());
        viewHolder.viewDate.setText(newsItem.getPublishDateString());
        Glide.with(context).load(newsItem.getImageUrl()).into(viewHolder.imageView);
        viewHolder.layout.setBackgroundColor(context.getResources().getColor(newsItem.getCategory().getColor()));
    }

    @Override
    public int getItemCount() {
        return listNews.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView  viewCategory;
        private final TextView  viewTitle;
        private final TextView viewPreview;
        private final TextView viewDate;
        private final ConstraintLayout layout;
        NewsViewHolder(View item) {
            super(item);
            layout = item.findViewById(R.id.layoutBG);
            imageView = item.findViewById(R.id.imageView);
            viewCategory = item.findViewById(R.id.viewCategory);
            viewTitle = item.findViewById(R.id.viewTitle);
            viewPreview = item.findViewById(R.id.viewPreview);
            viewDate = item.findViewById(R.id.viewDate);
            item.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION)
                    NewsDetailsActivity.start(context, listNews.get(pos));
            });

        }
    }
}
