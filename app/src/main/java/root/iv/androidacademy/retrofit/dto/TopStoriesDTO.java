package root.iv.androidacademy.retrofit.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopStoriesDTO {
    @SerializedName("section")
    private String section;

    @SerializedName("results")
    private List<NewsDTO> listNews;

    public String getSection() {
        return section;
    }
    public List<NewsDTO> getListNews() {
        return listNews;
    }

}
