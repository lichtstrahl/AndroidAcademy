package root.iv.androidacademy.retrofit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.util.Action1;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;

public class RetrofitLoader implements DataLoader {
    private TopStoriesObserver observer;
    private String section;

    public RetrofitLoader(String s, Action1<TopStoriesDTO> complete, Action error) {
        observer = TopStoriesObserver.getBuilder()
                .buildComplete(complete)
                .buildError(error)
                .build();
        section = s;
    }

    public void setSection(String section) {
        this.section = section;
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
        App.logI("Load: " + section);
    }
}
