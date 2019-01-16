package root.iv.androidacademy.news;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import root.iv.androidacademy.app.App;
import root.iv.androidacademy.retrofit.dto.MultimediaDTO;
import root.iv.androidacademy.retrofit.dto.NewsDTO;

public class NewsItem implements Parcelable {
    public static final String DATE_FORMAT = "E dd:MM:yyyy KK:mm a";
    private String title;
    private String imageUrl;
    private String previewText;
    private String fullText;
    private String subSection;
    private Date publishDate;


    private NewsItem(NewsItemBuilder builder) {
        this.title = builder.title;
        this.imageUrl = builder.imageUrl;
        this.subSection = builder.subSection;
        this.publishDate = builder.publishDate;
        this.previewText = builder.previewText;
        this.fullText = builder.fullText;
    }

    public static NewsItemBuilder getBuilder() {
        return new NewsItemBuilder();
    }

    public static class NewsItemBuilder {
        private String title = null;
        private String imageUrl = null;
        private String subSection = null;
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

        public NewsItemBuilder buildSubSection(String c) {
            subSection = c;
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
            NewsItem item = new NewsItem(this);
            return valid(item) ? item : null;
        }


        public static boolean valid(NewsItem x) {
                return validNull(x) && validEmpty(x) && validURL(x);
        }

        public static boolean validNull(NewsItem x) {
            return x.getFullText() != null && x.getPreviewText() != null && x.getImageUrl() != null && x.getPublishDate() != null && x.getSubSection() != null && x.getTitle() != null;
        }

        public static boolean validEmpty(NewsItem x) {
            return !x.getPreviewText().isEmpty() && !x.getTitle().isEmpty() && !x.getPublishDateString().isEmpty() && !x.getFullText().isEmpty();
        }

        public static boolean validURL(NewsItem x) {
            boolean validFullText = x.getFullText().contains("http://") || x.getFullText().contains("https://");
//            boolean validImageURL = x.getImageUrl().contains("http://") || x.getImageUrl().contains("https://");
            return validFullText;
        }
    }

    public static NewsItem fromNewsDTO(NewsDTO dto) throws ParseException {
            String imageURL = findImageURL(dto.getMulimedia());
            return NewsItem.getBuilder()
                    .buildTitle(dto.getTitle())
                    .buildFullText(dto.getFullTextURL())
                    .buildSubSection(dto.getCategoryName())
                    .buildPreviewText(dto.getPreviewText())
                    .buildPublishDate(ISO8601Utils.parse(dto.getPublishDate(), new ParsePosition(0)))
                    .buildImageURL(imageURL)
                    .build();
    }

    private static String findImageURL(List<MultimediaDTO> multimedia) {
        for (MultimediaDTO m : multimedia) {
            if (m.isImage()) return m.getUrl();
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSubSection() {
        return subSection;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public static class Comparator implements java.util.Comparator<NewsItem> {
        @Override
        public int compare(NewsItem o1, NewsItem o2) {
            if (o1.getPublishDate().after(o2.getPublishDate())) {
                return -1;
            } else if (o1.getPublishDate().equals(o2.getPublishDate())) {
                return 0;
            } else {
                return 1;
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
        subSection = data[4];
        publishDate = (Date)source.readValue(Date.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {title, imageUrl, previewText, fullText, subSection});
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
