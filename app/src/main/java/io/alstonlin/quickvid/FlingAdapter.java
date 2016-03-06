package io.alstonlin.quickvid;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.devbrackets.android.exomedia.EMVideoView;

import java.util.ArrayList;

/**
 * The Adapter for the FlingView that is used in ExploreFragment.
 */
public class FlingAdapter extends BaseAdapter{
    private ArrayList<VideoItem> items;
    private LayoutInflater inflater;
    private String minId = null;
    private String maxId = null;

    public FlingAdapter(Context context, ArrayList<VideoItem> items){
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    public void setupFirstItem(final EMVideoView emVideoView){
        if (emVideoView != null) { // Exists
            // Starts video when ready
            emVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    emVideoView.setRotation(90f);
                    emVideoView.start();
                }
            });
            emVideoView.setVideoURI(Uri.parse(items.get(0).getUrl()));
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item, parent, false);
        }
        if (position == 0){
            setupFirstItem((EMVideoView) convertView.findViewById(R.id.vidView));
        }
        TextView likes = (TextView) convertView.findViewById(R.id.likes);
        likes.setText("x" + Integer.toString(items.get(position).getLikes()) + " of " +
                Integer.toString(items.get(position).getLikes() + items.get(position).getPasses()));
        return convertView;
    }

    /**
     * Sets the `ta set that the adapter is showing and refreshes the View.
     * @param items The new data set
     */
    public void setItems(ArrayList<VideoItem> items){
        this.items = items;
        // Gets min and max
        for (VideoItem item : items){
            if (minId == null || item.getId().compareTo(minId) == 1) minId = item.getId();
            if (maxId == null || item.getId().compareTo(maxId) == -1) maxId = item.getId();
        }
        notifyDataSetChanged();
    }

    public String getMinId() {
        return minId;
    }

    public String getMaxId() {
        return maxId;
    }

    public ArrayList<VideoItem> getItems(){
        return items;
    }
}
