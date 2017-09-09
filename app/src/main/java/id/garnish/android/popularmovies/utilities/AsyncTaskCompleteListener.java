package id.garnish.android.popularmovies.utilities;

import id.garnish.android.popularmovies.models.Movie;
import id.garnish.android.popularmovies.models.Review;
import id.garnish.android.popularmovies.models.Trailer;

public interface AsyncTaskCompleteListener {
    void onAsyncTaskCompleted(Movie[] movies);

    void onReviewTaskCompleted(Review[] reviews);

    void onTrailerTaskCompleted(Trailer[] trailers);
}
