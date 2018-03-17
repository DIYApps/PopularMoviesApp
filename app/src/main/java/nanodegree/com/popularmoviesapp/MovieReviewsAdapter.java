package nanodegree.com.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nanodegree.com.popularmoviesapp.data.MoviesReview;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewHolder> {

    private ArrayList<MoviesReview> moviesReviews;
    private Context mContext;

    public void setMoviesReviews(ArrayList<MoviesReview> moviesReviews) {
        this.moviesReviews = moviesReviews;
        notifyDataSetChanged();
    }

    @Override
    public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movie_review_item_layout;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieReviewsAdapter.MovieReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewHolder holder, int position) {
        MoviesReview review =  moviesReviews.get(position);
        holder.mReviewContent.setText(review.getContent());
        holder.mReviewAuthorName.setText(review.getAuthor());
        holder.mReviewAuthorAvatar.setText(review.getAuthor().substring(0,1));
    }

    @Override
    public int getItemCount() {

        if (null == moviesReviews) {
            return 0;
        }
        return moviesReviews.size();
    }

    public class MovieReviewHolder extends RecyclerView.ViewHolder{

        TextView mReviewAuthorName;
        TextView mReviewAuthorAvatar;
        TextView mReviewContent;
        public MovieReviewHolder(View itemView) {
            super(itemView);
            mReviewAuthorName = (TextView) itemView.findViewById(R.id.tv_author_name);
            mReviewAuthorAvatar = (TextView) itemView.findViewById(R.id.tv_author_name_avatar);
            mReviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);

        }
    }
}
