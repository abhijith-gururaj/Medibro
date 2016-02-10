package b5.project.medibro.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import b5.project.medibro.Dashboard;
import b5.project.medibro.Medication;
import b5.project.medibro.MedicationDetails;
import b5.project.medibro.R;
import b5.project.medibro.utils.MyMedAdapter;


public class MedicationsFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView listView;

    public MedicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_medications, container, false);
        ((Dashboard) getActivity()).getSupportActionBar().setTitle("Medications");
        listView = (ListView) rootView.findViewById(R.id.medications_list);
        listView.setAdapter(new MyMedAdapter(getActivity(), "names"));
        listView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Medication medication = (Medication) parent.getAdapter().getItem(position);

        Bundle args = new Bundle();
        args.putInt("medId", medication.getId());
        args.putString("medName", medication.getName());
        args.putString("medIntervals", medication.getIntervals());
        args.putString("medReminders", medication.getReminder_times());
        args.putString("medStartDate", medication.getStart_date());
        args.putString("medNotes", medication.getNotes());
        args.putString("medDuration", medication.getDuration());

        Toast.makeText(getActivity(),
                medication.getName() + " : " + medication.getId(),
                Toast.LENGTH_LONG)
                .show();
        Intent intent = new Intent(getActivity(), MedicationDetails.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_edit_profile);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
