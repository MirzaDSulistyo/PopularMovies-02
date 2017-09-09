package id.garnish.android.popularmovies.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import id.garnish.android.popularmovies.BuildConfig;
import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Movie;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = MovieAdapter.class.getSimpleName();

    private Movie[] movies;
    private Context context;

    private ListItemClickListener listItemClickListener;

    public interface ListItemClickListener {
        void onListItemCLick(Movie[] mMovies, int clickedItemIndex);
    }

    public MovieAdapter(Context context, Movie[] movies, ListItemClickListener listItemClickListener) {
        this.context= context;
        this.movies = movies;
        this.listItemClickListener = listItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        View view = layoutInflater.inflate(R.layout.display_movie, parent, false);
        viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        configureViewHolder((MovieViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.length;
        }
        return 0;
    }

    private void configureViewHolder(MovieViewHolder movieViewHolder, int position) {
        Movie movie = movies[position];

        String imageUrl = movie.getPosterPath();

        if (!imageUrl.equals("")) {
            Picasso.with(context)
                    .load(context.getString(R.string.arg_image_url) + "/w342" + movie.getPosterPath() + "?api_key=" + BuildConfig.TMDB_API_KEY)
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.searching)
                    .into(movieViewHolder.poster);
        }
    }

    private class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView poster;

        private MovieViewHolder(View itemView) {
            super(itemView);

            poster = (ImageView) itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listItemClickListener.onListItemCLick(movies, clickedPosition);
        }
    }
}
