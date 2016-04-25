package com.example.d85j363.serial_talk;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.Tango.OnTangoUpdateListener;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import com.google.atap.tangoservice.*;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDeviceConnection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {
    private final String TAG = "Test";
    private Tango mTango;
    private TangoConfig mConfig;

    private static final String sTranslationFormat = "Translation: %f, %f, %f";
    private static final String sRotationFormat = "Rotation: %f, %f, %f, %f";
    private static final int SECS_TO_MILLISECS = 1000;
    private static final double UPDATE_INTERVAL_MS = 100.0;
    private double mPreviousTimeStamp;
    private double mTimeToNextUpdate = UPDATE_INTERVAL_MS;

    private TextView mTranslationTextView;
    private TextView mRotationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTranslationTextView = (TextView) findViewById(R.id.textView);

        mTango = new Tango(this);
        mConfig = new TangoConfig();
        mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_MOTIONTRACKING, true);

        startActivityForResult(
                Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_ADF_LOAD_SAVE),
                Tango.TANGO_INTENT_ACTIVITYCODE);
       /* try {
            setTangoListeners();
        } catch (TangoErrorException e) {

        }
        */

        //Snackbar.make(view, "Hello, is anyone out there?", Snackbar.LENGTH_LONG)
        //.setAction("Action", null).show();
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (availableDrivers.isEmpty()) {
            return;
        }
// Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());

        if (connection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
        }

// Read some data! Most have just one port (port 0).
        final UsbSerialPort port = driver.getPorts().get(0);

        try {
            port.open(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Button forward = (Button) findViewById(R.id.button);
        Button stop = (Button) findViewById(R.id.button5);
        Button adf = (Button) findViewById(R.id.button4);

        adf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, load_adf.class));

            }
        });


        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    port.setParameters(115200, 8, 1, 0);

                    //byte buffer[] = new byte[16];
                    byte buffer[];

                    buffer = "w".getBytes();
                    Snackbar.make(view, "Hello, is anyone?", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                    port.write(buffer, 10000);


                    // port.write(buffer, 10000);


                    //int numBytesRead = port.read(buffer, 1000);
                    //Log.d(TAG, "Read " + numBytesRead + " bytes.");
                } catch (IOException e) {
                    // Deal with error.
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    port.setParameters(115200, 8, 1, 0);

                    //byte buffer[] = new byte[16];
                    byte buffer[];

                    buffer = "h".getBytes();
                    Snackbar.make(view, "Hello, is anyone?", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                    port.write(buffer, 10000);


                    // port.write(buffer, 10000);


                    //int numBytesRead = port.read(buffer, 1000);
                    //Log.d(TAG, "Read " + numBytesRead + " bytes.");
                } catch (IOException e) {
                    // Deal with error.
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {


            }
        });

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

    public void start_adf(){

        startActivityForResult(
                Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_ADF_LOAD_SAVE),
                Tango.TANGO_INTENT_ACTIVITYCODE);

        ArrayList<String> fullUUIDList = new ArrayList<String>();
// Returns a list of ADFs with their UUIDs
        fullUUIDList = mTango.listAreaDescriptions();

// Load the latest ADF if ADFs are found.
        if (fullUUIDList.size() > 0) {
            mConfig.putString(TangoConfig.KEY_STRING_AREADESCRIPTION,
                    fullUUIDList.get(fullUUIDList.size() - 1));
        }

    }


    private void setTangoListeners() {
        final ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));

        // Listen for new Tango data
        mTango.connectListener(framePairs, new OnTangoUpdateListener() {


            @Override
            public void onPoseAvailable(TangoPoseData pose) {
                // Format Translation and Rotation data
                final String translationMsg = String.format(sTranslationFormat,
                        pose.translation[0], pose.translation[1],
                        pose.translation[2]);
                final String rotationMsg = String.format(sRotationFormat,
                        pose.rotation[0], pose.rotation[1], pose.rotation[2],
                        pose.rotation[3]);

                // Output to LogCat
                String logMsg = translationMsg + " | " + rotationMsg;
                Log.i(TAG, logMsg);

                final double deltaTime = (pose.timestamp - mPreviousTimeStamp)
                        * SECS_TO_MILLISECS;
                mPreviousTimeStamp = pose.timestamp;
                mTimeToNextUpdate -= deltaTime;

                // Throttle updates to the UI based on UPDATE_INTERVAL_MS.
                if (mTimeToNextUpdate < 0.0) {
                    mTimeToNextUpdate = UPDATE_INTERVAL_MS;

                    // Display data in TextViews. This must be done inside a
                    // runOnUiThread call because
                    // it affects the UI, which will cause an error if performed
                    // from the Tango
                    // service thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRotationTextView.setText(rotationMsg);
                            mTranslationTextView.setText(translationMsg);
                        }
                    });
                }
            }

            @Override
            public void onXyzIjAvailable(TangoXyzIjData arg0) {
                // We need this callback even if we don't use it
            }

            @Override
            public void onTangoEvent(final TangoEvent event) {
                // This callback also has to be here
            }
            @Override
            public void onFrameAvailable(int i){

            }
        });
    }
}
