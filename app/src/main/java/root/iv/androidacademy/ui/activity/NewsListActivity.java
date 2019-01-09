package root.iv.androidacademy.ui.activity;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.R;
import root.iv.androidacademy.ui.ivEditText;
import root.iv.androidacademy.util.listener.ButtonUpdateClickListener;
import root.iv.androidacademy.util.listener.ClickListener;
import root.iv.androidacademy.util.listener.ListenerEditText;
import root.iv.androidacademy.util.listener.NewsItemClickListener;
import root.iv.androidacademy.util.listener.NewsItemLongClickListener;
import root.iv.androidacademy.util.listener.ScrollListener;
import root.iv.androidacademy.util.listener.SpinnerInteractionListener;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.news.Section;
import root.iv.androidacademy.retrofit.RetrofitLoader;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;
import root.iv.androidacademy.util.Action1;
import root.iv.androidacademy.util.DBObserver;

public class NewsListActivity extends AppCompatActivity {
    private static final String LAST_SECTION = "LAST_SECTION";
    private static final String SAVE_SECTION = "save:section";
    private static final String SAVE_FILTER = "save:filter";
    private static final String SAVE_LOAD = "save:load";
    private RecyclerView recyclerListNews;
    private FloatingActionButton buttonUpdate;
    private NewsAdapter adapter;
    private AlertDialog loadDialog;
    private RetrofitLoader loader;
    private ListenerEditText inputListener;
    private ClickListener<Action1<View>> adapterListener;
    private ClickListener<Action> buttonUpdateListener;
    private NewsItemLongClickListener adapterLongListener;
    private ScrollListener scrollListener;
    private SpinnerInteractionListener spinnerListener;
    private Spinner spinner;
    @Nullable
    private DBObserver<List<NewsEntity>> loadDBObserver;
    @Nullable
    private DBObserver<Integer> itemClickObserver;
    @Nullable
    private DBObserver<Integer> itemLongClickObserver;
    @Nullable
    private DBObserver<TopStoriesDTO> deleteAllObserver;
    private ivEditText inputFilter;
    @Nullable
    private Disposable completeLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        recyclerListNews = findViewById(R.id.listNews);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        spinner = findViewById(R.id.spinner);
        inputFilter = findViewById(R.id.input);
        loadSpinner();

        adapter = new NewsAdapter(new LinkedList<>(), getLayoutInflater());
        recyclerListNews.setAdapter(adapter);
        recyclerListNews.addOnScrollListener(new ScrollListener());
        configureLayoutManagerForRecyclerView(getResources().getConfiguration().orientation);

        loadDialog = buildLoadDialog();
        initialListener();



        inputFilter.setOnEditorActionListener((view, action, event) -> {
            App.logI("Событие: " + action);
            releaseInputFilterFull();
            return false;
        });


        if (savedInstanceState != null) {
            spinner.setSelection(savedInstanceState.getInt(SAVE_SECTION, 0));
            inputFilter.setText(savedInstanceState.getString(SAVE_FILTER, ""));
        }

