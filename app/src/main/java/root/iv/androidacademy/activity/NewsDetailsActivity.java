package root.iv.androidacademy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.NewsItem;
import root.iv.androidacademy.R;

public class NewsDetailsActivity extends AppCompatActivity{
    public static void start(Context context, NewsItem item) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        item.putToExtra(intent);
        context.startActivity(intent);
    }

    @BindView(R.id.viewTitle)
    TextView viewTitle;
    @BindView(R.id.viewDate)
    TextView viewDate;
    @BindView(R.id.viewFull)
    TextView viewFull;
    @BindView(R.id.viewImage)
    ImageView viewImage;
    @BindView(R.id.layoutBG)
    ConstraintLayout layout;

    private NewsItem newsItem = new NewsItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);

        newsItem.loadFromExtra(getIntent());
        setTitle(newsItem.getCategory().getName());
        viewTitle.setText(newsItem.getTitle());
        viewDate.setText(newsItem.getPublishDateString());
        viewFull.setText(newsItem.getFullText());
        Glide.with(this).load(newsItem.getImageUrl()).into(viewImage);
        layout.setBackgroundColor(getResources().getColor(newsItem.getCategory().getColor()));
    }
}
