package root.iv.androidacademy.retrofit.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsDTO {
    @SerializedName("subsection")
    private String categoryName;
    @SerializedName("title")
    private String title;
    @SerializedName("abstract")
    private String previewText;
    @SerializedName("url")
    private String fullTextURL;
    @SerializedName("published_date")
    private String publishDate;
    @SerializedName("multimedia")
    private List<MultimediaDTO> mulimedia;

    public String getCategoryName() {
        return categoryName;
    }

    public String getTitle() {
        return title;
    }

    public String getPreviewText() {
        return previewText;
    }

    public String getFullTextURL() {
        return fullTextURL;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public List<MultimediaDTO> getMulimedia() {
        return mulimedia;
    }
}
