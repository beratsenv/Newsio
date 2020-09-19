package com.news.io.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.news.io.R;
import com.news.io.ui.bookmark.BookmarksFragment;
import com.news.io.ui.home.HomeFragment;
import com.news.io.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String HOME_TAB = "home fragment";
    private static final String SEARCH_TAB = "search fragment";
    private static final String BOOKMARKS_TAB = "bookmarks fragment";
    private static final String SETTINGS_TAB = "settings fragment";

    @BindView(R.id.bottom_navigation_main)
    BottomNavigationView mBottomNavigationView;

    private boolean backPressedTwice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadFragments(new HomeFragment(), HOME_TAB);
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_home:
                    return loadFragments(new HomeFragment(), HOME_TAB);
                case R.id.action_search:
                    return loadFragments(new SearchFragment(), SEARCH_TAB);
                case R.id.action_bookmarks:
                    return loadFragments(new BookmarksFragment(), BOOKMARKS_TAB);

            }
            return false;
        });
    }

    private boolean loadFragments(Fragment fragment, String tag) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout_main, fragment, tag)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout_main);
        if (fragment instanceof HomeFragment) {
            if (backPressedTwice) {
                super.onBackPressed();
            } else {
                this.backPressedTwice = true;
                Toast.makeText(this, getString(R.string.msg_press_again), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> backPressedTwice = false, 2000);
            }
        } else {
            loadFragments(new HomeFragment(), HOME_TAB);
            mBottomNavigationView.setSelectedItemId(R.id.action_home);
        }
    }

}
