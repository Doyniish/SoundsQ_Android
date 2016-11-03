package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.BitmapLoader.AsyncDrawable;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.UserState;
import com.noahbutler.soundsq.Network.Sender;
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
        return SoundQueue.queue_packages.size();
    }

    @Override
    public Object getItem(int position) {
        return SoundQueue.queue_packages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null) {

            convertView = layoutInflater.inflate(R.layout.sound_list_item, null);

            /* grab views from the original view */
            viewHolder.soundImage  = (ImageView) convertView.findViewById(R.id.sound_image);
            viewHolder.soundTitle       = (TextView)convertView.findViewById(R.id.sound_title);
            viewHolder.soundArtistName  = (TextView)convertView.findViewById(R.id.sound_artist_name);
            viewHolder.playingLight = (ImageView)convertView.findViewById(R.id.playing_light);
            viewHolder.likeButton = (ImageView) convertView.findViewById(R.id.like_button);
            viewHolder.likeButton.setClickable(true);
            viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    soundLiked(position);
                }
            });

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
        ImageView playingLight;
        ImageView likeButton;

    }

    private void soundLiked(int position) {
        String sound_url = SoundQueue.getSoundUrl(position);
        Sender.createExecute(Sender.LIKED_SOUND, sound_url);
    }

    /**
     * This method applies the certain SoundPackage
     * to the correct view on the list.
     */
    private void applySoundPackage(ViewHolder viewHolder, int position) {
        /* first we want to make sure the list is not empty */
        if(SoundQueue.queue_packages.size() != 0) {

            /* apply title */
            if(SoundQueue.queue_packages.get(position).title != null) {
                viewHolder.soundTitle.setText(SoundQueue.queue_packages.get(position).title);
            }else{
                viewHolder.soundTitle.setText(R.string.loading_title);
            }

            /* apply sound art */
            if(SoundQueue.queue_packages.get(position).soundImage != null) {
                InputStream in = null;
                try {
                    in = activity.openFileInput(SoundQueue.queue_packages.get(position).soundImage);
                    viewHolder.soundImage.setImageBitmap(BitmapFactory.decodeStream(in));
                }catch (FileNotFoundException e) {
                    Log.d("NO IMAGE", e.getMessage());
                }

            }

            /* apply artist */
            if(SoundQueue.queue_packages.get(position).artistName != null) {
                viewHolder.soundArtistName.setText(SoundQueue.queue_packages.get(position).artistName);
            }else{ //name still loading
                viewHolder.soundArtistName.setText(R.string.loading_artist);
            }

        }else{ // list is empty, just put in static text for now.
            viewHolder.soundTitle.setText(R.string.loading_title);
            viewHolder.soundArtistName.setText(R.string.loading_artist);
        }

        //apply light if sound is playing
        if(position == SoundQueue.getCurrentIndex()) {
            viewHolder.playingLight.setVisibility(View.VISIBLE);
        }else{
            viewHolder.playingLight.setVisibility(View.INVISIBLE);
        }
    }
}
