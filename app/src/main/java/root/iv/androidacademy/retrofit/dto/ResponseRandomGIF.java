package root.iv.androidacademy.retrofit.dto;

import com.google.gson.annotations.SerializedName;

public class ResponseRandomGIF {
    @SerializedName("data")
    private GifDataDTO dataDTO;

    public String getGifTitle() {
        return dataDTO.getTitle();
    }
}
