package id.garnish.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    private MoviesContract() {}

    public static final String CONTENT_AUTHORITY = "id.garnish.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class FavoriteListEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_MOVIE_ID = "idMovie";

        public static final String[] MOVIE_COLUMNS = {
                COLUMN_MOVIE_ID,
                COLUMN_TITLE,
                COLUMN_POSTER_PATH,
                COLUMN_OVERVIEW,
                COLUMN_VOTE_AVERAGE,
                COLUMN_RELEASE_DATE,
                COLUMN_BACKDROP_PATH
        };

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_TITLE = 1;
        public static final int COL_MOVIE_POSTER_PATH = 2;
        public static final int COL_MOVIE_OVERVIEW = 3;
        public static final int COL_MOVIE_VOTE_AVERAGE = 4;
        public static final int COL_MOVIE_RELEASE_DATE = 5;
        public static final int COL_MOVIE_BACKDROP_PATH = 6;

    }
}
