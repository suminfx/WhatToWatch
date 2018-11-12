package com.sumin.movies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sumin.movies.R;
import com.sumin.movies.data.FavouriteMovieEntry;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.FavouriteMoviesViewHolder> {

    private Context context;
    private final List<FavouriteMovieEntry> movies;
    private OnPosterClickListener onPosterClickListener;

    public FavouriteMoviesAdapter(Context context, OnPosterClickListener onPosterClickListener) {
        this.context = context;
        this.movies = new ArrayList<>();
        this.onPosterClickListener = onPosterClickListener;
    }

    public FavouriteMoviesAdapter(Context context) {
        this.context = context;
        this.movies = new ArrayList<>();
    }

    public interface OnPosterClickListener {
        void onPosterClick(int position);
        void onPosterLongClick(int position);
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public FavouriteMovieEntry getFavouriteMovieByPosition(int position) {
        return this.movies.get(position);
    }

    @NonNull
    @Override
    public FavouriteMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new FavouriteMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteMoviesViewHolder moviesViewHolder, int i) {
        FavouriteMovieEntry movie = movies.get(i);
        ImageView imageView = moviesViewHolder.imageViewSmallPoster;
        Uri uri = Uri.parse(movie.getSmallPosterURL());
        Picasso.with(context).load(uri).placeholder(android.R.drawable.alert_dark_frame).into(imageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<FavouriteMovieEntry> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public void addMovies(List<FavouriteMovieEntry> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }


    public void clearMovies() {
        this.movies.clear();
    }

    class FavouriteMoviesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewSmallPoster;

        public FavouriteMoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }
}



