package nanodegree.com.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movieDB.db";
    private static final int VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME +"("
                + MoviesContract.MovieEntry._ID +"  INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoviesContract.MovieEntry.BACKDROP_PATH + " TEXT,"
                + MoviesContract.MovieEntry.GENRE_ID + " TEXT, "
                + MoviesContract.MovieEntry.IS_ADULT + " INTEGER, "
                + MoviesContract.MovieEntry.MOVIES_ID + " TEXT UNIQUE NOT NULL, "
                + MoviesContract.MovieEntry.ORIGINAL_LANGUAGE +" TEXT, "
                + MoviesContract.MovieEntry.ORIGINAL_TITLE +" TEXT, "
                + MoviesContract.MovieEntry.OVERVIEW +" TEXT, "
                + MoviesContract.MovieEntry.POPULARITY +" TEXT, "
                + MoviesContract.MovieEntry.POSTER_PATH +" TEXT, "
                + MoviesContract.MovieEntry.RELEASE_DATE +" TEXT, "
                + MoviesContract.MovieEntry.TITLE +" TEXT NOT NULL, "
                + MoviesContract.MovieEntry.VIDEO +" INTEGER, "
                + MoviesContract.MovieEntry.VOTE_AVERAGE +" TEXT, "
                + MoviesContract.MovieEntry.VOTE_COUNT +" INTEGER);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
