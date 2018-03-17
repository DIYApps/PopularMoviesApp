package nanodegree.com.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nanodegree.com.popularmoviesapp.data.ErrorCodes;
import nanodegree.com.popularmoviesapp.data.MovieData;
import nanodegree.com.popularmoviesapp.data.MoviesContract;
import nanodegree.com.popularmoviesapp.data.MoviesResult;
import nanodegree.com.popularmoviesapp.tasks.FavouriteMovieLoader;
import nanodegree.com.popularmoviesapp.tasks.FetchMoviesTask;
import nanodegree.com.popularmoviesapp.utils.L;
import nanodegree.com.popularmoviesapp.utils.NetworkUtils;

public class MoviesListFragment extends Fragment implements MoviesResultAdapter.OnItemClickListener {

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final int SPAN_COUNT = 2;
    private static final int FAVOURITE_MOVIE_LOADER_ID = 0;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageTextView;
    private final static String API_KEY = BuildConfig.API_KEY;
    private MoviesResultAdapter moviesResultAdapter;
    private static final String FAVOURITE_MOVIES_KEY = "favourite_movies_key";
    private static final String POPULAR_MOVIES_KEY = "popular_movies_key";
    private static final String TOP_RATED_MOVIES_KEY = "top_rated_movies_key";
    private static final String MOVIES_CATEGORY_KEY = "movies_category_key";
    private ArrayList<MovieData> movieDataArrayList;
    private ArrayList<MovieData> topRatedDataArrayList;
    private ArrayList<MovieData> popularDataArrayList;
    private ArrayList<MovieData> favouriteDataArrayList;
    private static final String INTENT_MOVIE_DATA_KEY = "movie_data";
    private FragmentInteractionListener mFragmentInteractionListener;
    private String mMoviesCategory;
    private static boolean isMovieDbChanged;

