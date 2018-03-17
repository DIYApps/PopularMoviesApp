package nanodegree.com.popularmoviesapp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import nanodegree.com.popularmoviesapp.data.MoviesResult;
import nanodegree.com.popularmoviesapp.data.MoviesReviewData;
import nanodegree.com.popularmoviesapp.data.MoviesTrailerData;

public class JsonUtils {

    public static MoviesResult parseJson(String jsonString) throws JsonSyntaxException{

        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonString, MoviesResult.class);
    }

    public static MoviesTrailerData parseMovieTrailer(String jsonString) throws JsonSyntaxException{

        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonString, MoviesTrailerData.class);
    }

    public static MoviesReviewData parseMovieReview(String jsonString) throws JsonSyntaxException{

        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonString, MoviesReviewData.class);
    }
}
