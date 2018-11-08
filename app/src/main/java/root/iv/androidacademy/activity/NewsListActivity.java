package root.iv.androidacademy.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.App;
import root.iv.androidacademy.NewsAdapter;
import root.iv.androidacademy.R;
import root.iv.androidacademy.retrofit.TopStoriesAPI;
import root.iv.androidacademy.retrofit.TopStoriesObserver;

public class NewsListActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "NewsListActivity";
    private RecyclerView listNews;
    private AlertDialog loadDialog;
    private ILoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        setTitle(R.string.news);
        ButterKnife.bind(this);
        listNews = findViewById(R.id.listNews);
        listNews.setAdapter(
                NewsAdapter.getBuilderNewsAdapter()
                .buildInflater(LayoutInflater.from(this))
                .buildListener(this)
                .build()
        );
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            listNews.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            listNews.setLayoutManager(new GridLayoutManager(this, 2));
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.dialog).setCancelable(false);

        loadDialog = builder.create();
        loadDialog.show();
    }

    @Override
    public void onClick(View v) {
        int pos = listNews.getChildAdapterPosition(v);
        NewsDetailsActivity.start(this, ((NewsAdapter) listNews.getAdapter()).getItem(pos));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAbout:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemExit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        loader = new LoaderRetrofit();
        loader.load();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loader.stop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, String.valueOf(newConfig.orientation));
    }

    class LoaderRetrofit implements ILoader {
        private TopStoriesObserver observer = new TopStoriesObserver(
                ((NewsAdapter)listNews.getAdapter()),
                () -> {
                    loadDialog.dismiss();
                });
        @Override
        public void stop() {
            observer.dispose();
        }

        @Override
        public void load() {
            TopStoriesAPI topStoriesAPI = App.getRetrofit().create(TopStoriesAPI.class);
            topStoriesAPI.getTopStories("world")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
    }

    interface ILoader {
        void stop();
        void load();
    }
}
