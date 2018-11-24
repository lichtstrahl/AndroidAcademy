package root.iv.androidacademy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.R;

public class NewsDetailsActivity extends AppCompatActivity{
    @BindView(R.id.web)
    WebView webView;

    public static void start(Context context, NewsItem item) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.putExtra(NewsItem.INTENT_TAG, item);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);

        NewsItem newsItem = getIntent().getParcelableExtra(NewsItem.INTENT_TAG);
        setTitle(newsItem.getSubSection());
        webView.loadUrl(newsItem.getFullText());
    }
}
