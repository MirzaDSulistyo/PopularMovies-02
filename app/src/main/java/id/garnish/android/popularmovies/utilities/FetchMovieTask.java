package id.garnish.android.popularmovies.utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import id.garnish.android.popularmovies.models.Movie;

public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

    private static final String TAG = FetchMovieTask.class.getSimpleName();

    private final String apiKey;

    private final MovieTaskCompleteListener taskCompleteListener;

    public FetchMovieTask(MovieTaskCompleteListener taskCompleteListener, String apiKey) {
        this.taskCompleteListener = taskCompleteListener;
        this.apiKey = apiKey;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Holds data returned from the API
        String moviesJsonStr = null;

        try {
            URL url = buildUrl(params);

            // Start connecting to get JSON
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine())  != null) {
                // Adds '\n' at last line if not already there.
                // This supposedly makes it easier to debug.
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                return null;
            }

            moviesJsonStr = builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error : ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing stream : ", e);
                }
            }
        }

        try {
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return new Movie[0];
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);

        taskCompleteListener.onAsyncTaskCompleted(movies);
    }

    private URL buildUrl(String[] parameters) throws MalformedURLException {
        final String TMDB_BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular?";
        final String TMDB_BASE_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated?";

        final String API_KEY_PARAM = "api_key";

        final String popular = "popular";
        final String top_rated = "top_rated";

        Uri uri = null;

        if (parameters[0].equals(top_rated)) {
            uri = Uri.parse(TMDB_BASE_URL_TOP_RATED);
        } else {
            uri = Uri.parse(TMDB_BASE_URL_POPULAR);
        }

        Uri builtUri = uri.buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        return new URL(builtUri.toString());
    }

    private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // JSON tags
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";
        final String TAG_BACKDROP_PATH = "backdrop_path";
        final String TAG_ID = "id";

        // Get the array containing the movies found
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        // Create array of Movie objects that stores data from the JSON string
        Movie[] movies = new Movie[resultsArray.length()];

        // Traverse through movies one by one and get data
        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each object before it can be used
            movies[i] = new Movie();

            // Object contains all tags we're looking for
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            // Store data in movie object
            movies[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            movies[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            movies[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
            movies[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
            movies[i].setBackdropPath(movieInfo.getString(TAG_BACKDROP_PATH));
            movies[i].setId(movieInfo.getString(TAG_ID));
        }

        return movies;
    }
}
