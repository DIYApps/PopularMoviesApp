package nanodegree.com.popularmoviesapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MoviesResult {

    private int page;

    @SerializedName("total_results")
    private long totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    private ArrayList< MovieData > results;

    public int getPage() {

        return page;
    }

    public void setPage( int page ) {

        this.page = page;
    }

    public long getTotalResults() {

        return totalResults;
    }

    public void setTotalResults( long totalResults ) {

        this.totalResults = totalResults;
    }

    public int getTotalPages() {

        return totalPages;
    }

    public void setTotalPages( int totalPages ) {

        this.totalPages = totalPages;
    }

    public ArrayList< MovieData > getResults() {

        return results;
    }

    public void setResults( ArrayList< MovieData > results ) {

        this.results = results;
    }
}
