package christopher.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import christopher.popularmovies.Data.Database;
import christopher.popularmovies.Data.MainViewModel;
import christopher.popularmovies.Model.ApiResponse;
import christopher.popularmovies.Model.Movie;
import christopher.popularmovies.Utils.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    private MovieAdapter mAdapter;

    private RecyclerView recyclerView;
    private List<Movie> movieList;

    private static String TAG = "101";

    private Database movieDatabase;

    private List<Movie> mMoviesFav;

    Movie movie;

    private int recyclerViewState;

    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadViews();
        sorting();




    }


    private void loadViews() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_numbers);

        movieList = new ArrayList<>();
        mAdapter = new MovieAdapter(this, movieList);

        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(MainActivity.this, 2);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            layoutManager = new GridLayoutManager(MainActivity.this, 4);
            recyclerView.setLayoutManager(layoutManager);
        }


        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private void parseJson() {

        RetrofitClient client = new RetrofitClient();

        ApiService apiService = client.getClient().create(ApiService.class);

        Call<ApiResponse> call = apiService.getMovies(BuildConfig.MOVIE_DB_API);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                List<Movie> movies = response.body().getResults();


                recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                recyclerView.scrollToPosition(recyclerViewState);

                Toast.makeText(MainActivity.this, R.string.sort_popularity, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.internet_connect_error, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            /*
             * When you click the reset menu item, we want to start all over
             * and display the pretty gradient again. There are a few similar
             * ways of doing this, with this one being the simplest of those
             * ways. (in our humble opinion)
             */
            case R.id.settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;


        }
        return super.onOptionsItemSelected(item);

    }

    private void parseJsonHighestRated() {

        RetrofitClient client = new RetrofitClient();

        ApiService apiService = client.getClient().create(ApiService.class);

        Call<ApiResponse> call = apiService.getTopMovies(BuildConfig.MOVIE_DB_API);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                List<Movie> movies = response.body().getResults();


                recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                recyclerView.scrollToPosition(recyclerViewState);

                Toast.makeText(MainActivity.this, R.string.sort_toprated, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.internet_connect_error, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        sorting();
    }

    private void sorting() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String order = sharedPreferences.getString(
                this.getString(R.string.preference_order_key),
                this.getString(R.string.preference_popular)
        );
        if (order.equals(this.getString(R.string.preference_popular))) {

            parseJson();

        } else if (order.equals(this.getString(R.string.preference_rating))) {

            parseJsonHighestRated();
        } else if (order.equals(this.getString(R.string.preference_favorites))) {
            sortByFavorite();
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

    }



    private void sortByFavorite() {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {


            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                movieList = movies;
                Log.d(TAG, "Showing list of favorite movies");
                mAdapter.setMovies((ArrayList<Movie>) movies);
                recyclerView.scrollToPosition(recyclerViewState);
                Toast.makeText(getApplicationContext(), R.string.sort_favorites, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();




    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        recyclerViewState = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        outState.putInt("position", recyclerViewState);



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        recyclerViewState = savedInstanceState.getInt("position");




    }
}






