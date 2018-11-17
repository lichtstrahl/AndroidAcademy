package root.iv.androidacademy.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.NewsItem;
import root.iv.androidacademy.R;

public class NewsDetailsActivity extends AppCompatActivity{
    @BindView(R.id.web)
    WebView webView;

    public static void start(Context context, NewsItem item) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.putExtra(NewsItem.INTENT_TAG, item);
        intent.setExtrasClassLoader(NewsItem.class.getClassLoader());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);
        // TODO Какая-то магия для Samsung со StackOverflow
        getIntent().setExtrasClassLoader(NewsItem.class.getClassLoader());
        NewsItem newsItem = getIntent().getParcelableExtra(NewsItem.INTENT_TAG);
        setTitle(newsItem.getSection());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(newsItem.getFullText());
        webView.setWebViewClient(new WebClient());
    }

    class  WebClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
