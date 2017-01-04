package com.sidzi.circleofmusic.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sidzi.circleofmusic.R;

import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {
    EditText etTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        etTimePicker = (EditText) findViewById(R.id.etTimePicker);
        etTimePicker.setHint("hhmm");
        Button bSetAlarm = (Button) findViewById(R.id.bSetAlarm);
        bSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
    }

    void setAlarm() {

        Intent intent = new Intent("com.sidzi.circleofmusic.PLAY_TRACK");

        intent.putExtra("track_path", "https://circleofmusic-sidzi.rhcloud.com/streamrock");
        intent.putExtra("track_name", "Alarm");
        intent.putExtra("track_artist", "Wake up!");
        intent.putExtra("bucket", false);


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 445165, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);

        int hour = Integer.parseInt(etTimePicker.getText().subSequence(0, 2).toString());
        int minute = Integer.parseInt(etTimePicker.getText().subSequence(2, 4).toString());

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), Calendar.DATE, hour, minute);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
