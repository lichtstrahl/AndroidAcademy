package root.iv.androidacademy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsItem;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;


// clear
// sort
// append
// filter : clear, append, sort - Составная функция
/* TODO корректность заполнения списка
   TODO Протестировать то что попадает в список
   TODO После проверки на корректность
   TODO Разблокировать конструктор и проверять структуру новости
   TODO то что здесь это уже интеграция
*/
public class AdapterSetFilterUnitTests extends AppTests {

    // Arrange
    @Before
    public void initTestEnvironment() {
        MockitoAnnotations.initMocks(this);
        List<NewsItem> exampleNews = new LinkedList<>();

        for (int i = 0; i < COUNT_NEWS; i++) {
            try {
                Thread.sleep(1001);
                exampleNews.add(
                        NewsItem.getBuilder()
                                .buildTitle(EXAMPLE_TXT)
                                .buildFullText(EXAMPLE_TXT)
                                .buildImageURL(EXAMPLE_TXT)
                                .buildPreviewText(examplePreviews[i])
                                .buildSubSection(EXAMPLE_TXT)
                                .buildPublishDate(Calendar.getInstance().getTime())
                                .build()
                );
            } catch (InterruptedException ex) {

            }
        }

        adapter = spy(new NewsAdapter(exampleNews, mockInflater));
        doNothing().when(adapter).wrapNotifyDataSetChanged();
        doNothing().when(adapter).wrapNotifyItemRemoved(anyInt(), anyInt());
        doNothing().when(adapter).wrapNotifyItemInserted(anyInt());
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
        Assert.assertEquals(count2, COUNT_NEWS + d);
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
