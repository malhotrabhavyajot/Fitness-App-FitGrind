package ca.stclaircollege.fitgrind;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.api.Nutrient;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFoodFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFoodFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private long foodId;
    private Food food;
    private ListView mListView;

    private OnFragmentInteractionListener mListener;

    public EditFoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditFoodFragment.
     * @param id
     */
    // TODO: Rename and change types and number of parameters
    public static EditFoodFragment newInstance(long id) {
        EditFoodFragment fragment = new EditFoodFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodId = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_food, container, false);
        mListView = (ListView) view.findViewById(R.id.edit_food_listview);
        // we can instantiate a food object here
        if (foodId != 0) {
            DatabaseHandler db = new DatabaseHandler(getContext());
            food = db.selectFood(foodId);
            db.close();
            // check to make sure if food is correctly grabbed
            if (food != null) {
                // now we want to set it up, there is going to be a list view, and then if you click it'll open a dialog.
                // connect the adapter
                mListView.setAdapter(new CustomAdapter(getContext(), food.getNutrients()));

                // now we also want to edit by using the long item click listener
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        // create a dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        // setup title
                        Nutrient nutrient = (Nutrient) mListView.getItemAtPosition(position);
                        builder.setTitle("Edit " + nutrient.getNutrient() + " Value");

                        // Set up the input
                        final EditText input = new EditText(getContext());

                        // set the text to equal what nutrient it was
                        input.setText("" + nutrient.getValue());

                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // if it clicked ok, we need to create a db instance and make sure it goes through and works
                                DatabaseHandler db = new DatabaseHandler(getContext());
                                // we can use Food Id
                                food.setId(foodId);
                                // updaet the nutrient too
                                food.getNutrients().get(position).setValue(Double.parseDouble(input.getText().toString()));
                                // start the query
                                if (db.updateFood(food)) {
                                    ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                    Toast.makeText(getContext(), R.string.db_update_success, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
            }
        }

        return view;
    }

    public class CustomAdapter extends ArrayAdapter<Nutrient> {

        public CustomAdapter(Context context, ArrayList<Nutrient> nutrients) {
            super(context, 0, nutrients);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Nutrient nutrient = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_edited_food_item, parent, false);

            // connect Text View
            TextView nutrientName = (TextView) convertView.findViewById(R.id.nutrient_name);
            TextView nutrientValue = (TextView) convertView.findViewById(R.id.nutrient_value);

            // set the text view
            nutrientName.setText(nutrient.getNutrient());
            nutrientValue.setText(nutrient.getValue() + nutrient.getUnit());

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
