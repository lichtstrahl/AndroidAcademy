package root.iv.androidacademy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.List;

import io.reactivex.functions.Action;
import root.iv.androidacademy.activity.listener.ScrollListener;
import root.iv.androidacademy.activity.listener.SpinnerInteractionListener;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.R;
import root.iv.androidacademy.activity.listener.ButtonUpdateClickListener;
import root.iv.androidacademy.activity.listener.ClickListener;
import root.iv.androidacademy.activity.listener.ListenerEditText;
import root.iv.androidacademy.activity.listener.NewsItemClickListener;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.news.Section;
import root.iv.androidacademy.retrofit.RetrofitLoader;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;
import root.iv.androidacademy.util.Action1;

public class NewsListActivity extends AppCompatActivity {
    private static final String INTENT_SECTION = "INTENT_SECTION";
    private static final String LAST_SECTION = "LAST_SECTION";
    private RecyclerView recyclerListNews;
    private FloatingActionButton buttonUpdate;
    private NewsAdapter adapter;
    private AlertDialog loadDialog;
    private RetrofitLoader loader;
    private ListenerEditText inputListener;
    private ClickListener<Action1<View>> adapterListener;
    private ClickListener<Action> buttonUpdateListener;
    private ScrollListener scrollListener;
    private SpinnerInteractionListener spinnerListener;
    private Spinner spinner;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        recyclerListNews = findViewById(R.id.listNews);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        spinner = findViewById(R.id.spinner);
        input = findViewById(R.id.input);
        loadSpinner();

        if (savedInstanceState != null) {
            spinner.setSelection(savedInstanceState.getInt(INTENT_SECTION, 0));
        }

        adapter = new NewsAdapter(new LinkedList<>(), getLayoutInflater());
        recyclerListNews.setAdapter(adapter);
        recyclerListNews.addOnScrollListener(new ScrollListener());
        configureLayoutManagerForRecyclerView(getResources().getConfiguration().orientation);

        loadDialog = buildLoadDialog();

        loader = new RetrofitLoader(spinner.getSelectedItem().toString() ,this::completeLoad, this::errorLoad);
        initialListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadFromDB(spinner.getSelectedItem().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        buttonUpdateListener.subscribe(() -> {
            loadDialog.show();
            Object item = spinner.getSelectedItem();
            if (item == null) {
                App.logI("Spinner item is NULL");
            } else {
                App.logI("Spinner item not NULL");
            }
            loader.setSection(item.toString());
            loader.load();
        });

        adapterListener.subscribe((view) -> {
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            int id = App.getDatabase().getNewsDAO().getId(item.getTitle(), item.getPreviewText(), item.getPublishDateString());
            NewsDetailsActivity.start(recyclerListNews.getContext(), id);
        });

        scrollListener.subscribe((state) -> {
            if (state != 0) {
                buttonUpdate.hide();
            } else {
                buttonUpdate.show();
            }
        });

        spinnerListener.subscribe((position) -> {
            String section = Section.SECTIONS[position].getName();

            adapter.setNewSection(section);
            loader.setSection(section);
            loader.load();
        });

        recyclerListNews.addOnScrollListener(scrollListener);
        inputListener.subscribe(adapter::setFilter);
        adapter.addOnClickListener(adapterListener);
        buttonUpdate.setOnClickListener(buttonUpdateListener);
        spinner.setOnTouchListener(spinnerListener);
        spinner.setOnItemSelectedListener(spinnerListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scrollListener.unsubscribe();
        inputListener.unsubscribe();
        buttonUpdateListener.unsubscribe();
        adapterListener.unsubscribe();
        adapter.delOnClickListener();
        spinnerListener.unsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loader.stop();
        adapter.clear();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences
                .edit()
                .putInt(LAST_SECTION, spinner.getSelectedItemPosition())
                .apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INTENT_SECTION, spinner.getSelectedItemPosition());
    }

    private void loadSpinner() {
        ArrayAdapter<Section> spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinnerListItem));
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(getPreferences(MODE_PRIVATE).getInt(LAST_SECTION, 0));
    }

    private void loadFromDB(String section) {
        adapter.setNewSection(section);
        adapter.clear();
        List<NewsEntity> list = App.getDatabase().getNewsDAO().getAllAsList();
        for (NewsEntity entity : list) {
            adapter.append(entity.toNewsItem());
        }
        adapter.notifyOriginNews();
        adapter.sort();

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
        adapterListener = new NewsItemClickListener();
        buttonUpdateListener = new ButtonUpdateClickListener();
        scrollListener = new ScrollListener();
        spinnerListener = new SpinnerInteractionListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu_list,menu);
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

    /**
     * После окончания загрузки данных в адаптер сортирум их и замещаем originNews
     * @param stories
     */
    private void completeLoad(@Nullable TopStoriesDTO stories) {
        if (stories != null) {
            App.getDatabase().getNewsDAO().deleteAll();

            for (NewsDTO news : stories.getListNews()) {
                try {
                    NewsItem item = NewsItem.fromNewsDTO(news);
                    App.getDatabase().getNewsDAO().insert(NewsEntity.fromNewsItem(item));
                } catch (ParseException e) {
                    App.logE(e.getMessage());
                }
            }
            loadFromDB(stories.getSection());
        }

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
