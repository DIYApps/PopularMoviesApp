package nanodegree.com.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.R;
import nanodegree.com.popularmoviesapp.data.MoviesTrailer;

public class MovieTrailersAdapter extends RecyclerView.Adapter
        < MovieTrailersAdapter.MovieTrailerHolder > {

    private ArrayList< MoviesTrailer > moviesTrailers;
    private OnItemClickListener mItemClickListener;

    public MovieTrailersAdapter( OnItemClickListener mItemClickListener ) {

        this.mItemClickListener = mItemClickListener;
    }

    public void setMoviesTrailers( ArrayList< MoviesTrailer > moviesTrailers ) {

        this.moviesTrailers = moviesTrailers;
        notifyDataSetChanged();
    }

    @Override
    public MovieTrailerHolder onCreateViewHolder( ViewGroup parent, int viewType ) {

        Context mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movie_trailer_item_layout;
        LayoutInflater inflater = LayoutInflater.from( mContext );
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate( layoutIdForListItem, parent,
                shouldAttachToParentImmediately );
        return new MovieTrailersAdapter.MovieTrailerHolder( view );
    }

    @Override
    public void onBindViewHolder( MovieTrailerHolder holder, int position ) {

        MoviesTrailer trailer = moviesTrailers.get( position );
        holder.mMovieTrailerNameTextView.setText( trailer.getName() );
    }

    @Override
    public int getItemCount() {

        if ( null == moviesTrailers ) {
            return 0;
        }
        return moviesTrailers.size();
    }

    public interface OnItemClickListener {
        void onItemClicked( int position );
    }

    public class MovieTrailerHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView mMovieTrailerNameTextView;
        public MovieTrailerHolder( View itemView ) {

            super( itemView );
            mMovieTrailerNameTextView = ( TextView ) itemView.findViewById
                    ( R.id.tv_movie_trailer_name );
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick( View view ) {

            mItemClickListener.onItemClicked( getAdapterPosition() );
        }
    }
}
