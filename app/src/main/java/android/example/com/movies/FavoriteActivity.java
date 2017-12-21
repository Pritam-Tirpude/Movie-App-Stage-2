package android.example.com.movies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.com.movies.database.MoviesContract;
import android.example.com.movies.database.MoviesDatabaseHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.textclassifier.TextClassification;
import android.widget.TextView;
import android.widget.Toast;

public class FavoriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        FavoriteAdapter.ListItemClickListener {

    private RecyclerView mRecyclerViewFavorites;
    private int mPosition = RecyclerView.NO_POSITION;
    private FavoriteAdapter mFavoriteAdapter;

    private Cursor mCursor;
    private SQLiteDatabase mDb;

    View mEmptyView;

    public static final int MOVIE_LOADER_ID = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        MoviesDatabaseHelper mDbHelper = new MoviesDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();
        mCursor = getDetails();

        mRecyclerViewFavorites = findViewById(R.id.favorite_recycler_view);
        mRecyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewFavorites.setHasFixedSize(true);

        mEmptyView = findViewById(R.id.empty_view);

        mFavoriteAdapter = new FavoriteAdapter(this, this);
        mRecyclerViewFavorites.setAdapter(mFavoriteAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                String id = (String) viewHolder.itemView.getTag();

                String stringId = id;
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);

                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, FavoriteActivity.this);

                Toast.makeText(FavoriteActivity.this, "Removed from list", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(mRecyclerViewFavorites);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, FavoriteActivity.this);
    }

    public static final String[] MOVIE_PROJECTION = {
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_DATE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER
    };

    @Override
    public void onListItemClick(int clickedItemIndex) {
        final String[] posterPaths = new String[mCursor.getCount()];
        final String[] movieTitle = new String[mCursor.getCount()];
        final String[] plotSynopsis = new String[mCursor.getCount()];
        final String[] voteAverage = new String[mCursor.getCount()];
        final String[] releaseDate = new String[mCursor.getCount()];
        final String[] movieId = new String[mCursor.getCount()];
        int i = 0;

        if (mCursor.moveToFirst()) {
            while (!mCursor.isAfterLast()) {
                movieId[i] = mCursor.getString(mCursor.getColumnIndex("movieID"));
                movieTitle[i] = mCursor.getString(mCursor.getColumnIndex("movieTitle"));
                plotSynopsis[i] = mCursor.getString(mCursor.getColumnIndex("movieSynopsis"));
                voteAverage[i] = mCursor.getString(mCursor.getColumnIndex("movieRating"));
                releaseDate[i] = mCursor.getString(mCursor.getColumnIndex("movieReleaseDate"));
                posterPaths[i] = "http://image.tmdb.org/t/p/w342/" + mCursor.getString(mCursor.getColumnIndex("posterLink"));
                mCursor.moveToNext();
                i++;
            }
        }

        Intent intent = new Intent(FavoriteActivity.this, MovieDetailActivity.class);
        intent.putExtra("TITLE", movieTitle[clickedItemIndex]);
        intent.putExtra("DATE", releaseDate[clickedItemIndex]);
        intent.putExtra("OVERVIEW", plotSynopsis[clickedItemIndex]);
        intent.putExtra("RATING", voteAverage[clickedItemIndex]);
        intent.putExtra("POSTER_PATH", posterPaths[clickedItemIndex]);
        intent.putExtra("MOVIE_ID", movieId[clickedItemIndex]);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    public Cursor getDetails() {
        MoviesDatabaseHelper moviesDatabaseHelper = new MoviesDatabaseHelper(FavoriteActivity.this);
        SQLiteDatabase db = moviesDatabaseHelper.getReadableDatabase();
        Cursor mReadCursor = db.query(
                MoviesContract.MoviesEntry.TABLE_NAME,
                MOVIE_PROJECTION,
                null,
                null,
                null,
                null,
                null);

        if (mReadCursor != null && mReadCursor.getCount() > 0) {
            return mReadCursor;
        }
        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_LOADER_ID:

                Uri movieUri = MoviesContract.MoviesEntry.CONTENT_URI;

                return new CursorLoader(this,
                        movieUri,
                        MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerViewFavorites.setVisibility(View.GONE);
        }else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerViewFavorites.setVisibility(View.VISIBLE);
            mFavoriteAdapter.swapCursor(data);
        }

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        mRecyclerViewFavorites.scrollToPosition(mPosition);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteAdapter.swapCursor(null);
    }

}
