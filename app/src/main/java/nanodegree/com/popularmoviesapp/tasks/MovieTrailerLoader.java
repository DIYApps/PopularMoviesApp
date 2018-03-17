package nanodegree.com.popularmoviesapp.tasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.data.MoviesTrailer;
import nanodegree.com.popularmoviesapp.data.MoviesTrailerData;
import nanodegree.com.popularmoviesapp.utils.JsonUtils;
import nanodegree.com.popularmoviesapp.utils.L;
import nanodegree.com.popularmoviesapp.utils.NetworkUtils;

public class MovieTrailerLoader extends AsyncTaskLoader<ArrayList<MoviesTrailer>> {

    private static final String TAG = "MovieTrailerLoader::";
    final URL movieTrailerURL;
    private ArrayList<MoviesTrailer> moviesTrailers;
    public MovieTrailerLoader(Context context, URL movietrailerURL) {
        super(context);
        this.movieTrailerURL = movietrailerURL;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if(null != moviesTrailers){
            L.d(TAG+"onStartLoading()::cached result returned");
            deliverResult(moviesTrailers);
        }else {
            L.d(TAG+"onStartLoading()::called");
            forceLoad();
        }
    }

    @Override
    public ArrayList<MoviesTrailer> loadInBackground() {
        try {
            String result = NetworkUtils.getMoviesFromServer(movieTrailerURL);
            L.d(TAG+"loadInBackground() URL = " + movieTrailerURL.toString());
            MoviesTrailerData moviesTrailerData = JsonUtils.parseMovieTrailer(result);
            if (null == moviesTrailerData) {
                return null;
            }
            L.d(TAG+"loadInBackground() done \n " +  result);
            return moviesTrailerData.getResults();
        } catch (IOException e) {
            L.e(e.getMessage());
            e.printStackTrace();
            return null;
        }
        catch (JsonSyntaxException e){
            return null;
        }
    }

    @Override
    public void deliverResult(ArrayList<MoviesTrailer> data) {
        moviesTrailers = data;
        L.d(TAG+"deliverResult() data == null " +  (data == null));
        super.deliverResult(data);
    }
}
