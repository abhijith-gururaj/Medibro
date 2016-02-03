package b5.project.medibro.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import b5.project.medibro.R;

/**
 * Created by Abhijith on 1/31/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("b5.project.medibro.receivers.AlarmReceiver")) {
            Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
            Log.d("Alarm Receiver", "Alarm is invoked. ");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.drawable.pill);
            builder.setContentTitle("Time to take your medicine");
            String medName = intent.getStringExtra("MedName");
            String text = "Medicine name: " + medName;
            builder.setContentText(text);

            Notification notification = builder.build();
            NotificationManagerCompat.from(context).notify(0, notification);
            Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        }
    }
}
