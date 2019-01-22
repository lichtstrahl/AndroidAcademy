package root.iv.androidacademy;

import android.view.LayoutInflater;

import org.mockito.Mock;

import java.util.Calendar;

import io.reactivex.disposables.CompositeDisposable;
import root.iv.androidacademy.db.Synhro;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsDAO;
import root.iv.androidacademy.news.NewsItem;

public class AppTests {
    protected static final int COUNT_NEWS = 3;
    protected static final String[] examplePreviews = new String[] {
            "Asia", "New York", "USA"
    };
    protected static final long[] exampleTimeInMillis = new long[] {
            1000, 2000, 3000
    };

    protected static final String EXAMPLE_TXT = "***";
    protected static final String EXAMPLE_LINK = "https://www.google.com";
    // Тестирование adapter-а
    protected NewsAdapter adapter;
    @Mock
    protected LayoutInflater mockInflater;

    protected static final NewsItem.NewsItemBuilder newsBuilder = NewsItem.getBuilder()
            .buildTitle("Title")
            .buildFullText(EXAMPLE_LINK)
            .buildImageURL(EXAMPLE_LINK)
            .buildPreviewText("Preview")
            .buildSubSection("Section")
            .buildPublishDate(Calendar.getInstance().getTime());
    protected static final NewsItem exampleNews = newsBuilder.build();

    protected NewsDAO database;
    protected CompositeDisposable disposables;
    protected final static int COUNT_THREADS = 10;
    protected final static Synhro synhro = new Synhro(COUNT_THREADS);

}
