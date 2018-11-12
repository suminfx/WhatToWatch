package com.sumin.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sumin.movies.adapters.MoviesAdapter;
import com.sumin.movies.pojo.Movie;
import com.sumin.movies.utils.MovieJSONUtils;
import com.sumin.movies.utils.NetworkUtils;
import com.sumin.movies.utils.SimpleUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<JSONObject> {

    //Data
    private MoviesAdapter mMoviesAdapter;
    public static final ArrayList<Movie> MOVIES = new ArrayList<>();
    public static final String EXTRA_MOVIE_POSITION = "position";
    private int mMethodOfSorting = NetworkUtils.POPULARITY;
    private boolean mIsLoading = false;
    private int mPage = 1;

    //Download data
    private LoaderManager mLoaderManager;
    private static final int LOADER_MOVIES_ID = 155;
    private URL mLastUsedURL;
    private final String EXTRA_NUMBER_OF_PAGE = "page";
    private final String EXTRA_INIT_LOADING = "was loaded";
    private boolean mWasLoaded = false;
    private boolean mShouldClearData = false;

    //Views
    @BindView(R.id.swipeRecyclerViewMovies)
    SwipeRefreshLayout mSwipeRecyclerViewMovies;
    @BindView(R.id.recyclerViewMovies)
    RecyclerView mRecyclerViewMovies;
    @BindView(R.id.progressBarLoading)
    ProgressBar mProgressBarLoading;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prepareNavigationDrawer();

        //Restore page
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_NUMBER_OF_PAGE) && savedInstanceState.containsKey(EXTRA_INIT_LOADING)) {
            mPage = savedInstanceState.getInt(EXTRA_NUMBER_OF_PAGE);
            mWasLoaded = savedInstanceState.getBoolean(EXTRA_INIT_LOADING);
        }
        mLoaderManager = LoaderManager.getInstance(this);
        mSwipeRecyclerViewMovies.setColorSchemeResources(R.color.colorAccent);
        mRecyclerViewMovies.setLayoutManager(new GridLayoutManager(this, SimpleUtils.getCountOfMoviesOnWidthOfScreen(this)));
        mMoviesAdapter = new MoviesAdapter(this, MOVIES, new MoviesAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
                intent.putExtra(EXTRA_MOVIE_POSITION, position);
                intent.putExtra(SimpleUtils.EXTRA_CAME_FROM, SimpleUtils.VALUE_MOST_POPULAR_ACTIVITY);
                startActivity(intent);
            }
        });
        mRecyclerViewMovies.setAdapter(mMoviesAdapter);
        mMoviesAdapter.setOnReachEndOfListListener(new MoviesAdapter.OnReachEndOfListListener() {
            @Override
            public void onReachEnd() {
                if (!mIsLoading) {
                    loadDataFromURL(NetworkUtils.buildURLMethodOfSort(mMethodOfSorting, ++mPage));
                }
            }
        });

        //Getting data
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SimpleUtils.EXTRA_SORT_BY)) {
            mMethodOfSorting = intent.getIntExtra(SimpleUtils.EXTRA_SORT_BY, 0);
            mMoviesAdapter.clearMovies();
        }
        if (mMethodOfSorting == NetworkUtils.POPULARITY) {
            setTitle(R.string.most_popular);
        } else if (mMethodOfSorting == NetworkUtils.TOP_RATED) {
            setTitle(R.string.top_rated);
        }
        if (mMoviesAdapter.getItemCount() == 0) {
            loadDataFromURL(NetworkUtils.buildURLMethodOfSort(mMethodOfSorting));
        }
        mSwipeRecyclerViewMovies.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLastUsedURL != null && !mWasLoaded) {
                    loadDataFromURL(mLastUsedURL);
                }
                mSwipeRecyclerViewMovies.setRefreshing(false);
            }
        });
    }

    private void prepareNavigationDrawer() {
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_NUMBER_OF_PAGE, mPage);
        outState.putBoolean(EXTRA_INIT_LOADING, mWasLoaded);
    }


    private void loadDataFromURL(URL url) {
        mLastUsedURL = url;
        if (url == null || !checkInternetConnection()) {
            return;
        }
        Loader<ArrayList<Movie>> loader = mLoaderManager.getLoader(LOADER_MOVIES_ID);
        Bundle bundle = new Bundle();
        bundle.putString(NetworkUtils.KEY_URL, url.toString());
        if (loader == null) {
            mLoaderManager.initLoader(LOADER_MOVIES_ID, bundle, this);
        } else {
            mLoaderManager.restartLoader(LOADER_MOVIES_ID, bundle, this);
        }
    }

    private void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new NetworkUtils.JSONLoader(this, bundle, new NetworkUtils.JSONLoader.LoadingProgressListener() {
            @Override
            public void onLoadingStart() {
                mIsLoading = true;
                mProgressBarLoading.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        mProgressBarLoading.setVisibility(View.GONE);
        int loaderId = loader.getId();
        switch (loaderId) {
            case LOADER_MOVIES_ID:
                if (jsonObject != null) {
                    mWasLoaded = true;
                    ArrayList<Movie> movies = MovieJSONUtils.getMoviesFromJSON(jsonObject);
                    if (mShouldClearData) {
                        mMoviesAdapter.clearMovies();
                        mShouldClearData = false;
                    }
                    mMoviesAdapter.addMovies(movies);
                } else {
                    showToast(getResources().getString(R.string.slow_internet_connection_warning));
                }
                break;
        }
        mIsLoading = false;
        mLoaderManager.destroyLoader(LOADER_MOVIES_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }


    private boolean checkInternetConnection() {
        if (!NetworkUtils.isInternetConnection(this)) {
            showToast(getResources().getString(R.string.warning_no_internet_connection));
            return false;
        }
        return true;
    }
}
