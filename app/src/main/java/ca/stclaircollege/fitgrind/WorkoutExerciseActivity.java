package ca.stclaircollege.fitgrind;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.stclaircollege.fitgrind.database.Cardio;
import ca.stclaircollege.fitgrind.database.Strength;
import ca.stclaircollege.fitgrind.database.WorkoutType;

public class WorkoutExerciseActivity extends AppCompatActivity implements
        ExerciseFragment.OnFragmentInteractionListener {

    private static final int ADD_EXERCISE_REQUEST = 1;

    private long programId;
    private ViewPager viewPager;
    private SectionPagerAdapter sectionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_exercise);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get the id
        programId = getIntent().getLongExtra("id", -1);

        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.exerciseContent);
        viewPager.setAdapter(sectionPagerAdapter);

        final TextView day = (TextView) findViewById(R.id.day);
        ImageButton backButton = (ImageButton) findViewById(R.id.exercise_back_button);
        ImageButton forwardButton = (ImageButton) findViewById(R.id.exercise_forward_button);
        day.setText("Sunday");

        // set up listeners for viewpager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            //set the textview title
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        day.setText("Sunday");
                        break;
                    case 1:
                        day.setText("Monday");
                        break;
                    case 2:
                        day.setText("Tuesday");
                        break;
                    case 3:
                        day.setText("Wednesday");
                        break;
                    case 4:
                        day.setText("Thursday");
                        break;
                    case 5:
                        day.setText("Friday");
                        break;
                    case 6:
                        day.setText("Saturday");
                        break;
                    default:
                        day.setText("Sunday");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //viewpaher back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
            }
        });

        //move viewpager forward
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
            }
        });


    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {
        private SparseArray<ExerciseFragment> registeredFragments = new SparseArray<ExerciseFragment>();

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position){
            switch(position){
                case 0:
                    return ExerciseFragment.newInstance(1, programId);
                case 1:
                    return ExerciseFragment.newInstance(2, programId);
                case 2:
                    return ExerciseFragment.newInstance(3, programId);
                case 3:
                    return ExerciseFragment.newInstance(4, programId);
                case 4:
                    return ExerciseFragment.newInstance(5, programId);
                case 5:
                    return ExerciseFragment.newInstance(6, programId);
                case 6:
                    return ExerciseFragment.newInstance(7, programId);
                default:
                    return ExerciseFragment.newInstance(1, programId);
            }
        }

        @Override
        public int getCount(){
            return 7;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ExerciseFragment fragment = (ExerciseFragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workout_menu, menu);
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
            finish();
        } else if (id == R.id.navAdd) {
            Intent intent = new Intent(this, AddExerciseActivity.class);
            intent.putExtra("id", programId);
            startActivityForResult(intent, ADD_EXERCISE_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_EXERCISE_REQUEST && resultCode == RESULT_OK && data != null) {
            WorkoutType item = data.getExtras().getParcelable("item");
            ((ExerciseFragment) sectionPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem())).addItem(item);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
