package android.example.com.movies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.com.movies.database.MoviesContract.MoviesEntry;

/**
 * Created by Pritam on 14-12-2017.
 */

public class MoviesDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movielist.db";

    public static final int DATABASE_VERSION = 5;

    public MoviesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                MoviesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL," +
                MoviesEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL," +
                MoviesEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL," +
                MoviesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
