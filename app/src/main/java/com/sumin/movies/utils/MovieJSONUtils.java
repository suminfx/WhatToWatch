package com.sumin.movies.utils;

import android.support.annotation.NonNull;

import com.sumin.movies.pojo.Movie;
import com.sumin.movies.pojo.Review;
import com.sumin.movies.pojo.VideoTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieJSONUtils {

    private static final String KEY_RESULTS = "results";

    private static final String KEY_MOVIE_ID = "id";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_IS_VIDEO = "video";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POPULARITY = "popularity";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_OVERVIEW = "overview";

    private static final String KEY_VIDEO_NAME = "name";
    private static final String KEY_VIDEO_KEY = "key";

    private static final String KEY_REVIEW_AUTHOR = "author";
    private static final String KEY_REVIEW_CONTENT = "content";

    public static ArrayList<Movie> getMoviesFromJSON(@NonNull JSONObject jsonObject) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray moviesJSONArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < moviesJSONArray.length(); i++) {
                JSONObject jsonObjectMovie = moviesJSONArray.getJSONObject(i);
                Movie.Builder builder = new Movie.Builder();
                builder.setMovieId(jsonObjectMovie.getInt(KEY_MOVIE_ID))
                        .setMovieOriginalTitle(jsonObjectMovie.getString(KEY_ORIGINAL_TITLE))
                        .setMovieTitle(jsonObjectMovie.getString(KEY_TITLE))
                        .setOverview(jsonObjectMovie.getString(KEY_OVERVIEW))
                        .setPopularity(jsonObjectMovie.getInt(KEY_POPULARITY))
                        .setPosterPath(jsonObjectMovie.getString(KEY_POSTER_PATH))
                        .setReleaseDate(jsonObjectMovie.getString(KEY_RELEASE_DATE))
                        .setVideo(jsonObjectMovie.getBoolean(KEY_IS_VIDEO))
                        .setVoteAverage(jsonObjectMovie.getDouble(KEY_VOTE_AVERAGE))
                        .setVoteCount(jsonObjectMovie.getInt(KEY_VOTE_COUNT));
                Movie movie = builder.build();
                if (!movie.getOverview().isEmpty() && !movie.getReleaseDate().isEmpty() && !movie.getSmallPosterURL().isEmpty() && !movie.getLargePosterURL().isEmpty() && movie.getVoteCount() >= Movie.MIN_VOTE_COUNT) {
                    movies.add(movie);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static ArrayList<VideoTrailer> getVideosFromJSON(JSONObject jsonObject) {
        ArrayList<VideoTrailer> videosKeys = new ArrayList<>();
        try {
            JSONArray jsonArrayVideos = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArrayVideos.length(); i++) {
                JSONObject videoJSON = jsonArrayVideos.getJSONObject(i);
                String name = videoJSON.getString(KEY_VIDEO_NAME);
                String key = videoJSON.getString(KEY_VIDEO_KEY);
                videosKeys.add(new VideoTrailer(name, key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videosKeys;
    }

    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            JSONArray jsonArrayReviews = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArrayReviews.length(); i++) {
                JSONObject reviewJSON = jsonArrayReviews.getJSONObject(i);
                String author = reviewJSON.getString(KEY_REVIEW_AUTHOR);
                String content = reviewJSON.getString(KEY_REVIEW_CONTENT);
                reviews.add(new Review(author, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
