package christopher.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import christopher.popularmovies.Model.Movie;

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


    private List<Movie> movieList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        posterImage = (ImageView)findViewById(R.id.poster_image);
        movieTitle = (TextView)findViewById(R.id.movie_title);
        userRating = (TextView)findViewById(R.id.rating);
        releaseDate = (TextView)findViewById(R.id.releasedate);
        overview = (TextView)findViewById(R.id.overview);


        Intent intentFromMainActivity = getIntent();

        title = getIntent().getExtras().getString("title");
        image = getIntent().getExtras().getString("image");
        synopsis = getIntent().getExtras().getString("overview");
        rating = getIntent().getExtras().getDouble("average_rating");
        release = getIntent().getExtras().getString("release");

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(posterImage);

        movieTitle.setText(title);
        releaseDate.setText(release);
        userRating.setText(rating.toString() + " / 10");
        overview.setText(synopsis);




    }
}