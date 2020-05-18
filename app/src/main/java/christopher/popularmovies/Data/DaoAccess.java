package christopher.popularmovies.Data;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import christopher.popularmovies.Model.Movie;


@Dao
public interface DaoAccess {

    @Query("SELECT * FROM movie_table ORDER BY popularity")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM movie_table WHERE id IS :movieId")
    LiveData<Movie> getMovie(Integer movieId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movies);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movies);

    @Delete
    void deleteMovie(Movie movies);


}
