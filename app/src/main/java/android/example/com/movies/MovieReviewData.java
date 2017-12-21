package android.example.com.movies;

/**
 * Created by Pritam on 13-12-2017.
 */

public class MovieReviewData {

    String mReviewId;
    String mAuthor;
    String mContent;
    String mUrl;

    public MovieReviewData(String reviewId, String author, String content, String url) {
        mReviewId = reviewId;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public String getReviewId() {
        return mReviewId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }
}
