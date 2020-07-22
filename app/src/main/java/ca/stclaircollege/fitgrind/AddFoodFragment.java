package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.api.FoodAPI;
import ca.stclaircollege.fitgrind.api.Item;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * AddFoodFragment class handles the search and view aspects of the food item you've searched for.
 * Once searched, it opens up a new Fragment that allows us to find fragments
 */
public class AddFoodFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // get the connections
    private FloatingActionButton searchButton;
    private EditText searchField;
    private LinearLayout progressBar;

    // Recycler View
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    // set-up private API key
    // search for the food based on searchField text
    private FoodAPI foodApi;

    // Instead of creating a class for this, we can set up private variables for start, end, and total for the result returned
    private int start, end, total;

    private ArrayList<Item> itemList = new ArrayList<Item>();

    public AddFoodFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create api here
        foodApi = new FoodAPI(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);

        // connect from the layout
        searchButton = (FloatingActionButton) view.findViewById(R.id.searchButton);
        searchField = (EditText) view.findViewById(R.id.searchField);
        progressBar = (LinearLayout) view.findViewById(R.id.progressBar);

        // set-up the recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // we want to set a fixed size, we know the content won't change
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // use a search field for your adapter
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    dismissKeyboard();
                    if (searchField.getText().length() != 0) searchFood();
                    return true;
                }
                return false;
            }
        });

        // create search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissKeyboard();
                // we can use the same searchfood
                if (searchField.getText().length() != 0) searchFood();

            }
        });

        // add a recyclerview item click

        return view;
    }

    /**
     * This method dismisses keyboard as long as the view is active.
     */
    private void dismissKeyboard() {
        // dismisses keyboard
        // Check if no view has focus
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * This method searches for food
     */
    private void searchFood(){
        // show progress
        progressBar.setVisibility(View.VISIBLE);
        Observable<ArrayList<Item>> itemObservable = foodApi.searchFood(searchField.getText().toString());
        itemObservable.subscribe(new Observer<ArrayList<Item>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(ArrayList<Item> items) {
                mRecyclerView.setAdapter(new MyAdapter(items));
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() { progressBar.setVisibility(View.GONE); }
        });
    }

    /**
     * Our adapter class for RecyclerView. This handles the layout issues on what it needs to have.
     */
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Item> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private TextView name, group;

            public ViewHolder(View view) {
                super(view);

                this.name = (TextView) view.findViewById(R.id.name);
                this.group = (TextView) view.findViewById(R.id.group);
            }

            public TextView getNameTextView() { return this.name; }
            public TextView getGroupTextView() { return this.group; }

        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<Item> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent, false);

            // create an event handler for each adapter
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // clear textfield
                    searchField.setText("");
                    // we wanna get the position of where it is
                    int position = mRecyclerView.getChildLayoutPosition(v);
                    // we can reference from the mDataset, and launch a new fragment
                    // but we need to get the fragment Manager
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = fm.beginTransaction();
                    // get the position and reference mDataset to add
                    trans.replace(R.id.content_main, ViewFoodFragment.newInstance(mDataset.get(position).getNdbno()));
                    trans.addToBackStack(null);
                    trans.commit();
                }
            });

            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(view);

        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.getNameTextView().setText(mDataset.get(position).getName());
            holder.getGroupTextView().setText(mDataset.get(position).getGroup());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
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
