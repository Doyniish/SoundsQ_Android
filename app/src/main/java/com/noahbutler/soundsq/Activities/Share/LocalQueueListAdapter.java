package com.noahbutler.soundsq.Activities.Share;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by gildaroth on 9/28/16.
 */

public class LocalQueueListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private String[] queueNameList;

    public LocalQueueListAdapter(ShareActivity activity, String[] queueNameList) {
        this.queueNameList = queueNameList;
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

            convertView = layoutInflater.inflate(R.layout.local_queue_item, null);

            /* grab views from the original view */
            viewHolder.queueName  = (TextView) convertView.findViewById(R.id.queue_name);

            /* apply to original view */
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.queueName.setText(queueNameList[position]);

        return convertView;
    }

    public class ViewHolder {
        TextView queueName;
    }
}