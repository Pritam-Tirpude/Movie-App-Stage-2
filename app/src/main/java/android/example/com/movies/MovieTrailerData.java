package android.example.com.movies;

/**
 * Created by Pritam on 11-12-2017.
 */

public class MovieTrailerData {

    String mTrailerId;
    String mTrailerKey;
    String mTrailerName;
    String mTrailerSite;
    String mTrailerSize;
    String mTrailerType;


    public MovieTrailerData(String trailerKey, String trailerName) {
        mTrailerKey = trailerKey;
        mTrailerName = trailerName;
    }

    public MovieTrailerData(){

    }

    public String getTrailerId() {
        return mTrailerId;
    }

    public String getTrailerKey() {
        return mTrailerKey;
    }

    public String getTrailerName() {
        return mTrailerName;
    }

    public String getTrailerSite() {
        return mTrailerSite;
    }

    public String getTrailerSize() {
        return mTrailerSize;
    }

    public String getTrailerType() {
        return mTrailerType;
    }

}
