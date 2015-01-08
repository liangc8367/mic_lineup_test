package com.bluesky.osprey.miclineuptest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnToggle = (Button)findViewById(R.id.btnToggle);
        mTxtStatus = (TextView)findViewById(R.id.txtStatus);
        mMicLineup = new MicrophoneLineup();
    }

    @Override
    protected void onStop() {
        mMicLineup.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mMicLineup = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mMicLineup.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMicLineup.resume();
        super.onResume();
        updateGUI();
    }

    @Override
    protected void onStart() {
        mMicLineup.start();
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onToggleButton(View view){
        mEnabled = !mEnabled;
        updateGUI();
    }



    /** private methods and members */

    private void updateGUI(){
        if(mEnabled){
            mBtnToggle.setText("Stop");
        } else {
            mBtnToggle.setText("Start");
        }
    }

    Button mBtnToggle;
    TextView mTxtStatus;

    MicrophoneLineup    mMicLineup;

    boolean mEnabled = false;
}
