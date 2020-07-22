package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import java.io.File;
import ca.stclaircollege.fitgrind.database.Progress;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewProgressLogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewProgressLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProgressLogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Progress mProgress;
    private PhotoView mImageView;

    private OnFragmentInteractionListener mListener;

    public ViewProgressLogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param progress Parameter 1.
     * @return A new instance of fragment ViewProgressLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewProgressLogFragment newInstance(Progress progress) {
        ViewProgressLogFragment fragment = new ViewProgressLogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, progress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProgress = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_progress_log, container, false);

        // get action bar
        final ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // connect
        mImageView = (PhotoView) view.findViewById(R.id.progress_imageview);

        // check resource
        if (mProgress != null) {
            // Use Picasso set-up the imageview
            Picasso.with(getActivity()).load(new File(mProgress.getResource())).into(mImageView);

            // we also want to set up the listener too
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // hide or view action bar depending on state
                    if (bar.isShowing()) {
                        bar.hide();
                    } else {
                        bar.show();
                    }
                }
            });
        }

        return view;
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
