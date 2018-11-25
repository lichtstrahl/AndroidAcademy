package root.iv.androidacademy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.App;
import root.iv.androidacademy.GlideApp;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.R;
import root.iv.androidacademy.news.Section;

public class NewsDetailsActivity extends AppCompatActivity{

    public static final String INTENT_ID = "INTENT_ID";

    public static void start(Context context, int id) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.putExtra(INTENT_ID, id);
        context.startActivity(intent);
    }

    @BindView(R.id.viewTitle)
    TextView viewTitle;
    @BindView(R.id.viewDate)
    TextView viewDate;
    @BindView(R.id.viewImage)
    ImageView viewImage;
    @BindView(R.id.layoutBG)
    ScrollView scrollView;
    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);

        int id = getIntent().getIntExtra(INTENT_ID, -1);
        NewsItem newsItem = App.getDatabase().getNewsDAO().getItemById(id).toNewsItem();
        setTitle(newsItem.getSubSection());
        viewTitle.setText(newsItem.getTitle());
        viewDate.setText(newsItem.getPublishDateString());
        GlideApp.with(this).load(newsItem.getImageUrl()).into(viewImage);
        int color = Section.getColorForSection(newsItem.getSubSection());
        scrollView.setBackgroundColor(getResources().getColor(color));
        webView.loadUrl(newsItem.getFullText());
    }
}