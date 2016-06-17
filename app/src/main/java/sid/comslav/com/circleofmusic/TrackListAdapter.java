package sid.comslav.com.circleofmusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sid.comslav.com.circleofmusic.helpers.dbHandler;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    private String[] mTrackList;
    private String[] mTrackPathList;
    private int[] mTrackStatus;
    private Context mContext;


    public TrackListAdapter(String[] mTrackList, int[] mTrackStatus, String[] mTrackPathList, Context mContext) {
        this.mTrackList = mTrackList;
        this.mContext = mContext;
        this.mTrackStatus = mTrackStatus;
        this.mTrackPathList = mTrackPathList;
    }

    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_layout, parent, false);
        return new ViewHolder(view, new ViewHolder.ImViewHolderClick() {
            @Override
            public void downTrack(View track) {
                downloadMusicTrack(((TextView) track).getText().toString());
            }
        });
    }

    void downloadMusicTrack(final String selectedItem) {
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dbHandler dbInstance = new dbHandler(mContext, null);
                dbInstance.setStatus(selectedItem, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + selectedItem);
            }
        };
        String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + selectedItem;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading");
        request.setTitle(selectedItem);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItem);
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (mTrackStatus[position]) {
            case 0:
                holder.mTextView.setText(mTrackList[position]);
                break;
            case 1:
                holder.mTextView.setText(mTrackList[position]);
                holder.mTextView.setTextColor(Color.parseColor("#FFFFFF"));
                holder.itemView.setBackgroundColor(Color.parseColor("#009688"));
                break;
            case 2:
            case 3:
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(mTrackPathList[position]);
                holder.sTextView.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                holder.mTextView.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTrackList.length;
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
