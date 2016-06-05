package sid.comslav.com.circleofmusic;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        holder.mImageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.tvTrackName);
            this.mImageView = (ImageView) view.findViewById(R.id.ivTrackArt);
        }
    }
}
