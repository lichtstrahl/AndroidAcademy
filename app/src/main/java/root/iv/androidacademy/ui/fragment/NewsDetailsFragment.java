package root.iv.androidacademy.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import root.iv.androidacademy.app.App;
import root.iv.androidacademy.R;
import root.iv.androidacademy.news.NewsItem;

public class NewsDetailsFragment extends Fragment {
    private static final String INTENT_ID = "INTENT_ID";
    private int itemID;
    private WebView webView;
    private Listener listenerActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_details, container, false);

        webView = view.findViewById(R.id.webView);
        itemID = getArguments().getInt(INTENT_ID, -1);
        NewsItem newsItem = App.getDatabase().getNewsDAO().getItemById(itemID).toNewsItem();
        this.getActivity().setTitle(newsItem.getSubSection());
        webView.loadUrl(newsItem.getFullText());

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listenerActivity = (Listener) context;
    }

    @Override
    public void onDetach() {
        listenerActivity = null;
        getActivity().setTitle(R.string.app_name);
        super.onDetach();
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
                listenerActivity.menuItemDeleteSelected(itemID);
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .remove(this)
//                        .commit();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int getItemID() {
        return itemID;
    }

    public static NewsDetailsFragment newInstance(int id) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_ID, id);
        fragment.setArguments(bundle);

        return fragment;
    }

    public interface Listener {
        void menuItemDeleteSelected(int itemID);
    }
}