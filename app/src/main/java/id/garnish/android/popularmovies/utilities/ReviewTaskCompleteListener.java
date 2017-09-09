package id.garnish.android.popularmovies.utilities;

import id.garnish.android.popularmovies.models.Review;

public interface ReviewTaskCompleteListener {
    void onReviewTaskCompleted(Review[] reviews);
}
