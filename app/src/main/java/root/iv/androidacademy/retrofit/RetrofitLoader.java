package root.iv.androidacademy.retrofit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.App;
import root.iv.androidacademy.NewsAdapter;

public class RetrofitLoader implements DataLoader {
    private TopStoriesObserver observer;
    private String section;

    public RetrofitLoader(NewsAdapter adapter, String s, Action complete, Action error) {
        observer = TopStoriesObserver.getBuilder()
                .buildAdapter(adapter)
                .buildComplete(complete)
                .buildError(error)
                .build();
        section = s;
    }

    @Override
    public void stop() {
        observer.dispose();
    }

    @Override
    public void load() {
        App.getApiTopStories().getTopStories(section)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
