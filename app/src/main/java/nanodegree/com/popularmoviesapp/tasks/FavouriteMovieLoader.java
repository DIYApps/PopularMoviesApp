package nanodegree.com.popularmoviesapp.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.data.MovieData;
import nanodegree.com.popularmoviesapp.data.MoviesContract;
import nanodegree.com.popularmoviesapp.utils.L;

public class FavouriteMovieLoader extends AsyncTaskLoader<ArrayList<MovieData>> {

    private ArrayList<MovieData> movieDataArrayList;
    public FavouriteMovieLoader(Context context) {
        super(context);
    }
    private static final String TAG = "FavouriteMovieLoader::";

    @Override
    protected void onStartLoading() {

        if(movieDataArrayList != null){
            L.d(TAG+"onStartLoading  returning cache data." );
            deliverResult(movieDataArrayList);
        }else{
            L.d(TAG+"onStartLoading loading new data");
            forceLoad();
        }
        super.onStartLoading();
    }

    @Override
    public ArrayList<MovieData> loadInBackground() {

        Cursor cursor =  null;
        ArrayList<MovieData> movieList = new ArrayList<>();
        try {
            ContentResolver contentResolver = getContext().getContentResolver();
             cursor = contentResolver.query(MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }
            while (cursor.moveToNext()) {
                MovieData movieData = new MovieData();
                movieData.setAdult((cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.IS_ADULT)) == 1));
                movieData.setVideo((cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.VIDEO)) == 1));
                movieData.setBackdropPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.BACKDROP_PATH)));
                movieData.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.ORIGINAL_LANGUAGE)));
                movieData.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.ORIGINAL_TITLE)));
                movieData.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.TITLE)));
                movieData.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.OVERVIEW)));
                movieData.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.POSTER_PATH)));
                movieData.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.RELEASE_DATE)));
                movieData.setVoteCount(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.VOTE_COUNT)));
                movieData.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIES_ID))));
                movieData.setPopularity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIES_ID))));
                movieData.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.VOTE_AVERAGE))));
                movieList.add(movieData);
            }
        }
        finally {
            if(null != cursor){
                cursor.close();
            }
        }
        return movieList;
    }

    @Override
    public void deliverResult(ArrayList<MovieData> data) {
        movieDataArrayList =  data;
        super.deliverResult(data);
    }
}
