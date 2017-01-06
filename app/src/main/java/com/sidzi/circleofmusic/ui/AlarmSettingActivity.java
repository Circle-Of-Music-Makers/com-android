package com.sidzi.circleofmusic.ui;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

public class AlarmSettingActivity extends AppCompatActivity {
    EditText etTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);
        etTimePicker = (EditText) findViewById(R.id.etTimePicker);

        Button bSelectAlarm = (Button) findViewById(R.id.bSelectAlarm);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder innerBuilder = new AlertDialog.Builder(this);

        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);


        bSelectAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Select a sound")
                        .setMessage("Select from a list of CoM alarms or choose from your own collection")
                        .setPositiveButton("CoM Alarms", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
//                                Load CoM alarms
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                JsonObjectRequest alarmArray = new JsonObjectRequest(Request.Method.GET, config.com_url + "getAlarms", null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            final JSONArray alarms = response.getJSONArray("alarms");
                                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_row_alarm_selection);
                                            for (int i = 0; i < alarms.length(); i++)
                                                arrayAdapter.add(alarms.get(i).toString());
                                            innerBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (!new File(config.com_local_url + arrayAdapter.getItem(i)).exists()) {
                                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(config.com_url + "getAlarm" + arrayAdapter.getItem(i)));
                                                        request.setTitle("Downloading Alarm Sound");
                                                        request.setDestinationInExternalPublicDir("", "com-data/alarms/" + arrayAdapter.getItem(i));
                                                        downloadManager.enqueue(request);
                                                    }
                                                    setAlarm(arrayAdapter.getItem(i));
                                                    finish();
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                requestQueue.add(alarmArray);
                                dialogInterface.dismiss();
                                innerBuilder.create().show();
                            }
                        })
                        .setNegativeButton("Personal Music", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Load file browser
                            }
                        });
                builder.create().show();
            }
        });
        etTimePicker.setHint("\"hhmm\" in 24 hr format");
    }

    void setAlarm(String alarm) {

        Intent intent = new Intent(this, AlarmActivity.class);

        intent.putExtra("alarm_path", config.com_local_url + alarm);
        intent.putExtra("after_alarm_path", "Alarm");


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 443, intent, PendingIntent.FLAG_ONE_SHOT);

        int hour = Integer.parseInt(etTimePicker.getText().subSequence(0, 2).toString());
        int minute = Integer.parseInt(etTimePicker.getText().subSequence(2, 4).toString());

        int dH = diffH(calendar.get(Calendar.HOUR_OF_DAY), hour);

        if (calendar.get(Calendar.HOUR_OF_DAY) > hour)
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 1, hour, minute);
        else
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, minute);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(getApplicationContext(), "Setting Alarm for " + dH + " hrs from now", Toast.LENGTH_LONG).show();
    }

    int diffH(int c, int s) {
//        TODO Correct subtraction of time
        if (c <= s) {
            return s - c;
        } else {
            return s + 24 - c;
        }
    }
}
