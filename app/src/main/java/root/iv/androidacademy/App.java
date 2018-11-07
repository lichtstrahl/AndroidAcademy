package root.iv.androidacademy;

import android.app.Application;
import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static Retrofit retrofit;
    private static final String URL = "http://api.nytimes.com";
    private static final String APIKey = "94c9d30bd1334f149a0d3028ae662d27";
    private static final String TAG_GLOBAL = "AndroidAcademy";
    @Override
    public void onCreate() {
        super.onCreate();
        configurationRetrofit();
    }

    private static void configurationRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
    public static String getAPIKey() {
        return APIKey;
    }
    public static void stdErrorCatch(Throwable e) {
        Log.e(TAG_GLOBAL, e.getMessage());
    }
    public static void stdErrorCatch(String msg) {
        Log.e(TAG_GLOBAL, msg);
    }
}
