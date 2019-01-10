package root.iv.androidacademy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;
import root.iv.androidacademy.util.DBObserver;

public class EditNewsActivity extends AppCompatActivity {
    private static final String INTENT_ID = "INTENT_ID";
    private int itemID;
    @BindView(R.id.editCategory)
    EditText editCategory;
    @BindView(R.id.editTitle)
    EditText editTitle;
    @BindView(R.id.editPreview)
    EditText editPreview;
    private DBObserver<NewsEntity> updateNewsItemObserver;
    private DBObserver<Integer> updateNewsEntityObserver;
    private DBObserver<NewsEntity> findNewsItemObserver;

    @OnClick(R.id.buttonUpdate)
    public void clickUpdate() {
        App.getDatabase().getNewsDAO().getItemByIdAsSingle(itemID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateNewsItemObserver);
    }

    public static void start(Context context, int id) {
        Intent intent = new Intent(context, EditNewsActivity.class);
        intent.putExtra(INTENT_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_news);
        ButterKnife.bind(this);


        updateNewsItemObserver = new DBObserver<>(this::fillNewsItem, this::errorLoadFromDB);
        updateNewsEntityObserver = new DBObserver<>(this::updateNewsEntity, this::errorLoadFromDB);
        findNewsItemObserver = new DBObserver<>(this::bindUI, this::errorLoadFromDB);


        itemID = getIntent().getIntExtra(INTENT_ID, -1);

        App.getDatabase().getNewsDAO().getItemByIdAsSingle(itemID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(findNewsItemObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateNewsItemObserver.unsubscribe();
    }

    private void fillNewsItem(NewsEntity item) {
        item.setSubSection(editCategory.getText().toString());
        item.setTitle(editTitle.getText().toString());
        item.setPreviewText(editPreview.getText().toString());

        Single.fromCallable(() -> App.getDatabase().getNewsDAO().update(item))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateNewsEntityObserver);
    }

    private void updateNewsEntity(Integer id) {
        finish();
    }

    private void bindUI(NewsEntity item) {
        editCategory.setText(item.getSubSection());
        editTitle.setText(item.getTitle());
        editPreview.setText(item.getPreviewText());
    }

    private void errorLoadFromDB(Throwable t) {
        App.logE(t.getMessage());
    }
}
