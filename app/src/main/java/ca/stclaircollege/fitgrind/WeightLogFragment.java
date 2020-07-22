package ca.stclaircollege.fitgrind;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import com.github.clans.fab.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.Progress;
import ca.stclaircollege.fitgrind.database.Weight;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeightLogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeightLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeightLogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // ACTIVITY RESULTS
    private static final int GALLERY_INTENT = 1;
    private static final int CAMERA_INTENT = 2;

    // PERMISSION RESULTS
    private static final int STORAGE_REQUEST = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView mCurrentWeight, mWeightGoal;
    private Button mViewProgressButton;
    private ArrayList<Weight> weightList;
    private WeightCalculator weightCalculator;
    private ListView mListView;
    private FloatingActionButton fab;
    private String mCurrentPhotoPath;
    private long weightId = -1;

    public WeightLogFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeightLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeightLogFragment newInstance(String param1, String param2) {
        WeightLogFragment fragment = new WeightLogFragment();
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

        // weight calculator
        weightCalculator = new WeightCalculator(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight_log, container, false);

        // connect from the layout
        mCurrentWeight = (TextView) view.findViewById(R.id.current_weight_label);
        mWeightGoal = (TextView) view.findViewById(R.id.weight_goal_label);
        mViewProgressButton = (Button) view.findViewById(R.id.viewProgressButton);
        mListView = (ListView) view.findViewById(R.id.listview_weight);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        // open up db and set it up
        DatabaseHandler db = new DatabaseHandler(getContext());
        weightList = db.selectAllWeightLog();
        db.close();

        // set up custom adapter
        CustomAdapter adapter = new CustomAdapter(getContext(), weightList);
        mListView.setAdapter(adapter);

        // we want to set the text view for last logged weight, last calories and calories goal
        mCurrentWeight.setText("Current Weight: " + weightCalculator.getCurrentWeight());

        // now for weight goal
        mWeightGoal.setText("Weight Goal: " + weightCalculator.getWeightGoal());

        // set up listener for fab button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a db
                DatabaseHandler db = new DatabaseHandler(getContext());
                //get date today
                final String lastDate = db.lastRecordedWeightLog();
                db.close();
                // if it's past 7 days, we can let the user insert the weight log
                // find the difference between the dates.
                // if we can't retrieve the last date, we can set it automatically to 7 because there isn't a date recorded
                long diff = (lastDate != null) ? 0 : 7;
                // parse and check
                if (lastDate != null) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastDate);
                        Date now = Calendar.getInstance(Locale.getDefault()).getTime();
                        diff = now.getTime() - date.getTime();
                        diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                // check to make sure it's there
                if (diff >= 7) {
                    // once it clicks, instead of opening up another fragment, this time we're going to open a dialog instead.
                    // we're doing this because all we have to record is the weight, nothing more.
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Add Weight Log");
                    dialog.setMessage("Enter in your current weight:");

                    // we want to create an edit text for the user to input in
                    final EditText input = new EditText(getContext());

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    dialog.setView(input);

                    // now we want to set up the box

                    dialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // if clicked, we want to retrieve the current date and weight
                            Double weight = Double.parseDouble(input.getText().toString());
                            // we want to set the text view for last logged weight, last calories and calories goal
                            Calendar cal = Calendar.getInstance(Locale.getDefault());
                            String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                            // set it up as a new weight object
                            Weight weightLog = new Weight(weight, currDate);
                            // create a db
                            DatabaseHandler db = new DatabaseHandler(getContext());
                            long id = db.insertWeight(weightLog);
                            db.close();
                            // insert the weight
                            if (id != -1) {
                                // notify data set and add
                                weightLog.setId(id);
                                weightList.add(weightLog);
                                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                // we also want to set the edited shared preferences too
                                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor edit = SP.edit();
                                edit.putString("weight", ""+weight);
                                edit.commit();
                                // now we want to set the current weight text change
                                mCurrentWeight.setText(String.format("Current Weight: %.1f lbs", weight));
                                Toast.makeText(getActivity(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.db_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { dialogInterface.dismiss(); }
                    });
                    // show the dialog
                    dialog.show();

                } else {
                    // create a toast indicating that you need to create it another time.
                    Toast.makeText(getActivity(), String.format(getString(R.string.invalid_days), 7 - diff), Toast.LENGTH_LONG).show();
                }
            }
        });

        // create the listener for when an item is clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // in here, we want to add an image.
                // so we'll check if there's an image here or not through db
                // we also need the item obj
                Weight weight = (Weight) mListView.getItemAtPosition(i);
                DatabaseHandler db = new DatabaseHandler(getContext());
                Progress progress = db.selectProgress(weight.getId());
                db.close();
                // check if a progress exists
                // if it doesn't we can open up a gallery or a progress for them to get it in
                if (progress == null) {
                    // check for permissions
                    weightId = weight.getId();
                    requestStoragePermissions();
                } else {
                    // show the activity
                    Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
                    intent.putExtra("progress", progress);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });

        //   now we want to set-up event listeners for the button
        mViewProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we want to open using an activity so we have the trash icon on the top right
                // we also want to check for view progress on the button. if there's no photo then we shouldn't put anying there
                DatabaseHandler db = new DatabaseHandler(getContext());
                if (db.isProgressEmpty()) {
                    Toast.makeText(getActivity(), R.string.db_empty, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), ViewProgressActivity.class);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                db.close();
            }
        });

        return view;
    }

    // create a custom adapter for this
    public class CustomAdapter extends ArrayAdapter<Weight> {

        public CustomAdapter(Context context, ArrayList<Weight> weightList) {
            super(context, 0, weightList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // get the weight object
            final Weight weightItem = getItem(position);

            // setup layout
            if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_weight_log, parent, false);

            // get text views
            TextView date = (TextView) convertView.findViewById(R.id.recorded_date);
            final TextView weight = (TextView) convertView.findViewById(R.id.recorded_weight);

            // set it up
            date.setText(weightItem.getFormattedDate());
            weight.setText(weightItem.getWeight() + "lbs");

            // check out the menu button

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
                                    // create another alert dialog with a final edit text
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                                    // set up dialog messages
                                    dialog.setTitle("Edit Weight Log");
                                    dialog.setMessage("Edit weight log\'s weight.");

                                    // create 1 edit text
                                    // we want to create an edit text for the user to input in
                                    final EditText input = new EditText(getContext());

                                    //set up input
                                    input.setText("" + weightItem.getWeight());

                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                    dialog.setView(input);

                                    // set up listeners for positive and negative buttons

                                    dialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // here we attempt to update.
                                            // if update is sucessful, notify the dataset
                                            DatabaseHandler db = new DatabaseHandler(getContext());
                                            Double parsedWeight = Double.parseDouble(input.getText().toString());
                                            weightItem.setWeight(parsedWeight);
                                            if (db.updateWeight(weightItem)) {
                                                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                                Toast.makeText(getActivity(), R.string.db_update_success, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) { dialogInterface.dismiss(); }
                                    });

                                    dialog.show();

                                    break;
                                case R.id.delete:
                                    DatabaseHandler db = new DatabaseHandler(getContext());
                                    if (db.deleteWeight(weightItem.getId())) {
                                        // remove from the array too
                                        weightList.remove(position);
                                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                        // we also want to delete from progress too if it exists
                                        db.deleteProgressByWeight(weightItem.getId());
                                        Toast.makeText(getActivity(), R.string.db_delete_success, Toast.LENGTH_SHORT).show();
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
            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_INTENT && resultCode == getActivity().RESULT_OK) {
            String path = getPath(getContext(), data.getData());
            if (path != null) {
                // insert the progress
                DatabaseHandler db = new DatabaseHandler(getContext());
                boolean result = db.insertProgress(new Progress(path),weightId);
                if (result) {
                    Toast.makeText(getActivity(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.db_error, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_INTENT && resultCode == getActivity().RESULT_OK) {
            // insert the progress
            DatabaseHandler db = new DatabaseHandler(getContext());
            boolean result = db.insertProgress(new Progress(mCurrentPhotoPath), weightId);
            if (result) {
                Toast.makeText(getActivity(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.db_error, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // check for permisisons
        if (requestCode == STORAGE_REQUEST && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            openPhotoDialog();
        }
    }

    /**
     *  This method helps us get the real absolute path from a gallery picture. This is used when the user selects the gallery.
     *  Once selected, this will be inserted into the db.
     * @param context
     * @param uri
     * @return
     */
    private String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        return result;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openPhotoDialog() {
        // We need to open an alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
        dialog.setTitle("Select Option");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        // if ewe have a camera available, we can use it
                        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                            // launch camera intent
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                // Create a file where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch(IOException e) { e.printStackTrace(); }
                                // Continue only if file was successfully created
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(getContext(), "ca.stclaircollege.fileprovider", photoFile);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(intent, CAMERA_INTENT);
                                }
                            }
                        } else {
                            // if not then we launch the gallery intent instead
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivityForResult(intent, GALLERY_INTENT);
                            }
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(intent, GALLERY_INTENT);
                        }
                        break;
                    default:
                        dialogInterface.dismiss();
                        break;
                }
            }
        });
        // show dialog
        dialog.show();
    }

    public void requestStoragePermissions() {
        // check the the version and make sure it's higher than 23 which is marshmellow
        if (Build.VERSION.SDK_INT >= 23) {
            // check permissions here
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openPhotoDialog();
            } else {
                // request permissions here
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST);
            }
        } else {
            // permissions are automatically added with the android manifest xml file. if it's lower than 23, then it's using lollipop and lower.
            openPhotoDialog();
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
