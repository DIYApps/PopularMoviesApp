package nanodegree.com.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.R;
import nanodegree.com.popularmoviesapp.data.MovieData;
import nanodegree.com.popularmoviesapp.utils.L;

public class MoviesResultAdapter extends
        RecyclerView.Adapter< MoviesResultAdapter.MoviesResultAdapterViewHolder > {

    private ArrayList< MovieData > movieDataArrayList;
    private Context mContext;
    private OnItemClickListener mListener;

    public MoviesResultAdapter( OnItemClickListener mListener ) {

        this.mListener = mListener;
    }

    public void setMovieDataArrayList( ArrayList< MovieData > movieDataArrayList ) {

        L.d( "setMovieDataArrayList" + movieDataArrayList.size() );
        this.movieDataArrayList = movieDataArrayList;
        notifyDataSetChanged();
    }

    @Override
    public MoviesResultAdapterViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {

        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movies_thumbnail_layout;
        LayoutInflater inflater = LayoutInflater.from( mContext );
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate( layoutIdForListItem, parent, shouldAttachToParentImmediately );
        return new MoviesResultAdapterViewHolder( view );
    }

    @Override
    public void onBindViewHolder( MoviesResultAdapterViewHolder holder, final int position ) {

        MovieData movieData = movieDataArrayList.get( position );
        String posterPath = movieData.getPosterPath( "w185" );
        Picasso.with( mContext ).load( posterPath )
                .into( holder.mThumbnailImageView );
    }

    @Override
    public int getItemCount() {

        if ( movieDataArrayList == null ) {
            return 0;
        }
        return movieDataArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClicked( int position );
    }

    public class MoviesResultAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mThumbnailImageView;

        public MoviesResultAdapterViewHolder( View itemView ) {

            super( itemView );
            mThumbnailImageView = ( ImageView ) itemView.findViewById( R.id.iv_movie_thumbnail );
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick( View view ) {

            int clickedItemIndex = getAdapterPosition();
            mListener.onItemClicked( clickedItemIndex );
        }
    }
}
