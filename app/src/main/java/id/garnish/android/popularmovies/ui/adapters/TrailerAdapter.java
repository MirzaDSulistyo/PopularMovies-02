package id.garnish.android.popularmovies.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import id.garnish.android.popularmovies.R;
import id.garnish.android.popularmovies.models.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = TrailerAdapter.class.getSimpleName();

    private Trailer[] trailers;
    private Context context;

    public TrailerAdapter(Context context, Trailer[] trailers) {
        this.context = context;
        this.trailers = trailers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        View view = layoutInflater.inflate(R.layout.display_trailer, parent, false);
        viewHolder = new TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        configureViewHolder((TrailerViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (trailers != null) {
            return trailers.length;
        }
        return 0;
    }

    private void configureViewHolder(TrailerViewHolder trailerViewHolder, int position) {
        Trailer trailer = trailers[position];

        String key = trailer.getKey();
        String thumbnailURL = "http://img.youtube.com/vi/".concat(key).concat("/hqdefault.jpg");

        Picasso.with(context)
                .load(thumbnailURL)
                .error(R.drawable.not_found)
                .placeholder(R.drawable.example_movie_poster)
                .into(trailerViewHolder.trailerImage);
    }

    private class TrailerViewHolder extends RecyclerView.ViewHolder {

        ImageView trailerImage;

        private TrailerViewHolder(View itemView) {
            super(itemView);

            trailerImage = (ImageView) itemView.findViewById(R.id.trailer_image);
        }
    }
}
