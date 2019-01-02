package root.iv.androidacademy.news;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class NewsEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String imageUrl;
    private String previewText;
    private String fullText;
    private String subSection;
    private String publishDate;

    public static NewsEntity fromNewsItem(NewsItem item) {
        NewsEntity entity = new NewsEntity();

        entity.title = item.getTitle();
        entity.imageUrl = item.getImageUrl();
        entity.previewText = item.getPreviewText();
        entity.fullText = item.getFullText();
        entity.subSection = item.getSubSection();
        entity.publishDate = item.getPublishDateString();

        return entity;
    }

    public NewsItem toNewsItem() {
        return new NewsItem.NewsItemBuilder()
                .buildTitle(title)
                .buildImageURL(imageUrl)
                .buildPreviewText(previewText)
                .buildFullText(fullText)
                .buildSubSection(subSection)
                .buildPublishDate(getPublishDateAsDate())
                .build();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getSubSection() {
        return subSection;
    }

    public void setSubSection(String subSection) {
        this.subSection = subSection;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public Date getPublishDateAsDate() {
        SimpleDateFormat dFormat = new SimpleDateFormat(NewsItem.DATE_FORMAT, Locale.getDefault());
        return (Date)dFormat.parseObject(publishDate, new ParsePosition(0));
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
