package com.noahbutler.soundsq.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by NoahButler on 9/6/15.
 */
public class QueueFragment extends Fragment {

    private ImageButton showQueueID;
    private TextView queueIDDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View masterView = inflater.inflate(R.layout.fragment_queue, container, false);

//        /* setup show queue id button */
//        showQueueID = (ImageButton)masterView.findViewById(R.id.show_queue_id);
//        showQueueID.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(queueIDDisplay.getVisibility() == View.VISIBLE) {
//                    queueIDDisplay.setVisibility(View.INVISIBLE);
//                }else {
//                    queueIDDisplay.setText(SoundQueue.ID);
//                    queueIDDisplay.setVisibility(View.VISIBLE);
//                    Log.e("QUEUE_ID", SoundQueue.ID);
//                }
//
//            }
//        });
//
//        /* setup queue id display area */
//        queueIDDisplay = (TextView)masterView.findViewById(R.id.queue_id_display);
//        queueIDDisplay.setVisibility(View.INVISIBLE);
//
//        /* setup song list */
//        Constants.queueListView = (ListView)masterView.findViewById(R.id.queueView);
//        SoundQueue.createQueue();
//
//        /* create our queue list */
//        Constants.queueListAdapter = new QueueListAdapter((LaunchActivity) this.getActivity());
//        Constants.queueListView.setAdapter(Constants.queueListAdapter);

        return masterView;
    }

}
