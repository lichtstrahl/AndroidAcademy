package root.iv.androidacademy;

import android.view.LayoutInflater;

import org.mockito.Mock;

import java.util.Calendar;

import root.iv.androidacademy.news.NewsAdapter;
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

    protected static final NewsItem exampleNews = NewsItem.getBuilder()
            .buildTitle("Title")
            .buildFullText(EXAMPLE_LINK)
            .buildImageURL(EXAMPLE_LINK)
            .buildPreviewText("Preview")
            .buildSubSection("Section")
            .buildPublishDate(Calendar.getInstance().getTime())
            .build();

}
