package root.iv.androidacademy.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_details, container, false);

        webView = view.findViewById(R.id.webView);
        itemID = getArguments().getInt(INTENT_ID, -1);
        NewsItem newsItem = App.getDatabase().getNewsDAO().getItemById(itemID).toNewsItem();
        this.getActivity().setTitle(newsItem.getSubSection());
        webView.loadUrl(newsItem.getFullText());

        return view;
    }

    public static NewsDetailsFragment newInstance(int id) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_ID, id);
        fragment.setArguments(bundle);

        return fragment;
    }

    // TODO Сделать кнопку для удаления новости
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.options_menu_details, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.itemMenuDelete:
//                App.getDatabase().getNewsDAO().delete(itemID);
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}