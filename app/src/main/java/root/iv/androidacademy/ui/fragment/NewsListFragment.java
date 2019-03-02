package root.iv.androidacademy.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.background.NewsService;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.news.Section;
import root.iv.androidacademy.news.adapter.NewsAdapter;
import root.iv.androidacademy.news.adapter.NotifyWrapper;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;
import root.iv.androidacademy.ui.activity.EditNewsActivity;
import root.iv.androidacademy.ui.ivEditText;
import root.iv.androidacademy.ui.ivHorizontalScrollView;
import root.iv.androidacademy.util.DBObserver;
import root.iv.androidacademy.util.listener.ListenerEditText;
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
    private ListenerEditText inputListener;
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
    private SharedPreferences preferences;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listenerActivity = (Listener)context;
        preferences = context.getSharedPreferences(((Activity)context).getLocalClassName(), MODE_PRIVATE);
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
        section = preferences.getInt(LAST_SECTION, 0);
        loadSections();

        App.logI("Fragment: onCrateView");
        adapter = new NewsAdapter(new LinkedList<>(), getLayoutInflater(), new NotifyWrapper());
        recyclerListNews.setAdapter(adapter);
        recyclerListNews.setLayoutManager(new LinearLayoutManager(getActivity()));

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
            if (isLoading) {
                load(Section.getName(section));
            }

            RecyclerView.LayoutManager layoutManager = recyclerListNews.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(listState);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFromDB(Section.getName(section));
    }

    @Override
    public void onResume() {
        super.onResume();
        inputFilter.clearFocus();

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
        adapter.addOnClickListener(view -> {
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
        adapter.addOnLongClickListener(view -> {
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            itemLongClickObserver = new DBObserver<>(this::startEditActivity, this::errorLoadFromDB);

            App.getDatabase().getNewsDAO().getIdAsSingle(item.getTitle(), item.getPreviewText(), item.getPublishDateString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(itemLongClickObserver);
            return true;
        });
        buttonUpdate.setOnClickListener(v -> {
            releaseInputFilterFull();
            load(Section.getName(section));
        });
        inputFilter.subscribe(this::releaseInputFilterLite);
        viewSections.subscribe(this::scrollSections);
        App.setListFragmentVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        scrollListener.unsubscribe();
        inputListener.unsubscribe();
        adapter.delOnClickListener();
        adapter.delOnLongClickListener();
        inputFilter.unsubscribe();
        viewSections.unsubscribe();
        recyclerListNews.removeOnScrollListener(scrollListener);
        App.setListFragmentVisible(false);
    }

    @Override
    public void onStop() {
        super.onStop();
//        loader.stop();
        adapter.clear();

        RecyclerView.LayoutManager layoutManager = recyclerListNews.getLayoutManager();
        if (layoutManager != null) {
            preferences
                    .edit()
                    .putInt(LAST_SECTION, section)
                    .apply();

            App.logI("Fragment: onStop");
            listState = layoutManager.onSaveInstanceState();

            if (loadDBObserver != null) loadDBObserver.unsubscribe();
            if (itemClickObserver != null) itemClickObserver.unsubscribe();
            if (itemLongClickObserver != null) itemLongClickObserver.unsubscribe();
            if (deleteAllObserver != null) deleteAllObserver.unsubscribe();
            if (completeLoad != null) completeLoad.dispose();   // Возможно обновление не вызывалось
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        App.logI("Fragment: onSaveInstanceState");
        outState.putInt(SAVE_SECTION, section);
        outState.putString(SAVE_FILTER, inputFilter.getText().toString());
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
        Activity activity = getActivity();
        if (inputFilter.isFocused() && activity != null) {
            InputMethodManager manager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(inputFilter.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputFilter.clearFocus();
            App.logI("Release Input Filter");
        }
    }

    private void releaseInputFilterLite() {
        inputFilter.clearFocus();
    }

    private void loadSections() {
        Activity activity = this.getActivity();
        if (activity == null) return;

        for (int i = 0; i < Section.getCount(); i++) {
            Chip chip = new Chip(activity);
            TextViewCompat.setTextAppearance(chip, R.style.TextAppearance_AppCompat_Title_Inverse);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int space = getResources().getDimensionPixelSize(R.dimen.stdMargin);
            params.setMarginStart(space);
            params.setMarginEnd(space);

            chip.setLayoutParams(params);
            chip.setChipBackgroundColorResource(R.color.colorPrimary);
            chip.setText(Section.getName(i));
            int finalI = i;
            chip.setOnClickListener(view -> clickSection(finalI));
            layoutSections.addView(chip);
        }
    }

    private void clickSection(int index) {
        releaseInputFilterFull();
        section = index;
        adapter.setNewSection(Section.getName(index));
        load(Section.getName(index));
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

    private void successfulLoadFromDB(List<NewsEntity> list) {
        for (NewsEntity entity : list) {
            adapter.append(entity.toNewsItem());
        }
        adapter.notifyOriginNews();
        adapter.sort();
        adapter.setFilter(inputFilter.getText().toString());

        RecyclerView.LayoutManager layoutManager = recyclerListNews.getLayoutManager();
        if (listState != null && layoutManager != null) {
            layoutManager.onRestoreInstanceState(listState);
        }
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

    /**
     * Загрузка новостей
     */
    private void load(String section) {
        Activity activity = getActivity();
        if (activity != null) {
            NewsService.call(activity, section);
        }
    }
}
