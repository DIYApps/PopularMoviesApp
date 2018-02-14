package nanodegree.com.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import java.util.logging.Logger;

import nanodegree.com.popularmoviesapp.data.ErrorCodes;
import nanodegree.com.popularmoviesapp.data.MovieData;
import nanodegree.com.popularmoviesapp.data.MoviesResult;
import nanodegree.com.popularmoviesapp.tasks.FetchMoviesTask;
import nanodegree.com.popularmoviesapp.utils.L;
import nanodegree.com.popularmoviesapp.utils.NetworkUtils;

public class MoviesListFragment extends Fragment implements MoviesResultAdapter.OnItemClickListener {

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final int SPAN_COUNT = 2;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageTextView;
    private final static String API_KEY = BuildConfig.API_KEY;
    private MoviesResultAdapter moviesResultAdapter;
    private static final String MOVIES_KEY = "movies_key";
    private static final String MOVIES_CATEGORY_KEY = "movies_category_key";
    private ArrayList<MovieData> movieDataArrayList;
    private static final String INTENT_MOVIE_DATA_KEY = "movie_data";
    private FragmentInteractionListener mFragmentInteractionListener;
    private String mMoviesCategory;

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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_KEY)) {
            movieDataArrayList = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            moviesResultAdapter.setMovieDataArrayList(movieDataArrayList);
            mMoviesCategory = savedInstanceState.getString(MOVIES_CATEGORY_KEY);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
            L.d("Movie category restored" + mMoviesCategory);
        } else {
            loadMovies(POPULAR);
            mMoviesCategory = getString(R.string.menu_title_popular);
            L.d("Movie category default" + mMoviesCategory);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (movieDataArrayList != null && !movieDataArrayList.isEmpty()) {
            outState.putParcelableArrayList(MOVIES_KEY, movieDataArrayList);
        }
        if (mMoviesCategory != null && !mMoviesCategory.isEmpty()) {
            outState.putString(MOVIES_CATEGORY_KEY, mMoviesCategory);
        }
        super.onSaveInstanceState(outState);
    }

    private final FetchMoviesTask.TaskCompletionListener mCompletionListener = new FetchMoviesTask
            .TaskCompletionListener() {

        @Override
        public void onTaskCompleted(MoviesResult result) {

            L.d("onTaskCompleted called");
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            showMovieListDataView();
            movieDataArrayList = result.getResults();
            moviesResultAdapter.setMovieDataArrayList(movieDataArrayList);
        }

        @Override
        public void onError(ErrorCodes errorCodes) {

            L.e("ErrorCode" + errorCodes);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            showErrorMessage(getString(errorCodes.getCode()));
        }
    };

    private void showMovieListDataView() {

        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p/>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage(String errorMessage) {

        if (movieDataArrayList == null || movieDataArrayList.isEmpty()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageTextView.setVisibility(View.VISIBLE);
            mErrorMessageTextView.setText(errorMessage);
        } else {
            Snackbar.make(mRecyclerView, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mMoviesCategory.equals(getString(R.string.menu_title_top_rated))) {
                                loadMovies(TOP_RATED);
                            }
                            else{
                                loadMovies(POPULAR);
                            }
                        }
                    }).show();
        }
    }

    private void loadMovies(String category) {

        if (!NetworkUtils.checkConnectivity(getContext())) {
            L.e("No Internet connection");
            showErrorMessage(getString(R.string.no_internet_connection));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_popular) {
            mMoviesCategory = getString(R.string.menu_title_popular);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
            loadMovies(POPULAR);
            return true;
        }
        if (id == R.id.action_top_rated) {
            mMoviesCategory = getString(R.string.menu_title_top_rated);
            mFragmentInteractionListener.setActionBarTitle(mMoviesCategory);
            loadMovies(TOP_RATED);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public interface FragmentInteractionListener {
        void setActionBarTitle(String title);
    }

}
