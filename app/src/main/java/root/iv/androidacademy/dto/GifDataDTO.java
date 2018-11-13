package root.iv.androidacademy.dto;

import com.google.gson.annotations.SerializedName;

public class GifDataDTO {
    @SerializedName("title")
    private String title;

    public String getTitle() {
        return title;
    }
}
