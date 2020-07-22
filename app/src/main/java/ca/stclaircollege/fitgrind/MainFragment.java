package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.FoodLog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;

    private TextView mCurrentDate, mCaloriesGoal, mCaloriesObtained;
    private ListView mListView;
    private ArrayList<Food> recentFood;
    private CardView results;

    // connect from the xml layout here
    private FloatingActionButton foodFab, customFoodFab, weightLogFab;
    private WeightCalculator weightCalculator;

    public MainFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create our shared preferences here
        final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        // we want to launch an activity once, to ask the user if they've set-up their settings.
        // If they choose no, then we dont bother with them anymore.
        boolean isStarted = SP.getBoolean("last_start", false);
        // if it hasn't been then we can launch dialog
        if (!isStarted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Food Options");
            builder.setMessage("Please set-up your personalized fitness, to get the full experience!");
            builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor edit = SP.edit();
                    edit.putBoolean("last_start", Boolean.TRUE);
                    edit.commit();
                    // start activity here
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("I\'ll do it later.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor edit = SP.edit();
                    edit.putBoolean("last_start", Boolean.TRUE);
                    edit.commit();
                }
            });
            builder.show();
        }


        // create our weight calculator
        weightCalculator = new WeightCalculator(SP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // connect
        mCurrentDate = (TextView) view.findViewById(R.id.currentDate);
        mCaloriesGoal = (TextView) view.findViewById(R.id.calories_goal);
        mCaloriesObtained = (TextView) view.findViewById(R.id.calories_obtained_title);
        mListView = (ListView) view.findViewById(R.id.calorie_listview);
        results = (CardView) view.findViewById(R.id.results);

        // set up event listener for card view to go to view calorie day log fragment
        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Create a fragmentManager
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction trans = fm.beginTransaction();
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            trans.replace(R.id.content_main, new ViewCalorieLogFragment());
            trans.addToBackStack(null);
            trans.commit();
            }
        });

        // Create a database
        DatabaseHandler db = new DatabaseHandler(getContext());
        // let's set up calories and weight goal
        mCaloriesGoal.setText(weightCalculator.getCalorieGoal());
        // we will set up a calories obtained, to do this we need to call the db
        // at zero selects today.
        double caloriesObtained = db.selectCaloriesAt(0);
        double caloriesLeft = weightCalculator.getBMR() - caloriesObtained;
        // set the text colour depending on weight.
        mCaloriesObtained.setTextColor((caloriesLeft >= 0) ? Color.parseColor("#2ecc71") : Color.parseColor("#e74c3c"));
        mCaloriesObtained.setText("" + caloriesLeft);

        // retrieve a food log
        recentFood = db.selectRecentFoodLog();
        if (recentFood != null) mListView.setAdapter(new CustomAdapter(getContext(), recentFood));

        // we want to set the text view for last logged weight, last calories and calories goal
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        mCurrentDate.setText("Today\'s Date: " + new SimpleDateFormat("EEE, MMM d, ''yy").format(cal.getTime()));
        db.close();

        // set a long lcick for mlistview
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                // use the id
                final Food food = (Food) mListView.getItemAtPosition(i);
                CharSequence colors[] = new CharSequence[] {"Edit", "Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Food Options");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // 0 indicating edit, and 1 indicating delete
                        if (which == 0) {
                            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                            trans.replace(R.id.content_main, EditFoodFragment.newInstance(food.getId()));
                            trans.addToBackStack(null);
                            trans.commit();
                        } else {
                            DatabaseHandler db = new DatabaseHandler(getContext());
                            if (db.deleteFood(food.getId())) {
                                recentFood.remove(i);
                                // we also wanna make a notify update
                                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                Toast.makeText(getContext(), R.string.db_delete_success, Toast.LENGTH_SHORT).show();
                            }
                            db.close();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        // connect layout
        foodFab = (FloatingActionButton) view.findViewById(R.id.foodFab);
        customFoodFab = (FloatingActionButton) view.findViewById(R.id.customFoodFab);
        weightLogFab = (FloatingActionButton) view.findViewById(R.id.weightLogFab);

        foodFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                trans.replace(R.id.content_main, new AddFoodFragment());
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        customFoodFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                trans.replace(R.id.content_main, new AddCustomFoodFragment());
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        weightLogFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                trans.replace(R.id.content_main, new WeightLogFragment());
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        return view;
    }

    // we need to create a custom adapter
    public class CustomAdapter extends ArrayAdapter<Food> {

        public CustomAdapter(Context context, ArrayList<Food> foodList) {
            super(context, 0, foodList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Food food = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_calorie_log, parent, false);

            TextView name = (TextView) convertView.findViewById(R.id.calorie_food_name);
            TextView serving = (TextView) convertView.findViewById(R.id.calorie_serving);
            TextView recordedDate = (TextView) convertView.findViewById(R.id.recorded_date);
            TextView calories = (TextView) convertView.findViewById(R.id.calorie_calories);

            // create ImageView
            final ImageView menuButton = (ImageView) convertView.findViewById(R.id.menuButton);

            // create a listener
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // instantiate a pop up menu
                    PopupMenu menu = new PopupMenu(getContext(), menuButton);
                    // inflate the pop up menu with the xml
                    menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());

                    // create an event listener
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                                    trans.replace(R.id.content_main, EditFoodFragment.newInstance(food.getId()));
                                    trans.addToBackStack(null);
                                    trans.commit();
                                    break;
                                case R.id.delete:
                                    DatabaseHandler db = new DatabaseHandler(getContext());
                                    if (db.deleteFood(food.getId())) {
                                        recentFood.remove(position);
                                        // we also wanna make a notify update
                                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                        Toast.makeText(getContext(), R.string.db_delete_success, Toast.LENGTH_SHORT).show();
                                    }
                                    db.close();
                                    break;
                            }
                            return true;
                        }
                    });

                    // finally show the pop up menu
                    menu.show();
                }
            });

            name.setText(food.getName());
            serving.setText(food.getServingSize());
            recordedDate.setText(food.getLogDate());
            calories.setText(food.getNutrients().get(0).getValue() + " " + food.getNutrients().get(0).getNutrient());

            // Return the completed view to render on screen
            return convertView;
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
