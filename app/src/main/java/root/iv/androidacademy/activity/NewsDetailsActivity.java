package root.iv.androidacademy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.R;
import root.iv.androidacademy.news.NewsItem;

public class NewsDetailsActivity extends AppCompatActivity{
    private static final String INTENT_ID = "INTENT_ID";
    private int itemID;

    public static void start(Context context, int id) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.putExtra(INTENT_ID, id);
        context.startActivity(intent);
    }

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);

        itemID = getIntent().getIntExtra(INTENT_ID, -1);
        NewsItem newsItem = App.getDatabase().getNewsDAO().getItemById(itemID).toNewsItem();
        setTitle(newsItem.getSubSection());
        webView.loadUrl(newsItem.getFullText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemMenuDelete:
                App.getDatabase().getNewsDAO().delete(itemID);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}