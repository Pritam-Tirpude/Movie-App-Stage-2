package android.example.com.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    private ProgressBar mLoadingIndicator;

    private String movieSort;

    private GridView mGridView;

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String LIFE_KEY = "callbacks";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.loading_indicator);

        mGridView = findViewById(R.id.movie_grid);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LIFE_KEY)) {
                int position = savedInstanceState.getInt(LIFE_KEY);
                mGridView.smoothScrollToPosition(position);
            }
        }

        setupSharedPreference();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }


    private void makeMovieSearchQuery(String movieUrl) {
        URL fetchJsonResponse = NetworkUtils.buildUrl(movieUrl);
        new MovieAsyncTask().execute(fetchJsonResponse);
    }

    private void gridView(final MovieData[] movie) {
        final String[] posterPaths = new String[movie.length];
        final String[] movieTitle = new String[movie.length];
        final String[] releaseDate = new String[movie.length];
        final String[] plotSynopsis = new String[movie.length];
        final String[] voteAverage = new String[movie.length];
        final String[] movieId = new String[movie.length];

        for (int i = 0; i < movie.length; i++) {
            posterPaths[i] = "http://image.tmdb.org/t/p/w342/" + movie[i].mPosterPath;
            movieTitle[i] = movie[i].mMovieTitle;
            releaseDate[i] = movie[i].mReleaseDate;
            plotSynopsis[i] = movie[i].mPlotSynopsis;
            voteAverage[i] = movie[i].mMovieVoteAverage;
            movieId[i] = movie[i].mId;
        }

        GridView movieGridView = findViewById(R.id.movie_grid);
        MovieAdapter movieAdapter = new MovieAdapter(MainActivity.this, posterPaths);
        movieGridView.setAdapter(movieAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra("MOVIE_TITLE", movieTitle[position]);
                intent.putExtra("RELEASE_DATE", releaseDate[position]);
                intent.putExtra("SYNOPSIS", plotSynopsis[position]);
                intent.putExtra("VOTE_AVERAGE", voteAverage[position]);
                intent.putExtra("POSTER", posterPaths[position]);
                intent.putExtra("MOVIEID",movieId[position]);
                startActivity(intent);
            }
        });
    }

    public void setupSharedPreference(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sortMovies(sharedPreferences);
    }

    public void sortMovies(SharedPreferences sharedPreference){
        movieSort = sharedPreference.getString(getString(R.string.preference_movie_choice_value),
                getString(R.string.preference_pop_value));

        if (movieSort.equals(getString(R.string.preference_top_value))){
                String topRatedMovies = "https://api.themoviedb.org/3/movie/top_rated?api_key=API_KEY";
                makeMovieSearchQuery(topRatedMovies);
        }else if (movieSort.equals(R.string.preference_pop_value)) {
            String popularMovies = "http://api.themoviedb.org/3/movie/popular?api_key=API_KEY";
            makeMovieSearchQuery(popularMovies);
        } else {
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=API_KEY";
            makeMovieSearchQuery(MOVIE_BASE_URL);
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }





    private void showErrorMessage() {
        Context context = MainActivity.this;
        String errorMessage = getResources().getString(R.string.Error_message);
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int currentposition = mGridView.getFirstVisiblePosition();
        outState.putInt(LIFE_KEY, currentposition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int currentpostion = savedInstanceState.getInt(LIFE_KEY);
        mGridView.setSelection(currentpostion);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        if (selectedItem == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if(selectedItem == R.id.action_favorite){
            Intent favoriteIntent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(favoriteIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.preference_movie_choice_value))) {
            sortMovies(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }



    public class MovieAsyncTask extends AsyncTask<URL, Void, MovieData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieData[] doInBackground(URL... params) {

            Context context = MainActivity.this;
            URL searchUrl = params[0];
            String jsonResponse;

            try {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                    return MovieJsonUtils.getMovieFromJson(MainActivity.this, jsonResponse);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieData[] movieData) {
            super.onPostExecute(movieData);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                gridView(movieData);
            } else {
                showErrorMessage();
            }
        }
    }
}
