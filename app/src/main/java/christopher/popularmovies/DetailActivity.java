package christopher.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import christopher.popularmovies.Data.Database;
import christopher.popularmovies.Data.MainViewModel;
import christopher.popularmovies.Data.MovieContract;
import christopher.popularmovies.Model.Movie;
import christopher.popularmovies.Model.ReviewModel;
import christopher.popularmovies.Model.ReviewResponse;
import christopher.popularmovies.Model.TrailerModel;
import christopher.popularmovies.Model.TrailerResponse;
import christopher.popularmovies.Utils.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static String TAG = "101";

    @BindView(R.id.poster_image)
    ImageView posterImage;
    @BindView(R.id.movie_title)
    TextView movieTitle;
    @BindView(R.id.rating)
    TextView userRating;
    @BindView(R.id.releasedate)
    TextView releaseDate;
    @BindView(R.id.overview)
    TextView overview;
    TextView poster;

    @BindView(R.id.favorite_button)
    FloatingActionButton favorite;


    @BindView(R.id.detailScroll)
    ScrollView scrollView;

    int id;
    String title;
    String image;
    String synopsis;
    Double rating;
    String release;
    String userReviews;
    int idTrailer;
    int idReview;
    int idContentReview;


    Boolean isFavorite = false;

    Movie movie;

    SharedPreferences sharedPreferences;

    private List<Movie> movieList;

    private RecyclerView trailerRecyclerView;
    private TrailerAdapter adapter;
    private List<TrailerModel> trailerModelList;


    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<ReviewModel> reviewModelList;


    private Database movieDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);




        Intent intentFromMainActivity = getIntent();

        movie = getIntent().getParcelableExtra("movieParcel");

        id = movie.getId();
        title = movie.getTitle();
        image = movie.getPosterPath();
        synopsis = movie.getOverview();
        rating = movie.getVoteAverage();
        release = movie.getReleaseDate();


        idTrailer = movie.getId();
        idReview = movie.getId();
        idContentReview = movie.getId();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        movieDatabase = Database.getInstance(getApplicationContext());


        Glide.with(this)
                .load("http://image.tmdb.org/t/p/w92" + image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(posterImage);

        movieTitle.setText(title);
        releaseDate.setText(release);
        userRating.setText(rating.toString() + " / 10");
        overview.setText(synopsis);

        movie = new Movie(id, title, image, synopsis, rating, release, isFavorite);

        loadViews();

        loadViewsReview();

        parseJson();

        parseJsonReview();

        setUpViewModel();




    }

    private void loadViews() {
        trailerModelList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerModelList);

        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getMovieById(movie.getId()).observe(this, new Observer<Movie>() {

            @Override
            public void onChanged(@Nullable Movie movie) {
                if (movie != null && movie.isFavorite()) {
                    favorite.setImageResource(R.drawable.favoritefilled);
                }
            }
        });

        trailerRecyclerView = (RecyclerView) (findViewById(R.id.rv_trailers));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        trailerRecyclerView.setLayoutManager(layoutManager);
        trailerRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void parseJson() {
        RetrofitClient client = new RetrofitClient();

        ApiService apiService = client.getClient().create(ApiService.class);

        Call<TrailerResponse> call = apiService.getTrailer(idTrailer, BuildConfig.MOVIE_DB_API);
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                List<TrailerModel> trailer = response.body().getTrailerResults();
                trailerRecyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                trailerRecyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, R.string.internet_connect_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadViewsReview() {
        reviewModelList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewModelList);

        reviewRecyclerView = (RecyclerView) (findViewById(R.id.rv_reviews));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setAdapter(adapter);
        reviewAdapter.notifyDataSetChanged();
    }

    private void parseJsonReview() {
        RetrofitClient client = new RetrofitClient();

        ApiService apiService = client.getClient().create(ApiService.class);
        Call<ReviewResponse> call = apiService.getReviews(idReview, BuildConfig.MOVIE_DB_API);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                List<ReviewModel> review = response.body().getReviewResults();
                reviewRecyclerView.setAdapter(new ReviewAdapter(getApplicationContext(), review));
                reviewRecyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {

                Toast.makeText(DetailActivity.this, R.string.internet_connect_error, Toast.LENGTH_LONG).show();
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


    public void saveToFavorites(View v) {


// if it is not favorite(false) then update movie object with movie class setfavorite to true, toast, set image to filled heart, update boolean to true
        //insert into db, save boolean into shared preferences

        if (!isFavorite) {
            movie.setFavorite(true);
            Snackbar.make(v, R.string.add_favorite, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favoritefilled));

            isFavorite = true;
            new Task2().execute("INSERT");
            SharedPreferences.Editor editor = getSharedPreferences("com.christopher.popularmovies.DetailActivity", MODE_PRIVATE).edit();
            editor.putBoolean("add_favorite", isFavorite);
            editor.apply();

// otherwise update movie object with movie class setfavorite to false, toast, set image to filled heart, update boolean to false
            //delete from db, save boolean into shared preferences

        } else {
            movie.setFavorite(false);

            Snackbar.make(v, R.string.delete_favorite, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.favorite));
            isFavorite = false;
            new Task2().execute("DELETE");
            SharedPreferences.Editor editor = getSharedPreferences("com.christopher.popularmovies.DetailActivity", MODE_PRIVATE).edit();
            editor.putBoolean("delete_favorite", isFavorite);
            editor.apply();
        }

    }


    private void setUpViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {


            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(TAG, "Updating movies from LiveData in ViewModel");
                adapter.notifyDataSetChanged();
            }
        });


    }


    class Task2 extends AsyncTask<String, Void, Void>

    {
        @Override
        protected Void doInBackground(String... strings) {
            if (strings[0].equals("INSERT")) {
                movieDatabase.daoAccess().insertMovie(movie);

            } else {
                movieDatabase.daoAccess().deleteMovie(movie);

            }

            return null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        movie = getIntent().getParcelableExtra("movieParcel");
        outState.putParcelable("parcelDetail", movie);
        outState.putIntArray("SCROLL_STATE", new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
        outState.putBoolean("savetoFavoriteState", isFavorite);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable("parcelDetail");
            savedInstanceState.getIntArray("SCROLL_STATE");
            savedInstanceState.getBoolean("saveFavoriteState");
        }
    }


}







