package nanodegree.com.popularmoviesapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = "POPULAR_MOVIES";
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String PATH = "movie";

    /**
     *creates the URL from URI with all parameters.
     */
    public static URL buildUrl(final String API_KEY, final String END_POINT)
            throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        Uri buildUri = builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath(PATH)
                .appendPath(END_POINT)
                .appendQueryParameter("api_key", API_KEY)
                .build();
        L.d(TAG+"URI " + buildUri.toString());
        return new URL(buildUri.toString());
    }
    /**
     *creates the URL from URI with all parameters.
     */
    public static URL buildUrlWithId(final String API_KEY, final String END_POINT , final  String ID)
            throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        Uri buildUri = builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath(PATH)
                .appendPath(ID)
                .appendPath(END_POINT)
                .appendQueryParameter("api_key", API_KEY)
                .build();
        L.d(TAG+ "URI " + buildUri.toString());
        return new URL(buildUri.toString());
    }

    /**
     *method creates a http url connection and query the Web API
     */
    public static String getMoviesFromServer(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method checks if the device is connected to internet or not.
     */
    public static boolean checkConnectivity(Context mContext) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
