package com.sidzi.circleofmusic.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;
import com.sidzi.circleofmusic.BuildConfig;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.config;
import com.sidzi.circleofmusic.fragments.BucketFragment;
import com.sidzi.circleofmusic.fragments.LocalMusicFragment;
import com.sidzi.circleofmusic.fragments.PotmFragment;
import com.sidzi.circleofmusic.fragments.ShoutboxFragment;
import com.sidzi.circleofmusic.fragments.TheFifthFragment;
import com.sidzi.circleofmusic.helpers.BucketSaver;
import com.sidzi.circleofmusic.helpers.DatabaseSynchronization;
import com.sidzi.circleofmusic.receivers.MediaButtonHandler;
import com.sidzi.circleofmusic.receivers.MusicPlayerViewHandler;
import com.sidzi.circleofmusic.services.MusicPlayerService;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ServiceConnection musicServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Rollbar.init(this, config.rollbar_key, config.rollbar_environment);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
                requestPermissions(perms, 202);
            }
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest eosCheck = new JsonObjectRequest(Request.Method.GET, config.com_url + "checkEOSVersion", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if ((int) response.get("eos_version") > BuildConfig.VERSION_CODE) {
                            startActivity(new Intent(MainActivity.this, EosActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(eosCheck);


            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MusicPlayerService.ACTION_UPDATE_METADATA);
            intentFilter.addAction(MusicPlayerService.ACTION_PAUSE);
            intentFilter.addAction(MusicPlayerService.ACTION_PLAY);
            intentFilter.addAction(MusicPlayerService.ACTION_CLOSE);


            Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
            if (MusicPlayerService.PLAYING_TRACK == null)
                startService(intent);
            musicServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) iBinder;

                    if (MusicPlayerService.PLAYING_TRACK != null) {
                        Intent intent = new Intent(MusicPlayerService.ACTION_UPDATE_METADATA);
                        intent.putExtra("track_metadata", MusicPlayerService.PLAYING_TRACK);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        if (musicBinder.getService().mMediaPlayer.isPlaying())
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(MusicPlayerService.ACTION_PLAY));
                        else
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(MusicPlayerService.ACTION_PAUSE));
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };
            bindService(intent, musicServiceConnection, 0);

            MusicPlayerViewHandler mMusicPlayerViewHandler = new MusicPlayerViewHandler(this);
            LocalBroadcastManager.getInstance(this).registerReceiver(mMusicPlayerViewHandler, intentFilter);


            /* Handles headphone button click */


            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            ComponentName componentName = new ComponentName(getPackageName(), MediaButtonHandler.class.getName());
            //noinspection deprecation
            audioManager.registerMediaButtonEventReceiver(componentName);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


            ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            final LinearLayout llPlaybackPanel = (LinearLayout) findViewById(R.id.llPlayerPanel);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 4) {
                        llPlaybackPanel.setVisibility(View.GONE);
                    } else {
                        if (llPlaybackPanel.getVisibility() != View.VISIBLE) {
                            llPlaybackPanel.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            new DatabaseSynchronization(MainActivity.this).execute();
            final SearchView mSearchView = (SearchView) findViewById(R.id.svTrackSearch);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    mSearchView.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        SharedPreferences settings = getSharedPreferences("com_prefs", 0);
        if (settings.getBoolean("registered", false))
            menu.removeItem(R.id.register);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                SearchView mSearchView = (SearchView) findViewById(R.id.svTrackSearch);
                if (mSearchView.getVisibility() == View.VISIBLE) {
                    mSearchView.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                } else {
                    mSearchView.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.alarm:
                Intent intent = new Intent(this, AlarmSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.sleepTimer:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setTitle("# of songs till sleep")
                        .setView(editText)
                        .setPositiveButton("set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
                                ServiceConnection musicServiceConnection = new ServiceConnection() {
                                    @Override
                                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                                        MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) iBinder;
                                        musicBinder.getService().setSongsTillSleep(Integer.parseInt(((editText.getText().toString()))));
                                        unbindService(this);
                                    }

                                    @Override
                                    public void onServiceDisconnected(ComponentName componentName) {

                                    }
                                };
                                bindService(intent, musicServiceConnection, 0);
                            }
                        });
                builder.create().show();
                break;
            case R.id.register:
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                final EditText etUsername = new EditText(this);
                builder1.setTitle("Enter a username")
                        .setView(etUsername)
                        .setPositiveButton("register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                final ProgressDialog registrationProgressDialog = new ProgressDialog(MainActivity.this);
                                registrationProgressDialog.show();
                                final JSONObject params = new JSONObject();
                                try {
                                    params.put("username", etUsername.getText().toString());
//                                    this is temporary TODO use blockchain ==> future
                                    params.put("uuid", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, config.com_url + "register", params, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        SharedPreferences settings = getSharedPreferences("com_prefs", 0);
                                        settings.edit().putBoolean("registered", true).apply();
                                        try {
                                            settings.edit().putString("username", params.getString("username")).apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        registrationProgressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_LONG).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
//                                        Failed to register
                                        registrationProgressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                requestQueue.add(jsonObjectRequest);
                            }
                        });
                builder1.create().show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        recreate();
    }

    @Override
    protected void onDestroy() {
        try {
            BucketSaver bucketSaver = new BucketSaver(this);
            bucketSaver.saveFile();
            unbindService(musicServiceConnection);
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return new LocalMusicFragment();
                case 1:
                    return new PotmFragment();
                case 2:
                    return new BucketFragment();
                case 3:
                    return new TheFifthFragment();
                case 4:
                    return new ShoutboxFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            SharedPreferences settings = getSharedPreferences("com_prefs", 0);
            if (!settings.getBoolean("registered", false))
                // Show 4 total pages.
                return 4;
            else
                return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Local";
                case 1:
                    return "Potm";
                case 2:
                    return "Bucket";
                case 3:
                    return "Com";
                case 4:
                    return "Shoutbox";
                default:
                    return null;
            }
        }
    }
}
