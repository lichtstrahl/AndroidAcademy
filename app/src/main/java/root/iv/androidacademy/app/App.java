package root.iv.androidacademy.app;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.Stetho;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import root.iv.androidacademy.BuildConfig;
import root.iv.androidacademy.db.NewsDatabase;
import root.iv.androidacademy.news.NewsDAO;
import root.iv.androidacademy.retrofit.TopStoriesAPI;
import root.iv.androidacademy.util.InterceptorAPIKey;

public class App extends Application {
    private static final String ROBO_UNIT_TEST = "robolectric";
    private static Retrofit retrofit;
    private static TopStoriesAPI apiTopStories;
    protected static NewsDatabase database;

    public static TopStoriesAPI getApiTopStories() {
        return apiTopStories;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        if (!isRoboUnitTest()) {
            // Create an InitializerBuilder
            Stetho.InitializerBuilder initializerBuilder =
                    Stetho.newInitializerBuilder(this);

            // Enable Chrome DevTools
            initializerBuilder.enableWebKitInspector(
                    Stetho.defaultInspectorModulesProvider(this)
            );

            // Enable command line interface
            initializerBuilder.enableDumpapp(
                    Stetho.defaultDumperPluginsProvider(this)
            );

            // Use the InitializerBuilder to generate an Initializer
            Stetho.Initializer initializer = initializerBuilder.build();

            // Initialize Stetho with the Initializer
            Stetho.initialize(initializer);
        }

        OkHttpClient client = createClient();
        configurationRetrofit(client);
        apiTopStories = retrofit.create(TopStoriesAPI.class);

        database = getDatabaseBuilder(false).build();
    }

    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new InterceptorAPIKey())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
    }

    protected RoomDatabase.Builder<NewsDatabase> getDatabaseBuilder(boolean allowUIThread) {
        RoomDatabase.Builder<NewsDatabase> builder =  Room.databaseBuilder(this, NewsDatabase.class, BuildConfig.DATABASE_NAME)
                .addMigrations(new Migration(1, 2) {
                                   @Override
                                   public void migrate(@NonNull SupportSQLiteDatabase database) {
                                       App.logI("Used migration 1,2");
                                   }
                               },
                        new Migration(2,1) {
                            @Override
                            public void migrate(@NonNull SupportSQLiteDatabase database) {
                                App.logI("Used migration 2,1");
                            }
                        });
        if (allowUIThread) builder.allowMainThreadQueries();
        return builder;
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

    public static NewsDatabase getDatabase() {
        return database;
    }

    public static void logE(String e) {
        Log.e(BuildConfig.TAG_GLOBAL, e);
    }

    public static void logW(String msg) {
        Log.w(BuildConfig.TAG_GLOBAL, msg);
    }

    public static void logI(String msg) {
        Log.i(BuildConfig.TAG_GLOBAL, msg);
    }

    public static boolean isRoboUnitTest() {
        return ROBO_UNIT_TEST.equals(Build.FINGERPRINT);
    }
}
