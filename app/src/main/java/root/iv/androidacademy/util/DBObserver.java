package root.iv.androidacademy.util;

import javax.annotation.Nullable;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class DBObserver<T> implements SingleObserver<T> {
    @Nullable
    private Disposable disposable;
    @Nullable
    private Action1<T> action;
    @Nullable
    private Action1<Throwable> error;

    public DBObserver(@Nullable Action1<T> a, @Nullable Action1<Throwable> e) {
        action = a;
        error = e;
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onSuccess(T o) {
        if (action != null) action.run(o);
    }

    @Override
    public void onError(Throwable e) {
        if (error != null) error.run(e);
    }

    public void unsubscribe() {
        if (disposable != null) disposable.dispose();
        action = null;
        error = null;
    }
}
