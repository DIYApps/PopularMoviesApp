package nanodegree.com.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nanodegree.com.popularmoviesapp.utils.L;

public class MovieContentProvider extends ContentProvider {

    private MovieDBHelper mMovieDbHelper;
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mMovieDbHelper = new MovieDBHelper(context);
        return true;
    }

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH, MOVIE);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH+"/#", MOVIE_WITH_ID);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor = null;
        try {
            final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
            int movie = sUriMatcher.match(uri);
            switch (movie) {
                case MOVIE:
                    retCursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return retCursor;
                default:
                    break;
            }
        }
        catch (Exception e){
            L.e("Error in Querying the db " + e.getLocalizedMessage());
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        Uri insertUri = null;
        try {
            int movie = sUriMatcher.match(uri);
            if (movie == MOVIE) {
                SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
                long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    insertUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, id);
                    L.d("insert:: insertUri " + insertUri);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            }
        }
        catch (Exception e){
            L.e("Exception in inserting " + e.getLocalizedMessage());
        }
        return insertUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {

        int deletedRowCount = 0;
        try {
            int movie = sUriMatcher.match(uri);
            L.d("delete():: uri==" + uri);
            L.d("delete():: movie==" + movie);
            SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

            switch (movie) {
                case MOVIE:
                    deletedRowCount = db.delete(MoviesContract.MovieEntry.TABLE_NAME, null, null);
                    break;
                case MOVIE_WITH_ID:
                    String id = uri.getPathSegments().get(1);
                    deletedRowCount = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                            MoviesContract.MovieEntry.MOVIES_ID + "=?", new String[]{id});
                    break;
                default:
                   break;
            }
            L.d("delete() number of rows delete " + deletedRowCount);
            if (deletedRowCount > 0) {
                getContext().getContentResolver().notifyChange(MoviesContract.MovieEntry.CONTENT_URI, null);
            }
        }
        catch (Exception e){
            L.e("Exception in deleting " + e.getLocalizedMessage());
        }
        return deletedRowCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        return 0;
    }
}
