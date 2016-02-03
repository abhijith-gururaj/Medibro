package b5.project.medibro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import b5.project.medibro.utils.NonScrollListView;

public class MedicationDetails extends AppCompatActivity {

    NonScrollListView listView;
    TextView startDate, notes, duration;
    int medId;
    String intervals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (NonScrollListView) findViewById(R.id.reminders_listView);
        startDate = (TextView) findViewById(R.id.start_date_tv);
        notes = (TextView) findViewById(R.id.additional_notes);
        duration = (TextView) findViewById(R.id.duration_tv);

        Bundle args = getIntent().getExtras();
        medId = args.getInt("medId");
        startDate.setText(args.getString("medStartDate"));
        notes.setText(args.getString("medNotes"));
        duration.setText(args.getString("medDuration"));
        intervals = args.getString("medIntervals");
        getSupportActionBar().setTitle(args.getString("medName"));

        String[] timers = args.getString("medReminders").split("\\$\\$\\$");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                timers);

        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medication_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_med) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
