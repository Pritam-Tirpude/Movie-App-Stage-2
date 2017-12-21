package android.example.com.movies;



public class MovieData {

    String mId;
    String mPosterPath;
    String mMovieTitle;
    String mReleaseDate;
    String mPlotSynopsis;
    String mMovieVoteAverage;
    String mMovieBackDropPath;

    public MovieData(String id, String posterPath, String movieTitle, String releaseDate,
                     String plotSynopsis, String movieVoteAverage, String movieBackDropPath) {
        mId = id;
        mPosterPath = posterPath;
        mMovieTitle = movieTitle;
        mReleaseDate = releaseDate;
        mPlotSynopsis = plotSynopsis;
        mMovieVoteAverage = movieVoteAverage;
        mMovieBackDropPath = movieBackDropPath;
    }
}
