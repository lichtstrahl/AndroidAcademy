package root.iv.androidacademy.retrofit.dto;

import com.google.gson.annotations.SerializedName;

public class MultimediaDTO {
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;

    public String getType() {
        return type;
    }
    public String getUrl() {
        return url;
    }

    public boolean isImage() {
        return type.equals("image");
    }
}
