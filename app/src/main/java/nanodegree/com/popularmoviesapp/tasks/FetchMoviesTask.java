package nanodegree.com.popularmoviesapp.tasks;

import android.os.AsyncTask;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;

import nanodegree.com.popularmoviesapp.data.ErrorCodes;
import nanodegree.com.popularmoviesapp.data.MoviesResult;
import nanodegree.com.popularmoviesapp.utils.JsonUtils;
import nanodegree.com.popularmoviesapp.utils.L;
import nanodegree.com.popularmoviesapp.utils.NetworkUtils;

public class FetchMoviesTask extends AsyncTask<URL, String, ErrorCodes> {

    private final TaskCompletionListener taskCompletionListener;
    private boolean status;
    private MoviesResult moviesResult;

    public FetchMoviesTask(TaskCompletionListener taskCompletionListener) {

        this.taskCompletionListener = taskCompletionListener;
    }

    @Override
    protected ErrorCodes doInBackground(URL... urls) {

        URL moviesUrl = urls[0];
        String result = null;
        if (moviesUrl == null) {
            status = false;
        }
        try {
            result = NetworkUtils.getMoviesFromServer(moviesUrl);
            moviesResult = JsonUtils.parseJson(result);
            if (null == moviesResult) {
                status = false;
                return ErrorCodes.NO_DATA_FOUND;
            }
            status = true;
            return ErrorCodes.SUCCESS;
        } catch (IOException e) {
            L.e(e.getMessage());
            e.printStackTrace();
            status = false;
            return ErrorCodes.SERVER_ERROR;
        }
        catch (JsonSyntaxException e){
            status = false;
            return ErrorCodes.INVALID_DATA;
        }
    }

    @Override
    protected void onPostExecute(ErrorCodes errorCodes) {

        super.onPostExecute(errorCodes);
        if (status) {
            taskCompletionListener.onTaskCompleted(moviesResult);
        } else {
            taskCompletionListener.onError(errorCodes);
        }
    }

    public interface TaskCompletionListener {
        void onTaskCompleted(MoviesResult result);

        void onError(ErrorCodes errorCodes);
    }
}
