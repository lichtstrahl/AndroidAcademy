package root.iv.androidacademy.activity.listener;

import android.view.View;

// Это нормально? Что я расширяю стандартного слушателя, чтобы отписываться?
// Просто у меня уже параноя с отписыванием от ВСЕГО
public interface Listener<A> extends View.OnClickListener {
    void subscribe(A a);
    void unsubscribe();
}
