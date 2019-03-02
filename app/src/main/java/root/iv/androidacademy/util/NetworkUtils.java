package root.iv.androidacademy.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import root.iv.androidacademy.app.App;

public class NetworkUtils {
    public static NetworkUtils instance = new NetworkUtils();
    private Subject<Boolean> networkState = BehaviorSubject.createDefault(NetworkUtils.isNetworkAbailable());
    private NetworkMonitor monitor = new NetworkMonitor(networkState);

    public Single<Boolean> getOnlineNetwork() {
        return networkState
                .subscribeOn(Schedulers.io())
                .filter(online -> online)
                .firstOrError();
    }

    public NetworkMonitor getMonitor() {
        return monitor;
    }

    public static boolean isNetworkAbailable() {
        ConnectivityManager manager = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
