package android.example.com.movies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pritam on 13-12-2017.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder>{

    public static final String TAG = MovieTrailerAdapter.class.getSimpleName();

    List<MovieTrailerData> mTrailers;

    public MovieTrailerAdapter(List<MovieTrailerData> trailers) {
        mTrailers = trailers;
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_video;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MovieTrailerViewHolder movieTrailerViewHolder = new MovieTrailerViewHolder(view);

        return movieTrailerViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        MovieTrailerData movieTrailerData = mTrailers.get(position);
        holder.bindTrailer(movieTrailerData);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder
     implements View.OnClickListener{

        private MovieTrailerData mTrailerData;

        ImageView mPlayImageView;
        TextView mTrailerNameTextView;

        public MovieTrailerViewHolder(View itemView) {
            super(itemView);

            mPlayImageView = itemView.findViewById(R.id.play_icon);
            mTrailerNameTextView = itemView.findViewById(R.id.movie_trailer_name_text_view);

            itemView.setOnClickListener(this);
        }

        public  void bindTrailer(MovieTrailerData trailerData){
            mTrailerData = trailerData;
            mTrailerNameTextView.setText(mTrailerData.getTrailerName());
        }

        @Override
        public void onClick(View view) {
            Context context = itemView.getContext();
            try {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mTrailerData.getTrailerKey()));
                context.startActivity(intent);
            }catch (ActivityNotFoundException e){
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + mTrailerData.getTrailerKey()));
                context.startActivity(intent);
            }

        }
    }
}
