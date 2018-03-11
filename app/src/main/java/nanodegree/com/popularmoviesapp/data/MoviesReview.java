package nanodegree.com.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MoviesReview implements Parcelable {

    private String id;
    private String author;
    private String content;

    protected MoviesReview(Parcel in) {

        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<MoviesReview> CREATOR = new Creator<MoviesReview>() {
        @Override
        public MoviesReview createFromParcel(Parcel in) {

            return new MoviesReview(in);
        }

        @Override
        public MoviesReview[] newArray(int size) {

            return new MoviesReview[size];
        }
    };

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
    }

    public String getId() {

        return id;
    }

    public String getAuthor() {

        return author;
    }

    public String getContent() {

        return content;
    }
}
