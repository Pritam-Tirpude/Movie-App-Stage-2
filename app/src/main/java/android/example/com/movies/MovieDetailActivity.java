package android.example.com.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.example.com.movies.database.MoviesContract.MoviesEntry;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TAG = MovieDetailActivity.class.getSimpleName();

    String title;
    String releaseDate;
    String rating;
    String synopsis;
    String poster_path;
    String movieId;

    private Uri mCurrentUri;

    private TextView mMovieTitleTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMoviePlotSynopsis;
    private TextView mMovieVoteAverage;
    private ImageView mMoviePosterImage;
    private TextView mMovieReviewEmptyTextView;
    private TextView mMovieTrailerEmptyTextView;

    private ProgressBar mLoadingIndicatorReview;
    private ProgressBar mLoadingIndicatorTrailer;

    private ImageButton mFavoritesButton;

    private RecyclerView mReviewRecyclerView;
    private RecyclerView mTrailerRecyclerView;

    private MovieReviewsAdapter mReviewsAdapter;
    private MovieTrailerAdapter mTrailerAdapter;

    private MovieTrailerData mTrailerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMovieTitleTextView = findViewById(R.id.movie_title);
        mMovieReleaseDateTextView = findViewById(R.id.movie_release_date);
        mMoviePlotSynopsis = findViewById(R.id.movie_plot_synopsis);
        mMovieVoteAverage = findViewById(R.id.movie_vote_average);
        mMoviePosterImage = findViewById(R.id.movie_poster_image);
        mMovieReviewEmptyTextView = findViewById(R.id.review_text_empty_label);
        mMovieTrailerEmptyTextView = findViewById(R.id.trailer_empty_text_view_label);

        mLoadingIndicatorReview = findViewById(R.id.loading_indicator_review);
        mLoadingIndicatorTrailer = findViewById(R.id.loading_indicator_trailer);

        mFavoritesButton = findViewById(R.id.imageSaveButton);
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMovies();
                mFavoritesButton.setImageResource(R.drawable.ic_action_bookmark);
            }
        });

        mReviewRecyclerView = findViewById(R.id.review_recycler_view);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mReviewRecyclerView.setHasFixedSize(true);

        mTrailerRecyclerView = findViewById(R.id.trailer_recycler_view);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mTrailerRecyclerView.setHasFixedSize(true);

        Intent receiveIntent = getIntent();

        if (receiveIntent.hasExtra("MOVIE_TITLE")) {
            title = receiveIntent.getStringExtra("MOVIE_TITLE");
            mMovieTitleTextView.setText(title);
        }
        if (receiveIntent.hasExtra("RELEASE_DATE")) {
            releaseDate = receiveIntent.getStringExtra("RELEASE_DATE");
            mMovieReleaseDateTextView.setText(releaseDate);
        }
        if (receiveIntent.hasExtra("SYNOPSIS")) {
            synopsis = receiveIntent.getStringExtra("SYNOPSIS");
            mMoviePlotSynopsis.setText(synopsis);
        }
        if (receiveIntent.hasExtra("VOTE_AVERAGE")) {
            rating = receiveIntent.getStringExtra("VOTE_AVERAGE");
            mMovieVoteAverage.setText(rating + "/10");
        }
        if (receiveIntent.hasExtra("POSTER")) {
            poster_path = receiveIntent.getStringExtra("POSTER");
            Picasso.with(MovieDetailActivity.this)
                    .load(poster_path)
                    .into(mMoviePosterImage);
        }
        if (receiveIntent.hasExtra("MOVIEID")) {
            movieId = receiveIntent.getStringExtra("MOVIEID");
        }

        setMovieTrailer();
        setMovieReview();

        FavoriteIntent();

    }

    public void FavoriteIntent() {

        String movieTitle = null;
        String movieReleaseDate = null;
        String movieRating = null;
        String movieSynopsis = null;
        String moviePosterPath = null;
        String movieId = null;

        Intent favoriteIntent = getIntent();
        mCurrentUri = favoriteIntent.getData();

        if (favoriteIntent.hasExtra("TITLE")) {
            movieTitle = favoriteIntent.getStringExtra("TITLE");
            mMovieTitleTextView.setText(movieTitle);
        }
        if (favoriteIntent.hasExtra("DATE")) {
            movieReleaseDate = favoriteIntent.getStringExtra("DATE");
            mMovieReleaseDateTextView.setText(movieReleaseDate);
        }
        if (favoriteIntent.hasExtra("OVERVIEW")) {
            movieSynopsis = favoriteIntent.getStringExtra("OVERVIEW");
            mMoviePlotSynopsis.setText(movieSynopsis);
        }
        if (favoriteIntent.hasExtra("RATING")) {
            movieRating = favoriteIntent.getStringExtra("RATING");
            mMovieVoteAverage.setText(movieRating + "/10");
        }
        if (favoriteIntent.hasExtra("POSTER_PATH")) {
            moviePosterPath = favoriteIntent.getStringExtra("POSTER_PATH");
            Picasso.with(MovieDetailActivity.this)
                    .load(moviePosterPath)
                    .into(mMoviePosterImage);
        }
        if (favoriteIntent.hasExtra("MOVIE_ID")) {
            movieId = favoriteIntent.getStringExtra("MOVIE_ID");
        }

        if (movieId != null && movieTitle != null && movieReleaseDate != null &&
                movieSynopsis != null && moviePosterPath != null && movieRating != null) {
            mFavoritesButton.setImageResource(R.drawable.ic_action_bookmark);
            mFavoritesButton.setEnabled(false);
        } else {
            mFavoritesButton.setVisibility(View.VISIBLE);
        }
    }


    public void addMovies() {
        ContentValues values = new ContentValues();
        values.put(MoviesEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MoviesEntry.COLUMN_MOVIE_TITLE, title);
        values.put(MoviesEntry.COLUMN_MOVIE_DATE, releaseDate);
        values.put(MoviesEntry.COLUMN_MOVIE_RATING, rating);
        values.put(MoviesEntry.COLUMN_MOVIE_SYNOPSIS, synopsis);
        values.put(MoviesEntry.COLUMN_MOVIE_POSTER, poster_path);

        Uri uri = getContentResolver().insert(MoviesEntry.CONTENT_URI, values);

        String message = "Added to favorite list";

        if (uri != null) {
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        }

        finish();
    }

    public void setMovieReview() {
        String movieReviewLink = "http://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=API_KEY";
        URL movieReviewJSON = NetworkUtils.buildUrl(movieReviewLink);

        new ReviewAsyncTask().execute(movieReviewJSON);

    }

    public void setMovieTrailer() {
        String movieTrailerUrl = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=API_KEY";
        URL movieTrailerJSON = NetworkUtils.buildUrl(movieTrailerUrl);

        new TrailerAsyncTask().execute(movieTrailerJSON);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class TrailerAsyncTask extends AsyncTask<URL, Void, List<MovieTrailerData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicatorTrailer.setVisibility(View.VISIBLE);
            mMovieTrailerEmptyTextView.setVisibility(View.GONE);
        }

        @Override
        protected List<MovieTrailerData> doInBackground(URL... urls) {
            Context context = MovieDetailActivity.this;
            URL searchUrl = urls[0];
            String jsonResponse;

            try {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                    return MovieJsonUtils.getMovieTrailerFromjson(context, jsonResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieTrailerData> movieTrailerData) {
            super.onPostExecute(movieTrailerData);

            mLoadingIndicatorTrailer.setVisibility(View.GONE);

            if (movieTrailerData == null || movieTrailerData.size() == 0) {
                mTrailerRecyclerView.setVisibility(View.GONE);
                mMovieTrailerEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mTrailerRecyclerView.setVisibility(View.VISIBLE);
                mMovieTrailerEmptyTextView.setVisibility(View.GONE);
            }

            mTrailerAdapter = new MovieTrailerAdapter(movieTrailerData);
            mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        }
    }

    private class ReviewAsyncTask extends AsyncTask<URL, Void, List<MovieReviewData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicatorReview.setVisibility(View.VISIBLE);
            mMovieReviewEmptyTextView.setVisibility(View.GONE);
        }

        @Override
        protected List<MovieReviewData> doInBackground(URL... urls) {
            Context context = MovieDetailActivity.this;
            URL searchUrl = urls[0];
            String jsonResponse;

            try {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                    return MovieJsonUtils.getMovieReviewFromJson(context, jsonResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieReviewData> movieReviewData) {
            super.onPostExecute(movieReviewData);

            mLoadingIndicatorReview.setVisibility(View.GONE);

            if (movieReviewData == null || movieReviewData.size() == 0) {
                mReviewRecyclerView.setVisibility(View.GONE);
                mMovieReviewEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mReviewRecyclerView.setVisibility(View.VISIBLE);
                mMovieReviewEmptyTextView.setVisibility(View.GONE);
            }

            mReviewsAdapter = new MovieReviewsAdapter(movieReviewData);
            mReviewRecyclerView.setAdapter(mReviewsAdapter);
        }
    }
}
