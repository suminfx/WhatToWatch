package com.sumin.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sumin.movies.adapters.FavouriteMoviesAdapter;
import com.sumin.movies.data.FavouriteMovieEntry;
import com.sumin.movies.utils.NetworkUtils;
import com.sumin.movies.utils.SimpleUtils;
import com.sumin.movies.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteMoviesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Views
    @BindView(R.id.recyclerViewFavouriteMovies)
    RecyclerView mRecyclerViewFavouriteMovies;
    @BindView(R.id.textViewWarningNoFavouriteMovies)
    TextView mTextViewWarningNoFavouriteMovies;

    //Data
    private MainViewModel mMainViewModel;
    private FavouriteMoviesAdapter mFavouriteMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);
        ButterKnife.bind(this);
        setDrawer();
        setTitle(R.string.my_favourite);
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mFavouriteMoviesAdapter = new FavouriteMoviesAdapter(this, new FavouriteMoviesAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Intent intent = new Intent(FavouriteMoviesActivity.this, DetailMovieActivity.class);
                intent.putExtra(SimpleUtils.EXTRA_CAME_FROM, SimpleUtils.VALUE_FAVOURITE_FILMS);
                intent.putExtra(SimpleUtils.EXTRA_MOVIE_ID, mFavouriteMoviesAdapter.getFavouriteMovieByPosition(position).getMovieId());
                startActivity(intent);
            }

            @Override
            public void onPosterLongClick(int position) {
                deleteMovieByPosition(position);
            }
        });
        mRecyclerViewFavouriteMovies.setAdapter(mFavouriteMoviesAdapter);
        if (mFavouriteMoviesAdapter.getItemCount() == 0) {
            showWarning(true);
        }
        mRecyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, SimpleUtils.getCountOfMoviesOnWidthOfScreen(this)));
        mMainViewModel.getFavouriteMovies().observe(this, new Observer<List<FavouriteMovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovieEntry> favouriteMovieEntries) {
                if (favouriteMovieEntries == null || favouriteMovieEntries.isEmpty()) {
                    showWarning(true);
                } else {
                    showWarning(false);
                }
                mFavouriteMoviesAdapter.setMovies(favouriteMovieEntries);
            }
        });
    }

    private void showWarning(boolean show) {
        if (show) {
            mTextViewWarningNoFavouriteMovies.setVisibility(View.VISIBLE);
        } else {
            mTextViewWarningNoFavouriteMovies.setVisibility(View.GONE);
        }
    }

    private void deleteMovieByPosition(final int position) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle(R.string.alert_title_delete_from_favourite)
                .setMessage(R.string.alert_message_delete_from_favourite)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int movieId = mFavouriteMoviesAdapter.getFavouriteMovieByPosition(position).getMovieId();
                        FavouriteMovieEntry favouriteMovieEntry = mMainViewModel.getFavouriteMovieByMovieId(movieId);
                        mMainViewModel.deleteFavouriteMovie(favouriteMovieEntry);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
}
