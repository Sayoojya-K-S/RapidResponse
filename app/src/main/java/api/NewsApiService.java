package api;

import DataModel.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("everything") // Endpoint for fetching news
    Call<NewsResponse> getDisasterNews(
            @Query("q") String query,  // Use 'q' for NewsAPI query
            @Query("apiKey") String apiKey,  // Correct key name
            @Query("sortBy") String sortBy,
            @Query("language") String language
    );
}