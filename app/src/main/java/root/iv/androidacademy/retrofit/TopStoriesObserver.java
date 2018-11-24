package root.iv.androidacademy.retrofit;


import android.support.annotation.Nullable;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import root.iv.androidacademy.Action1;
import root.iv.androidacademy.App;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;
// TODO Добавить функцию removeAll, чтобы больше не забывать про notify
public class TopStoriesObserver implements SingleObserver<TopStoriesDTO> {
    private static final String NULL_BODY = "Тело ответа от сервера null (TopStoriesObserver)";
    private Action1<TopStoriesDTO> complete;
    private Action error;
    private Disposable disposable;

    /**
     * Если ответ пришел не null, тогда перебираем все полученные новости.
     * "Строим" элемент для RecycleView и обновляем adapter
     * В конце выполняем заверщающие действия в Activity (complete)
     * Всё работает на UI, так как здесь уже все загружено, осталось только нарисовать.
     * @param stories - DTO, полученное из сети. Содержит список всех новостей.
     */
    @Override
    public void onSuccess(TopStoriesDTO stories) {
        App.logI("Observer: onSuccess for " + stories.getSection());
        complete.run(stories);
        disposable.dispose();
    }

    private TopStoriesObserver(Builder builder) {
        this.complete = builder.complete;
        this.error = builder.error;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Action1<TopStoriesDTO> complete = null;
        private Action error = null;

        public Builder buildComplete(Action1<TopStoriesDTO> c) {
            complete = c;
            return this;
        }

        public Builder buildError(Action e) {
            error = e;
            return this;
        }

        @Nullable
        public TopStoriesObserver build() {
            boolean done = complete != null && error != null;
            if (done) {
                return new TopStoriesObserver(this);
            }
            else {
                return null;
            }
        }
    }

    private void error() {
        try {
            error.run();
        } catch (Exception e) {
            App.stdLog(e);
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onError(Throwable e) {
        App.stdLog(e);
        error();
    }

    // Теперь здесь проверка на null, т.к. не обязательно будет вызвана загрузка и подписка на событие
    public void dispose() {
        if (disposable != null)
            disposable.dispose();
    }
}
