package root.iv.androidacademy.activity.listener;

public interface Signed<A> {
    void subscribe(A a);
    void unsubscribe();
}