        loader = new RetrofitLoader(spinner.getSelectedItem().toString(), this::completeLoad, this::errorLoad);
    }

    private void releaseInputFilterFull() {
        if (inputFilter.isFocused()) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(inputFilter.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputFilter.clearFocus();
            App.logI("Release Input Filter");
        }
    }

    private void releaseInputFilterLite() {
        inputFilter.clearFocus();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        releaseInputFilterFull();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Если при повороте была загрузка, значит она была уже остановлена. Поэтому нужно начать всё заново.
        boolean isLoading = savedInstanceState.getBoolean(SAVE_LOAD, false);
        if (isLoading) loader.load();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFromDB(spinner.getSelectedItem().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputFilter.clearFocus();

        buttonUpdateListener.subscribe(() -> {
            loadDialog.show();
            releaseInputFilterFull();
            Object item = spinner.getSelectedItem();
            loader.setSection(item.toString());
            loader.load();
        });

        adapterListener.subscribe((view) -> {
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            // Аналогично loadFromDB. Single создаём там же, где и используем
            itemClickObserver = new DBObserver<>(this::startDetailsActivity, this::errorLoadFromDB);

            App.getDatabase().getNewsDAO().getIdAsSingle(item.getTitle(), item.getPreviewText(), item.getPublishDateString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(itemClickObserver);
        });

        adapterLongListener.subscribe((view) -> {
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            itemLongClickObserver = new DBObserver<>(this::startEditActivity, this::errorLoadFromDB);

            App.getDatabase().getNewsDAO().getIdAsSingle(item.getTitle(), item.getPreviewText(), item.getPublishDateString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(itemLongClickObserver);
        });

        scrollListener.subscribe((state) -> {
            if (state != 0) {
                buttonUpdate.hide();
                releaseInputFilterFull();
            } else {
                buttonUpdate.show();
            }
        });

        spinnerListener.subscribe((position) -> {
            String section = Section.SECTIONS[position].getName();
            loadDialog.show();
            adapter.setNewSection(section);
            loader.setSection(section);
            loader.load();
        }, this::releaseInputFilterFull);

        recyclerListNews.addOnScrollListener(scrollListener);
        inputListener.subscribe(adapter::setFilter);
        adapter.addOnClickListener(adapterListener);
        adapter.addOnLongClickListener(adapterLongListener);
        buttonUpdate.setOnClickListener(buttonUpdateListener);
        spinner.setOnTouchListener(spinnerListener);
        spinner.setOnItemSelectedListener(spinnerListener);
        inputFilter.subscribe(this::releaseInputFilterLite);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scrollListener.unsubscribe();
        inputListener.unsubscribe();
        buttonUpdateListener.unsubscribe();
        adapterListener.unsubscribe();
        adapter.delOnClickListener();
        adapter.delOnLongClickListener();
        spinnerListener.unsubscribe();
        inputFilter.unsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loader.stop();
        adapter.clear();    // Зачем это здесь!?!?!??!

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences
                .edit()
                .putInt(LAST_SECTION, spinner.getSelectedItemPosition())
                .apply();

        if (loadDBObserver != null) loadDBObserver.unsubscribe();
        if (itemClickObserver != null) itemClickObserver.unsubscribe();
        if (itemLongClickObserver != null) itemLongClickObserver.unsubscribe();
        if (deleteAllObserver != null) deleteAllObserver.unsubscribe();
        if (completeLoad != null) completeLoad.dispose();   // Возможно обновление не вызывалось
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_SECTION, spinner.getSelectedItemPosition());
        outState.putString(SAVE_FILTER, inputFilter.getText().toString());
        outState.putBoolean(SAVE_LOAD, loadDialog.isShowing());
    }

    private void loadSpinner() {
        ArrayAdapter<Section> spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinnerListItem));
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(getPreferences(MODE_PRIVATE).getInt(LAST_SECTION, 0));
    }

    private void loadFromDB(String section) {
        adapter.setNewSection(section);
        adapter.clear();

        // Наблюдателя создаём именно здесь, а не в onCreate, иначе Single больше не отреагирует
        loadDBObserver = new DBObserver<>(this::successfulLoadFromDB, this::errorLoadFromDB);
        App.getDatabase().getNewsDAO().getAllAsSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadDBObserver);
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
        inputListener = new ListenerEditText(inputFilter);
        adapterListener = new NewsItemClickListener();
        adapterLongListener = new NewsItemLongClickListener();
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
     * Если истории получены и все впорядке, тогда сначала наблюдаем за их удалением. Ждем, параллельно наблюдая просто за stories.
     * Как только удаление завершилось, реагируем на это, получив наши stories благодаря "storiesDTO"
     * @param stories
     */
    private void completeLoad(@Nullable TopStoriesDTO stories) {
        if (stories != null) {
            Single<Integer> deleteAll = Single.fromCallable(() -> App.getDatabase().getNewsDAO().deleteAll());
            Single<TopStoriesDTO> storiesDTO = Single.fromCallable(() -> stories);

            deleteAllObserver = new DBObserver<>(this::insertAllToDB, this::errorLoadFromDB);
            deleteAll.zipWith(storiesDTO, (integer, st) -> st)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(deleteAllObserver);
        } else {
            loadDialog.dismiss();
        }
    }

    /**
     * Реакция на удаление старых данных из БД. Её очистки.
     * Теперь каждую новость нужно вставить в БД.
     * Для этого мы будем наблюдать за КАЖДОЙ вставкой, создавая массив Single
     * После того как они все завершатся, а мы объединили их завершение в zip
     * Мы обновляем содержимое UI и убираем диалог загрузки.
     * При ошибке мы также скрываем диалог и выводим в лог сообщение.
     * @param stories
     */
    private void insertAllToDB(TopStoriesDTO stories) {
        List<Single<Long>> singles = new LinkedList<>();

        for (NewsDTO news : stories.getListNews()) {
            try {
                NewsItem item = NewsItem.fromNewsDTO(news);
                singles.add(Single.fromCallable(() -> App.getDatabase().getNewsDAO().insert(NewsEntity.fromNewsItem(item))));
            } catch (ParseException e) {
                App.logE(e.getMessage());
            }
        }

         completeLoad = Single.zip(singles, (args) -> 0)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe((i) -> {
                     loadFromDB(stories.getSection());
                     loadDialog.dismiss();
                 }, (t) -> {
                     loadDialog.dismiss();
                     errorLoadFromDB(t);
                 });
    }


    private void errorLoad() {
        Toast.makeText(NewsListActivity.this, R.string.errorLoading, Toast.LENGTH_SHORT).show();
        loadDialog.findViewById(R.id.progress).setVisibility(View.GONE);
        TextView textView = loadDialog.findViewById(R.id.text);
        textView.setText(R.string.errorLoading);
        loadDialog.findViewById(R.id.buttonReconnect).setVisibility(View.VISIBLE);
        loadDialog.findViewById(R.id.buttonReconnect).setOnClickListener(view -> loader.load());
    }

    private void successfulLoadFromDB(List<NewsEntity> list) {
        for (NewsEntity entity : list) {
            adapter.append(entity.toNewsItem());
        }
        adapter.notifyOriginNews();
        adapter.sort();
    }

    private void startDetailsActivity(Integer id) {
        NewsDetailsActivity.start(recyclerListNews.getContext(), id);
    }

    private void startEditActivity(Integer id) {
        EditNewsActivity.start(recyclerListNews.getContext(), id);
    }

    private void errorLoadFromDB(Throwable t) {
        App.logE(t.getMessage());
    }
}
