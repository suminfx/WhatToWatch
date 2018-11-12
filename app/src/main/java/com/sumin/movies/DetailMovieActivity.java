package com.sumin.movies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sumin.movies.data.FavouriteMovieEntry;
import com.sumin.movies.databinding.ActivityDetailMovieBinding;
import com.sumin.movies.pojo.Movie;
import com.sumin.movies.pojo.Review;
import com.sumin.movies.pojo.VideoTrailer;
import com.sumin.movies.utils.MovieJSONUtils;
import com.sumin.movies.utils.NetworkUtils;
import com.sumin.movies.utils.SimpleUtils;
import com.sumin.movies.viewmodel.MainViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailMovieActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<JSONObject> {

    //Views
    @BindView(R.id.textViewOriginalTitle)
    TextView mTextViewOriginalTitle;
    @BindView(R.id.textViewOverview)
    TextView mTextViewOverview;
    @BindView(R.id.textViewReleaseDate)
    TextView mTextViewReleaseDate;
    @BindView(R.id.textViewRating)
    TextView mTextViewRating;
    @BindView(R.id.textViewVoteAverage)
    TextView mTextViewVoteAverage;
    @BindView(R.id.textViewTitle)
    TextView mTextViewTitle;
    @BindView(R.id.imageViewBigPoster)
    ImageView mImageViewBigPoster;
    @BindView(R.id.imageViewIconFavourite)
    ImageView mImageViewAddToFavourite;

    //For cache purpose
    private final String EXTRA_JSON_TRAILERS = "trailers";
    private final String EXTRA_JSON_REVIEWS = "reviews";
    private String mJsonTrailerAsString;
    private String mJsonReviewsAsString;

    //Download data
    private LoaderManager mLoaderManager;
    private static final int LOADER_TRAILERS_ID = 100;
    private static final int LOADER_REVIEWS_ID = 110;

    private MainViewModel mMainViewModel;
    private FavouriteMovieEntry mFavouriteMovie;
    private ActivityDetailMovieBinding mBinding;
    private boolean mIsAddedToFavourite = false;

    //Info about movie
    private int mMovieId;
    private String mMovieTitle;
    private String mMovieOriginalTitle;
    private String mOverview;
    private double mVoteAverage;
    private String mSmallPosterURL;
    private String mLargePosterURL;
    private String mVideosInfoUri;
    private String mReviewsInfoUri;
    private String mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_movie);
        ButterKnife.bind(this);
        setDrawer();
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(SimpleUtils.EXTRA_CAME_FROM)) {
            finish();
            return;
        }
        String cameFrom = intent.getStringExtra(SimpleUtils.EXTRA_CAME_FROM);
        switch (cameFrom) {
            case SimpleUtils.VALUE_MOST_POPULAR_ACTIVITY:
                int moviePosition = intent.getIntExtra(MainActivity.EXTRA_MOVIE_POSITION, -1);
                Movie movie = MainActivity.MOVIES.get(moviePosition);
                getDataFromMovie(movie);
                break;
            case SimpleUtils.VALUE_FAVOURITE_FILMS:
                int extraMovieId = intent.getIntExtra(SimpleUtils.EXTRA_MOVIE_ID, -1);
                mFavouriteMovie = mMainViewModel.getFavouriteMovieByMovieId(extraMovieId);
                if (mFavouriteMovie != null) {
                    getDataFromFavouriteMovie(mFavouriteMovie);
                } else {
                    finish();
                }
                break;
        }
        fillUI();
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_JSON_TRAILERS) && savedInstanceState.containsKey(EXTRA_JSON_REVIEWS)) {
            mJsonTrailerAsString = savedInstanceState.getString(EXTRA_JSON_TRAILERS);
            mJsonReviewsAsString = savedInstanceState.getString(EXTRA_JSON_REVIEWS);
            try {
                JSONObject jsonObjectTrailers = new JSONObject(mJsonTrailerAsString);
                JSONObject jsonObjectReviews = new JSONObject(mJsonReviewsAsString);
                setTrailersFromJSON(jsonObjectTrailers);
                setReviewsFromJSON(jsonObjectReviews);
            } catch (JSONException e) {
                e.printStackTrace();
                startDownLoadData();
            }
        } else {
            startDownLoadData();
        }
    }

    private void fillUI() {
        setRightImage();
        setTitle(mMovieTitle);
        mTextViewOriginalTitle.setText(mMovieOriginalTitle);
        mTextViewOverview.setText(mOverview);
        mTextViewReleaseDate.setText(mReleaseDate);
        mTextViewVoteAverage.setText(String.valueOf(mVoteAverage));
        mTextViewTitle.setText(mMovieTitle);
        mTextViewRating.setText(String.valueOf(mVoteAverage));
        Picasso.with(this).load(mLargePosterURL).placeholder(R.drawable.placeholder_large).into(mImageViewBigPoster);
    }

    private void setRightImage() {
        mFavouriteMovie = mMainViewModel.getFavouriteMovieByMovieId(mMovieId);
        mIsAddedToFavourite = mFavouriteMovie != null;
        if (mIsAddedToFavourite) {
            mImageViewAddToFavourite.setImageResource(R.mipmap.ic_remove_favourite);
        } else {
            mImageViewAddToFavourite.setImageResource(R.mipmap.ic_add_to_favourite);
        }
    }

    private void getDataFromMovie(Movie movie) {
        mMovieId = movie.getMovieId();
        mMovieTitle = movie.getMovieTitle();
        mMovieOriginalTitle = movie.getMovieOriginalTitle();
        mOverview = movie.getOverview();
        mVoteAverage = movie.getVoteAverage();
        mSmallPosterURL = movie.getSmallPosterURL();
        mLargePosterURL = movie.getLargePosterURL();
        mVideosInfoUri = movie.getVideosInfoUri();
        mReviewsInfoUri = movie.getReviewsInfoUri();
        mReleaseDate = movie.getReleaseDate();
    }

    private void getDataFromFavouriteMovie(FavouriteMovieEntry favouriteMovie) {
        mMovieId = favouriteMovie.getMovieId();
        mMovieTitle = favouriteMovie.getMovieTitle();
        mMovieOriginalTitle = favouriteMovie.getMovieOriginalTitle();
        mOverview = favouriteMovie.getOverview();
        mVoteAverage = favouriteMovie.getVoteAverage();
        mSmallPosterURL = favouriteMovie.getSmallPosterURL();
        mLargePosterURL = favouriteMovie.getLargePosterURL();
        mVideosInfoUri = favouriteMovie.getVideosInfoUri();
        mReviewsInfoUri = favouriteMovie.getReviewsInfoUri();
        mReleaseDate = favouriteMovie.getReleaseDate();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mJsonReviewsAsString != null && !mJsonReviewsAsString.isEmpty() && mJsonTrailerAsString != null && !mJsonTrailerAsString.isEmpty()) {
            outState.putString(EXTRA_JSON_TRAILERS, mJsonTrailerAsString);
            outState.putString(EXTRA_JSON_REVIEWS, mJsonReviewsAsString);
        }
    }

    private void startDownLoadData() {
        mLoaderManager = LoaderManager.getInstance(this);
        Loader<JSONObject> loaderTrailers = mLoaderManager.getLoader(LOADER_TRAILERS_ID);
        Bundle bundleTrailers = new Bundle();
        bundleTrailers.putString(NetworkUtils.KEY_URL, mVideosInfoUri);
        if (loaderTrailers == null) {
            mLoaderManager.initLoader(LOADER_TRAILERS_ID, bundleTrailers, this);
        } else {
            mLoaderManager.restartLoader(LOADER_TRAILERS_ID, bundleTrailers, this);
        }
        Loader<JSONObject> loaderReviews = mLoaderManager.getLoader(LOADER_TRAILERS_ID);
        Bundle bundleReviews = new Bundle();
        bundleReviews.putString(NetworkUtils.KEY_URL, mReviewsInfoUri);
        if (loaderReviews == null) {
            mLoaderManager.initLoader(LOADER_REVIEWS_ID, bundleReviews, this);
        } else {
            mLoaderManager.restartLoader(LOADER_REVIEWS_ID, bundleReviews, this);
        }
    }

    public void onClickChangeFavouriteState(View view) {
        String msg;
        if (!mIsAddedToFavourite) {
            mImageViewAddToFavourite.setImageResource(R.mipmap.ic_remove_favourite);
            mIsAddedToFavourite = true;
            mFavouriteMovie = new FavouriteMovieEntry(mMovieId, mMovieTitle, mMovieOriginalTitle, mOverview, mVoteAverage, mSmallPosterURL, mLargePosterURL, mVideosInfoUri, mReviewsInfoUri, mReleaseDate);
            mFavouriteMovie = addNewFavouriteMovie(mFavouriteMovie);
            msg = getResources().getString(R.string.added_to_favourite);
        } else {
            mImageViewAddToFavourite.setImageResource(R.mipmap.ic_add_to_favourite);
            mIsAddedToFavourite = false;
            if (mFavouriteMovie != null) {
                mMainViewModel.deleteFavouriteMovie(mFavouriteMovie);
            }
            msg = getResources().getString(R.string.deleted_from_favourite);
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new NetworkUtils.JSONLoader(this, bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        switch (loader.getId()) {
            case LOADER_TRAILERS_ID:
                mJsonTrailerAsString = jsonObject.toString();
                setTrailersFromJSON(jsonObject);
                mLoaderManager.destroyLoader(LOADER_TRAILERS_ID);
                break;
            case LOADER_REVIEWS_ID:
                mJsonReviewsAsString = jsonObject.toString();
                setReviewsFromJSON(jsonObject);
                mLoaderManager.destroyLoader(LOADER_REVIEWS_ID);
                break;
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }

    private void setReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> reviewList = MovieJSONUtils.getReviewsFromJSON(jsonObject);
        if (reviewList != null && !reviewList.isEmpty()) {
            mBinding.appBarDetailMovie.contentDetailMovie.reviews.parentLayoutReviews.setVisibility(View.VISIBLE);
            for (Review review : reviewList) {
                View view = LayoutInflater.from(this).inflate(R.layout.review_item, mBinding.appBarDetailMovie.contentDetailMovie.reviews.parentLayoutReviews, false);
                mBinding.appBarDetailMovie.contentDetailMovie.reviews.linearLayoutReviews.addView(view);
                TextView textViewAuthorName = view.findViewById(R.id.textViewAuthorName);
                TextView textViewReviewContent = view.findViewById(R.id.textViewReviewContent);
                textViewAuthorName.setText(review.getAuthorName());
                textViewReviewContent.setText(review.getTextReview());
            }
        }
    }

    private void setTrailersFromJSON(JSONObject jsonObject) {
        ArrayList<VideoTrailer> videoTrailerList = MovieJSONUtils.getVideosFromJSON(jsonObject);
        if (videoTrailerList != null && !videoTrailerList.isEmpty()) {
            mBinding.appBarDetailMovie.contentDetailMovie.trailers.parentLayoutTrailers.setVisibility(View.VISIBLE);
            for (final VideoTrailer videoTrailer : videoTrailerList) {
                View viewTrailerItem = LayoutInflater.from(this).inflate(R.layout.trailer_item, mBinding.appBarDetailMovie.contentDetailMovie.trailers.parentLayoutTrailers, false);
                View viewBorder = LayoutInflater.from(this).inflate(R.layout.border_between_components, mBinding.appBarDetailMovie.contentDetailMovie.trailers.parentLayoutTrailers, false);
                mBinding.appBarDetailMovie.contentDetailMovie.trailers.linearLayoutTrailers.addView(viewTrailerItem);
                mBinding.appBarDetailMovie.contentDetailMovie.trailers.linearLayoutTrailers.addView(viewBorder);
                TextView textViewVideoTitle = viewTrailerItem.findViewById(R.id.textViewTrailerTitle);
                textViewVideoTitle.setText(videoTrailer.getName());
                viewTrailerItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoTrailer.getUriToYoutube()));
                        startActivity(intent);
                    }
                });
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;
        switch (id) {
            case R.id.top_rated_movies:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(SimpleUtils.EXTRA_SORT_BY, NetworkUtils.TOP_RATED);
                break;
            case R.id.most_popular_movies:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(SimpleUtils.EXTRA_SORT_BY, NetworkUtils.POPULARITY);
                break;
            case R.id.my_favourite_movies:
                intent = new Intent(this, FavouriteMoviesActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private FavouriteMovieEntry addNewFavouriteMovie(FavouriteMovieEntry favouriteMovieEntry) {
        mMainViewModel.addFavouriteMovie(favouriteMovieEntry);
        return mMainViewModel.getFavouriteMovieByMovieId(favouriteMovieEntry.getMovieId());
    }
}
