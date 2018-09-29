package root.iv.androidacademy.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.ButterKnife;
import root.iv.androidacademy.DataUtils;
import root.iv.androidacademy.NewsAdapter;
import root.iv.androidacademy.NewsItem;
import root.iv.androidacademy.R;

public class NewsListActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    RecyclerView listNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        setTitle(R.string.news);
        ButterKnife.bind(this);
        listNews = findViewById(R.id.listNews);
        listNews.setAdapter(new NewsAdapter(this, DataUtils.generateNews()));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            listNews.setLayoutManager(new LinearLayoutManager(this));
        else
            listNews.setLayoutManager(new GridLayoutManager(this, 2));
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, String.valueOf(newConfig.orientation));

    }
}
