package id.garnish.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import id.garnish.android.popularmovies.data.MoviesContract.*;

public class MoviesProvider extends ContentProvider {

    public static final String LOG = MoviesProvider.class.getSimpleName();

    private static final int MOVIES = 100;

    private static final int MOVIES_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);

        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_ID);
    }

    private MoviesDbHelper moviesDbHelper;

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = moviesDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                cursor = database.query(FavoriteListEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case (MOVIES_ID):
                selection = FavoriteListEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };

                cursor = database.query(FavoriteListEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return FavoriteListEntry.CONTENT_LIST_TYPE;
            case MOVIES_ID:
                return FavoriteListEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return insertMovie(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values) {
        String title = values.getAsString(FavoriteListEntry.COLUMN_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Movie requires a title");
        }

        SQLiteDatabase database = moviesDbHelper.getWritableDatabase();

        long id = database.insert(FavoriteListEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = moviesDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                rowsDeleted = database.delete(FavoriteListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_ID:
                selection = FavoriteListEntry._ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowsDeleted = database.delete(FavoriteListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
