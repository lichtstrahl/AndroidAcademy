package root.iv.androidacademy.db;


import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.internal.operators.observable.BlockingObservableLatest;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import root.iv.androidacademy.AppTests;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.app.RobolectricApp;
import root.iv.androidacademy.news.NewsDAO;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = Config.NONE, application = RobolectricApp.class)
public class EmptyDatabaseTest extends AppTests {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    
    @Mock 
    protected NewsDAO database;
    protected NewsItem exampleItem;
    protected CompositeDisposable disposables;
    protected Scheduler scheduler;
    private final Object lock = new Object();

    @Before
    public void onStart() {
        Calendar calendar = Calendar.getInstance();
        disposables = new CompositeDisposable();

        exampleItem = new NewsItem.NewsItemBuilder()
                .buildFullText(EXAMPLE_LINK)
                .buildImageURL(EXAMPLE_LINK)
                .buildTitle(EXAMPLE_TXT)
                .buildPreviewText(EXAMPLE_TXT)
                .buildSubSection(EXAMPLE_TXT)
                .buildPublishDate(calendar.getTime())
                .build();
        database = RobolectricApp.getDatabase().getNewsDAO();

        ThreadFactory factory = new TestThreadFactory();
        scheduler = Schedulers.from(Executors.newSingleThreadExecutor(factory));
    }

    // Обращение к БД из главного потока
    // Ожидаем исключение "IllegalStateException"
    @Test
    public void callDBFromMainThread() {
        try {
            disposables.add(
                    database.getAllAsSingle()
                    .subscribe(this::stdEmptyHandler, this::stdErrorHandler)
            );
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalStateException);
        }
    }

    // Получение данных из БД в другом потоке (не UI)
    // Ожидается, что БД пуста
    @Test
    public void getAllDBFromIO() {
        List<NewsEntity> list = database.getAllAsSingle()
                                    .subscribeOn(scheduler)
                                    .toObservable()
                                    .blockingLast();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testHello() {

    }

    @Test
    public void multiTest() {
        final  Synhro synhro = new Synhro();

        database.getAllAsSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
                .subscribe(
                        list -> {
                            Assert.assertNotNull(list);
                            App.logI("Size: " + list.size());
                            synhro.pick();
                        }, this::stdErrorHandler
                );

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(synhro::getFlag);
    }

    @Test
    public void multiTest2() {
        List<NewsEntity> list = database.getAllAsSingle()
                .subscribeOn(Schedulers.io())
                .toObservable()
                .blockingLast();

        Assert.assertEquals(0, list.size());
        System.out.println("Тест завершился");
    }

    @Test
    public void multiTest3() {
        final TestObserver<List<NewsEntity>> testSubscriber = database.getAllAsSingle().subscribeOn(Schedulers.io()).test();
        testSubscriber.awaitTerminalEvent();

        testSubscriber
                .assertNoErrors();
        RobolectricApp.logI("Тест завершился");
    }


    @After
    public void onStop() {
        disposables.dispose();
    }

    private void stdEmptyHandler(Object o) { }
    private void stdErrorHandler(@NonNull Throwable t) {
        RobolectricApp.logE(t.getMessage());
    }

    // Класс, переключатель
    private class Synhro {
        private boolean flag = false;

        public void pick() {
            flag = !flag;
        }

        public boolean getFlag() {
            return flag;
        }
    }
}