    public MoviesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_movies_thumbnails);
        mErrorMessageTextView = (TextView) view.findViewById(R.id.tv_error_message);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
        setHasOptionsMenu(true);
        //create a adapter
        moviesResultAdapter = new MoviesResultAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        mRecyclerView.setAdapter(moviesResultAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        registerContentObserver();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            topRatedDataArrayList = savedInstanceState.getParcelableArrayList(TOP_RATED_MOVIES_KEY);
            popularDataArrayList = savedInstanceState.getParcelableArrayList(POPULAR_MOVIES_KEY);
            favouriteDataArrayList = savedInstanceState.getParcelableArrayList(FAVOURITE_MOVIES_KEY);
            mMoviesCategory = savedInstanceState.getString(MOVIES_CATEGORY_KEY);
            if(mMoviesCategory.equals(getString(R.string.menu_title_popular))
                    && isListNotEmptyOrNull(popularDataArrayList)){
                movieDataArrayList = popularDataArrayList;
            }else if(mMoviesCategory.equals(getString(R.string.menu_title_top_rated))
                    && isListNotEmptyOrNull(topRatedDataArrayList)){
                movieDataArrayList = topRatedDataArrayList;
            }else{
                if (isListNotEmptyOrNull(favouriteDataArrayList)) {
                    movieDataArrayList = favouriteDataArrayList;
                }
            }
            if(isListNotEmptyOrNull(movieDataArrayList)){
                moviesResultAdapter.setMovieDataArrayList(movieDataArrayList);
                mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
                L.d("Movie category restored" + mMoviesCategory);
            }
            else{
                loadMovies(POPULAR);
                mMoviesCategory = getString(R.string.menu_title_popular);
                L.d("Movie category default" + mMoviesCategory);
                mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
            }
        } else {
            loadMovies(POPULAR);
            mMoviesCategory = getString(R.string.menu_title_popular);
            L.d("Movie category default" + mMoviesCategory);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (isListNotEmptyOrNull(topRatedDataArrayList)) {
            outState.putParcelableArrayList(TOP_RATED_MOVIES_KEY, topRatedDataArrayList);
        }
        if (isListNotEmptyOrNull(popularDataArrayList)) {
            outState.putParcelableArrayList(POPULAR_MOVIES_KEY, popularDataArrayList);
        }
        if (isListNotEmptyOrNull(favouriteDataArrayList)) {
            outState.putParcelableArrayList(FAVOURITE_MOVIES_KEY, favouriteDataArrayList);
        }
        if (mMoviesCategory != null && !mMoviesCategory.isEmpty()) {
            outState.putString(MOVIES_CATEGORY_KEY, mMoviesCategory);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_popular) {
            mMoviesCategory = getString(R.string.menu_title_popular);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
            loadPopularMovies();
            return true;
        }
        if (id == R.id.action_top_rated) {
            mMoviesCategory = getString(R.string.menu_title_top_rated);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
            loadTopRatedMovies();
            return true;
        }
        if (id == R.id.action_favourite) {
            mMoviesCategory = getString(R.string.menu_title_favourite);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
            if(!isMovieDbChanged){
                fetchFavouriteMovie();
            }else{
                reloadFavouriteMovies();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMoviesCategory.equals(getString(R.string.menu_title_favourite))){
            reloadFavouriteMovies();
            isMovieDbChanged = false;
        }
    }

    @Override
    public void onItemClicked(int position) {

        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra(INTENT_MOVIE_DATA_KEY, movieDataArrayList.get(position));
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            mFragmentInteractionListener = (FragmentInteractionListener) context;
        } else {
            throw new IllegalArgumentException("Activity must implement FragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentInteractionListener = null;
    }

    private void loadTopRatedMovies(){
        if(topRatedDataArrayList ==  null || topRatedDataArrayList.isEmpty()){
            loadMovies(TOP_RATED);
        }else{
            movieDataArrayList = topRatedDataArrayList;
            moviesResultAdapter.setMovieDataArrayList(topRatedDataArrayList);
        }
    }

    private void loadPopularMovies(){
        if(popularDataArrayList ==  null || popularDataArrayList.isEmpty()){
            loadMovies(POPULAR);
        }else{
            movieDataArrayList = popularDataArrayList;
            moviesResultAdapter.setMovieDataArrayList(movieDataArrayList);
        }
    }

    private boolean isListNotEmptyOrNull(List list){
        return !(list == null || list.isEmpty());
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        unRegisterContentObserver();
    }

    /**
     * Helper methods
     */
    private void showMovieListDataView() {

        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {

        if (movieDataArrayList == null || movieDataArrayList.isEmpty()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showSnackBarWhenNoInternet(String errorMessage) {

        Snackbar.make(mRecyclerView, errorMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchFavouriteMovie();
                    }
                }).show();
    }

    private void loadMovies(String category) {

        if (!NetworkUtils.checkConnectivity(getContext())) {
            L.e("No Internet connection");
            showSnackBarWhenNoInternet(getString(R.string.no_internet_connection) + "\n" +
                    getString(R.string.load_movie_from_favourite_list));
            return;
        }
        L.d("Loading movies from server");
        URL moviesUrl = null;
        try {
            moviesUrl = NetworkUtils.buildUrl(API_KEY, category);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        mLoadingIndicator.setVisibility(View.VISIBLE);
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(mCompletionListener);
        fetchMoviesTask.execute(moviesUrl);
    }

    private void fetchFavouriteMovie() {

        Bundle favouriteMovieLoaderBundle = null;
        getLoaderManager().initLoader(FAVOURITE_MOVIE_LOADER_ID,
                favouriteMovieLoaderBundle,
                favouriteMoviesLoader);
    }

    private void reloadFavouriteMovies() {

        Bundle favouriteMovieLoaderBundle = null;
        if (isMovieDbChanged) {
            getLoaderManager().restartLoader(FAVOURITE_MOVIE_LOADER_ID,
                    favouriteMovieLoaderBundle,
                    favouriteMoviesLoader);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }
    private void registerContentObserver(){
        getContext().getContentResolver().registerContentObserver(
                MoviesContract.MovieEntry.CONTENT_URI,
                false,
                favouriteMovieObserver);
    }
    private void unRegisterContentObserver(){
        getContext().getContentResolver().unregisterContentObserver(favouriteMovieObserver);
    }

    /**
     * Callbacks
     */
    private final FetchMoviesTask.TaskCompletionListener mCompletionListener =
            new FetchMoviesTask.TaskCompletionListener() {

                @Override
                public void onTaskCompleted(MoviesResult result) {

                    L.d("onTaskCompleted called");
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    showMovieListDataView();
                    movieDataArrayList = result.getResults();
                    if(mMoviesCategory.equals(getString(R.string.menu_title_popular))){
                        popularDataArrayList = movieDataArrayList;
                    }
                    else{
                        topRatedDataArrayList = movieDataArrayList;
                    }
                    moviesResultAdapter.setMovieDataArrayList(movieDataArrayList);
                }

                @Override
                public void onError(ErrorCodes errorCodes) {

                    L.e("ErrorCode" + errorCodes);
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    showErrorMessage();
                }
            };

    private final LoaderManager.LoaderCallbacks<ArrayList<MovieData>> favouriteMoviesLoader =
            new LoaderManager.LoaderCallbacks<ArrayList<MovieData>>() {
                @Override
                public Loader<ArrayList<MovieData>> onCreateLoader(int id, Bundle args) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    return new FavouriteMovieLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<MovieData>> loader,
                                           ArrayList<MovieData> data) {

                    mLoadingIndicator.setVisibility(View.GONE);
                    L.d("onLoadFinished::()________________");
                    if (data != null) {
                        movieDataArrayList = data;
                        moviesResultAdapter.setMovieDataArrayList(movieDataArrayList);
                        favouriteDataArrayList = movieDataArrayList;
                        isMovieDbChanged =  false;
                    } else {
                        if (movieDataArrayList == null || movieDataArrayList.isEmpty()) {
                            showErrorMessage();
                            return;
                        }
                        Snackbar.make(mRecyclerView, getString(R.string.no_movie_in_db)
                                , Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<MovieData>> loader) {

                    L.d("onLoaderReset::()=============");
                }
            };
    final Handler handler = new Handler();
    private final ContentObserver favouriteMovieObserver =  new ContentObserver(handler) {
        @Override
        public boolean deliverSelfNotifications() {

            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {

            super.onChange(selfChange);
            isMovieDbChanged =  true;
            L.d("ContentObserver()onChange():: Database changed.");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {

            super.onChange(selfChange, uri);
            isMovieDbChanged =  true;
            L.d("ContentObserver()onChange():: Database changed. +URI " + uri );
        }
    };

    /**
     * FragmentInteractionListener interface.
     */
    public interface FragmentInteractionListener {
        void setActionBarTitle(String title);
    }
}
