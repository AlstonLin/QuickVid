package io.alstonlin.quickvid;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


/**
 * The only activity in the app; Handles all actions and passes off to utility / helper calsses
 */
public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTabs();
        setupViewPager();
    }

    /**
     * Sets up the tabs of the TabLayout.
     */
    private void setupTabs(){
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this, R.drawable.explore)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this, R.drawable.camera)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    /**
     * Sets up the ViewPager, the PagerAdapter and the Listeners.
     */
    private void setupViewPager(){
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
