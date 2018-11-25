package root.iv.androidacademy.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import root.iv.androidacademy.App;
import root.iv.androidacademy.activity.listener.ListenerEditText;
import root.iv.androidacademy.activity.listener.ButtonUpdateClickListener;
import root.iv.androidacademy.activity.listener.Listener;
import root.iv.androidacademy.activity.listener.NewsItemClickListener;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.R;
import root.iv.androidacademy.news.Section;
import root.iv.androidacademy.retrofit.RetrofitLoader;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;

public class NewsListActivity extends AppCompatActivity {
    private RecyclerView recyclerListNews;
    private FloatingActionButton buttonUpdate;
    private NewsAdapter adapter;
    private AlertDialog loadDialog;
    private RetrofitLoader loader;
    private ListenerEditText inputListener;
    private Listener adapterListener;
    private Listener buttonUpdateListener;
    private int spinnerCount = 0;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.input)
    EditText input;

    private void loadSpinner() {
        ArrayAdapter<Section> spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinnerListItem));
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (spinnerCount++ > 0) {
                            String section = Section.SECTIONS[position].getName();

                            adapter.setNewSection(section);
                            loader.setSection(section);
                            loader.load();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Никогда не вызывается
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        ButterKnife.bind(this);
        loadSpinner();

        recyclerListNews = findViewById(R.id.listNews);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        adapter = new NewsAdapter(new LinkedList<>(), getLayoutInflater());
        recyclerListNews.setAdapter(adapter);

        configureLayoutManagerForRecyclerView(getResources().getConfiguration().orientation);

        loadDialog = buildLoadDialog();

        loader = new RetrofitLoader(spinner.getSelectedItem().toString() ,this::completeLoad, this::errorLoad);
        initialListener();
    }

    private void configureLayoutManagerForRecyclerView(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerListNews.setLayoutManager(new LinearLayoutManager(this));

        } else {
            recyclerListNews.setLayoutManager(new GridLayoutManager(this, 2));

        }
    }

    private AlertDialog buildLoadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.dialog).setCancelable(false);

       return builder.create();
    }

    private void initialListener() {
        inputListener = new ListenerEditText(input);
        adapterListener = new NewsItemClickListener(recyclerListNews);
        buttonUpdateListener = new ButtonUpdateClickListener(loader, spinner, loadDialog);
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
    protected void onStop() {
        super.onStop();
        loader.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputListener.subscribe(((NewsAdapter) recyclerListNews.getAdapter())::setFilter);
        adapter.addOnClickListener(adapterListener);
        buttonUpdate.setOnClickListener(buttonUpdateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        inputListener.unsubscribe();
        buttonUpdateListener.unsubscribe();
        adapterListener.unsubscribe();
        adapter.delOnClickListener();
    }

    /**
     * После окончания загрузки данных в адаптер сортирум их и замещаем originNews
     * @param stories
     */
    private void completeLoad(@Nullable TopStoriesDTO stories) {
        App.logI("Complete load: " + stories.getSection());
        adapter.clear();

        for (NewsDTO news : stories.getListNews()) {
            try {
                adapter.append(NewsItem.fromNewsDTO(news));
            } catch (ParseException e) {
                App.stdLog(e);
            }
        }

        adapter.notifyOriginNews();
        adapter.sort();

        loadDialog.dismiss();
    }

    private void errorLoad() {
        Toast.makeText(NewsListActivity.this, R.string.errorLoading, Toast.LENGTH_SHORT).show();
        loadDialog.findViewById(R.id.progress).setVisibility(View.GONE);
        TextView textView = loadDialog.findViewById(R.id.text);
        textView.setText(R.string.errorLoading);
        loadDialog.findViewById(R.id.buttonReconnect).setVisibility(View.VISIBLE);
        loadDialog.findViewById(R.id.buttonReconnect).setOnClickListener(view -> loader.load());
    }
}
