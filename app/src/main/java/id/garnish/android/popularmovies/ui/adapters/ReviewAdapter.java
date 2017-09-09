package id.garnish.android.popularmovies.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Review;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = ReviewAdapter.class.getSimpleName();

    private Review[] reviews;

    public ReviewAdapter(Review[] reviews) {
        this.reviews = reviews;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        View view = layoutInflater.inflate(R.layout.display_review, parent, false);
        viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        configureViewHolder((ReviewViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.length;
        }
        return 0;
    }

    private void configureViewHolder(ReviewViewHolder reviewViewHolder, int position) {
        Review review = reviews[position];

        reviewViewHolder.textViewAuthor.setText(review.getAuthor());
        reviewViewHolder.textViewReview.setText(review.getContent());
    }

    private class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAuthor;
        TextView textViewReview;

        private ReviewViewHolder(View itemView) {
            super(itemView);

            textViewAuthor = (TextView) itemView.findViewById(R.id.author_text);
            textViewReview = (TextView) itemView.findViewById(R.id.review_text);
        }
    }
}
