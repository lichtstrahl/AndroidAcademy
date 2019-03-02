package root.iv.androidacademy.news;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import root.iv.androidacademy.AppTests;
import root.iv.androidacademy.news.adapter.NewsAdapter;
import root.iv.androidacademy.news.adapter.NotifyWrapper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;


// clear
// sort
// append
// filter : clear, append, sort - Составная функция
public class AdapterSetFilterTests extends AppTests {
    private NotifyWrapper wrapper;
    // Arrange
    @Before
    public void onStart() {
        MockitoAnnotations.initMocks(this);
        List<NewsItem> exampleNews = new LinkedList<>();

        for (int i = 0; i < COUNT_NEWS; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(exampleTimeInMillis[i]);
                exampleNews.add(
                        NewsItem.getBuilder()
                                .buildTitle(EXAMPLE_TXT)
                                .buildFullText(EXAMPLE_LINK)
                                .buildImageURL(EXAMPLE_LINK)
                                .buildPreviewText(examplePreviews[i])
                                .buildSubSection(EXAMPLE_TXT)
                                .buildPublishDate(calendar.getTime())
                                .build()
                );
        }

        wrapper = spy(NotifyWrapper.class);
        doNothing().when(wrapper).wrapNotifyDataSetChanged(anyObject());
        doNothing().when(wrapper).wrapNotifyItemRemoved(anyObject(), anyInt(), anyInt());
        doNothing().when(wrapper).wrapNotifyItemInserted(anyObject(), anyInt());
        adapter = spy(new NewsAdapter(exampleNews, mockInflater, wrapper));
    }

    @Test
    public void testClear1() {
        // Action
        int count0 = adapter.getItemCount();
        adapter.clear();
        int count2 = adapter.getItemCount();
        // Assert
        Assert.assertTrue(count0 == COUNT_NEWS && count2 == 0);
    }

    @Test
    public void testClear2() {
        adapter.clear();
        int count0 = adapter.getItemCount();
        adapter.clear();
        int count2 = adapter.getItemCount();
        // Assert
        Assert.assertTrue(count2 == 0 && count2 == count0);
    }

    @Test
    public void testSort1() {
        // Action
        adapter.sort();
        NewsItem item1 = adapter.getItem(0);
        NewsItem item2 = adapter.getItem(COUNT_NEWS-1);
        // Assert
        Assert.assertEquals(item1.getPreviewText(), examplePreviews[COUNT_NEWS-1]);
        Assert.assertEquals(item2.getPreviewText(), examplePreviews[0]);
    }

    @Test
    public void testSort2() {
        adapter.clear();
        // Action
        adapter.sort();
        Assert.assertEquals(0, adapter.getItemCount());
    }


    // Добавление 5 корректных новостей
    @Test
    public void testAppend1() {
        // Action
        int d = 5;
        for (int i = 0; i < d; i++)
            adapter.append(exampleNews);
        int count2 = adapter.getItemCount();

        // Assert
        Assert.assertEquals(COUNT_NEWS + d, count2);
    }

    // Добавление null-ов
    @Test
    public void testAppend2() {
        // Action
        int d = 5;
        for (int i = 0; i < d; i++)
            adapter.append(null);
        int count1 = adapter.getItemCount();
        // Assert
        Assert.assertEquals(COUNT_NEWS, count1);
    }

    // Проверка на полное соответствие
    @Test
    public void testFilterSearch1() {
        // Action
        adapter.setFilter(examplePreviews[2]);
        // Assert
        Assert.assertEquals(1, adapter.getItemCount());
        // Assert
        Assert.assertEquals(adapter.getItem(0).getPreviewText(), examplePreviews[2]);
    }

    // Неполное соответствие, но уникальный элемент
    @Test
    public void testFilterSearch2() {
        // Action
        adapter.setFilter("US");
        // Assert
        Assert.assertEquals(1, adapter.getItemCount());
        // Assert
        Assert.assertEquals(adapter.getItem(0).getPreviewText(), examplePreviews[2]);
    }

    // Неполное соответствие и несколько подходящих вариантов
    @Test
    public void testFilterSearch3() {
        // Action
        adapter.setFilter("A");
        // Assert
        Assert.assertEquals(2, adapter.getItemCount());
        // Assert - sort
        Assert.assertTrue(adapter.getItem(0).getPublishDate().after(adapter.getItem(1).getPublishDate()));
    }

    // Поиск пустой строки. Её содержат все
    @Test
    public void testFilterSearch4() {
        // Action
        adapter.setFilter("");
        // Assert
        Assert.assertEquals(COUNT_NEWS, adapter.getItemCount());
    }

    // Поиск несуществующей строки
    @Test
    public void testFilterSearch5() {
        // Action
        adapter.setFilter("Igor");
        // Assert
        Assert.assertEquals(0, adapter.getItemCount());
    }
}
