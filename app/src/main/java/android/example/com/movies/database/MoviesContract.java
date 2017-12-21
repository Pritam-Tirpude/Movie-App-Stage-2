package android.example.com.movies.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Pritam on 14-12-2017.
 */

public class MoviesContract {

    public static final String AUTHORITY = "android.example.com.movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TABLE = "movieslist";

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TABLE).build();

        public static final String TABLE_NAME = "movieslist";

        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_MOVIE_RATING = "movieRating";
        public static final String COLUMN_MOVIE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_POSTER = "posterLink";
    }
}
