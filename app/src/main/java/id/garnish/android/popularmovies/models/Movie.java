package id.garnish.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String originalTitle;
    private String posterPath;
    private String overview;
    private Double voteAverage;
    private String releaseDate;
    private String backdropPath;
    private String id;

    /**
     * Constructor for a Movie object
     */
    public Movie() {

    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setOverview(String overview) {
        if (!overview.equals("null")) {
            this.overview = overview;
        }
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        if (!releaseDate.equals("null")) {
            this.releaseDate = releaseDate;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getDateFormat() {
        return DATE_FORMAT;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        final String TMDB_POSTER_BASE_PATH = "https://image.tmdb.org/t/p/w185";

        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage()) + "/10";
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeValue(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(backdropPath);
        dest.writeString(id);
    }

    private Movie(Parcel parcel) {
        originalTitle = parcel.readString();
        posterPath = parcel.readString();
        overview = parcel.readString();
        voteAverage = (Double) parcel.readValue(Double.class.getClassLoader());
        releaseDate = parcel.readString();
        backdropPath = parcel.readString();
        id = parcel.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
