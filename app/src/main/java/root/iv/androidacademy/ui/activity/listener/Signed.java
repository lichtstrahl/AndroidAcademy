package root.iv.androidacademy.ui.activity.listener;

public interface Signed<A> {
    void subscribe(A a);
    void unsubscribe();
}
