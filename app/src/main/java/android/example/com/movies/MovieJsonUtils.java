package android.example.com.movies;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public final class MovieJsonUtils {

    public static final String TAG = MovieJsonUtils.class.getSimpleName();

    public static MovieData[] getMovieFromJson(Context context, String movieJsonStr)
    throws JSONException{
        MovieData[] parseMovieData = null;

        JSONObject baseJsonResponse = new JSONObject(movieJsonStr);

        JSONArray resultsArray = baseJsonResponse.getJSONArray("results");
        parseMovieData = new MovieData[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {

            String id;
            String posterPath;
            String movieTitle;
            String releaseDate;
            String plotSynopsis;
            String voteAverage;
            String backDropPath;

            JSONObject features = resultsArray.getJSONObject(i);

            id = features.getString("id");
            posterPath = features.getString("poster_path");
            movieTitle = features.getString("title");
            releaseDate = features.getString("release_date");
            plotSynopsis = features.getString("overview");
            voteAverage = features.getString("vote_average");
            backDropPath = features.getString("backdrop_path");

            parseMovieData[i] = new MovieData(id, posterPath, movieTitle, releaseDate, plotSynopsis,
                    voteAverage, backDropPath);
        }

        return parseMovieData;
    }
    public static List<MovieReviewData> getMovieReviewFromJson(Context context,String reviewJson){
        List<MovieReviewData> reviewsList = new ArrayList<>();

        try{
            JSONObject root = new JSONObject(reviewJson);

            JSONArray reviewsArray = root.getJSONArray("results");

            for (int i = 0; i < reviewsArray.length(); i++) {
                String id;
                String author;
                String content;
                String url;

                JSONObject review = reviewsArray.getJSONObject(i);

                id = review.getString("id");
                author = review.getString("author");
                content = review.getString("content");
                url = review.getString("url");

                MovieReviewData reviewData = new MovieReviewData(id, author, content, url);

                reviewsList.add(reviewData);
            }
        }catch (JSONException e){
            Log.e(TAG, "Problem parsing the reviews JSON results", e);
        }

        return reviewsList;
    }
    public static List<MovieTrailerData> getMovieTrailerFromjson(Context context, String trailerJsonString) {

        List<MovieTrailerData> trailerDataList = new ArrayList<>();

        try{
            JSONObject root = new JSONObject(trailerJsonString);

            JSONArray resultsArray = root.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {

                String id;
                String key;
                String name;
                String site;
                String size;
                String type;

                JSONObject movieTrailer = resultsArray.getJSONObject(i);

                id = movieTrailer.getString("id");
                key = movieTrailer.getString("key");
                name = movieTrailer.getString("name");
                site = movieTrailer.getString("site");
                size = movieTrailer.getString("size");
                type = movieTrailer.getString("type");

                trailerDataList.add(new MovieTrailerData(key, name));
            }
        }catch (JSONException e){
            Log.e(TAG, "Problem parsing the trailer JSON results", e);
        }
        return trailerDataList;
    }
}
