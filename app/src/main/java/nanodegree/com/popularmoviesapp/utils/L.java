package nanodegree.com.popularmoviesapp.utils;

import android.util.Log;

import nanodegree.com.popularmoviesapp.BuildConfig;

public class L {

    private static final String TAG = "__Popular_Movies__";
    private static boolean isDebug = BuildConfig.DEBUG;


    public static void d(String message){
        if (isDebug){
            Log.d(TAG ,"::"+message);
        }
    }
    public static void i(String message){
        if (isDebug){
            Log.i(TAG ,"::"+message);
        }
    }

    public static void e(String message){
        Log.e(TAG ,"::"+message);
    }

    public static void w(String message){
        Log.w(TAG ,"::"+message);
    }

    public static void setIsDebug(boolean isDebug) {

        L.isDebug = isDebug;
    }
}
