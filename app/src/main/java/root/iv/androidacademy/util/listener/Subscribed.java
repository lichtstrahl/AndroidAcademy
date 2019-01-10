package root.iv.androidacademy.util.listener;

public interface Subscribed<A> {
    void subscribe(A a);
    void unsubscribe();
}
