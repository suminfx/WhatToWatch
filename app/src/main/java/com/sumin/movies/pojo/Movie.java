package com.sumin.movies.pojo;

import com.sumin.movies.utils.NetworkUtils;

public class Movie {

    public static final int MIN_VOTE_COUNT = 100;

    private static final String MOVIE_POSTERS_BASE_URI = "http://image.tmdb.org/t/p/";
    public static final int SMALL_IMAGE_SIZE = 185;
    public static final int BIG_IMAGE_SIZE = 780;
    private static final String SMALL_IMAGE_PATH = "w" + SMALL_IMAGE_SIZE;
    private static final String BIG_IMAGE_PATH = "w" + BIG_IMAGE_SIZE;

    private int movieId;
    private String movieTitle;
    private String movieOriginalTitle;
    private String overview;
    private int voteCount;
    private boolean isVideo;
    private double voteAverage;
    private String smallPosterURL;
    private String largePosterURL;
    private String videosInfoUri;
    private String reviewsInfoUri;
    private int popularity;
    private String releaseDate;
    private boolean isFavourite;

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getVideosInfoUri() {
        return videosInfoUri;
    }

    public String getReviewsInfoUri() {
        return reviewsInfoUri;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getSmallPosterURL() {
        return smallPosterURL;
    }

    public String getLargePosterURL() {
        return largePosterURL;
    }

    public int getPopularity() {
        return popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    private Movie(){}

    public static class Builder {

        Movie movie = new Movie();

        public Builder setMovieId(int movieId) {
            movie.movieId = movieId;
            movie.videosInfoUri = NetworkUtils.buildUriToLoadVideoInfoByMovieId(movieId);
            movie.reviewsInfoUri = NetworkUtils.buildUriToLoadReviewsInfoByMovieId(movieId);
            return this;
        }

        public Builder setMovieTitle(String movieTitle) {
            movie.movieTitle = movieTitle;
            return this;
        }

        public Builder setMovieOriginalTitle(String movieOriginalTitle) {
            movie.movieOriginalTitle = movieOriginalTitle;
            return this;
        }

        public Builder setOverview(String overview) {
            movie.overview = overview;
            return this;
        }

        public Builder setPosterPath(String posterPath) {
            movie.largePosterURL = MOVIE_POSTERS_BASE_URI + BIG_IMAGE_PATH + posterPath;
            movie.smallPosterURL = MOVIE_POSTERS_BASE_URI + SMALL_IMAGE_PATH + posterPath;
            return this;
        }

        public Builder setVoteCount(int voteCount) {
            movie.voteCount = voteCount;
            return this;
        }

        public Builder setVideo(boolean isVideo) {
            movie.isVideo = isVideo;
            return this;
        }

        public Builder setVoteAverage(double voteAverage) {
            movie.voteAverage = voteAverage;
            return this;
        }

        public Builder setPopularity(int popularity) {
            movie.popularity = popularity;
            return this;
        }

        public Builder setReleaseDate(String releaseDate) {
            movie.releaseDate = releaseDate;
            return this;
        }

        public Movie build() {
            return movie;
        }
    }
}
