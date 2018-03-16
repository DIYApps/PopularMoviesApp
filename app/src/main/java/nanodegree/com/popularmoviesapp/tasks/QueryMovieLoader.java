package nanodegree.com.popularmoviesapp.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.data.MovieData;
import nanodegree.com.popularmoviesapp.data.MoviesContract;

public class QueryMovieLoader extends AsyncTaskLoader<Boolean> {

    private String id;
    public QueryMovieLoader(Context context , String  id) {
        super(context);
        this.id = id;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public Boolean loadInBackground() {

        Cursor cursor =  null;
        ArrayList<MovieData> movieDatas = new ArrayList<>();
        try {
            ContentResolver contentResolver = getContext().getContentResolver();
             cursor = contentResolver.query(MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                     MoviesContract.MovieEntry.MOVIES_ID +"=?",
                    new String[]{id},
                    null);
            if (cursor.getCount() == 0) {
                return false;
            }
        }
        finally {
            if(null != cursor){
                cursor.close();
            }
        }
        return true;
    }

    @Override
    public void deliverResult(Boolean result) {
        super.deliverResult(result);
    }
}
