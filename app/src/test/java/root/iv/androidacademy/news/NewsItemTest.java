package root.iv.androidacademy.news;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Calendar;

import root.iv.androidacademy.AppTests;


public class NewsItemTest extends AppTests {
    private Constructor constructorNewsItems;
    private NewsItem.NewsItemBuilder builder;

    @Before
    public void onStart() throws Exception {
        constructorNewsItems = findItemsContructor();
        builder = NewsItem.getBuilder()
                .buildSubSection(EXAMPLE_TXT)
                .buildTitle(EXAMPLE_TXT)
                .buildPreviewText(EXAMPLE_TXT)
                .buildFullText(EXAMPLE_LINK)
                .buildImageURL(EXAMPLE_LINK)
                .buildPublishDate(Calendar.getInstance().getTime());
    }

    // Создаём абсолютно корректный объект
    @Test
    public void testStdCorrect() throws Exception {
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.valid(item);
        // Assert
        Assert.assertTrue(valid);
    }

    // Передаём null в качестве Date
    @Test
    public void testDateIsNULL() {
        try {
            // Agree
            builder.buildPublishDate(null);
            // Action
            NewsItem item = (NewsItem) constructorNewsItems.newInstance(builder);
            boolean valid = NewsItem.NewsItemBuilder.valid(item);
            // Assert (Deprecated)
            Assert.assertFalse(valid);
        } catch (Exception e) {
            // Assert
            Assert.assertTrue(e.toString().contains("NullPointer"));
        }
    }

    // Передаём пустую строку в качестве Preview
    @Test
    public void testPreviewIsEmpty() throws Exception {
        // Agree
        builder.buildPreviewText("");
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.valid(item);
        // Assert
        Assert.assertFalse(valid);
    }

    // Через рефлексию находим приватный конструктор
    private Constructor findItemsContructor() {
        for (Constructor c : NewsItem.class.getDeclaredConstructors()) {
            if (c.getParameterCount() == 1 && c.getParameters()[0].getType().equals(NewsItem.NewsItemBuilder.class)) {
                c.setAccessible(true);
                return c;
            }
        }
        return null;
    }

    @Deprecated
    // Через рефлексию находим метод "valid" у NewsItemBuilder
    private Method findValid() {
        return null;
    }
}