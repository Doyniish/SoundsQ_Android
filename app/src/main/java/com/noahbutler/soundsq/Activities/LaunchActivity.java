package com.noahbutler.soundsq.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.MainFragment;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.UserState;
import com.noahbutler.soundsq.Fragments.OpenFragment;
import com.noahbutler.soundsq.Network.FCM.FCMInitiate;
import com.noahbutler.soundsq.NotificationController;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;

public class LaunchActivity extends Activity {


    /*************/
    /* DEBUG TAG */
    private static final String TAG = "Launch";


    /**********************/
    /* Saved Instance Key */
    private static final String SAVED = "saved";


    /*******************/
    /* Local Variables */
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        new NotificationController(this);

        if (savedInstanceState == null) {
            /* FCM Token */
            FCMInitiate fcmInitiate = new FCMInitiate(this);
            fcmInitiate.register();

            /* Sound Player Controller Creator */
            SoundPlayerController.createController(getBaseContext());

            mainFragment = new MainFragment();
            mainFragment.setRetainInstance(true);

            //show opening fragment for 2 seconds, then show main fragment.
            getFragmentManager().beginTransaction().replace(R.id.main_content_area, new OpenFragment()).commit();

        }
    }

    public void showMainFragment() {
        getFragmentManager().beginTransaction().replace(R.id.main_content_area, mainFragment).commit();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mainFragment != null) {
            mainFragment.onSaveInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mainFragment != null) {
            mainFragment.onResume();
        }
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

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        View menuOwner;
        switch(UserState.STATE) {

            case UserState.OWNER:
                menuOwner = getLayoutInflater().inflate(R.layout.menu_owner, null);
                mainFragment.setMenuView(menuOwner);
                actionBar.setCustomView(menuOwner);
                //getMenuInflater().inflate(R.menu.menu_owner, menu);
                break;
            case UserState.SPECTATOR:
                menuOwner = getLayoutInflater().inflate(R.layout.menu_spectator, null);
                mainFragment.setMenuView(menuOwner);
                actionBar.setCustomView(menuOwner);
                break;
            default:
                getMenuInflater().inflate(R.menu.menu_owner, menu);
                break;
        }

        Toolbar parent =(Toolbar) actionBar.getCustomView().getParent();
        parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0,0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Used by Registration View: catches back clicks
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mainFragment.onKeyDown(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
