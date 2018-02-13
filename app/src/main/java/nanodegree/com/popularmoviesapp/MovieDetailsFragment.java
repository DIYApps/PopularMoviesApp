package nanodegree.com.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import nanodegree.com.popularmoviesapp.data.MovieData;

public class MovieDetailsFragment extends Fragment {

    private static final String MOVIE_DATA_KEY = "movie_data";
    private static final String INTENT_MOVIE_DATA_KEY = "movie_data";
    private MovieData movieData;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    private TextView mMovieTitleTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieOverviewTextView;
    private ImageView mPosterImageView;

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
        return view;
    }

    private void setDetails(MovieData movieData) {

        if (movieData != null) {
            mMovieTitleTextView.setText(movieData.getTitle());
            mMovieOverviewTextView.setText(movieData.getOverview());
            mMovieRatingTextView.setText(String.valueOf(movieData.getVoteAverage()+"/10"));
            mMovieReleaseDateTextView.setText(String.valueOf(movieData.getReleaseDate()));
            loadPosterPath(movieData.getPosterPath("w185"));
        }
    }

    private void loadPosterPath(String posterPath){
        if(null != posterPath && !posterPath.isEmpty()){
            Context mContext = getContext();
            Picasso.with(mContext).load(posterPath)
                    .into(mPosterImageView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_DATA_KEY , movieData);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DATA_KEY)) {
            movieData = savedInstanceState.getParcelable(MOVIE_DATA_KEY);
        } else {
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(INTENT_MOVIE_DATA_KEY)) {
                movieData = intent.getParcelableExtra(INTENT_MOVIE_DATA_KEY);
            }
        }
        setDetails(movieData);
    }
}
