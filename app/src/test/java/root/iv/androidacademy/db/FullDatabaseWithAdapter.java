package root.iv.androidacademy.db;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.AppTests;
import root.iv.androidacademy.app.RobolectricApp;
import root.iv.androidacademy.news.adapter.NewsAdapter;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.news.adapter.NotifyWrapper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = Config.NONE, application = RobolectricApp.class)
public class FullDatabaseWithAdapter extends AppTests {

    @Before
    public void onStart() {
        disposables = new CompositeDisposable();
        database = RobolectricApp.getDatabase().getNewsDAO();

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < COUNT_NEWS; i++) {
            calendar.setTimeInMillis(exampleTimeInMillis[i]);
            NewsItem item = newsBuilder.buildPublishDate(calendar.getTime()).build();
            Single.fromCallable(() -> database.insert(NewsEntity.fromNewsItem(item)))
                    .subscribeOn(Schedulers.io())
                    .toObservable()
                    .blockingLast();
        }


        NotifyWrapper wrapper = spy(NotifyWrapper.class);
        doNothing().when(wrapper).wrapNotifyDataSetChanged(anyObject());
        doNothing().when(wrapper).wrapNotifyItemRemoved(anyObject(), anyInt(), anyInt());
        doNothing().when(wrapper).wrapNotifyItemInserted(anyObject(), anyInt());
        adapter = spy(new NewsAdapter(new LinkedList<>(), mockInflater, wrapper));
        adapter = new NewsAdapter(new LinkedList<>(), mockInflater, wrapper);
    }

    @Test
    public void fillAdapter() {
        List<NewsEntity> entitys = database.getAllAsSingle()
                .subscribeOn(Schedulers.io())
                .toObservable()
                .blockingLast();

        for (NewsEntity entity : entitys)
            adapter.append(entity.toNewsItem());

        // Adapter содержит 3 различные новости
        int n = adapter.getItemCount();
        Assert.assertEquals(COUNT_NEWS, n);
        Assert.assertNotEquals(adapter.getItem(0), adapter.getItem(n-1));
    }

    @After
    public void onStop() {
        disposables.dispose();
        RobolectricApp.getDatabase().close();
        synhro.reset();
    }
}
