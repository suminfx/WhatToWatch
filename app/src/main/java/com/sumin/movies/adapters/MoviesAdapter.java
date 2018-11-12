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
import com.sumin.movies.pojo.Movie;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private Context context;
    private final ArrayList<Movie> movies;
    private OnPosterClickListener onPosterClickListener;
    private OnReachEndOfListListener onReachEndOfListListener;

    public MoviesAdapter(Context context, ArrayList<Movie> movies, OnPosterClickListener onPosterClickListener, OnReachEndOfListListener onReachEndOfListListener) {
        this.context = context;
        this.movies = movies;
        this.onPosterClickListener = onPosterClickListener;
        this.onReachEndOfListListener = onReachEndOfListListener;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies, OnPosterClickListener onPosterClickListener) {
        this.context = context;
        this.movies = movies;
        this.onPosterClickListener = onPosterClickListener;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    public interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    public interface OnReachEndOfListListener {
        void onReachEnd();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndOfListListener(OnReachEndOfListListener onReachEndOfListListener) {
        this.onReachEndOfListListener = onReachEndOfListListener;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder moviesViewHolder, int i) {
        Movie movie = movies.get(i);
        ImageView imageView = moviesViewHolder.imageViewSmallPoster;
        Uri uri = Uri.parse(movie.getSmallPosterURL());
        Picasso.with(context).load(uri).placeholder(android.R.drawable.alert_dark_frame).into(imageView);
        if (i == movies.size() - 1) {
            if (onReachEndOfListListener != null) {
                onReachEndOfListListener.onReachEnd();
            }
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public void addMovies(ArrayList<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public void clearMovies() {
        this.movies.clear();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewSmallPoster;

        public MoviesViewHolder(@NonNull View itemView) {
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
        }
    }
}
