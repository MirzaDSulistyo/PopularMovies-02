package id.garnish.android.popularmovies.utilities;

import id.garnish.android.popularmovies.models.Movie;

public interface AsyncTaskCompleteListener {
    void onAsyncTaskCompleted(Movie[] movies);
}
