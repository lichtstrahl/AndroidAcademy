package root.iv.androidacademy.retrofit;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import root.iv.androidacademy.BuildConfig;

public class InterceptorAPIKey implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HttpUrl url = oldRequest.url()
                .newBuilder()
                .addQueryParameter("api-key", BuildConfig.API_KEY)
                .build();
        Request newRequest = oldRequest.newBuilder()
                .url(url)
                .build();
        return chain.proceed(newRequest);
    }
}
