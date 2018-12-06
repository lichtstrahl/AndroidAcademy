package root.iv.androidacademy.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.news.NewsEntity;
import root.iv.androidacademy.news.NewsItem;

public class EditNewsActivity extends AppCompatActivity {
    private static final String INTENT_ID = "INTENT_ID";
    private int itemID;
    @BindView(R.id.editCategory)
    EditText editCategory;
    @BindView(R.id.editTitle)
    EditText editTitle;
    @BindView(R.id.editPreview)
    EditText editPreview;

    @OnClick(R.id.buttonUpdate)
    public void clickUpdate() {
        NewsEntity item = App.getDatabase().getNewsDAO().getItemById(itemID);
        item.setSubSection(editCategory.getText().toString());
        item.setTitle(editTitle.getText().toString());
        item.setPreviewText(editPreview.getText().toString());
        App.getDatabase().getNewsDAO().update(item);
        finish();
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

        itemID = getIntent().getIntExtra(INTENT_ID, -1);
        NewsItem item = App.getDatabase().getNewsDAO().getItemById(itemID).toNewsItem();
        editCategory.setText(item.getSubSection());
        editTitle.setText(item.getTitle());
        editPreview.setText(item.getPreviewText());
    }
}
