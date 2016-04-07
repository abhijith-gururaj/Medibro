package b5.project.medibro.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import b5.project.medibro.R;
import b5.project.medibro.receivers.Medication;

/**
 * Created by Abhijith on 1/30/2016.
 */
public class MyMedAdapter extends BaseAdapter {
    ArrayList<Medication> medications;
    public static final String TAG = "MyMedAdapter";
    Context context;
    String type;
    TextView medName, medIntervals, timersTv;

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        populateMedications();
    }

    public MyMedAdapter(Context context, String type, ArrayList<Medication> medications) {
        this.context = context;
        this.type = type;
        this.medications = medications;
    }

    private void populateMedications() {
        medications = new ArrayList<>();
        DatabaseHandler db = new DatabaseHandler(context);
        HashMap<Integer, Medication> hashMap = db.getMedicationDetails();
        for (int i = 0; i < hashMap.size(); i++) {
            medications.add(i, hashMap.get(i));
        }
    }

    @Override
    public int getCount() {
        return medications.size();
    }

    @Override
    public Object getItem(int position) {
        return medications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    View layout;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (type.equals("all")) {
            layout = inflater.inflate(R.layout.prescriptions_list_item, parent, false);

            medName = (TextView) layout.findViewById(R.id.med_name_tv);
            medIntervals = (TextView) layout.findViewById(R.id.intervals_tv);
            timersTv = (TextView) layout.findViewById(R.id.timers_tv);

            Medication medication = medications.get(position);
            medName.setText(medication.getName());
            medIntervals.setText(medication.getIntervals() + " times a day");

            String[] timers = medication.getReminder_times().split("\\$\\$\\$");
            String reminder_times = "";

            for (String timer : timers) {
                reminder_times += timer + "     ";
            }
            timersTv.setText(reminder_times);

            Log.d(TAG, "Medication : " + medication.getName() + " : " + reminder_times);
        } else if (type.equals("names")) {
            layout = inflater.inflate(R.layout.simple_medication_item, parent, false);
            Medication medication = medications.get(position);
            medName = (TextView) layout.findViewById(R.id.med_name_tv);
            medName.setText(medication.getName());
        }
        return layout;
    }
}
