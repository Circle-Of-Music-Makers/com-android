package com.sidzi.circleofmusic.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
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
import com.sidzi.circleofmusic.fragments.TheFifthFragment;
import com.sidzi.circleofmusic.helpers.BucketSaver;
import com.sidzi.circleofmusic.helpers.DatabaseSynchronization;
import com.sidzi.circleofmusic.helpers.MusicServiceConnection;
import com.sidzi.circleofmusic.recievers.MediaButtonHandler;
import com.sidzi.circleofmusic.recievers.MusicPlayerViewHandler;
import com.sidzi.circleofmusic.services.MusicPlayerService;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public MusicServiceConnection mMusicServiceConnection;

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
            mMusicServiceConnection = new MusicServiceConnection(getApplicationContext());
            bindService(intent, mMusicServiceConnection, BIND_AUTO_CREATE);

            MusicPlayerViewHandler mMusicPlayerViewHandler = new MusicPlayerViewHandler(this);
            LocalBroadcastManager.getInstance(this).registerReceiver(mMusicPlayerViewHandler, intentFilter);


            /* Handles headphone button click */


            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            ComponentName componentName = new ComponentName(getPackageName(), MediaButtonHandler.class.getName());
            audioManager.registerMediaButtonEventReceiver(componentName);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


            ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setTitle("# of songs till sleep")
                        .setView(editText)
                        .setPositiveButton("set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mMusicServiceConnection.getMusicPlayerService().setSongsTillSleep(Integer.parseInt(((editText.getText().toString()))));
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
                break;
            case R.id.exit:
                mMusicServiceConnection.getMusicPlayerService().onDestroy();
                unbindService(mMusicServiceConnection);
                stopService(new Intent(getApplicationContext(), MusicPlayerService.class));
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MusicPlayerService.ACTION_CLOSE));
                finish();
                break;
            case R.id.register:
//                POST com_url+"register" : id:Settings.Secure.ANDROID_ID , username : etUsername
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
            unbindService(mMusicServiceConnection);
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
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
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
                default:
                    return null;
            }
        }
    }
}
