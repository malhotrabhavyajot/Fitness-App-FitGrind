package ca.stclaircollege.fitgrind;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.Progress;
import me.relex.circleindicator.CircleIndicator;

public class ViewProgressActivity extends AppCompatActivity implements ViewProgressLogFragment.OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private SectionPagerAdapter mPageAdapter;
    private Progress currProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.activity_view_progress);

        // get the action bar and set the home enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // connect our design here
        mViewPager = (ViewPager) findViewById(R.id.progress_viewpager);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);

        // create the page adapter here
        DatabaseHandler db = new DatabaseHandler(this);
        final ArrayList<Progress> progress = db.selectAllProgress();
        mPageAdapter = new SectionPagerAdapter(getSupportFragmentManager(), progress);
        currProgress = progress.get(mViewPager.getCurrentItem());

        // set the adapter and view pager
        mViewPager.setAdapter(mPageAdapter);
        indicator.setViewPager(mViewPager);

        // set event listener for pages
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currProgress = progress.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // destroy this activity
            finish();
        } else if (id == R.id.action_delete) {
            // if it's delete then we create a db to delete
            DatabaseHandler db = new DatabaseHandler(this);
            // if we can successfully delete, we can finish activity and close
            if (currProgress != null && db.deleteProgress(currProgress.getId())) {
                Toast.makeText(this, R.string.db_delete_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Progress> progressList;

        public SectionPagerAdapter(FragmentManager fm, ArrayList<Progress> progressList) {
            super(fm);
            this.progressList = progressList;
        }

        @Override
        public Fragment getItem(int position) {
            // we can use the arraylist in newInstance() method.
            return ViewProgressLogFragment.newInstance(progressList.get(position));
        }

        @Override
        public int getCount() {
            return progressList.size();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

}
