package com.sumin.movies.utils;

import android.content.Context;
import android.content.Intent;

import com.sumin.movies.FavouriteMoviesActivity;
import com.sumin.movies.MainActivity;
import com.sumin.movies.R;
import com.sumin.movies.pojo.Movie;

public class SimpleUtils {

    public static final String EXTRA_SORT_BY = "sort by";
    public static final String EXTRA_CAME_FROM = "came from";
    public static final String VALUE_MOST_POPULAR_ACTIVITY = "main";
    public static final String VALUE_FAVOURITE_FILMS = "favourite";
    public static final String EXTRA_MOVIE_ID = "id";

    public static int getCountOfMoviesOnWidthOfScreen(Context context) {
        int count = context.getResources().getConfiguration().screenWidthDp / Movie.SMALL_IMAGE_SIZE;
        if (count < 2) {
            count = 2;
        }
        return count;
    }
}
