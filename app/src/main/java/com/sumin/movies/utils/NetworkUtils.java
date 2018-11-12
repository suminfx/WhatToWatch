package com.sumin.movies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class NetworkUtils {

    public static final String KEY_URL = "url";
    private static final int REQUEST_TIMEOUT = 3000;

    private static final String MOVIE_DB_URI_AS_STRING = "https://api.themoviedb.org/3/movie/";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String VIDEO_PATH = "%s/videos";
    private static final String REVIEW_PATH = "%s/reviews";

    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_PAGE = "page";

    private static final String YOUTUBE_VIDEO_BASE_URI = "https://www.youtube.com/watch?v=";

    private static final String API_KEY = ""; //TODO: add your api key here

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    public static String buildUriToLoadVideoByKey(String key) {
        return YOUTUBE_VIDEO_BASE_URI + key;
    }

    public static String buildUriToLoadVideoInfoByMovieId(int id) {
        String baseUri = MOVIE_DB_URI_AS_STRING + String.format(VIDEO_PATH, String.valueOf(id));
        Uri uri = Uri.parse(baseUri).buildUpon().appendQueryParameter(PARAMS_API_KEY, API_KEY).build();
        return uri.toString();
    }

    public static String buildUriToLoadReviewsInfoByMovieId(int id) {
        String baseUri = MOVIE_DB_URI_AS_STRING + String.format(REVIEW_PATH, String.valueOf(id));
        Uri uri = Uri.parse(baseUri).buildUpon().appendQueryParameter(PARAMS_API_KEY, API_KEY).build();
        return uri.toString();
    }

    public static URL buildURLMethodOfSort(int sortBy, int page) {
        String uri = "";
        switch (sortBy) {
            case TOP_RATED:
                uri = MOVIE_DB_URI_AS_STRING + TOP_RATED_PATH;
                break;
            case POPULARITY:
                uri = MOVIE_DB_URI_AS_STRING + POPULAR_PATH;
                break;
        }
        Uri resultUri = Uri.parse(uri).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, Locale.getDefault().getLanguage())
                .appendQueryParameter(PARAMS_PAGE, String.valueOf(page)).build();
        try {
            return new URL(resultUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildURLMethodOfSort(int sortBy) {
        return buildURLMethodOfSort(sortBy, 1);
    }

    public static JSONObject getJSONFromURL(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(REQUEST_TIMEOUT);
            urlConnection.setConnectTimeout(REQUEST_TIMEOUT);
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder builderResult = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builderResult.append(line);
                line = reader.readLine();
            }
            return new JSONObject(builderResult.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private LoadingProgressListener loadingProgressListener;

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle, LoadingProgressListener loadingProgressListener) {
            super(context);
            this.bundle = bundle;
            this.loadingProgressListener = loadingProgressListener;
        }

        public interface LoadingProgressListener {
            void onLoadingStart();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (bundle == null || !bundle.containsKey(KEY_URL)) {
                return;
            }
            if (loadingProgressListener != null) {
                loadingProgressListener.onLoadingStart();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            String urlAsString = bundle.getString(KEY_URL);
            try {
                URL url = new URL(urlAsString);
                return NetworkUtils.getJSONFromURL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void setLoadingProgressListener(LoadingProgressListener loadingProgressListener) {
            this.loadingProgressListener = loadingProgressListener;
        }
    }

    public static boolean isInternetConnection(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }


}
