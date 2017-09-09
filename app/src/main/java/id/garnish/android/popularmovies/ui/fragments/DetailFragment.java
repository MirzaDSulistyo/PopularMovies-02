package id.garnish.android.popularmovies.ui.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.garnish.android.popularmovies.BuildConfig;
import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.data.MoviesContract;
import id.garnish.android.popularmovies.models.Movie;
import id.garnish.android.popularmovies.models.Review;
import id.garnish.android.popularmovies.models.Trailer;
import id.garnish.android.popularmovies.ui.adapters.ReviewAdapter;
import id.garnish.android.popularmovies.ui.adapters.TrailerAdapter;
import id.garnish.android.popularmovies.utilities.DateTimeHelper;
import id.garnish.android.popularmovies.utilities.FetchReviewsTask;
import id.garnish.android.popularmovies.utilities.FetchTrailersTask;
import id.garnish.android.popularmovies.utilities.ReviewTaskCompleteListener;
import id.garnish.android.popularmovies.utilities.TrailerTaskCompleteListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements TrailerAdapter.ListItemClickListener, ReviewAdapter.ListItemCLickListener {

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

    Review[] reviewArray;
    Trailer[] trailerArray;

    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;

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
        setHasOptionsMenu(true);
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

        reviewAdapter = new ReviewAdapter(reviewArray, DetailFragment.this);
        trailerAdapter = new TrailerAdapter(getContext(), trailerArray, DetailFragment.this);

        reviewsRecyclerView.setAdapter(reviewAdapter);
        trailersRecyclerView.setAdapter(trailerAdapter);

        tvOriginalTitle.setText(movie.getOriginalTitle());

        String imageUrl = getString(R.string.arg_image_url) + "/w342" + movie.getPosterPath() + "?api_key?=" + BuildConfig.TMDB_API_KEY;

        Picasso.with(getContext())
                .load(movie.getPosterPath())
                .error(R.drawable.not_found)
                .placeholder(R.drawable.searching)
                .into(ivPoster);

        String overview = movie.getOverview();
        if (overview == null) {
            tvOverView.setTypeface(null, Typeface.ITALIC);
            overview = getString(R.string.no_summary_found);
        }

        tvOverView.setText(overview);
        tvVoteAverage.setText(movie.getDetailedVoteAverage());

        String releaseDate = movie.getReleaseDate();
        if (releaseDate != null) {
            try {
                releaseDate = DateTimeHelper.getLocalizedDate(getContext(),
                        releaseDate, movie.getDateFormat());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tvReleaseDate.setTypeface(null, Typeface.ITALIC);
            releaseDate = getString(R.string.no_release_date_found);
        }
        tvReleaseDate.setText(releaseDate);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.share).setVisible(true);
        MenuItem item = menu.findItem(R.id.fav);
        item.setVisible(true);
        item.setIcon(!isFavorite() ? R.drawable.fav_remove : R.drawable.fav_add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, movie.getOriginalTitle());
                intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=".concat(trailerArray[0].getKey()));
                startActivity(Intent.createChooser(intent, "Share Trailer!"));
                break;
            case R.id.fav:

                if (!isFavorite()) {
                    // add to favorite
                    item.setIcon(R.drawable.fav_add);
                    addFavorites();
                } else {
                    // remove from favorite
                    item.setIcon(R.drawable.fav_remove);
                    removeFromFavorites();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFavorite() {

        Cursor movieCursor = getContext().getContentResolver().query(
                MoviesContract.FavoriteListEntry.CONTENT_URI,
                new String[]{MoviesContract.FavoriteListEntry.COLUMN_MOVIE_ID},
                MoviesContract.FavoriteListEntry.COLUMN_MOVIE_ID + " = " + movie.getId(),
                null,
                null );

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

    private void addFavorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MoviesContract.FavoriteListEntry.COLUMN_MOVIE_ID, movie.getId());
                    movieValues.put(MoviesContract.FavoriteListEntry.COLUMN_TITLE, movie.getOriginalTitle());
                    movieValues.put(MoviesContract.FavoriteListEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                    movieValues.put(MoviesContract.FavoriteListEntry.COLUMN_OVERVIEW, movie.getOverview());
                    movieValues.put(MoviesContract.FavoriteListEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                    movieValues.put(MoviesContract.FavoriteListEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                    movieValues.put(MoviesContract.FavoriteListEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());

                    getContext().getContentResolver().insert(
                            MoviesContract.FavoriteListEntry.CONTENT_URI,
                            movieValues
                    );

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getContext(), "Success add favorite", Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MoviesContract.FavoriteListEntry.CONTENT_URI,
                            MoviesContract.FavoriteListEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null);
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getDataFromTMDb(String id) {
        if (isNetworkAvailable()) {
            ReviewTaskCompleteListener reviewTaskCompleteListener
                    = new ReviewTaskCompleteListener() {
                @Override
                public void onReviewTaskCompleted(Review[] reviews) {
                    reviewsRecyclerView.setAdapter(new ReviewAdapter(reviews, DetailFragment.this));
                    reviewArray = new Review[reviews.length];
                    for (int i = 0; i < reviews.length; i++) {
                        reviewArray[i] = new Review();

                        reviewArray[i].setId(reviews[i].getId());
                        reviewArray[i].setUrl(reviews[i].getUrl());
                        reviewArray[i].setContent(reviews[i].getContent());
                        reviewArray[i].setAuthor(reviews[i].getAuthor());
                    }
                    if (reviews.length == 0) {
                        reviewsRecyclerView.setVisibility(View.INVISIBLE);
                        tvNoReview.setVisibility(View.VISIBLE);
                    }
                }
            };

            TrailerTaskCompleteListener trailerTaskCompleteListener
                    = new TrailerTaskCompleteListener() {
                @Override
                public void onTrailerTaskCompleted(Trailer[] trailers) {
                    trailersRecyclerView.setAdapter(new TrailerAdapter(getContext(), trailers, DetailFragment.this));
                    trailerArray = new Trailer[trailers.length];
                    for (int i = 0; i < trailers.length; i++) {
                        trailerArray[i] = new Trailer();

                        trailerArray[i].setId(trailers[i].getId());
                        trailerArray[i].setKey(trailers[i].getKey());
                    }
                    if (trailers.length == 0) {
                        trailersRecyclerView.setVisibility(View.INVISIBLE);
                        tvNoTrailer.setVisibility(View.VISIBLE);
                    }
                }
            };
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(reviewTaskCompleteListener, BuildConfig.TMDB_API_KEY);
            fetchReviewsTask.execute(id);

            FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(trailerTaskCompleteListener, BuildConfig.TMDB_API_KEY);
            fetchTrailersTask.execute(id);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onListItemClick(Trailer[] mTrailers, int clickedItemIndex) {
        Trailer trailer = mTrailers[clickedItemIndex];
        String url = "https://www.youtube.com/watch?v=".concat(trailer.getKey());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onListItemClick(Review[] mReviews, int clickedItemIndex) {
        Review review = mReviews[clickedItemIndex];
        String url = review.getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
