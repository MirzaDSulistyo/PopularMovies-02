package id.garnish.android.popularmovies.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.garnish.android.popularmovies.BuildConfig;
import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Movie;
import id.garnish.android.popularmovies.ui.adapters.MovieAdapter;
import id.garnish.android.popularmovies.utilities.MovieTaskCompleteListener;
import id.garnish.android.popularmovies.utilities.FetchMovieTask;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

//    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.movie_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar loadingBar;

    Movie[] mMovies;

    private Menu menu;

    Parcelable movieState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getMoviesFromTMDb(getSortMethod());
        } else {
            movieState = savedInstanceState.getParcelable(getString(R.string.saved_movie_state));

            Parcelable[] parcelables = savedInstanceState.getParcelableArray(getString(R.string.saved_movie_list));
            if (parcelables != null) {
                int numMovieObjects = parcelables.length;
                Movie[] movies = new Movie[numMovieObjects];
                for (int i = 0; i < numMovieObjects; i++) {
                    movies[i] = (Movie) parcelables[i];
                }
                mMovies = movies;
                recyclerView.setAdapter(new MovieAdapter(MainActivity.this, movies, this));
                loadingBar.setVisibility(View.GONE);
            } else {
                getMoviesFromTMDb(getSortMethod());
            }
        }

        GridLayoutManager grid;
        if (findViewById(R.id.landscape_layout) == null) {
            grid = new GridLayoutManager(this, 2);
        } else {
            grid = new GridLayoutManager(this, 3);
        }

        recyclerView.setLayoutManager(grid);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(getString(R.string.saved_movie_list), mMovies);
        outState.putParcelable(getString(R.string.saved_movie_state), recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movieState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(movieState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;
        updateMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_popular :
                updateSharedPrefs(getString(R.string.tmdb_sort_popular));
                updateMenu(menu);
                getMoviesFromTMDb(getSortMethod());
                return true;
            case R.id.sort_top_rated :
                updateSharedPrefs(getString(R.string.tmdb_sort_top_rated));
                updateMenu(menu);
                getMoviesFromTMDb(getSortMethod());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateMenu(Menu menu) {
        String sortMethod = getSortMethod();

        if (sortMethod.equals(getString(R.string.tmdb_sort_popular))) {
            menu.findItem(R.id.sort_popular).setVisible(false);
            menu.findItem(R.id.sort_top_rated).setVisible(true);
        } else {
            menu.findItem(R.id.sort_popular).setVisible(true);
            menu.findItem(R.id.sort_top_rated).setVisible(false);
        }
    }

    private void getMoviesFromTMDb(String sortMethod) {
        if (isNetworkAvailable()) {
            MovieTaskCompleteListener taskCompleteListener = new MovieTaskCompleteListener() {
                @Override
                public void onAsyncTaskCompleted(Movie[] movies) {
                    loadingBar.setVisibility(View.GONE);
                    mMovies = movies;
                    recyclerView.setAdapter(new MovieAdapter(MainActivity.this, movies, MainActivity.this));
                }
            };
            FetchMovieTask fetchMovieTask = new FetchMovieTask(taskCompleteListener, BuildConfig.TMDB_API_KEY);
            fetchMovieTask.execute(sortMethod);
        } else {
            Snackbar.make(findViewById(R.id.movie_recycler_view), "No Connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }
    }

    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_popular));
    }

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    @Override
    public void onListItemCLick(Movie[] mMovies, int clickedItemIndex) {
        Movie movie = mMovies[clickedItemIndex];
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(getString(R.string.parcel_movie), movie);
        startActivity(intent);
    }
}
