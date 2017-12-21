package android.example.com.movies;

import android.content.Context;
import android.database.Cursor;
import android.example.com.movies.database.MoviesContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Pritam on 18-12-2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {

    private Context mContext;
    private Cursor mCursor;

    final private ListItemClickListener mOnClickListener;

    public FavoriteAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_favorite, parent, false);

        view.setFocusable(true);

        return new FavoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteHolder holder, int position) {

        mCursor.moveToPosition(position);

        int movieIdIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
        int titleIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE);
        int synopsisIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS);

        String movieId = mCursor.getString(movieIdIndex);
        String movieTitle = mCursor.getString(titleIndex);
        String movieSynopsis = mCursor.getString(synopsisIndex);

        holder.itemView.setTag(movieId);
        holder.mTextViewMovieTitle.setText(movieTitle);
        holder.mTextViewMovieSynopsis.setText(movieSynopsis);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public class FavoriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTextViewMovieTitle;
        TextView mTextViewMovieSynopsis;

        public FavoriteHolder(View itemView) {
            super(itemView);

            mTextViewMovieTitle = itemView.findViewById(R.id.textView_movie_title);
            mTextViewMovieSynopsis = itemView.findViewById(R.id.textView_movie_synopsis);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
