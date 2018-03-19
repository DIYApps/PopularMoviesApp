package nanodegree.com.popularmoviesapp.tasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.data.MoviesReview;
import nanodegree.com.popularmoviesapp.data.MoviesReviewData;
import nanodegree.com.popularmoviesapp.utils.JsonUtils;
import nanodegree.com.popularmoviesapp.utils.L;
import nanodegree.com.popularmoviesapp.utils.NetworkUtils;

public class MovieReviewLoader extends AsyncTaskLoader< ArrayList< MoviesReview > > {

    private static final String TAG = "MovieReviewLoader::";
    private final URL movieReviewURL;
    private ArrayList< MoviesReview > moviesReviews;
    public MovieReviewLoader( Context context, URL movieReviewURL ) {

        super( context );
        this.movieReviewURL = movieReviewURL;
    }
    @Override
    public ArrayList< MoviesReview > loadInBackground() {

        try {
            String result = NetworkUtils.getMoviesFromServer( movieReviewURL );
            L.d( TAG + "loadInBackground() URL = " + movieReviewURL.toString() );
            MoviesReviewData moviesTrailerData = JsonUtils.parseMovieReview( result );
            if ( null == moviesTrailerData ) {
                return null;
            }
            return moviesTrailerData.getResults();
        } catch ( IOException e ) {
            L.e( e.getMessage() );
            e.printStackTrace();
            return null;
        } catch ( JsonSyntaxException e ) {
            return null;
        }
    }
    @Override
    public void deliverResult( ArrayList< MoviesReview > data ) {

        moviesReviews = data;
        L.d( TAG + "deliverResult() data == null " + (data == null) );
        super.deliverResult( data );
    }
    @Override
    protected void onStartLoading() {

        super.onStartLoading();

        if ( null != moviesReviews ) {
            L.d( TAG + "onStartLoading()::cached result returned" );
            deliverResult( moviesReviews );
        } else {
            L.d( TAG + "onStartLoading()::called" );
            forceLoad();
        }
    }
}
