package root.iv.androidacademy;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.util.Log;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import root.iv.androidacademy.retrofit.TopStoriesAPI;

public class App extends Application {
    private static Retrofit retrofit;
    private static TopStoriesAPI apiTopStories;

    public static TopStoriesAPI getApiTopStories() {
        return apiTopStories;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient client = createClient();
        configurationRetrofit(client);
        apiTopStories = retrofit.create(TopStoriesAPI.class);

        Room.databaseBuilder(this, NewsDatabase.class, BuildConfig.DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor((Interceptor.Chain chain) -> {
                    Request oldRequest = chain.request();
                    HttpUrl url = oldRequest.url()
                            .newBuilder()
                            .addQueryParameter("api-key", BuildConfig.API_KEY)
                            .build();
                    Request newRequest = oldRequest.newBuilder()
                            .url(url)
                            .build();
                    return chain.proceed(newRequest);
                })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
    }

    private static void configurationRetrofit(OkHttpClient client) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static void stdLog(Throwable e) {
        Log.e(BuildConfig.TAG_GLOBAL, e.getMessage());
    }

    public static void stdLog(String msg) {
        Log.e(BuildConfig.TAG_GLOBAL, msg);
    }
    public static void logI(String msg) {
        Log.i(BuildConfig.TAG_GLOBAL, msg);
    }
}
