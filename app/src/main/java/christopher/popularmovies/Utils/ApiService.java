package christopher.popularmovies.Utils;

import christopher.popularmovies.Model.ApiResponse;
import christopher.popularmovies.Model.TrailerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {



@GET("movie/popular")
Call<ApiResponse> getMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<ApiResponse> getTopMovies(@Query("api_key") String apiKey);

    @GET("movie{id}/videos")
    Call<TrailerResponse> getTrailer(@Path("id") int id, @Query("api_key") String apiKey);
}

