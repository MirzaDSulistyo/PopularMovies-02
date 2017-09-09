package id.garnish.android.popularmovies.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.garnish.android.popularmovies.BuildConfig;
import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Movie;
import id.garnish.android.popularmovies.ui.fragments.DetailFragment;
import id.garnish.android.popularmovies.utilities.DateTimeHelper;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.toolImage) ImageView imageView;
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            Intent dataIntent = getIntent();
            movie = dataIntent.getParcelableExtra(getString(R.string.parcel_movie));
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            Picasso.with(this).load(getString(R.string.arg_image_url) + "/w500" + movie.getBackdropPath() + "?api_key=" + BuildConfig.TMDB_API_KEY)
                    .placeholder(R.drawable.searching)
                    .error(R.drawable.not_found)
                    .into(imageView);

        }

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.parcel_movie), movie);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

//        tvOriginalTitle.setText(movie.getOriginalTitle());
//
//        Picasso.with(this)
//                .load(movie.getPosterPath())
//                .error(R.drawable.not_found)
//                .placeholder(R.drawable.searching)
//                .into(ivPoster);
//
//        String overview = movie.getOverview();
//        if (overview == null) {
//            tvOverView.setTypeface(null, Typeface.ITALIC);
//            overview = getString(R.string.no_summary_found);
//        }
//
//        tvOverView.setText(overview);
//        tvVoteAverage.setText(movie.getDetailedVoteAverage());
//
//        String releaseDate = movie.getReleaseDate();
//        if (releaseDate != null) {
//            try {
//                releaseDate = DateTimeHelper.getLocalizedDate(this,
//                        releaseDate, movie.getDateFormat());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        } else {
//            tvReleaseDate.setTypeface(null, Typeface.ITALIC);
//            releaseDate = getString(R.string.no_release_date_found);
//        }
//        tvReleaseDate.setText(releaseDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
