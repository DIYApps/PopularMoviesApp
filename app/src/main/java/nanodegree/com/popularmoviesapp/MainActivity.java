package nanodegree.com.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import nanodegree.com.popularmoviesapp.utils.L;

public class MainActivity extends AppCompatActivity implements MoviesListFragment.FragmentInteractionListener {

    private ActionBar mActionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBar =  getSupportActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_categories_menu, menu);
        return true;
    }

    @Override
    public void setActionBarTitle(String title) {
        if (mActionBar == null) {
            L.e("Action bar is null *****");
            mActionBar = getSupportActionBar();
        }
        mActionBar.setTitle(title);
    }
}
