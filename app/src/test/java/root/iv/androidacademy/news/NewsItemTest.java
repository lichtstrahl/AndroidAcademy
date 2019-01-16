package root.iv.androidacademy.news;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Calendar;

import root.iv.androidacademy.AppTests;


public class NewsItemTest extends AppTests {
    private Constructor constructorNewsItems;
    private NewsItem.NewsItemBuilder builder;

    @Before
    public void onStart() {
        constructorNewsItems = findItemsConstructor();
        builder = NewsItem.getBuilder()
                .buildSubSection(EXAMPLE_TXT)
                .buildTitle(EXAMPLE_TXT)
                .buildPreviewText(EXAMPLE_TXT)
                .buildFullText(EXAMPLE_LINK)
                .buildImageURL(EXAMPLE_LINK)
                .buildPublishDate(Calendar.getInstance().getTime());
    }

    @Test
    public void testStdCorrect() throws Exception {
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.valid(item);
        // Assert
        Assert.assertTrue(valid);
    }

    @Test
    public void testNULL1() throws Exception{
        // Agree
        builder.buildPublishDate(null);
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validNull(item);
        // Assert
        Assert.assertFalse(valid);
    }

    @Test
    public void testNULL2() throws Exception {
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validNull(item);
        // Assert
        Assert.assertTrue(valid);
    }

    @Test
    public void testEmpty1() throws Exception {
        // Agree
        builder.buildSubSection("");
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validEmpty(item);
        // Assert
        Assert.assertTrue(valid);
    }

    @Test
    public void testEmpty2() throws Exception {
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validEmpty(item);
        // Assert
        Assert.assertTrue(valid);
    }

    @Test
    public void testEmpty3() throws Exception {
        // Agree
        builder.buildPreviewText("");
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validEmpty(item);
        // Assert
        Assert.assertTrue(valid);
    }

    @Test
    public void testURL1() throws Exception {
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validURL(item);
        // Assert
        Assert.assertTrue(valid);
    }

    @Test
    public void testURL2() throws Exception {
        // Agree
        builder.buildImageURL(EXAMPLE_TXT);
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validURL(item);
        // Assert
        Assert.assertTrue(valid);
    }

    @Test
    public void testAllNULL() throws Exception {
        // Agree
        builder.buildTitle(null);
        builder.buildPreviewText(null);
        builder.buildFullText(null);
        builder.buildPublishDate(null);
        builder.buildSubSection(null);
        builder.buildImageURL(null);
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validNull(item);
        // Assert
        Assert.assertFalse(valid);
    }

    @Test
    public void testEmptyAll() throws Exception {
        // Agree
        builder.buildTitle("");
        builder.buildPreviewText("");
        builder.buildFullText("");
        builder.buildSubSection("");
        builder.buildImageURL("");
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validEmpty(item);
        // Assert
        Assert.assertFalse(valid);
    }

    @Test
    public void testURLAll() throws Exception {
        // Agree
        builder.buildImageURL(EXAMPLE_TXT);
        builder.buildFullText(EXAMPLE_TXT);
        // Action
        NewsItem item = (NewsItem)constructorNewsItems.newInstance(builder);
        boolean valid = NewsItem.NewsItemBuilder.validURL(item);
        // Assert
        Assert.assertFalse(valid);
    }

    // Через рефлексию находим приватный конструктор
    private Constructor findItemsConstructor() {
        for (Constructor c : NewsItem.class.getDeclaredConstructors()) {
            if (c.getParameterCount() == 1 && c.getParameters()[0].getType().equals(NewsItem.NewsItemBuilder.class)) {
                c.setAccessible(true);
                return c;
            }
        }
        return null;
    }
}