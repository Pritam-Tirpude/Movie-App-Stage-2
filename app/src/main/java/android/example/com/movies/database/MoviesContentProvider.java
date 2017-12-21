package android.example.com.movies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.example.com.movies.database.MoviesContract.MoviesEntry;

/**
 * Created by Pritam on 14-12-2017.
 */

public class MoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MoviesDatabaseHelper mDatabaseHelper;


    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TABLE, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TABLE + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDatabaseHelper = new MoviesDatabaseHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match){
            case MOVIES:
                returnCursor = db.query(MoviesEntry.TABLE_NAME,
                        new String[]{
                                MoviesEntry.COLUMN_MOVIE_ID,
                                MoviesEntry.COLUMN_MOVIE_TITLE,
                                MoviesEntry.COLUMN_MOVIE_DATE,
                                MoviesEntry.COLUMN_MOVIE_SYNOPSIS,
                                MoviesEntry.COLUMN_MOVIE_RATING,
                                MoviesEntry.COLUMN_MOVIE_POSTER
                        },
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
       final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

       int match = sUriMatcher.match(uri);
       Uri returnUri;

       switch (match){
           case MOVIES:
               long id = db.insert(MoviesEntry.TABLE_NAME, null, contentValues);
               if (id > 0){
                   returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
               }else {
                   throw new SQLException("Failed to insert row into " + uri);
               }
               break;
           default:
               throw new UnsupportedOperationException("Unknown Uri: " + uri);
       }

       getContext().getContentResolver().notifyChange(uri, null);

       return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int moviesDeleted;

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(MoviesEntry.TABLE_NAME, "movieID=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
