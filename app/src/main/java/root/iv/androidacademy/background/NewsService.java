package root.iv.androidacademy.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;
import root.iv.androidacademy.util.NetworkUtils;
import root.iv.androidacademy.util.NotificationFactory;

public class NewsService extends Service {
    private static final String INTENT_SECTION = "args:section";
    private static final int FOREGROUND_ID = 100;
    @Nullable
    private Disposable disposable;
    @Nullable
    private Disposable completeLoad;

    public static void call(Context context, String section) {
        Intent intent = new Intent(context, NewsService.class);
        intent.putExtra(INTENT_SECTION, section);
        App.logI("Вызов сервиса");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.logI("Создание сервиса");
        startForeground(FOREGROUND_ID, NotificationFactory.loading(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String section = intent.getStringExtra(INTENT_SECTION);
        App.logI("Запуск сервиса: " + section);
        disposable = NetworkUtils.instance.getOnlineNetwork()
                .timeout(1, TimeUnit.MINUTES)
                .flatMap(aLong -> App.getApiTopStories().getTopStories(section))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateDataInDB,
                        error -> {
                            NotificationFactory.show(this, NotificationFactory.error(this));
                            stop();
                        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        if (completeLoad != null && !completeLoad.isDisposed()) {
            completeLoad.dispose();
        }
    }

    /**
     * Обновление данных в БД
     * @param stories - список новостей
     */
    private void updateDataInDB(TopStoriesDTO stories) {
        List<Single<Long>> singles = new LinkedList<>();

        for (NewsDTO news : stories.getListNews()) {
            try {
                NewsItem item = NewsItem.fromNewsDTO(news);
                singles.add(Single.fromCallable(() -> App.getDatabase().getNewsDAO().insert(NewsEntity.fromNewsItem(item))));
            } catch (ParseException e) {
                App.logE(e.getMessage());
            }
        }

        completeLoad = Single.zip(singles, args -> 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    i -> {
                        NotificationFactory.show(this, NotificationFactory.complete(this));
                        stop();
                    },
                    t -> {
                        NotificationFactory.show(this, NotificationFactory.error(this));
                        stop();
                    }
                );
    }

    private void stop() {
        App.logI("Сервис остановлен");
        stopForeground(true);
        stopSelf();
    }
}
