package root.iv.androidacademy;

import android.support.annotation.Nullable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.app.RobolectricApp;
import root.iv.androidacademy.news.NewsAdapter;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.ui.activity.MainActivity;
import root.iv.androidacademy.ui.fragment.NewsListFragment;
import root.iv.androidacademy.util.DBObserver;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = RobolectricApp.class, manifest = Config.NONE)
public class AdapterIntegrationTests extends AppTests {
    @Nullable
    private DBObserver<List<NewsEntity>> getAllObserver;
    @Nullable
    private DBObserver<Integer> getIdObserver;
    @Nullable
    private DBObserver<NewsEntity> getItemByIDObserver;
    private List<NewsItem> items;

    @Before
    public void onStart() {
        MockitoAnnotations.initMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> AndroidSchedulers.mainThread());
        getAllObserver = new DBObserver<>(this::fillAdapter, this::failLoadFromDB);
        adapter = new NewsAdapter(new LinkedList<>(), mockInflater);

        items = new LinkedList<>();
        for (int i = 0; i < COUNT_NEWS; i++) {
            items.add(NewsItem.getBuilder()
                            .buildTitle(EXAMPLE_TXT)
                            .buildFullText(EXAMPLE_TXT)
                            .buildImageURL(EXAMPLE_TXT)
                            .buildPreviewText(examplePreviews[i])
                            .buildSubSection(EXAMPLE_TXT)
                            .buildPublishDate(Calendar.getInstance().getTime())
                            .build());
        }

        for (NewsItem item : items) {
            RobolectricApp.getDatabase().getNewsDAO().insert(NewsEntity.fromNewsItem(item));
        }
    }

    @After
    public void onStop() {
        if (getAllObserver != null) getAllObserver.unsubscribe();
        if (getIdObserver != null) getIdObserver.unsubscribe();
        if (getItemByIDObserver != null) getItemByIDObserver.unsubscribe();
        App.getDatabase().getNewsDAO().deleteAll();
        App.getDatabase().close();
    }

    // Данные из БД перенаправляются в Adapter
    @Test
    public void testFillAdapter() {
        RobolectricApp.getDatabase().getNewsDAO().getAllAsSingle()
                .subscribe(getAllObserver);
    }


    // Здесь ничего не найдётся, поэтому успешную функцию можно не задавать
    @Test
    public void testGetId1() {
        getIdObserver = new DBObserver<>(null, this::errorLoadFromDB);
        RobolectricApp.getDatabase().getNewsDAO().getIdAsSingle(EXAMPLE_TXT, examplePreviews[0], "")
                .subscribe(getIdObserver);
    }

    // Ищем существующую новость
    @Test
    public void testGetId2() {
        getIdObserver = new DBObserver<>(this::successfulFindId, this::failLoadFromDB);
        RobolectricApp.getDatabase().getNewsDAO().getIdAsSingle(EXAMPLE_TXT, examplePreviews[0], items.get(0).getPublishDateString())
                .subscribe(getIdObserver);
    }

    // Т.е. клик успешно совершён. ID было найдено в adapter, который перенаправил его в БД. После чего запустился фрагмент
    @Test
    public void testItemClick1() {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
        NewsListFragment fragment = (NewsListFragment)mainActivity.getSupportFragmentManager().findFragmentByTag(NewsListFragment.TAG);
        fragment.performClickItem(0);

        // Assert. DetailsFragment появился, т.е. теперь стало 2 фрагмента
        Assert.assertEquals(2, mainActivity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testItemClick2() {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
        NewsListFragment fragment = (NewsListFragment)mainActivity.getSupportFragmentManager().findFragmentByTag(NewsListFragment.TAG);
        fragment.performClickItem(10);

        // Assert. DetailsFragment не появился, потому что такого id нет ни в adapter ни тем более в БД
        Assert.assertEquals(2, mainActivity.getSupportFragmentManager().getFragments().size());
    }

    private void successfulFindId(Integer id) {
        getItemByIDObserver = new DBObserver<>(this::successfulFindItemByID, this::failLoadFromDB);
        RobolectricApp.getDatabase().getNewsDAO().getItemByIdAsSingle(id)
                .subscribe(getItemByIDObserver);

    }

    private void successfulFindItemByID(NewsEntity entity) {
        // Action
        int count = adapter.getItemCount();
        adapter.append(entity.toNewsItem());

        // Assert
        Assert.assertEquals(count+1, adapter.getItemCount());
        Assert.assertNotNull(entity);
    }

    private void fillAdapter(List<NewsEntity> entities) {
        // Action
        adapter.clear();
        for (NewsEntity entity : entities) {
            adapter.append(entity.toNewsItem());
        }
        adapter.notifyOriginNews();

        // Assert
        Assert.assertEquals(COUNT_NEWS, adapter.getItemCount());
        Assert.assertNotEquals(adapter.getItem(0), adapter.getItem(COUNT_NEWS-1));
    }

    // Ошибка при работе с БД. Раз пришли сюда, печатаем сообщение и сразу выходим.
    private void errorLoadFromDB(Throwable t) {
        System.err.println(t.getMessage());

    }

    private void failLoadFromDB(Throwable t) {
        errorLoadFromDB(t);
        Assert.fail();
    }
}
