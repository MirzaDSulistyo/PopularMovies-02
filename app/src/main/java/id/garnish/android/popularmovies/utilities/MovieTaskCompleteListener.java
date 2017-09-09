package id.garnish.android.popularmovies.utilities;

import id.garnish.android.popularmovies.models.Movie;

public interface MovieTaskCompleteListener {
    void onAsyncTaskCompleted(Movie[] movies);
}
