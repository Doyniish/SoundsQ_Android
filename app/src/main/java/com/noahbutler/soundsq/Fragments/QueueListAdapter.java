package com.noahbutler.soundsq.Fragments;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by NoahButler on 12/27/15.
 */
public class QueueListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private LaunchActivity activity;

    public QueueListAdapter(LaunchActivity activity) {
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return SoundQueue.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null) {

            convertView = layoutInflater.inflate(R.layout.sound_list_item, null);

            /* grab views from the original view */
            viewHolder.soundImage  = (ImageView) convertView.findViewById(R.id.album_art);
            viewHolder.soundTitle       = (TextView)convertView.findViewById(R.id.sound_title);
            viewHolder.soundArtistName  = (TextView)convertView.findViewById(R.id.sound_artist_name);
            viewHolder.soundPlayingHighlighter = (ImageView)convertView.findViewById(R.id.soundplaying_listhighlighter);


            /* apply to original view */
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        /* if we have a package, apply it to the view */
        applySoundPackage(viewHolder, position);


        return convertView;
    }

    public class ViewHolder {
        TextView soundTitle;
        TextView soundArtistName;
        ImageView soundImage;
        ImageView soundPlayingHighlighter;

    }

    /**
     * This method applies the certain SoundPackage from QUEUE_SOUND_PACKAGES
     * to the correct view on the list.
     */
    private void applySoundPackage(ViewHolder viewHolder, int position) {

        /* first we want to make sure the list is not empty */
        if(Constants.QUEUE_SOUND_PACKAGES.size() != 0) {

            /* apply data to the views TODO: fix to not get packages by positions but by url, will make no mistakes then */
            if(Constants.QUEUE_SOUND_PACKAGES.get(position).soundName != null) {
                viewHolder.soundTitle.setText(Constants.QUEUE_SOUND_PACKAGES.get(position).soundName);
            }else{
                viewHolder.soundTitle.setText("Loading...");
            }

            /* when ready, apply sound art */
            if(Constants.QUEUE_SOUND_PACKAGES.get(position).soundImage != null) {
                InputStream in = null;
                try {
                    in = activity.openFileInput(Constants.QUEUE_SOUND_PACKAGES.get(position).soundImage);
                }catch (FileNotFoundException e) {
                    Log.d("NO IMAGE", e.getMessage());
                }
                viewHolder.soundImage.setImageBitmap(BitmapFactory.decodeStream(in));
            }

            /* apply artist name to field */
            if(Constants.QUEUE_SOUND_PACKAGES.get(position).artistName != null) {
                viewHolder.soundArtistName.setText(Constants.QUEUE_SOUND_PACKAGES.get(position).artistName);
            }else{ //name still loading
                viewHolder.soundArtistName.setText("Loading...");
            }

            if(Constants.QUEUE_SOUND_PACKAGES.get(position).isPlaying) {
                viewHolder.soundPlayingHighlighter.setVisibility(View.VISIBLE);
            }else{
                viewHolder.soundPlayingHighlighter.setVisibility(View.INVISIBLE);
            }
        }else{ // list is empty, just put in static text for now.
            viewHolder.soundTitle.setText("Loading...");
            viewHolder.soundArtistName.setText("Loading...");
        }

    }
}
