package com.sumin.movies.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;

import com.sumin.movies.pojo.Movie;

@Entity(tableName = "movie")
public class FavouriteMovieEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int movieId;
    private String movieTitle;
    private String movieOriginalTitle;
    private String overview;
    private double voteAverage;
    private String smallPosterURL;
    private String largePosterURL;
    private String videosInfoUri;
    private String reviewsInfoUri;
    private String releaseDate;

    public FavouriteMovieEntry(int id, int movieId, String movieTitle, String movieOriginalTitle, String overview, double voteAverage, String smallPosterURL, String largePosterURL, String videosInfoUri, String reviewsInfoUri, String releaseDate) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieOriginalTitle = movieOriginalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.smallPosterURL = smallPosterURL;
        this.largePosterURL = largePosterURL;
        this.videosInfoUri = videosInfoUri;
        this.reviewsInfoUri = reviewsInfoUri;
        this.releaseDate = releaseDate;
    }

    @Ignore
    public FavouriteMovieEntry(int movieId, String movieTitle, String movieOriginalTitle, String overview, double voteAverage, String smallPosterURL, String largePosterURL, String videosInfoUri, String reviewsInfoUri, String releaseDate) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieOriginalTitle = movieOriginalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.smallPosterURL = smallPosterURL;
        this.largePosterURL = largePosterURL;
        this.videosInfoUri = videosInfoUri;
        this.reviewsInfoUri = reviewsInfoUri;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle;
    }

    public void setMovieOriginalTitle(String movieOriginalTitle) {
        this.movieOriginalTitle = movieOriginalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getSmallPosterURL() {
        return smallPosterURL;
    }

    public void setSmallPosterURL(String smallPosterURL) {
        this.smallPosterURL = smallPosterURL;
    }

    public String getLargePosterURL() {
        return largePosterURL;
    }

    public void setLargePosterURL(String largePosterURL) {
        this.largePosterURL = largePosterURL;
    }

    public String getVideosInfoUri() {
        return videosInfoUri;
    }

    public void setVideosInfoUri(String videosInfoUri) {
        this.videosInfoUri = videosInfoUri;
    }

    public String getReviewsInfoUri() {
        return reviewsInfoUri;
    }

    public void setReviewsInfoUri(String reviewsInfoUri) {
        this.reviewsInfoUri = reviewsInfoUri;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
