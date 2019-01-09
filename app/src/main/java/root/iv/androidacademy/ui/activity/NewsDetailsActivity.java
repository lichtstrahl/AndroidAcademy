package root.iv.androidacademy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.util.DBObserver;

public class NewsDetailsActivity extends AppCompatActivity{
    private static final String INTENT_ID = "INTENT_ID";
    private int itemID;
    private DBObserver<NewsEntity> findNewsItemObserver;
    private DBObserver<Integer> deleteNewsItemObserver;

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
        // Вроде это немножко помогло в ускорении прогрузки
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl( "javascript:window.location.reload( true )" );

        findNewsItemObserver = new DBObserver<>(this::successfulFindNewsItem, this::errorLoadFromDB);
        deleteNewsItemObserver = new DBObserver<>(this::finish, this::errorLoadFromDB);
        itemID = getIntent().getIntExtra(INTENT_ID, -1);

        Single.fromCallable(() -> App.getDatabase().getNewsDAO().getItemById(itemID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(findNewsItemObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        findNewsItemObserver.unsubscribe();
        deleteNewsItemObserver.unsubscribe();
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
                Single.fromCallable(() -> App.getDatabase().getNewsDAO().delete(itemID))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(deleteNewsItemObserver);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void successfulFindNewsItem(NewsEntity entity) {
        setTitle(entity.getSubSection());
        webView.loadUrl(entity.getFullText());
        App.logI("Web view load \"" + entity.getFullText() + "\"");
    }

    private void finish(Integer i) {
        finish();
    }

    private void errorLoadFromDB(Throwable t) {
        App.logE(t.getMessage());
    }
}