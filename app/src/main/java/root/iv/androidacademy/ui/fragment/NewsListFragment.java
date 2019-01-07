package root.iv.androidacademy.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.functions.Action;
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
import root.iv.androidacademy.ui.activity.listener.ButtonUpdateClickListener;
import root.iv.androidacademy.ui.activity.listener.ClickListener;
import root.iv.androidacademy.ui.activity.listener.ListenerEditText;
import root.iv.androidacademy.ui.activity.listener.NewsItemClickListener;
import root.iv.androidacademy.ui.activity.listener.NewsItemLongClickListener;
import root.iv.androidacademy.ui.activity.listener.ScrollListener;
import root.iv.androidacademy.ui.activity.listener.SpinnerInteractionListener;
import root.iv.androidacademy.util.Action1;

import static android.content.Context.MODE_PRIVATE;

public class NewsListFragment extends Fragment {
    private static final String SAVE_SECTION = "SAVE_SECTION";
    private static final String SAVE_FILTER = "SAVE_FILTER";
    private static final String LAST_SECTION = "LAST_SECTION";
    private static final String ARGUMENT_ORIENTATION = "args:orientation";
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
    private EditText inputFilter;
    private Listener listenerActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_list, container, false);

        recyclerListNews = view.findViewById(R.id.listNews);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        spinner = view.findViewById(R.id.spinner);
        inputFilter = view.findViewById(R.id.input);
        loadSpinner();

        if (savedInstanceState != null) {
            spinner.setSelection(savedInstanceState.getInt(SAVE_SECTION, 0));
            inputFilter.setText(savedInstanceState.getString(SAVE_FILTER, ""));
        }

        adapter = new NewsAdapter(new LinkedList<>(), getLayoutInflater());
        recyclerListNews.setAdapter(adapter);
        recyclerListNews.addOnScrollListener(new ScrollListener());
        recyclerListNews.setLayoutManager(new LinearLayoutManager(this.getContext()));

        loadDialog = buildLoadDialog();

        loader = new RetrofitLoader(spinner.getSelectedItem().toString() ,this::completeLoad, this::errorLoad);
        initialListener();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFromDB(spinner.getSelectedItem().toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        buttonUpdateListener.subscribe(() -> {
            loadDialog.show();
            Object item = spinner.getSelectedItem();
            loader.setSection(item.toString());
            loader.load();
        });

        adapterListener.subscribe((view) -> {
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            int id = App.getDatabase().getNewsDAO().getId(item.getTitle(), item.getPreviewText(), item.getPublishDateString());
            if (listenerActivity != null) listenerActivity.clickItemNews(id);
        });

        adapterLongListener.subscribe((view) -> {
            int pos = recyclerListNews.getChildAdapterPosition(view);
            NewsItem item = adapter.getItem(pos);
            int id = App.getDatabase().getNewsDAO().getId(item.getTitle(), item.getPreviewText(), item.getPublishDateString());
            EditNewsActivity.start(recyclerListNews.getContext(), id);
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
            loadDialog.show();
            adapter.setNewSection(section);
            loader.setSection(section);
            loader.load();
        });

        recyclerListNews.addOnScrollListener(scrollListener);
        inputListener.subscribe(adapter::setFilter);
        adapter.addOnClickListener(adapterListener);
        adapter.addOnLongClickListener(adapterLongListener);
        buttonUpdate.setOnClickListener(buttonUpdateListener);
        spinner.setOnTouchListener(spinnerListener);
        spinner.setOnItemSelectedListener(spinnerListener);
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
        spinnerListener.unsubscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        loader.stop();
        adapter.clear();

        SharedPreferences preferences = this.getActivity().getPreferences(MODE_PRIVATE);
        preferences
                .edit()
                .putInt(LAST_SECTION, spinner.getSelectedItemPosition())
                .apply();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listenerActivity = (Listener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listenerActivity = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_SECTION, spinner.getSelectedItemPosition());
        outState.putString(SAVE_FILTER, inputFilter.getText().toString());
    }

    private void loadSpinner() {
        ArrayAdapter<Section> spinnerAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinnerListItem));
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(this.getActivity().getPreferences(MODE_PRIVATE).getInt(LAST_SECTION, 0));
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
        adapter.setFilter(inputFilter.getText().toString());
    }

    private AlertDialog buildLoadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext())
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
        Toast.makeText(this.getContext(), R.string.errorLoading, Toast.LENGTH_SHORT).show();
        loadDialog.findViewById(R.id.progress).setVisibility(View.GONE);
        TextView textView = loadDialog.findViewById(R.id.text);
        textView.setText(R.string.errorLoading);
        loadDialog.findViewById(R.id.buttonReconnect).setVisibility(View.VISIBLE);
        loadDialog.findViewById(R.id.buttonReconnect).setOnClickListener(view -> loader.load());
    }

    public static NewsListFragment newInstance(boolean isLandTabletOrientation) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARGUMENT_ORIENTATION, isLandTabletOrientation);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.itemAbout).setVisible(true);
        menu.findItem(R.id.itemExit).setVisible(true);
        menu.findItem(R.id.itemDelete).setVisible(false);
        super.onPrepareOptionsMenu(menu);
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

    public interface Listener {
        void clickItemNews(int id);
        void menuItemAboutSelected();
        void menuItemExitSelected();
    }
}
