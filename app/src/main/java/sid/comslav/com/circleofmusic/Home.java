package sid.comslav.com.circleofmusic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Home extends AppCompatActivity {
    int count;
    String songs[];
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        APIHelper api = new APIHelper();

        try {
            obj=new JSONObject(api.execute("http://circleofmusic-sidzi.rhcloud.com/getTrackCount").get());
            count=(int)obj.get("count");
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        songs=new String[count];
        for(int i=0;i<count;i++)
        {
            try {
                songs[i]= obj.get("file"+i).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        GridView gVTrackList = (GridView)findViewById(R.id.gVTrackList);
        gVTrackList.setAdapter(new TrackAdapter());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TrackAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            TextView tv = new TextView(getApplicationContext());
            tv.setText(songs[i]);
            tv.setTextColor(Color.BLACK);
            return tv;
        }
    }
}