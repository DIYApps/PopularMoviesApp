package nanodegree.com.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MoviesTrailer implements Parcelable {

    private String id;

    @SerializedName("iso_639_1")
    private String iso639;

    @SerializedName("iso_3166_1")
    private String iso3166;

    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    protected MoviesTrailer(Parcel in) {

        id = in.readString();
        iso639 = in.readString();
        iso3166 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public static final Creator<MoviesTrailer> CREATOR = new Creator<MoviesTrailer>() {
        @Override
        public MoviesTrailer createFromParcel(Parcel in) {

            return new MoviesTrailer(in);
        }

        @Override
        public MoviesTrailer[] newArray(int size) {

            return new MoviesTrailer[size];
        }
    };

    public String getType() {

        return type;
    }

    public String getId() {

        return id;
    }

    public String getIso639() {

        return iso639;
    }

    public String getIso3166() {

        return iso3166;
    }

    public String getKey() {

        return key;
    }

    public String getName() {

        return name;
    }

    public String getSite() {

        return site;
    }

    public int getSize() {

        return size;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(iso639);
        parcel.writeString(iso3166);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeInt(size);
        parcel.writeString(type);
    }
}
