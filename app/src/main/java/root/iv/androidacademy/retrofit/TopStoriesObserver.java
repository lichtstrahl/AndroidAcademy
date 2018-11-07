package root.iv.androidacademy.retrofit;


import android.util.Log;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.text.ParseException;
import java.text.ParsePosition;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import root.iv.androidacademy.App;
import root.iv.androidacademy.Category;
import root.iv.androidacademy.NewsAdapter;
import root.iv.androidacademy.NewsItem;
import root.iv.androidacademy.R;
import root.iv.androidacademy.retrofit.dto.MultimediaDTO;
import root.iv.androidacademy.retrofit.dto.NewsDTO;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;

public class TopStoriesObserver implements SingleObserver<TopStoriesDTO> {
    private final String NULL_BODY = "Тело ответа от сервера null (TopStoriesObserver)";
    private NewsAdapter adapter;
    private Action complete;

    public TopStoriesObserver(NewsAdapter a, Action action) {
        adapter = a;
        complete = action;
    }

    private Disposable disposable;
    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    /**
     * Если ответ пришел не null, тогда перебираем все полученные новости.
     * Для каждой новости запоминаем первую найденную картинку в списке multimedia, если такой нет, то null и ничего рисоваться не будет.
     * "Строим" элемент для RecycleView и обновляем adapter
     * В конце выполняем заверщающие действия в Activity (complete)
     * Всё работает на UI, так как здесь уже все загружено, осталось только нарисовать.
     * @param stories
     */
    @Override
    public void onSuccess(TopStoriesDTO stories) {
        if (stories != null) {
            for (NewsDTO news : stories.getListNews()) {
                try {
                    String imageURL = null;
                    for (MultimediaDTO multimedia : news.getMulimedia()) {
                        if (multimedia.isImage())
                            imageURL = multimedia.getUrl();
                    }
                    NewsItem newItem = NewsItem.getNewsItemBuilder()
                            .buildTitle(news.getTitle())
                            .buildCategory(new Category(0, news.getCategoryName(), R.color.darwinColor))
                            .buildFullText(news.getFullTextURL())
                            .buildPreviewText(news.getPreviewText())
                            .buildPublishDate(ISO8601Utils.parse(news.getPublishDate(), new ParsePosition(0)))
                            .buildImageURL(imageURL)
                            .build();
                    adapter.append(newItem);
                    adapter.notifyItemInserted(adapter.getItemCount()-1);
                } catch (ParseException e) {
                    App.stdErrorCatch(e);
                }
            }
            try {
                complete.run();
            } catch (Exception e) {
                App.stdErrorCatch(e);
            }
        } else
            App.stdErrorCatch(NULL_BODY);
        // Это правильно? Или он тоже сам где-то отпишется при Success
        disposable.dispose();
    }

    @Override
    public void onError(Throwable e) {
        App.stdErrorCatch(e);
    }

    public void dispose() {
        disposable.dispose();
    }
}
