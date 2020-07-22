package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.FoodLog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewCalorieDayLogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewCalorieDayLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewCalorieDayLogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private FoodLog foodLog;
    private ListView mListView;
    private int index;
    private TextView noLogText, calorieGoal, caloriesObtained, totalFat, totalCarbs, totalProtein;

    private OnFragmentInteractionListener mListener;

    public ViewCalorieDayLogFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ViewCalorieDayLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewCalorieDayLogFragment newInstance(Parcelable foodLog, int index) {
        ViewCalorieDayLogFragment fragment = new ViewCalorieDayLogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, foodLog);
        args.putInt(ARG_PARAM2, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodLog = getArguments().getParcelable(ARG_PARAM1);
            index = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_calorie_day_log, container, false);

        // connect design
        mListView = (ListView) view.findViewById(R.id.calorie_listview);
        noLogText = (TextView) view.findViewById(R.id.no_log_text);
        calorieGoal = (TextView) view.findViewById(R.id.calorie_goal_label);
        caloriesObtained = (TextView) view.findViewById(R.id.calorie_obtained_label);
        totalFat = (TextView) view.findViewById(R.id.total_fat_day);
        totalCarbs = (TextView) view.findViewById(R.id.total_carb_day);
        totalProtein = (TextView) view.findViewById(R.id.total_protein_day);

        // check if object is able to be passed through
        if (foodLog != null) {
            // set up db
            DatabaseHandler db = new DatabaseHandler(getContext());
            double[] nutrients = db.selectNutrientsAt(index);
            db.close();
            // set the adapter
            CustomAdapter adapter = new CustomAdapter(getActivity(), foodLog.getFoodList());
            mListView.setAdapter(adapter);
            // We now also want to get the calories obtained during this time and the calorie goal
            WeightCalculator weightCalculator = new WeightCalculator(getContext());
            double caloriesLeft = weightCalculator.getBMR() - nutrients[0];
            // we can check the caloriesLeft, and set the colour based on if we past the code or not
            // without using the R.colour, we can set using a hex-value
            caloriesObtained.setTextColor((caloriesLeft >= 0) ? Color.parseColor("#2ecc71") : Color.parseColor("#e74c3c"));
            // set the text
            calorieGoal.setText(weightCalculator.getCalorieGoal());
            caloriesObtained.setText("" + caloriesLeft);
            // set up other nutrients to add
            totalFat.setText(String.format("%.2f", nutrients[1]));
            totalCarbs.setText(String.format("%.2f", nutrients[2]));
            totalProtein.setText(String.format("%.2f", nutrients[3]));
        } else {
            // if it is, we'll show up the textview
            noLogText.setVisibility(View.VISIBLE);
        }

        return view;
    }

    // we need to create a custom adapter.
    // here we can display our food.
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

            name.setText(food.getName());
            serving.setText(food.getServingSize());
            recordedDate.setText(food.getLogDate());
            calories.setText(food.getNutrient("calories").getValue() + " " + food.getNutrient("calories").getNutrient());

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
                                        foodLog.getFoodList().remove(position);
                                        // we also wanna make a notify update
                                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                        updateNutrients();
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

            // Return the completed view to render on screen
            return convertView;
        }

    }
    // updates nutrients after an update or delete
    public void updateNutrients() {
        DatabaseHandler db = new DatabaseHandler(getContext());
        double[] nutrients = db.selectNutrientsAt(index);
        db.close();
        // We now also want to get the calories obtained during this time and the calorie goal
        WeightCalculator weightCalculator = new WeightCalculator(getContext());
        double caloriesLeft = weightCalculator.getBMR() - nutrients[0];
        // we can check the caloriesLeft, and set the colour based on if we past the code or not
        // without using the R.colour, we can set using a hex-value
        caloriesObtained.setTextColor((caloriesLeft >= 0) ? Color.parseColor("#2ecc71") : Color.parseColor("#e74c3c"));
        // set the text
        calorieGoal.setText(weightCalculator.getCalorieGoal());
        caloriesObtained.setText("" + caloriesLeft);
        // set up other nutrients to add
        totalFat.setText(String.format("%.2f", nutrients[1]));
        totalCarbs.setText(String.format("%.2f", nutrients[2]));
        totalProtein.setText(String.format("%.2f", nutrients[3]));
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
