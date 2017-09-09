package id.garnish.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.garnish.android.popularmovies.data.MoviesContract.*;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + FavoriteListEntry.TABLE_NAME + " (" +
                FavoriteListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteListEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteListEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteListEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteListEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteListEntry.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                FavoriteListEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteListEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}