package root.iv.androidacademy;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsItem implements Parcelable {
    private final String dateFormat = "E dd:MM:yyyy KK:mm a";
    public static final String INTENT_TAG = "NewsItem";
    private String title;
    private String imageUrl;
    private String previewText;
    private String fullText;
    private Category category;
    private Date publishDate;

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

    public NewsItem(Parcel source) {
        String data[] = new String[4];
        source.readStringArray(data);
        title = data[0];
        imageUrl = data[1];
        previewText = data[2];
        fullText = data[3];
        category = (Category)source.readValue(Category.class.getClassLoader());
        publishDate = (Date)source.readValue(Date.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {title, imageUrl, previewText, fullText});
        dest.writeValue(category);
        dest.writeValue(publishDate);
    }

    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel source) {
            return new NewsItem(source);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
