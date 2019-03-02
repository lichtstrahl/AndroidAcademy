package root.iv.androidacademy.db;


import androidx.room.EmptyResultSetException;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.AppTests;
import root.iv.androidacademy.app.RobolectricApp;
import root.iv.androidacademy.news.NewsEntity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = Config.NONE, application = RobolectricApp.class)
public class EmptyDatabaseTest extends AppTests {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void onStart() {
        disposables = new CompositeDisposable();
        database = RobolectricApp.getDatabase().getNewsDAO();
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
                                    .subscribeOn(Schedulers.io())
                                    .toObservable()
                                    .blockingLast();
        Assert.assertTrue(list.isEmpty());
    }

    // Сразу из нескольких потоков пытаемся получить информацию из БД
    @Test
    public void callDBFromMultiThread() {
        for (int i = 0; i < COUNT_THREADS; i++)
            new Thread(() -> {
                List<NewsEntity> list = database.getAllAsSingle()
                        .toObservable()
                        .blockingLast();
                Assert.assertTrue(list.isEmpty());
                synhro.threadFinished();
            }).start();

        // Ждём 10 секунд, пока 10 потоков обратятся к БД
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(synhro::allIsFinished);
    }

    // Пробуем получить несуществующие данные из БД
    // Ожидаем получить EmptyResultSetException
    @Test
    public void getNullFromDB() {
        TestObserver<NewsEntity> getItemObserver = database.getItemByIdAsSingle(10)
                .subscribeOn(Schedulers.io())
                .test();
        getItemObserver.awaitTerminalEvent();

        Assert.assertEquals(1, getItemObserver.errorCount());
        Assert.assertTrue(getItemObserver.errors().get(0) instanceof EmptyResultSetException);
    }

    @After
    public void onStop() {
        disposables.dispose();
        synhro.reset();
    }

    private void stdEmptyHandler(Object o) { }
    private void stdErrorHandler(@NonNull Throwable t) {
        RobolectricApp.logE(t.getMessage());
    }
}
