package id.garnish.android.popularmovies.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Movie;
import id.garnish.android.popularmovies.models.Review;
import id.garnish.android.popularmovies.models.Trailer;
import id.garnish.android.popularmovies.utilities.ReviewTaskCompleteListener;
import id.garnish.android.popularmovies.utilities.TrailerTaskCompleteListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    @BindView(R.id.textview_original_title)
    TextView tvOriginalTitle;
    @BindView(R.id.textview_overview)
    TextView tvOverView;
    @BindView(R.id.textview_vote_average)
    TextView tvVoteAverage;
    @BindView(R.id.textview_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.imageview_poster)
    ImageView ivPoster;
    @BindView(R.id.reviews_recycler_view)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.trailers_recycler_view)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.no_trailer_view)
    TextView tvNoTrailer;
    @BindView(R.id.no_review)
    TextView tvNoReview;

    Movie movie;

    Trailer[] trailers;
    Review[] reviews;

    // TODO : make TrailerAdapter;
    // TODO : make ReviewAdapter;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detail_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBar = (CollapsingToolbarLayout) activity.findViewById(R.id.collapsing_toolbar);
                if (appBar != null) {
                    appBar.setTitle("");
                }
                movie = getArguments().getParcelable(getString(R.string.parcel_movie));
                assert movie != null;
            }
            if (movie != null) {
                getDataFromTMDb(movie.getId());
            }
        } // TODO : else : get savedInstanceState

        LinearLayoutManager trailerLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager reviewLayoutManager
                = new LinearLayoutManager(getContext());

        trailersRecyclerView.setLayoutManager(trailerLayoutManager);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

    }

    private void getDataFromTMDb(String id) {
        if (isNetworkAvailable()) {
            ReviewTaskCompleteListener reviewTaskCompleteListener
                    = new ReviewTaskCompleteListener() {
                @Override
                public void onReviewTaskCompleted(Review[] reviews) {
//                    TODO : setAdaper for ReviewAdapter
                }
            };

            TrailerTaskCompleteListener trailerTaskCompleteListener
                    = new TrailerTaskCompleteListener() {
                @Override
                public void onTrailerTaskCompleted(Trailer[] trailers) {
//                    TODO : setAdapter for TrailerADapter
                }
            };
//            TODO : FetchTrailers
//            TODO : FetchReviews
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
