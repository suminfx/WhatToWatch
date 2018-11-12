package com.sumin.movies.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {FavouriteMovieEntry.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String DB_NAME = "movie.db";
    private static final Object LOCK = new Object();
    private static MovieDatabase movieDatabase;

    public static MovieDatabase getInstance(Context context) {
        if (movieDatabase == null) {
            synchronized (LOCK) {
                movieDatabase = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME).build();
            }
        }
        return movieDatabase;
    }

    public abstract MovieDao movieDao();
}
