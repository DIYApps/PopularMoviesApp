package nanodegree.com.popularmoviesapp.data;

import nanodegree.com.popularmoviesapp.R;

public enum ErrorCodes {

    INVALID_DATA( R.string.app_name ),
    SERVER_ERROR( R.string.server_error ),
    NO_DATA_FOUND( R.string.no_data_found ),
    SUCCESS( R.string.success );
    final int code;

    ErrorCodes( int code ) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
