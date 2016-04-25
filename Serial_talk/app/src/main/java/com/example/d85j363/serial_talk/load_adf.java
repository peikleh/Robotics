package com.example.d85j363.serial_talk;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoErrorException;

import java.util.ArrayList;

public class load_adf extends AppCompatActivity {
    private Tango mTango;
    private TangoConfig mConfig;
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_adf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mTango = new Tango(this);
        mConfig = new TangoConfig();
        mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_MOTIONTRACKING, true);
        start_adf();



    }

    public void start_adf(){
        String logMsg = " ADF WORKED";
        Log.i(TAG, logMsg);

        ArrayList<String> fullUUIDList = new ArrayList<String>();
// Returns a list of ADFs with their UUIDs
        fullUUIDList = mTango.listAreaDescriptions();

// Load the latest ADF if ADFs are found.
        if (fullUUIDList.size() > 0) {
            mConfig.putString(TangoConfig.KEY_STRING_AREADESCRIPTION,
                    fullUUIDList.get(fullUUIDList.size() - 1));
        }

    }

}
