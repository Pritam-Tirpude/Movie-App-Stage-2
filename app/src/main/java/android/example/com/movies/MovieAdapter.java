package android.example.com.movies;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;



public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mNumIcons;


    public MovieAdapter(Context context, String[] numIcons) {
        super();
        mContext = context;
        mNumIcons = numIcons;
    }

    @Override
    public int getCount() {
        return mNumIcons.length;
    }

    @Override
    public Object getItem(int position) {
        return mNumIcons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ImageView imageView;
        if (view == null){
            imageView = new ImageView(mContext);
            view = imageView;
        }else {
            imageView = (ImageView) view;
        }

        Picasso.with(mContext)
                .load(mNumIcons[position])
                .into(imageView);

        return view;
    }
}
