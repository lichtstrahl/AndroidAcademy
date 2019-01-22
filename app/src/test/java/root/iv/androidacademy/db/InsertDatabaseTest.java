package root.iv.androidacademy.db;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.AppTests;
import root.iv.androidacademy.app.RobolectricApp;
import root.iv.androidacademy.news.NewsEntity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = Config.NONE, application = RobolectricApp.class)
public class InsertDatabaseTest extends AppTests {

    @Before
    public void onStart() {
        disposables = new CompositeDisposable();
        database = RobolectricApp.getDatabase().getNewsDAO();
    }

    @Test
    public void testInsertFromMultiThread() {
        for (int i = 0; i < COUNT_THREADS; i++) {
            new Thread(()->{
                long id = database.insert(NewsEntity.fromNewsItem(exampleNews));
                synhro.threadFinished();
            }).start();
        }
        // Ждём 10 секунд, пока 10 потоков заполнят БД
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(synhro::allIsFinished);

        TestObserver<List<NewsEntity>> observer = database.getAllAsSingle()
                .subscribeOn(Schedulers.io())
                .test();
        observer.awaitTerminalEvent();

        observer.assertNoErrors();
        List<NewsEntity> list = observer.values().get(0);
        Assert.assertEquals(COUNT_THREADS, list.size());
    }

    // 10 потоков будут одновременно обновлять одну и ту же запись
    @Test
    public void testInsertAndUpdate() {
        exampleNews.setTitle("");
        TestObserver<Long> insertObserver = Single.fromCallable(() -> database.insert(NewsEntity.fromNewsItem(exampleNews)))
                .subscribeOn(Schedulers.io())
                .test();
        insertObserver.awaitTerminalEvent();


        // 10 потоков пытаются обновить запись. Каждый приписывает "1"
        for (int i = 0; i < COUNT_THREADS; i++) {
            new Thread(() -> {
                synchronized (synhro) {
                    TestObserver<NewsEntity> getObserver = database.getItemByIdAsSingle(insertObserver.values().get(0).intValue())
                            .test();
                    getObserver.awaitTerminalEvent();


                    NewsEntity entity = getObserver.values().get(0);
                    entity.setTitle(entity.getTitle() + "1");
                    TestObserver<Integer> updateObserver = Single.fromCallable(() -> database.update(entity))
                            .test();
                    updateObserver.awaitTerminalEvent();
                    synhro.threadFinished();
                }
            }).start();
        }

        // Ждём пока потоки закончат свою работу
        Awaitility.await().atMost(15, TimeUnit.SECONDS).until(synhro::allIsFinished);


        // В БД содержится единственная запись, у которой Title состоит из 10 единиц
        TestObserver<List<NewsEntity>> getAllObserver = database.getAllAsSingle()
                .subscribeOn(Schedulers.io())
                .test();
        getAllObserver.awaitTerminalEvent();

        getAllObserver.assertNoErrors();
        Assert.assertEquals(1, getAllObserver.values().get(0).size());
        Assert.assertEquals("1111111111", getAllObserver.values().get(0).get(0).getTitle());

    }

    @After
    public void onStop() {
        disposables.dispose();
        RobolectricApp.getDatabase().close();
        synhro.reset();
    }

}
