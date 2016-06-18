package sid.comslav.com.circleofmusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
                dbHandler dbInstance = new dbHandler(mContext, null);
                final String tempTrackName = track.getTag().toString();
                if (dbInstance.fetchStatus(tempTrackName) < 2) {
                    downloadMusicTrack(tempTrackName);
                } else {
                    try {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(mContext, Uri.fromFile(new File(dbInstance.fetchTrackPaths(tempTrackName))));
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    void downloadMusicTrack(final String selectedItem) {
        final BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dbHandler dbInstance = new dbHandler(mContext, null);
                dbInstance.setStatus(selectedItem, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + selectedItem);
            }
        };
        mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        String url = "http://circleofmusic-sidzi.rhcloud.com/downloadTrack" + selectedItem;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading");
        request.setTitle(selectedItem);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItem);
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tnTextView.setTag(mTrackList[position]);
        switch (mTrackStatus[position]) {
            case 0:
                holder.tnTextView.setText(mTrackList[position]);
                holder.tdTextView.setVisibility(View.GONE);
                holder.taImageView.setVisibility(View.GONE);
                break;
            case 1:
                holder.tnTextView.setText(mTrackList[position]);
                holder.tnTextView.setTextColor(Color.parseColor("#FFFFFF"));
                holder.itemView.setBackgroundColor(Color.parseColor("#009688"));
                holder.tdTextView.setVisibility(View.GONE);
                holder.taImageView.setVisibility(View.GONE);
                break;
            case 2:
            case 3:
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                try {
                    mediaMetadataRetriever.setDataSource(mTrackPathList[position]);
                    String tempTitle = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    if (!Objects.equals(tempTitle, null)) {
                        holder.tnTextView.setText(tempTitle);
                    } else {
                        holder.tnTextView.setText(mTrackList[position]);
                        holder.tdTextView.setVisibility(View.GONE);
                    }
                    holder.tdTextView.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    try {
                        holder.taImageView.setImageBitmap(BitmapFactory.decodeByteArray(mediaMetadataRetriever.getEmbeddedPicture(), 0, mediaMetadataRetriever.getEmbeddedPicture().length));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } catch (IllegalArgumentException e) {
                    holder.tnTextView.setText(mTrackList[position]);
                    holder.tdTextView.setVisibility(View.GONE);
                    holder.taImageView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTrackList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tnTextView;
        public TextView tdTextView;
        public ImageView taImageView;
        public ImViewHolderClick mListener;

        public ViewHolder(View view, ImViewHolderClick listener) {
            super(view);
            this.tnTextView = (TextView) view.findViewById(R.id.tvTrackName);
            this.tdTextView = (TextView) view.findViewById(R.id.tvTrackInfo);
            this.taImageView = (ImageView) view.findViewById(R.id.ivTrackArt);
            mListener = listener;
            view.setOnClickListener(this);
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
