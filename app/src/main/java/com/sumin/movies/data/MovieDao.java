package com.sumin.movies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie")
    LiveData<List<FavouriteMovieEntry>> loadAllFavouriteMovies();

    @Insert
    void addNewFavouriteMovie(FavouriteMovieEntry favouriteMovie);

    @Delete
    void deleteFavouriteMovie(FavouriteMovieEntry favouriteMovie);

    @Query("SELECT * FROM movie WHERE movieId = :movieId")
    FavouriteMovieEntry getFavouriteMovieByMovieId(int movieId);

}
