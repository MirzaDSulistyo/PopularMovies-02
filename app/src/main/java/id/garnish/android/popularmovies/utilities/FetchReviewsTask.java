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

import id.garnish.android.popularmovies.models.Review;

public class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {

    private static final String TAG = FetchReviewsTask.class.getSimpleName();

    private final String apiKey;

    private AsyncTaskCompleteListener taskCompleteListener;

    public FetchReviewsTask(AsyncTaskCompleteListener taskCompleteListener, String apiKey) {
        this.taskCompleteListener = taskCompleteListener;
        this.apiKey = apiKey;
    }

    @Override
    protected Review[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewsJsonStr = null;

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

            reviewsJsonStr = builder.toString();
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
            return getReviewsDataFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return new Review[0];
    }

    @Override
    protected void onPostExecute(Review[] reviews) {
        super.onPostExecute(reviews);

        taskCompleteListener.onReviewTaskCompleted(reviews);
    }

    private URL buildUrl(String id) throws MalformedURLException {
        final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews?";

        final String API_KEY_PARAM = "api_key";

        Uri uri = Uri.parse(TMDB_BASE_URL);

        Uri builtUri = uri.buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        return new URL(builtUri.toString());
    }

    private Review[] getReviewsDataFromJson(String reviewsJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ID = "id";
        final String TAG_AUTHOR = "author";
        final String TAG_CONTENT = "content";
        final String TAG_URL = "url";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray resultsArray = reviewsJson.getJSONArray(TAG_RESULTS);

        Review[] reviews = new Review[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            reviews[i] = new Review();

            JSONObject reviewInfo = resultsArray.getJSONObject(i);

            reviews[i].setId(reviewInfo.getString(TAG_ID));
            reviews[i].setAuthor(reviewInfo.getString(TAG_AUTHOR));
            reviews[i].setContent(reviewInfo.getString(TAG_CONTENT));
            reviews[i].setUrl(reviewInfo.getString(TAG_URL));
        }

        return reviews;
    }
}
