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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.DataUtils;
import root.iv.androidacademy.NewsAdapter;
import root.iv.androidacademy.NewsItem;
import root.iv.androidacademy.R;

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
        loader = new LoaderRX();
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

   public static void pause(int mlsec) {
        try {Thread.sleep(mlsec);}
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Загружаем все новости за раз в фоновом потоке
    class LoaderCommon implements ILoader {
        private Thread th = null;
        @Override
        public void stop() {
            if (th != null) th.interrupt();
            th = null;
        }

        @Override
        public void load() {
            th = new Thread(() ->{
                ((NewsAdapter)listNews.getAdapter()).append(DataUtils.news);
                runOnUiThread(()->listNews.getAdapter().notifyDataSetChanged());
                loadDialog.dismiss();
            }
            );
            th.start();
        }
    }

    // Загружаем новости по отдельности
    class LoaderSplit implements ILoader {
        private ExecutorService executor = null;
        @Override
        public void stop() {
            if (executor != null) executor.shutdownNow();
        }

        @Override
        public void load() {
            int count = DataUtils.news.size();
            executor = Executors.newFixedThreadPool(count);
            for (int i = 0; i < count; i++)
                executor.execute(new RunnableNewsLoad(i, (NewsAdapter)listNews.getAdapter()));
            executor.submit(() ->
                    loadDialog.dismiss()
            );
        }
    }

    class LoaderRX implements ILoader {
        private NewsItemObserver observer = null;
        @Override
        public void stop() {
            if (observer != null) observer.dispose();
        }

        @Override
        public void load() {
            observer = new NewsItemObserver((NewsAdapter)listNews.getAdapter());
            Observable.fromIterable(DataUtils.news)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(observer);
        }
    }

    interface ILoader {
        void stop();
        void load();
    }


    class NewsItemObserver implements Observer<NewsItem> {
        private Disposable disposable;
        private NewsAdapter adapter;
        NewsItemObserver(NewsAdapter a) {
            adapter = a;
        }
        @Override
        public void onSubscribe(Disposable d) {
            disposable = d;
        }

        @Override
        public void onNext(NewsItem newsItem) {
            adapter.append(newsItem);
            runOnUiThread(() ->adapter.notifyDataSetChanged());
        }

        @Override
        public void onError(Throwable e) {
            Log.e(NewsListActivity.TAG, e.getMessage());
        }

        @Override
        public void onComplete() {
            loadDialog.dismiss();
        }

        void dispose() {
            disposable.dispose();
        }
    }

    class RunnableNewsLoad implements Runnable {
        private int index;
        private NewsAdapter adapter;
        RunnableNewsLoad(int i, NewsAdapter adapter) {
            this.index = i;
            this.adapter = adapter;
        }
        @Override
        public void run() {
            adapter.append(DataUtils.news.get(index));
            runOnUiThread(()->adapter.notifyDataSetChanged());
        }
    }
}
