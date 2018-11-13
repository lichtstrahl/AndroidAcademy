package root.iv.androidacademy.retrofit;


import android.support.annotation.Nullable;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import root.iv.androidacademy.App;
import root.iv.androidacademy.Section;
import root.iv.androidacademy.NewsAdapter;
import root.iv.androidacademy.NewsItem;
import root.iv.androidacademy.R;
import root.iv.androidacademy.retrofit.dto.MultimediaDTO;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;
// TODO Добавить функцию removeAll, чтобы больше не забывать про notify
public class TopStoriesObserver implements SingleObserver<TopStoriesDTO> {
    private static final String NULL_BODY = "Тело ответа от сервера null (TopStoriesObserver)";
    private NewsAdapter adapter;
    private Action complete;
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
        if (stories != null) {
            // ПИЗДЕЦ БЛЯТЬ ОТКУДА Я ДОЛЖЕН БЫЛ ЭТО УЗНАТЬ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // АРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРРР!!!!!!!!
            int count = adapter.getItemCount();
            adapter.clear();
            adapter.notifyItemRangeRemoved(0, count);

            for (NewsDTO news : stories.getListNews()) {
                try {
                    adapter.append(buildNewsItem(news));
                } catch (ParseException e) {
                    App.stdLog(e);
                }
            }
            complete();
        } else
            App.stdLog(NULL_BODY);
        // Это правильно? Или он тоже сам где-то отпишется при Success
        disposable.dispose();
    }

    private TopStoriesObserver(Builder builder) {
        this.adapter = builder.adapter;
        this.complete = builder.complete;
        this.error = builder.error;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private NewsAdapter adapter = null;
        private Action complete = null;
        private Action error = null;

        public Builder buildAdapter(NewsAdapter a) {
            this.adapter = a;
            return this;
        }

        public Builder buildComplete(Action c) {
            complete = c;
            return this;
        }

        public Builder buildError(Action e) {
            error = e;
            return this;
        }

        @Nullable
        public TopStoriesObserver build() {
            boolean done = adapter != null && complete != null && error != null;
            if (done) {
                return new TopStoriesObserver(this);
            }
            else {
                return null;
            }
        }
    }

    private void complete() {
        try {
            complete.run();
        } catch (Exception e) {
            App.stdLog(e);
        }
    }

    private void error() {
        try {
            error.run();
        } catch (Exception e) {
            App.stdLog(e);
        }
    }

    private String findImageURL(List<MultimediaDTO> multimedia) {
        for (MultimediaDTO m : multimedia) {
            if (m.isImage()) return m.getUrl();
        }
        return null;
    }

    private NewsItem buildNewsItem(NewsDTO dto) throws ParseException {
        String imageURL = findImageURL(dto.getMulimedia());
        return NewsItem.getBuilder()
                .buildTitle(dto.getTitle())
                .buildCategory(Section.valueOf(dto.getCategoryName(), R.color.colorHome))
                .buildFullText(dto.getFullTextURL())
                .buildPreviewText(dto.getPreviewText())
                .buildPublishDate(ISO8601Utils.parse(dto.getPublishDate(), new ParsePosition(0)))
                .buildImageURL(imageURL)
                .build();
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

    public void dispose() {
        disposable.dispose();
    }
}
