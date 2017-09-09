package id.garnish.android.popularmovies.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Movie;

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

        if (getArguments() != null) {
            Movie movie = getArguments().getParcelable(getString(R.string.parcel_movie));
        }
    }
}
