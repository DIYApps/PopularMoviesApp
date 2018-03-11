package nanodegree.com.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
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

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.data.MovieData;
import nanodegree.com.popularmoviesapp.data.MoviesResult;
import nanodegree.com.popularmoviesapp.data.MoviesReview;
import nanodegree.com.popularmoviesapp.data.MoviesTrailer;
import nanodegree.com.popularmoviesapp.tasks.MovieReviewLoader;
import nanodegree.com.popularmoviesapp.tasks.MovieTrailerLoader;
import nanodegree.com.popularmoviesapp.utils.L;
import nanodegree.com.popularmoviesapp.utils.NetworkUtils;

public class MovieDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<MoviesTrailer>>,
        MovieTrailersAdapter.OnItemClickListener {

    private static final String MOVIE_DATA_KEY = "movie_data";
    private static final String MOVIE_TRAILER_KEY = "movie_trailer";
    private static final String MOVIE_REVIEWS_KEY = "movie_reviews";
    private static final String MOVIE_TRAILER = "videos";
    private static final String MOVIE_REVIEWS = "reviews";
    private static final String TAG = MovieDetailsFragment.class.getName()+"::";
    private MovieData movieData;
    private ArrayList<MoviesTrailer> mMoviesTrailers;
    private ArrayList<MoviesReview> mMoviesReviews;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

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
        LinearLayoutManager  trailersLayoutManager= new LinearLayoutManager(getActivity());
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(getActivity());
        mTrailerRecyclerView.setLayoutManager(trailersLayoutManager);
        mMovieTrailersAdapter = new MovieTrailersAdapter(this);
        mTrailerRecyclerView.setAdapter(mMovieTrailersAdapter);
        mTrailerRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mMovieReviewsAdapter = new MovieReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mMovieReviewsAdapter);
        mReviewsRecyclerView.setHasFixedSize(true);
        return view;
    }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_DATA_KEY, movieData);
        outState.putParcelableArrayList(MOVIE_TRAILER_KEY, mMoviesTrailers);
        outState.putParcelableArrayList(MOVIE_REVIEWS_KEY, mMoviesReviews);
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
        } else {
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(MOVIE_DATA_KEY)) {
                movieData = intent.getParcelableExtra(MOVIE_DATA_KEY);
                Bundle trailerLoaderBundle = null;
                getActivity().getSupportLoaderManager().initLoader(TRAILER_LOADER_ID,
                        trailerLoaderBundle , this);

                Bundle reviewLoaderBundle = null;
                getActivity().getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID,
                        reviewLoaderBundle , mReviewLoaderCallback);
            }

        }
        setDetails(movieData);
    }

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

        L.d(TAG+"onLoadFinished()::data == null "+(data==null));
        if(null != data){
            mMoviesTrailers = data;
            mMovieTrailersAdapter.setMoviesTrailers(mMoviesTrailers);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MoviesTrailer>> loader) {

    }

    @Override
    public void onItemClicked(int position) {

    }

    private LoaderManager.LoaderCallbacks<ArrayList<MoviesReview>> mReviewLoaderCallback =
            new LoaderManager.LoaderCallbacks<ArrayList<MoviesReview>>() {
        @Override
        public Loader<ArrayList<MoviesReview>> onCreateLoader(int id, Bundle args) {
            try {
                URL movieTrailerURL = NetworkUtils.buildUrlWithId(BuildConfig.API_KEY, MOVIE_REVIEWS,
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
            L.d(TAG+"onLoadFinished()::MoviesReview == null "+(data==null));
            if(null != data){
                mMoviesReviews = data;
                mMovieReviewsAdapter.setMoviesReviews(mMoviesReviews);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MoviesReview>> loader) {

        }
    };
}
