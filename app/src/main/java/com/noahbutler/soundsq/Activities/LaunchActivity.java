package com.noahbutler.soundsq.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.MainFragment;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.Network.FCM.FCMInitiate;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;

public class LaunchActivity extends Activity {


    /*************/
    /* DEBUG TAG */
    private static final String TAG = "Launch";


    /*******************/
    /* Local Variables */
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        /* FCM Token */
        FCMInitiate fcmInitiate = new FCMInitiate(this);
        fcmInitiate.register();

        /* Sound Player Controller Creator */
        SoundPlayerController.createController(getBaseContext());

        /* hand of to Queue Ball Fragment */
        mainFragment = new MainFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_content_area, mainFragment).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "creating menu...");
        super.onCreateOptionsMenu(menu);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        switch(StateController.USER_STATE) {
            case StateController.OWNER:
                getMenuInflater().inflate(R.menu.menu_owner, menu);
                break;
            case StateController.SPECTATOR:
                getMenuInflater().inflate(R.menu.menu_spectator, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.menu_owner, menu);
                break;
        }
//
//        Toolbar parent =(Toolbar) actionBar.getCustomView().getParent();
//        parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
//        parent.setContentInsetsAbsolute(0,0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mainFragment.onClickMenuItem(item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}
