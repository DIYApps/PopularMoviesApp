package nanodegree.com.popularmoviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String AUTHORITY = "nanodegree.com.popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH = "Movie";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String TABLE_NAME = "Movie";

        public static final String VOTE_COUNT = "vote_count";
        public static final String MOVIES_ID = "id";
        public static final String VIDEO = "video";
        public static final String VOTE_AVERAGE = "voteAverage";
        public static final String TITLE = "title";
        public static final String POPULARITY = "popularity";
        public static final String POSTER_PATH= "posterPath";
        public static final String ORIGINAL_LAUNGUAGE = "originalLanguage";
        public static final String ORIGINAL_TITLE = "originalTitle";
        public static final String GENRE_ID= "genreIds";
        public static final String RELEASE_DATE= "releaseDate";
        public static final String BACKDROP_PATH= "backdropPath";
        public static final String IS_ADULT = "adult";
        public static final String OVERVIEW = "overview";
    }
}
