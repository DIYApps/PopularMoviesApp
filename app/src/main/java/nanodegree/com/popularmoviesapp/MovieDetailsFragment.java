package nanodegree.com.popularmoviesapp;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.squareup.picasso.Picasso;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import nanodegree.com.popularmoviesapp.data.MovieData;
import nanodegree.com.popularmoviesapp.data.MoviesContract;
import nanodegree.com.popularmoviesapp.data.MoviesReview;
import nanodegree.com.popularmoviesapp.data.MoviesTrailer;
import nanodegree.com.popularmoviesapp.tasks.MovieReviewLoader;
import nanodegree.com.popularmoviesapp.tasks.MovieTrailerLoader;
import nanodegree.com.popularmoviesapp.tasks.QueryMovieLoader;
import nanodegree.com.popularmoviesapp.utils.L;
import nanodegree.com.popularmoviesapp.utils.NetworkUtils;

public class MovieDetailsFragment extends Fragment
        implements MovieTrailersAdapter.OnItemClickListener {

    private static final String MOVIE_DATA_KEY = "movie_data";
    private static final String MOVIE_TRAILER_KEY = "movie_trailer";
    private static final String MOVIE_REVIEWS_KEY = "movie_reviews";
    private static final String MOVIE_TRAILER = "videos";
    private static final String MOVIE_REVIEWS = "reviews";
    private static final String TAG = MovieDetailsFragment.class.getName() + "::";
    private static final int QUERY_LOADER_ID = 2;
    private static final String IS_FAVOURITE_KEY = "isFavourite";
    private MovieData movieData;
    private ArrayList<MoviesTrailer> mMoviesTrailers;
    private ArrayList<MoviesReview> mMoviesReviews;
    private TextView mMovieTitleTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieOverviewTextView;
    private ImageView mPosterImageView;
    private RecyclerView mTrailerRecyclerView;
    private RecyclerView mReviewsRecyclerView;
    private MovieTrailersAdapter mMovieTrailersAdapter;
    private MovieReviewsAdapter mMovieReviewsAdapter;
    private static final int TRAILER_LOADER_ID = 0;
    private static final int REVIEWS_LOADER_ID = 1;
    private static final String APP_YOUTUBE_BASE_URL = "vnd.youtube:";
    private static final String WEB_YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private ToggleButton mAddToFavouriteButton;
    private boolean isMovieFavourite;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        mMovieOverviewTextView = (TextView) view.findViewById(R.id.tv_movie_overview);
        mMovieRatingTextView = (TextView) view.findViewById(R.id.tv_rating);
        mMovieReleaseDateTextView = (TextView) view.findViewById(R.id.tv_release_date);
        mMovieTitleTextView = (TextView) view.findViewById(R.id.tv_movie_title);
        mPosterImageView = (ImageView) view.findViewById(R.id.iv_movie_poster);
        mTrailerRecyclerView = (RecyclerView) view.findViewById(R.id.rv_movies_trailer);
        mReviewsRecyclerView = (RecyclerView) view.findViewById(R.id.rv_movies_reviews);
        mAddToFavouriteButton = (ToggleButton) view.findViewById(R.id.tb_add_to_fav);
        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(getActivity());
        mTrailerRecyclerView.setLayoutManager(trailersLayoutManager);
        mMovieTrailersAdapter = new MovieTrailersAdapter(this);
        mTrailerRecyclerView.setAdapter(mMovieTrailersAdapter);
        mTrailerRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mMovieReviewsAdapter = new MovieReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mMovieReviewsAdapter);
        mReviewsRecyclerView.setHasFixedSize(true);
        mAddToFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isChecked = mAddToFavouriteButton.isChecked();
                if (isChecked && !isMovieFavourite) {
                    storeMovieToDB();
                    isMovieFavourite = true;
                } else {
                    deleteMovieFromDatabase();
                    isMovieFavourite = false;
                }
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_DATA_KEY, movieData);
        outState.putParcelableArrayList(MOVIE_TRAILER_KEY, mMoviesTrailers);
        outState.putParcelableArrayList(MOVIE_REVIEWS_KEY, mMoviesReviews);
        outState.putBoolean(IS_FAVOURITE_KEY, isMovieFavourite);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DATA_KEY)) {
            movieData = savedInstanceState.getParcelable(MOVIE_DATA_KEY);
            mMoviesTrailers = savedInstanceState.getParcelableArrayList(MOVIE_TRAILER_KEY);
            mMovieTrailersAdapter.setMoviesTrailers(mMoviesTrailers);
            mMoviesReviews = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS_KEY);
            mMovieReviewsAdapter.setMoviesReviews(mMoviesReviews);
            isMovieFavourite = savedInstanceState.getBoolean(IS_FAVOURITE_KEY);
            changeFavouriteState();
        } else {
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(MOVIE_DATA_KEY)) {
                movieData = intent.getParcelableExtra(MOVIE_DATA_KEY);
                queryMovie();
                Bundle trailerLoaderBundle = null;
                getActivity().getSupportLoaderManager().initLoader(TRAILER_LOADER_ID,
                        trailerLoaderBundle, movieTrailerLoader);

                Bundle reviewLoaderBundle = null;
                getActivity().getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID,
                        reviewLoaderBundle, mReviewLoaderCallback);
            }
        }
        setDetails(movieData);
    }

    @Override
    public void onItemClicked(int position) {

        if (mMoviesTrailers != null && !mMoviesTrailers.isEmpty()) {
            MoviesTrailer moviesTrailer = mMoviesTrailers.get(position);
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_YOUTUBE_BASE_URL +
                    moviesTrailer.getKey()));
            try {
                getActivity().startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(WEB_YOUTUBE_BASE_URL+ moviesTrailer.getKey()));
                getActivity().startActivity(webIntent);
            }
        }
    }

    /**
     * Loader Callbacks
     */

    private final LoaderManager.LoaderCallbacks<Boolean> queryMovieLoaderCallback =
            new LoaderManager.LoaderCallbacks<Boolean>() {
                @Override
                public Loader<Boolean> onCreateLoader(int id, Bundle args) {

                    return new QueryMovieLoader(getActivity(), movieData.getId() + "");
                }

                @Override
                public void onLoadFinished(Loader<Boolean> loader, Boolean data) {

                    isMovieFavourite = data;
                    changeFavouriteState();
                }

                @Override
                public void onLoaderReset(Loader<Boolean> loader) {

                }
            };

    private final LoaderManager.LoaderCallbacks<ArrayList<MoviesReview>> mReviewLoaderCallback =
            new LoaderManager.LoaderCallbacks<ArrayList<MoviesReview>>() {
                @Override
                public Loader<ArrayList<MoviesReview>> onCreateLoader(int id, Bundle args) {

                    try {
                        URL movieTrailerURL = NetworkUtils.buildUrlWithId(BuildConfig.API_KEY,
                                MOVIE_REVIEWS,
                                movieData.getId() + "");
                        return new MovieReviewLoader(getActivity(), movieTrailerURL);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<MoviesReview>> loader,
                                           ArrayList<MoviesReview> data) {

                    L.d(TAG + "onLoadFinished()::MoviesReview == null " + (data == null));
                    if (null != data) {
                        mMoviesReviews = data;
                        mMovieReviewsAdapter.setMoviesReviews(mMoviesReviews);
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<MoviesReview>> loader) {

                }
            };

    private final LoaderManager.LoaderCallbacks<ArrayList<MoviesTrailer>> movieTrailerLoader =
            new LoaderManager.LoaderCallbacks<ArrayList<MoviesTrailer>>() {
                @Override
                public Loader<ArrayList<MoviesTrailer>> onCreateLoader(int id, Bundle args) {

                    try {
                        URL movieTrailerURL = NetworkUtils.buildUrlWithId(BuildConfig.API_KEY, MOVIE_TRAILER,
                                movieData.getId() + "");
                        return new MovieTrailerLoader(getActivity(), movieTrailerURL);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<MoviesTrailer>> loader, ArrayList<MoviesTrailer> data) {

                    L.d(TAG + "onLoadFinished()::data == null " + (data == null));
                    if (null != data) {
                        mMoviesTrailers = data;
                        mMovieTrailersAdapter.setMoviesTrailers(mMoviesTrailers);
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<MoviesTrailer>> loader) {

                }
            };

    /**
     * Helper methods
     */
    private void setDetails(MovieData movieData) {

        if (movieData != null) {
            mMovieTitleTextView.setText(movieData.getTitle());
            mMovieOverviewTextView.setText(movieData.getOverview());
            mMovieRatingTextView.setText(String.valueOf(movieData.getVoteAverage() + "/10"));
            mMovieReleaseDateTextView.setText(String.valueOf(movieData.getReleaseDate()));
            loadPosterPath(movieData.getPosterPath("w185"));
        }
    }

    private void loadPosterPath(String posterPath) {

        if (null != posterPath && !posterPath.isEmpty()) {
            Context mContext = getContext();
            Picasso.with(mContext).load(posterPath)
                    .into(mPosterImageView);
        }
    }

    private void changeFavouriteState() {

        mAddToFavouriteButton.setChecked(isMovieFavourite);
    }

    private void storeMovieToDB() {

        ContentResolver contentResolver = getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.BACKDROP_PATH, movieData.getBackdropPath());
        contentValues.put(MoviesContract.MovieEntry.IS_ADULT, (movieData.isAdult() ? 1 : 0));
        contentValues.put(MoviesContract.MovieEntry.VIDEO, (movieData.isVideo() ? 1 : 0));
        contentValues.put(MoviesContract.MovieEntry.MOVIES_ID, movieData.getId());
        contentValues.put(MoviesContract.MovieEntry.ORIGINAL_LAUNGUAGE, movieData.getOriginalLanguage());
        contentValues.put(MoviesContract.MovieEntry.ORIGINAL_TITLE, movieData.getOriginalTitle());
        contentValues.put(MoviesContract.MovieEntry.OVERVIEW, movieData.getOverview());
        contentValues.put(MoviesContract.MovieEntry.POPULARITY, movieData.getPopularity() + "");
        contentValues.put(MoviesContract.MovieEntry.POSTER_PATH, movieData.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.TITLE, movieData.getTitle());
        contentValues.put(MoviesContract.MovieEntry.RELEASE_DATE, movieData.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.VOTE_AVERAGE, movieData.getVoteAverage() + "");
        contentValues.put(MoviesContract.MovieEntry.VOTE_COUNT, movieData.getVoteCount());

        try {
            Uri uri = contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI,
                    contentValues);
            if (uri != null) {
                Toast.makeText(getActivity().getBaseContext(),
                        getActivity().getResources().getString(R.string.movie_added_to_favourite),
                        Toast.LENGTH_LONG).show();
            }
        } catch (SQLiteException e) {
            L.e(e.getMessage() + "");
        }
    }

    private void deleteMovieFromDatabase() {

        try {
            ContentResolver contentResolver = getContext().getContentResolver();
            String stringId = Integer.toString(movieData.getId());
            Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            int rowCount = contentResolver.delete(uri, null, null);
            if (rowCount > 0) {
                Toast.makeText(getActivity().getBaseContext(),
                        getActivity().getResources().getString(R.string.movie_delete_from_favourite),
                        Toast.LENGTH_LONG).show();
            }
        } catch (SQLiteException e) {
            L.e(e.getMessage() + "");
        }
    }

    private void queryMovie() {

        getActivity().getSupportLoaderManager().initLoader(QUERY_LOADER_ID,
                null,
                queryMovieLoaderCallback);
    }
}
