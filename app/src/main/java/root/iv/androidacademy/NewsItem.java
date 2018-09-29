package root.iv.androidacademy;

import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsItem {
    private final String TAG = getClass().getName();
    public  final String KEY_INTENT_TITLE = "KEY_INSTANCE_TITLE";
    private String title;
    public final String KEY_INTENT_IMAGE_URL = "KEY_INSTANCE_IMAGE_URL";
    private String imageUrl;
    public final String KEY_INTENT_CATEGORY_NAME = "KEY_INSTANCE_CATEGORY_NAME";
    public final String KEY_INTENT_CATEGORY_ID = "KEY_INSTANCE_CATEGORY_ID";
    public final String KEY_INTENT_CATEGORY_COLOR = "KEY_INTENT_CATEGORY_COLOR";
    private Category category;
    public final String KEY_INTENT_DATE = "KEY_INTENT_DATE";
    private Date publishDate;
    public final String KEY_INTENT_PREVIEW = "KEY_INTENT_PREVIEW";
    private String previewText;
    public final String KEY_INTENT_FULL = "KEY_INTENT_FULL";
    private String fullText;
    private final String dateFormat = "E dd:MM:yyyy KK:mm a";

    public NewsItem() {

    }
    public NewsItem(String title, String imageUrl, Category category, Date publishDate, String previewText, String fullText) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.category = category;
        this.publishDate = publishDate;
        this.previewText = previewText;
        this.fullText = fullText;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public String getPreviewText() {
        return previewText;
    }

    public String getFullText() {
        return fullText;
    }

    public String getPublishDateString() {
        SimpleDateFormat dFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return dFormat.format(publishDate);
    }

    public void putToExtra(Intent intent) {
        intent.putExtra(KEY_INTENT_TITLE, title);
        intent.putExtra(KEY_INTENT_IMAGE_URL, imageUrl);
        intent.putExtra(KEY_INTENT_CATEGORY_NAME, category.getName());
        intent.putExtra(KEY_INTENT_CATEGORY_ID, category.getId());
        intent.putExtra(KEY_INTENT_CATEGORY_COLOR, category.getColor());
        intent.putExtra(KEY_INTENT_DATE, getPublishDateString());
        intent.putExtra(KEY_INTENT_PREVIEW, previewText);
        intent.putExtra(KEY_INTENT_FULL, fullText);

    }
    public void loadFromExtra(Intent intent) {
        try {
            title = intent.getStringExtra(KEY_INTENT_TITLE);
            imageUrl = intent.getStringExtra(KEY_INTENT_IMAGE_URL);
            // TODO Сделай что-нибудь с этим! Это кастыль
            category = new Category(
                    intent.getIntExtra(KEY_INTENT_CATEGORY_ID, 0),
                    intent.getStringExtra(KEY_INTENT_CATEGORY_NAME),
                    intent.getIntExtra(KEY_INTENT_CATEGORY_COLOR, 0)
            );
            publishDate = new SimpleDateFormat(dateFormat, Locale.getDefault()).parse(intent.getStringExtra(KEY_INTENT_DATE));
            previewText = intent.getStringExtra(KEY_INTENT_PREVIEW);
            fullText = intent.getStringExtra(KEY_INTENT_FULL);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
