package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.Program;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkoutProgramFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkoutProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  WorkoutProgramFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int LIST_REQUEST = 1;
    ListView list;
    CustomAdapter adapter;
    ArrayList<Program> programsList;


    private OnFragmentInteractionListener mListener;

    public WorkoutProgramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutProgramFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutProgramFragment newInstance(String param1, String param2) {
        WorkoutProgramFragment fragment = new WorkoutProgramFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    FragmentManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_program, container, false);
        fm = getActivity().getSupportFragmentManager();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabProgram);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a dialog instead of the activity intent
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Create New Program");
                // create a view to inflate from
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_edited_program, null);

                // set up the edit text
                final EditText name = (EditText) dialogView.findViewById(R.id.editNameEditText);
                final EditText desc = (EditText) dialogView.findViewById(R.id.editDescriptionEditText);
                // set the view
                dialog.setView(dialogView);

                // create event listeners for ok and cancel

                dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (name.getText().toString().trim().length() != 0 && desc.getText().toString().trim().length() != 0) {
                            // Create db handler
                            DatabaseHandler db = new DatabaseHandler(getContext());
                            Program program = new Program(name.getText().toString(), desc.getText().toString());
                            long id = db.insertProgram(program);
                            db.close();
                            // check for id
                            if (id != -1) {
                                // set the id so we can add it to the array
                                program.setId(id);
                                // add it to the arraylist and notify data set
                                programsList.add(program);
                                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                // create a toast
                                Toast.makeText(getContext(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), R.string.db_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), R.string.invalid_field, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                // show dialog
                dialog.show();
            }
        });
        list = (ListView) view.findViewById(R.id.workoutProgramList);
        DatabaseHandler db = new DatabaseHandler(getContext());
        //final ArrayList<Program> programsList = new ArrayList<Program>();
        programsList = db.selectAllRoutine();
        db.close();

        adapter = new CustomAdapter(getContext(), programsList);
        list.setAdapter(adapter);

        //launch to new activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), WorkoutExerciseActivity.class);
                intent.putExtra("id", programsList.get(position).getId());
                startActivity(intent);
            }
        });

        return view;
    }


    public class CustomAdapter extends ArrayAdapter<Program> {
        public CustomAdapter(Context context, ArrayList<Program> items) {
            super(context, 0, items);
        }

        //get each item and assign a view to it
        public View getView(final int position, View convertView, ViewGroup parent){
            final Program program = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.program_view, parent, false);
            }

            //set the listview items
            final TextView name = (TextView) convertView.findViewById(R.id.programName);
            name.setText(program.getName());

            TextView description = (TextView) convertView.findViewById(R.id.programDescription);
            description.setText(program.getDescription());

            // create ImageView
            final ImageView menuButton = (ImageView) convertView.findViewById(R.id.editProgram);

            // create a listener
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // pop up menu
                    PopupMenu menu = new PopupMenu(getContext(), menuButton);
                    //inflate the pop up menu with the xml
                    menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());

                    // create an event listener
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    //
                                    builder.setTitle("Edit " + program.getName());
                                    //edittext for input
//                                    final EditText editText = new EditText(getContext());
                                    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_edited_program, null);
                                    // get edit text
                                    final EditText editText = (EditText) dialogView.findViewById(R.id.editNameEditText);
                                    final EditText editText2 = (EditText) dialogView.findViewById(R.id.editDescriptionEditText);

                                    // setup the text from program
                                    editText.setText(program.getName());
                                    editText2.setText(program.getDescription());

                                    builder.setView(dialogView);
                                    // Set up the buttons
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            program.setName(editText.getText().toString());
                                            program.setDescription(editText2.getText().toString());
                                            // if it clicked ok, we need to create a db instance and make sure it goes through and works
                                            DatabaseHandler db = new DatabaseHandler(getContext());
                                            // start the query
                                            if (db.updateRoutine(program)) {
                                                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                                Toast.makeText(getContext(), R.string.db_update_success, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                    break;

                                case R.id.delete:
                                    //delete from db
                                    DatabaseHandler db = new DatabaseHandler(getContext());
                                    if (db.deleteRoutine(program.getId())) {
                                        programsList.remove(position);
                                        // we also wanna make a notify update
                                        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
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

            return  convertView;
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
