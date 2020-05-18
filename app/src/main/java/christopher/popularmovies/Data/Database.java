package christopher.popularmovies.Data;



import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import christopher.popularmovies.Model.Movie;


@androidx.room.Database(entities = {Movie.class}, version = 3, exportSchema = false)

public abstract class Database extends RoomDatabase {

    private static final String LOG_TAG = Database.class.getSimpleName();

    private static final Object LOCK = new Object();

    private static final String DATABASE_NAME = "moviesdb";

    private static Database sInstance;

    public static Database getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        Database.class, Database.DATABASE_NAME)
                        //.allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Creating new database instance");
        return sInstance;
    }

    public abstract DaoAccess daoAccess();
}

