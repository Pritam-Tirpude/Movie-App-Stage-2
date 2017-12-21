package android.example.com.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pritam on 13-12-2017.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MoviewReviewHolder>{

    List<MovieReviewData> mReviews;

    public MovieReviewsAdapter(List<MovieReviewData> reviews) {
        mReviews = reviews;
    }
    @Override
    public MoviewReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_review_items;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MoviewReviewHolder moviewReviewHolder = new MoviewReviewHolder(view);

        return moviewReviewHolder;
    }

    @Override
    public void onBindViewHolder(MoviewReviewHolder holder, int position) {
        MovieReviewData reviewData = mReviews.get(position);
        holder.bindReview(reviewData);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class MoviewReviewHolder extends RecyclerView.ViewHolder{

        private MovieReviewData mReviewData;

        TextView mAuthorTextView;
        TextView mContentTextView;
        TextView mAuthorLabelTextView;

        public MoviewReviewHolder(View itemView) {
            super(itemView);

            mAuthorTextView = itemView.findViewById(R.id.review_author_text_view);
            mContentTextView = itemView.findViewById(R.id.review_content_text_view);
            mAuthorLabelTextView = itemView.findViewById(R.id.author_label);
        }

        public void bindReview(MovieReviewData review) {
            mReviewData = review;
            mAuthorTextView.setText(mReviewData.getAuthor());
            mContentTextView.setText(mReviewData.getContent());
        }

    }
}
