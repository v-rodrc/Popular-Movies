package christopher.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import christopher.popularmovies.Data.MovieContract;
import christopher.popularmovies.Model.Movie;
import christopher.popularmovies.Model.TrailerModel;
import christopher.popularmovies.Model.TrailerResponse;
import christopher.popularmovies.Utils.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    ImageView posterImage;
   TextView movieTitle;
   TextView userRating;
   TextView releaseDate;
   TextView overview;
   TextView poster;

   String title;
   String image;
   String synopsis;
   Double rating;
   String release;
    int id;


    private List<Movie> movieList;

    private RecyclerView trailerRecyclerView;
    private TrailerAdapter adapter;
    private List<TrailerModel> trailerModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        posterImage = (ImageView) findViewById(R.id.poster_image);
        movieTitle = (TextView) findViewById(R.id.movie_title);
        userRating = (TextView) findViewById(R.id.rating);
        releaseDate = (TextView) findViewById(R.id.releasedate);
        overview = (TextView) findViewById(R.id.overview);


        Intent intentFromMainActivity = getIntent();

        title = getIntent().getExtras().getString("title");
        image = getIntent().getExtras().getString("image");
        synopsis = getIntent().getExtras().getString("overview");
        rating = getIntent().getExtras().getDouble("average_rating");
        release = getIntent().getExtras().getString("release");

        id = getIntent().getExtras().getInt("trailerId");

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(posterImage);

        movieTitle.setText(title);
        releaseDate.setText(release);
        userRating.setText(rating.toString() + " / 10");
        overview.setText(synopsis);

        loadViews();

        parseJson();
    }

    private void loadViews() {
        trailerModelList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerModelList);

        trailerRecyclerView = (RecyclerView) (findViewById(R.id.rv_trailers));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        trailerRecyclerView.setLayoutManager(layoutManager);
        trailerRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void parseJson() {
        RetrofitClient client = new RetrofitClient();

        ApiService apiService = client.getClient().create(ApiService.class);

        Call<TrailerResponse> call = apiService.getTrailer(id, BuildConfig.MOVIE_DB_API);
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                List<TrailerModel> trailer = response.body().getTrailerResults();
                trailerRecyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                trailerRecyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Network Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public Cursor loadInBackground() {
        try {
            return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
