package com.cynixadmin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cynixadmin.R;
import com.cynixadmin.models.MoviesImage;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesIconAdapter extends RecyclerView.Adapter<MoviesIconAdapter.iconViewHolder>{

    private static final String TAG = "MoviesIconAdapter";
    private Context mContext;
    private ArrayList<MoviesImage> mMovieIcon;


    public MoviesIconAdapter(Context mContext, ArrayList<MoviesImage> mMovieIcon) {
        this.mContext = mContext;
        this.mMovieIcon = mMovieIcon;
    }

    @Override
    public MoviesIconAdapter.iconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_icon_grid, parent, false);
        MoviesIconAdapter.iconViewHolder viewHolder = new MoviesIconAdapter.iconViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesIconAdapter.iconViewHolder holder, int position) {
        try {
            holder.bindMovieIcon(mMovieIcon.get(position));
        }catch (NullPointerException e){
            Log.e(TAG, "onBindViewHolder: "+e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mMovieIcon.size();
    }

    public class iconViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieIcon)ImageView mIcon;
        @BindView(R.id.iconDescription) TextView mDescription;
        @BindView(R.id.iconCategory) TextView mIconCategory;

        public iconViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mContext = itemView.getContext();
        }

        public void bindMovieIcon(MoviesImage moviesImage){
            Picasso.get().load(moviesImage.getImage()).fit().into(mIcon);
            mDescription.setText(moviesImage.getDescription());
            mIconCategory.setText(moviesImage.getCategory());
        }
    }
}
