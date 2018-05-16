package christopher.popularmovies.Utils;

import java.util.List;

import christopher.popularmovies.Model.ApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {



@GET("movie/popular")
Call<ApiResponse> getMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<List<ApiResponse>> getTopMovies(@Query("api_key") String apiKey);
}

