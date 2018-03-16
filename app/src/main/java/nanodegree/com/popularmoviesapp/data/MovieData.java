package nanodegree.com.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieData implements Parcelable {

    private static final String MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/";
    private static final String DEFAULT_IMAGE_SIZE = "w185";
    @SerializedName("vote_count")
    private int voteCount;
    private int id;
    private boolean video;

    @SerializedName("vote_average")
    private double voteAverage;
    private String title;
    private double popularity;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("genre_ids")
    private int[] genreIds;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("backdrop_path")
    private String backdropPath;
    private boolean adult;
    private String overview;

    public MovieData(){

    }
    protected MovieData(Parcel in) {

        voteCount = in.readInt();
        id = in.readInt();
        video = in.readByte() != 0;
        voteAverage = in.readDouble();
        title = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        originalLanguage = in.readString();
        originalTitle = in.readString();
        genreIds = in.createIntArray();
        releaseDate = in.readString();
        backdropPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {

            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {

            return new MovieData[size];
        }
    };

    public int getVoteCount() {

        return voteCount;
    }

    public void setVoteCount(int voteCount) {

        this.voteCount = voteCount;
    }

    public String getOverview() {

        return overview;
    }

    public void setOverview(String overview) {

        this.overview = overview;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public boolean isVideo() {

        return video;
    }

    public void setVideo(boolean video) {

        this.video = video;
    }

    public double getVoteAverage() {

        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {

        this.voteAverage = voteAverage;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public double getPopularity() {

        return popularity;
    }

    public void setPopularity(double popularity) {

        this.popularity = popularity;
    }

    public String getPosterPath() {

        return posterPath;
    }

    public String getPosterPath(String size) {

        if (size == null || size.isEmpty()) {
            return buildImageUrl(DEFAULT_IMAGE_SIZE, this.posterPath);
        }
        return buildImageUrl(size, this.posterPath);
    }

    public void setPosterPath(String posterPath) {

        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {

        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {

        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {

        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {

        this.originalTitle = originalTitle;
    }

    public int[] getGenreIds() {

        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {

        this.genreIds = genreIds;
    }

    public String getBackdropPath() {

        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {

        this.backdropPath = backdropPath;
    }

    public boolean isAdult() {

        return adult;
    }

    public void setAdult(boolean adult) {

        this.adult = adult;
    }

    public String getReleaseDate() {

        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {

        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(voteCount);
        parcel.writeInt(id);
        parcel.writeByte((byte) (video ? 1 : 0));
        parcel.writeDouble(voteAverage);
        parcel.writeString(title);
        parcel.writeDouble(popularity);
        parcel.writeString(posterPath);
        parcel.writeString(originalLanguage);
        parcel.writeString(originalTitle);
        parcel.writeIntArray(genreIds);
        parcel.writeString(releaseDate);
        parcel.writeString(backdropPath);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(overview);
    }

    private static String buildImageUrl(String size, String imageId) {

        return MOVIE_POSTER_URL + "/" + size + "/" + imageId;
    }

}
