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

import id.garnish.android.popularmovies.models.Trailer;

public class FetchTrailersTask extends AsyncTask<String, Void, Trailer[]> {

    private static final String TAG = FetchTrailersTask.class.getSimpleName();

    private final String apiKey;

    private TrailerTaskCompleteListener taskCompleteListener;

    public FetchTrailersTask(TrailerTaskCompleteListener taskCompleteListener, String apiKey) {
        this.taskCompleteListener = taskCompleteListener;
        this.apiKey = apiKey;
    }

    @Override
    protected Trailer[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailersJsonStr = null;

        try {
            URL url = buildUrl(params[0]);

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

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                return null;
            }

            trailersJsonStr = builder.toString();
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
            return getTrailersDataFromJson(trailersJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return new Trailer[0];
    }

    @Override
    protected void onPostExecute(Trailer[] trailers) {
        super.onPostExecute(trailers);

        taskCompleteListener.onTrailerTaskCompleted(trailers);
    }

    private URL buildUrl(String id) throws MalformedURLException {
        final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/videos?";

        final String API_KEY_PARAM = "api_key";

        Uri uri = Uri.parse(TMDB_BASE_URL);

        Uri builtUri = uri.buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        return new URL(builtUri.toString());
    }

    private Trailer[] getTrailersDataFromJson(String trailersJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ID = "id";
        final String TAG_KEY = "key";

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray resultsArray = trailersJson.getJSONArray(TAG_RESULTS);

        Trailer[] trailers = new Trailer[resultsArray.length()];

        for (int i =0; i < resultsArray.length(); i++) {
            trailers[i] = new Trailer();

            JSONObject trailerInfo = resultsArray.getJSONObject(i);

            trailers[i].setId(trailerInfo.getString(TAG_ID));
            trailers[i].setKey(trailerInfo.getString(TAG_KEY));
        }

        return trailers;
    }
}
