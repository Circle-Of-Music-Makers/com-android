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

import sid.comslav.com.circleofmusic.helpers.dbHandler;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    private String[] mDataSet;
    private boolean[] newNotification;
    private Context mContext;
    private boolean[] download_status;


    public TrackListAdapter(String[] mDataSet, boolean[] newNotification, boolean[] download_status, Context mContext) {
        this.mDataSet = mDataSet;
        this.newNotification = newNotification;
        this.mContext = mContext;
        this.download_status = download_status;
    }

    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_layout, parent, false);
        TrackListAdapter.ViewHolder vh = new ViewHolder(view, new TrackListAdapter.ViewHolder.ImViewHolderClick() {
            @Override
            public void downTrack(View track) {
                downloadMusicTrack(((TextView) track).getText().toString());
            }
        });
        return vh;
    }

    void downloadMusicTrack(String selectedItem) {
        String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + selectedItem;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading");
        request.setTitle(selectedItem);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItem);
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
//        TODO Add on broadcast receive to check if download completed successfully before adding to db
        dbHandler dbInstance = new dbHandler(mContext, null);
        dbInstance.setDownloadStatus(selectedItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataSet[position]);
        if (newNotification[position]) {
            holder.mTextView.setTextColor(Color.GREEN);
        }
        if (download_status[position]) {
            holder.sTextView.setText("");
        } else {
            holder.sTextView.setText("Artistry");
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public TextView sTextView;
        public ImViewHolderClick mListener;

        public ViewHolder(View view, ImViewHolderClick listener) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.tvTrackName);
            this.sTextView = (TextView) view.findViewById(R.id.tvTrackInfo);
            mListener = listener;
            mTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.downTrack(v);
        }

        public interface ImViewHolderClick {
            void downTrack(View track);
        }
    }
}
