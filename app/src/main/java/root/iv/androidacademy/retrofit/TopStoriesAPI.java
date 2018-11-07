package root.iv.androidacademy.retrofit;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import root.iv.androidacademy.retrofit.dto.TopStoriesDTO;

public interface TopStoriesAPI {
    @GET("/svc/topstories/v2/{section}.json")
    Single<TopStoriesDTO> getTopStories(@Path("section")String section, @Query("api-key") String api_key);
}
