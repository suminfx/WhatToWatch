package com.sumin.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.sumin.movies.data.FavouriteMovieEntry;
import com.sumin.movies.data.MovieDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavouriteMovieEntry>> favouriteMovies;
    private static MovieDatabase movieDatabase;

    public MainViewModel(@NonNull Application application) {
        super(application);
        movieDatabase = MovieDatabase.getInstance(getApplication().getApplicationContext());
        favouriteMovies = movieDatabase.movieDao().loadAllFavouriteMovies();
    }

    public LiveData<List<FavouriteMovieEntry>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public void addFavouriteMovie(FavouriteMovieEntry favouriteMovieEntry) {
        new AddMovieTask().execute(favouriteMovieEntry);
    }

    public FavouriteMovieEntry getFavouriteMovieByMovieId(int movieId)  {
        try {
            return new GetMovieTask().execute(movieId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteFavouriteMovie(FavouriteMovieEntry favouriteMovieEntry) {
        new RemoveMovieTask().execute(favouriteMovieEntry);
    }

    private static class AddMovieTask extends AsyncTask<FavouriteMovieEntry, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovieEntry... favouriteMovieEntries) {
            if (favouriteMovieEntries != null && favouriteMovieEntries.length > 0) {
                movieDatabase.movieDao().addNewFavouriteMovie(favouriteMovieEntries[0]);
            }
            return null;
        }
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, FavouriteMovieEntry> {
        @Override
        protected FavouriteMovieEntry doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return movieDatabase.movieDao().getFavouriteMovieByMovieId(integers[0]);
            }
            return null;
        }
    }

    private static class RemoveMovieTask extends AsyncTask<FavouriteMovieEntry, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovieEntry... favouriteMovieEntries) {
            if (favouriteMovieEntries != null && favouriteMovieEntries.length > 0) {
                movieDatabase.movieDao().deleteFavouriteMovie(favouriteMovieEntries[0]);
            }
            return null;
        }
    }
}
