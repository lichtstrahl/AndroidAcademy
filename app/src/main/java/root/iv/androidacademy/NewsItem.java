package root.iv.androidacademy;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsItem implements Parcelable {
    private static final String DATE_FORMAT = "E dd:MM:yyyy KK:mm a";
    public static final String INTENT_TAG = "NewsItem";
    private String title;
    private String imageUrl;
    private String previewText;
    private String fullText;
    private Category category;
    private Date publishDate;

    private NewsItem(NewsItemBuilder builder) {
        this.title = builder.title;
        this.imageUrl = builder.imageUrl;
        this.category = builder.category;
        this.publishDate = builder.publishDate;
        this.previewText = builder.previewText;
        this.fullText = builder.fullText;
    }

    public static NewsItemBuilder getNewsItemBuilder() {
        return new NewsItemBuilder();
    }

    public static class NewsItemBuilder {
        private String title = null;
        private String imageUrl = null;
        private Category category = null;
        private Date publishDate = null;
        private String previewText = null;
        private String fullText = null;

        public NewsItemBuilder buildTitle(String t) {
            title = t;
            return this;
        }

        public NewsItemBuilder buildImageURL(@Nullable String url) {
            imageUrl = (url != null) ? url : "";
            return this;
        }

        public NewsItemBuilder buildCategory(Category c) {
            category = c;
            return this;
        }


        public NewsItemBuilder buildPublishDate(Date date) {
            publishDate = date;
            return this;
        }

        public NewsItemBuilder buildPreviewText(String text) {
            previewText = text;
            return this;
        }

        public NewsItemBuilder buildFullText(String text) {
            fullText = text;
            return this;
        }

        @Nullable
        public NewsItem build() {
            boolean done =
                    title != null &&
                    imageUrl != null &&
                    category != null &&
                    publishDate != null &&
                    previewText != null &&
                    fullText != null;

            if (done) {
                return new NewsItem(this);
            } else {
                return null;
            }
        }
    }

    private NewsItem(Parcel source) {
        String[] data = new String[5];
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

    public String getPreviewText() {
        return previewText;
    }

    public String getFullText() {
        return fullText;
    }

    public String getPublishDateString() {
        SimpleDateFormat dFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
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
