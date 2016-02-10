package b5.project.medibro;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import b5.project.medibro.receivers.AlarmReceiver;
import b5.project.medibro.receivers.CancelAlarmBroadcastReceiver;
import b5.project.medibro.utils.DatabaseHandler;
import b5.project.medibro.utils.NonScrollListView;

public class AddPrescription extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    AppCompatEditText mMedicationName, mAdditionalNotes;
    Spinner spinner;
    NonScrollListView mListView;
    TextView mStartDate;
    String startDate;
    Toolbar toolbar;
    int intervals;
    String[] timers;
    String duration;
    String medication_name, additional_notes;
    int medSelectedDay, medSelectedMonth, medSelectedYear;

    private static final String TAG = "Add Prescription";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Medication");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMedicationName = (AppCompatEditText) findViewById(R.id.medication_name);
        mAdditionalNotes = (AppCompatEditText) findViewById(R.id.additional_notes);
        mListView = (NonScrollListView) findViewById(R.id.reminders_listView);
        spinner = (Spinner) findViewById(R.id.reminders_spinner);
        mStartDate = (TextView) findViewById(R.id.start_date_tv);
        Calendar cal = Calendar.getInstance();
        Date currDate = cal.getTime();
        String currentDate = currDate.getDate() + "/" + (currDate.getMonth() + 1) + "/" + (currDate.getYear() + 1900);
        mStartDate.setText(currentDate);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                intervals = position + 1;
                timers = new String[intervals];
                for (int i = 1; i <= intervals; i++) {
                    timers[i - 1] = "8:00";
                }
                ListAdapter adapter = new ArrayAdapter<String>(AddPrescription.this,
                        R.layout.reminders_time_list_item,
                        timers);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddPrescription.this, "Please make a selection.", Toast.LENGTH_SHORT).show();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                String t = (String) parent.getItemAtPosition(position);
                Log.d("Listview item clicked", t);

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddPrescription.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String enteredTime = selectedHour + ":" + selectedMinute;
                        ((TextView) view).setText(enteredTime);
                        timers[position] = enteredTime;
                        Log.d(TAG, "" + position + " : " + timers[position]);
                    }
                }, hour, minute, true);//24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                final DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(AddPrescription.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        medSelectedDay = selectedday;
                        medSelectedMonth = selectedmonth;
                        medSelectedYear = selectedyear;
                        startDate = "" + selectedday + "/" + selectedmonth + "/" + selectedyear;
                        mStartDate.setText(startDate);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });
    }

    public void onDurationButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.continuous_days:
                duration = "CONTINUOUS";
                break;

            case R.id.no_of_days:
                show();
                break;
        }
    }

    private void show() {
        final Dialog d = new Dialog(AddPrescription.this);
        d.setContentView(R.layout.number_picker);
        Button b1 = (Button) d.findViewById(R.id.btn_set);
        Button b2 = (Button) d.findViewById(R.id.btn_cancel);
        final RadioButton mRadio = (RadioButton) findViewById(R.id.no_of_days);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration = String.valueOf(np.getValue());
                String radioText = "Number of days: " + duration;
                mRadio.setText(radioText);
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_prescription, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            int alarms = mListView.getAdapter().getCount();
            Log.d(TAG, "No. of Alarms: " + alarms);
            addMedication();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    long id;

    private void addMedication() {
        DatabaseHandler handler = new DatabaseHandler(this);

        medication_name = mMedicationName.getText().toString();
        additional_notes = mAdditionalNotes.getText().toString();
        String reminder_times = "";
        for (String timer : timers) {
            reminder_times += timer + "$$$";
        }
        reminder_times = reminder_times.substring(0, reminder_times.lastIndexOf("$$$"));
        id = handler.addMedication(medication_name, intervals, reminder_times,
                startDate, duration, additional_notes);

        handler.close();
        setAlarms(id);
    }

    private void setAlarms(long id) {
        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        myIntent.putExtra("MedName", medication_name);
        myIntent.setAction("b5.project.medibro.receivers.AlarmReceiver");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        DatabaseHandler db = new DatabaseHandler(this);
        int setId = db.getLatestAlarmId();
        int cancelId = Integer.valueOf(String.valueOf(setId) + "0000");
        for (String timer : timers) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm");
            String[] comps = timer.split(":");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(comps[0]));
            cal.set(Calendar.MINUTE, Integer.valueOf(comps[1]));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            //Date date= dateFormat.parse(startDate + " " + timer);
            Log.d(TAG, comps[0] + " " + comps[1] + "Alarm Time: " + cal.getTime().toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), cancelId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    setId,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
            Log.d(TAG, "Alarm pid: " + setId);
            setId++;
            if (!duration.equals("CONTINUOUS")) {
                int cancellationDate = medSelectedDay + Integer.valueOf(duration);
                String cancelDate = cancellationDate + "/" + medSelectedMonth + "/" + medSelectedYear;

                Calendar cancelCal = Calendar.getInstance();
                cancelCal.set(Calendar.DAY_OF_MONTH, medSelectedDay);
                cancelCal.set(Calendar.MONTH, medSelectedMonth);
                cancelCal.set(Calendar.YEAR, medSelectedYear);
                cancelCal.set(Calendar.HOUR_OF_DAY, 23);
                cancelCal.set(Calendar.MINUTE, 59);
                cancelCal.set(Calendar.SECOND, 59);
                Intent cancellationIntent = new Intent(this, CancelAlarmBroadcastReceiver.class);
                cancellationIntent.putExtra("key", pendingIntent);
                PendingIntent cancellationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), cancelId, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP,
                        cancelCal.getTimeInMillis(),
                        cancellationPendingIntent);
                Log.d(TAG, "Cancelling alarm at: " + cancelDate + " pid: " + cancelId);
            }
            cancelId++;
            db.addAlarm(id, setId, cancelId);
        }
    }
}
