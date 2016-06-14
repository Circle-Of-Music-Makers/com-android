package sid.comslav.com.circleofmusic;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    private String[] mDataSet;
    private boolean[] newNotification;


    public TrackListAdapter(String[] mDataSet, boolean[] newNotification) {
        this.mDataSet = mDataSet;
        this.newNotification = newNotification;
    }

    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_layout, parent, false);
        view.setPadding(0, 0, 5, 5);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataSet[position]);
        if (newNotification[position]) {
            holder.mTextView.setTextColor(Color.GREEN);
        }
        holder.sTextView.setText("Track Info After Download");
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public TextView sTextView;

        public ViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.tvTrackName);
            this.sTextView = (TextView) view.findViewById(R.id.tvTrackInfo);
        }

        @Override
        public void onClick(View v) {
            downloadMusicTrack(v.toString());
        }

        void downloadMusicTrack(String selectedItem) {
            String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + selectedItem;
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading");
            request.setTitle(selectedItem);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItem);
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }
}
