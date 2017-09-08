package id.garnish.android.popularmovies.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Movie;
import id.garnish.android.popularmovies.utilities.DateTimeHelper;

public class DetailActivity extends AppCompatActivity {

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

    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            Intent dataIntent = getIntent();
            movie = dataIntent.getParcelableExtra(getString(R.string.parcel_movie));
        }

        tvOriginalTitle.setText(movie.getOriginalTitle());

        Picasso.with(this)
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
                releaseDate = DateTimeHelper.getLocalizedDate(this,
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
}
