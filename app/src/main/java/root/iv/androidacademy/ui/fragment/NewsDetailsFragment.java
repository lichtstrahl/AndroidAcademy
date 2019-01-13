package root.iv.androidacademy.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.util.DBObserver;

public class NewsDetailsFragment extends Fragment {
    private static final String INTENT_ID = "INTENT_ID";
    public static final String TAG = "fragment:details";
    private int itemID;
    private DBObserver<NewsEntity> findNewsItemObserver;
    private DBObserver<Integer> deleteNewsItemObserver;
    private Listener listenerActivity;
    WebView webView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_details, container, false);
        webView = view.findViewById(R.id.webView);
        // Вроде это немножко помогло в ускорении прогрузки
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl( "javascript:window.location.reload( true )" );
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Загрузка закночена", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findNewsItemObserver = new DBObserver<>(this::successfulFindNewsItem, this::errorLoadFromDB);
        deleteNewsItemObserver = new DBObserver<>(this::finish, this::errorLoadFromDB);
        itemID = getArguments().getInt(INTENT_ID, -1);

        Single.fromCallable(() -> App.getDatabase().getNewsDAO().getItemById(itemID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(findNewsItemObserver);

        setHasOptionsMenu(true);
        App.setDetailsFragmentVisible(true);
        return view;
    }



    @Override
    public void onStop() {
        super.onStop();
        findNewsItemObserver.unsubscribe();
        deleteNewsItemObserver.unsubscribe();
        App.setDetailsFragmentVisible(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listenerActivity = (Listener) context;
        navigateUpEnable(context, true);
    }

    @Override
    public void onDetach() {
        listenerActivity = null;
        navigateUpEnable(getContext(), false);
        getActivity().setTitle(R.string.app_name);
        super.onDetach();
    }

    public static NewsDetailsFragment newInstance(int id) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_ID, id);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.itemAbout).setVisible(false);
        menu.findItem(R.id.itemExit).setVisible(false);
        menu.findItem(R.id.itemDelete).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemDelete:
                Single.fromCallable(() -> App.getDatabase().getNewsDAO().delete(itemID))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(deleteNewsItemObserver);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateUpEnable(Context context, boolean flag) {
        final ActionBar bar = ((AppCompatActivity) context).getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(flag);
    }

    private void successfulFindNewsItem(NewsEntity entity) {
        getActivity().setTitle(entity.getSubSection());
        webView.loadUrl(entity.getFullText());
        App.logI("Web view load \"" + entity.getFullText() + "\"");
    }

    private void finish(Integer i) {
        listenerActivity.menuItemDeleteSelected(itemID);
    }

    private void errorLoadFromDB(Throwable t) {
        App.logE(t.getMessage());
    }

    public interface Listener {
        void menuItemDeleteSelected(int itemID);
    }
}