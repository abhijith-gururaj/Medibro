package b5.project.medibro.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import b5.project.medibro.AddPrescription;
import b5.project.medibro.R;
import b5.project.medibro.receivers.Medication;
import b5.project.medibro.utils.DatabaseHandler;
import b5.project.medibro.utils.MyMedAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrescriptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PrescriptionsFragment extends Fragment {

    private static final String TAG = PrescriptionsFragment.class.getSimpleName();
    FloatingActionButton fab;
    ListView prescriptionsList;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Medication> medications;
    TextView tv;

    public PrescriptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prescriptions, container, false);
        Log.d(TAG, "On Create Called");
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPrescription.class));
            }
        });
        tv = (TextView) v.findViewById(R.id.noItems);
        prescriptionsList = (ListView) v.findViewById(R.id.prescriptions_list);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "On Resume");
        DatabaseHandler db = new DatabaseHandler(getActivity());
        HashMap<Integer, Medication> hashMap = db.getMedicationDetails();
        medications = new ArrayList<>();
        Log.d(TAG, "hashmap size: " + hashMap.size());
        for (int i = 0; i < hashMap.size(); i++) {
            medications.add(i, hashMap.get(i));
        }
        prescriptionsList.setAdapter(new MyMedAdapter(getActivity(), "all", medications));
        prescriptionsList.setEmptyView(tv);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
