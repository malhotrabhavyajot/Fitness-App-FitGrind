package ca.stclaircollege.fitgrind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    MainFragment.OnFragmentInteractionListener,
                    AddFoodFragment.OnFragmentInteractionListener,
                    ViewFoodFragment.OnFragmentInteractionListener,
                    ExerciseFragment.OnFragmentInteractionListener,
                    ViewCalorieLogFragment.OnFragmentInteractionListener, ViewCalorieDayLogFragment.OnFragmentInteractionListener,
                    EditFoodFragment.OnFragmentInteractionListener, AddCustomFoodFragment.OnFragmentInteractionListener,
                    WeightLogFragment.OnFragmentInteractionListener, CreditsFragment.OnFragmentInteractionListener,
                    WorkoutProgramFragment.OnFragmentInteractionListener {

    // create fragment manager
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the layout here
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.content_main, new MainFragment());
        trans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // setup intent
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        final FragmentTransaction trans = fm.beginTransaction();

        if (id == R.id.nav_home) {
            fragment = new MainFragment();
        } else if (id == R.id.nav_diary) {
            fragment = new ViewCalorieLogFragment();
        } else if (id == R.id.nav_add_custom_food) {
            fragment = new AddCustomFoodFragment();
        } else if (id == R.id.nav_add_food) {
            fragment = new AddFoodFragment();
        } else if (id == R.id.nav_weight_log) {
            fragment = new WeightLogFragment();
        } else if (id == R.id.nav_workout_schedule) {
            fragment = new WorkoutProgramFragment();
        } else if (id == R.id.nav_credits) {
            fragment = new CreditsFragment();
        }

        trans.replace(R.id.content_main, fragment);
        trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        trans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
