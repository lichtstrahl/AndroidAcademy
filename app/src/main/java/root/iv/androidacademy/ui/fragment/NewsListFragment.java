package root.iv.androidacademy.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.news.Section;
import root.iv.androidacademy.retrofit.RetrofitLoader;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;
import root.iv.androidacademy.ui.activity.EditNewsActivity;
import root.iv.androidacademy.ui.ivEditText;
import root.iv.androidacademy.ui.ivHorizontalScrollView;
import root.iv.androidacademy.util.Action1;
import root.iv.androidacademy.util.DBObserver;
import root.iv.androidacademy.util.listener.ButtonUpdateClickListener;
import root.iv.androidacademy.util.listener.ClickListener;
import root.iv.androidacademy.util.listener.ListenerEditText;
import root.iv.androidacademy.util.listener.NewsItemClickListener;
import root.iv.androidacademy.util.listener.NewsItemLongClickListener;
import root.iv.androidacademy.util.listener.ScrollListener;

import static android.content.Context.MODE_PRIVATE;

public class NewsListFragment extends Fragment {
    private static final String LAST_SECTION = "LAST_SECTION";
    private static final String SAVE_SECTION = "save:section";
    private static final String SAVE_FILTER = "save:filter";
    private static final String SAVE_LOAD = "save:load";
    public static final String TAG = "fragment:list";
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
    @Nullable
    private Parcelable listState;
    private int section;    // Индекс текущей секции
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
    private LinearLayout layoutSections;
    private ivHorizontalScrollView viewSections;
    private Listener listenerActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listenerActivity = (Listener)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_list, container, false);
        viewSections = view.findViewById(R.id.viewSections);
        layoutSections = view.findViewById(R.id.layoutSections);
        recyclerListNews = view.findViewById(R.id.listNews);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        inputFilter = view.findViewById(R.id.inputFilter);
        section = getActivity().getPreferences(MODE_PRIVATE).getInt(LAST_SECTION, 0);
        loadSections();

        App.logI("Fragment: onCrateView");
        adapter = new NewsAdapter(new LinkedList<>(), getLayoutInflater());
        recyclerListNews.setAdapter(adapter);
        recyclerListNews.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadDialog = buildLoadDialog();
        initialListener();

        inputFilter.setOnEditorActionListener((v, action, event) -> {
            App.logI("Событие: " + action);
            releaseInputFilterFull();
            return false;
        });

        if (savedInstanceState != null) {
            section = savedInstanceState.getInt(SAVE_SECTION);
            inputFilter.setText(savedInstanceState.getString(SAVE_FILTER, ""));
        }

        loader = new RetrofitLoader(Section.SECTIONS[section].getName(), this::completeLoad, this::errorLoad);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        App.logI("Fragment: onViewStateRestored");
        if (savedInstanceState != null) {
            // Если при повороте была загрузка, значит она была уже остановлена. Поэтому нужно начать всё заново.
            boolean isLoading = savedInstanceState.getBoolean(SAVE_LOAD, false);
            if (isLoading) loader.load();

            recyclerListNews.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFromDB(Section.SECTIONS[section].getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        inputFilter.clearFocus();

        buttonUpdateListener.subscribe(() -> {
            loadDialog.show();
            releaseInputFilterFull();
            loader.setSection(Section.SECTIONS[section].getName());
            loader.load();
        });

        adapterListener.subscribe(view -> {
            releaseInputFilterFull();
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            // Аналогично loadFromDB. Single создаём там же, где и используем
            itemClickObserver = new DBObserver<>(listenerActivity::clickItemNews, this::errorLoadFromDB);
            App.getDatabase().getNewsDAO().getIdAsSingle(item.getTitle(), item.getPreviewText(), item.getPublishDateString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(itemClickObserver);
        });

        adapterLongListener.subscribe(view -> {
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            itemLongClickObserver = new DBObserver<>(this::startEditActivity, this::errorLoadFromDB);

            App.getDatabase().getNewsDAO().getIdAsSingle(item.getTitle(), item.getPreviewText(), item.getPublishDateString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(itemLongClickObserver);
        });

        scrollListener.subscribe(state -> {
            if (state != 0) {
                buttonUpdate.hide();
                releaseInputFilterFull();
            } else {
                buttonUpdate.show();
            }
        });

        recyclerListNews.addOnScrollListener(scrollListener);
        inputListener.subscribe(adapter::setFilter);
        adapter.addOnClickListener(adapterListener);
        adapter.addOnLongClickListener(adapterLongListener);
        buttonUpdate.setOnClickListener(buttonUpdateListener);
        inputFilter.subscribe(this::releaseInputFilterLite);
        viewSections.subscribe(this::scrollSections);
        App.setListFragmentVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        scrollListener.unsubscribe();
        inputListener.unsubscribe();
        buttonUpdateListener.unsubscribe();
        adapterListener.unsubscribe();
        adapter.delOnClickListener();
        adapter.delOnLongClickListener();
        inputFilter.unsubscribe();
        viewSections.unsubscribe();

        App.setListFragmentVisible(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        loader.stop();
        adapter.clear();    // Зачем это здесь!?!?!??!

        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        preferences
                .edit()
                .putInt(LAST_SECTION, section)
                .apply();

        App.logI("Fragment: onStop");
        listState = recyclerListNews.getLayoutManager().onSaveInstanceState();

        if (loadDBObserver != null) loadDBObserver.unsubscribe();
        if (itemClickObserver != null) itemClickObserver.unsubscribe();
        if (itemLongClickObserver != null) itemLongClickObserver.unsubscribe();
        if (deleteAllObserver != null) deleteAllObserver.unsubscribe();
        if (completeLoad != null) completeLoad.dispose();   // Возможно обновление не вызывалось
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        App.logI("Fragment: onSaveInstanceState");
        outState.putInt(SAVE_SECTION, section);
        outState.putString(SAVE_FILTER, inputFilter.getText().toString());
        outState.putBoolean(SAVE_LOAD, loadDialog.isShowing());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listenerActivity = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.itemAbout).setVisible(true);
        menu.findItem(R.id.itemExit).setVisible(true);
        menu.findItem(R.id.itemDelete).setVisible(false);
        releaseInputFilterFull();
        super.onPrepareOptionsMenu(menu);
    }

    private void scrollSections(int dx) {
        if (Math.abs(dx) > 2) {
            releaseInputFilterFull();
        }
    }

    private void releaseInputFilterFull() {
        if (inputFilter.isFocused()) {
            InputMethodManager manager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(inputFilter.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputFilter.clearFocus();
            App.logI("Release Input Filter");
        }
    }

    private void releaseInputFilterLite() {
        inputFilter.clearFocus();
    }

    private void loadSections() {
        for (int i = 0; i < Section.SECTIONS.length; i++) {
            Chip chip = new Chip(this.getActivity());
            TextViewCompat.setTextAppearance(chip, R.style.TextAppearance_AppCompat_Title_Inverse);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int space = getResources().getDimensionPixelSize(R.dimen.stdMargin);
            params.setMarginStart(space);
            params.setMarginEnd(space);

            chip.setLayoutParams(params);
            chip.setChipBackgroundColorResource(R.color.colorPrimary);
            chip.setText(Section.SECTIONS[i].getName());
            int finalI = i;
            chip.setOnClickListener(view -> clickSection(finalI));
            layoutSections.addView(chip);
        }
    }

    private void clickSection(int index) {
        releaseInputFilterFull();
        section = index;
        loadDialog.show();
        adapter.setNewSection(Section.SECTIONS[index].getName());
        loader.setSection(Section.SECTIONS[index].getName());
        loader.load();
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

    private AlertDialog buildLoadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
                .setView(R.layout.dialog).setCancelable(false);

       return builder.create();
    }

    private void initialListener() {
        inputListener = new ListenerEditText(inputFilter);
        adapterListener = new NewsItemClickListener();
        adapterLongListener = new NewsItemLongClickListener();
        buttonUpdateListener = new ButtonUpdateClickListener();
        scrollListener = new ScrollListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAbout:
                listenerActivity.menuItemAboutSelected();
                return true;
            case R.id.itemExit:
                listenerActivity.menuItemExitSelected();
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

         completeLoad = Single.zip(singles, args -> 0)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(i -> {
                     loadFromDB(stories.getSection());
                     loadDialog.dismiss();
                 }, t -> {
                     loadDialog.dismiss();
                     errorLoadFromDB(t);
                 });
    }

    private void errorLoad() {
        Toast.makeText(this.getActivity(), R.string.errorLoading, Toast.LENGTH_SHORT).show();
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
        adapter.setFilter(inputFilter.getText().toString());
        if (listState != null) recyclerListNews.getLayoutManager().onRestoreInstanceState(listState);
    }

    private void startEditActivity(Integer id) {
        EditNewsActivity.start(this.getActivity(), id);
    }

    private void errorLoadFromDB(Throwable t) {
        App.logE(t.getMessage());
    }

    public interface Listener {
        void clickItemNews(int id);
        void menuItemAboutSelected();
        void menuItemExitSelected();
    }

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    public void performClickItem(int id) {
        RecyclerView.ViewHolder viewHolder = recyclerListNews.findViewHolderForAdapterPosition(id);
        if (viewHolder != null) {
            viewHolder.itemView.performClick();
        }
    }
}
