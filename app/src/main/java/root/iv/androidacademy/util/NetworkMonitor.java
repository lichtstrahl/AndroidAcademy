package root.iv.androidacademy.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import io.reactivex.subjects.Subject;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.background.NewsService;

public class NetworkMonitor extends ConnectivityManager.NetworkCallback {
    private final NetworkRequest request;
    private Subject<Boolean> networkState;


    public NetworkMonitor(Subject<Boolean> state) {
        request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        networkState = state;
    }

    public void enable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.registerNetworkCallback(request, this);
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        networkState.onNext(NetworkUtils.isNetworkAbailable());
        App.logI("Интернет появился");
        NewsService.call(App.getContext(), "food");
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        networkState.onNext(NetworkUtils.isNetworkAbailable());
        App.logI("Интернет отключился");
    }
}
