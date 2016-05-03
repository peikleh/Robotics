package com.projecttango.experiments.javaquickstart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.atap.tangoservice.Tango;
import com.projecttango.examples.java.quickstart.R;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.Tango.OnTangoUpdateListener;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoInvalidException;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import com.badlogic.gdx.math.*;
import android.widget.Button;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Localization extends Activity implements View.OnClickListener {
    private static final String TAG = Localization.class.getSimpleName();
    private Tango mTango;
    private TangoConfig mConfig;
    private boolean mIsRelocalized;
    private final Object mSharedLock = new Object();

    private static final double UPDATE_INTERVAL_MS = 100.0;
    private static final DecimalFormat FORMAT_THREE_DECIMAL = new DecimalFormat("00.000");
    private static final int SECS_TO_MILLISECS = 1000;
    private double mPreviousPoseTimeStamp;
    private double mTimeToNextUpdate = UPDATE_INTERVAL_MS;
    private TextView mRelocalizationTextView;
    private TextView markTextView;
    private TextView angleTextView;
    private TextView tAngleTextView;
    private double X;
    private double Y;
    private double Z;
    private double[] rot;
    private double[] location;
    private double rotation;
    private Button markButton;
    private Button manualButton;
    float[] coor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        coor = new float[4];
        location = new double[3];
        rot = new double[4];
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localization);
        mRelocalizationTextView = (TextView) findViewById(R.id.text);
        markTextView = (TextView) findViewById(R.id.ismark);
        angleTextView = (TextView) findViewById(R.id.angle);

        mRelocalizationTextView.setText("HERRO");
        markButton = (Button) findViewById(R.id.mark);
        markButton.setOnClickListener(this);

        mTango = new Tango(this);
        mIsRelocalized = false;
        mConfig = setTangoConfig(mTango, false, true);
        setUpTangoListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Clear the relocalization state: we don't know where the device has been since our app
        // was paused.
        mIsRelocalized = false;

        // Re-attach listeners.
        try {
            setUpTangoListeners();
        } catch (TangoErrorException e) {

        } catch (SecurityException e) {

        }

        // Connect to the tango service (start receiving pose updates).
        try {
            mTango.connect(mConfig);
        } catch (TangoOutOfDateException e) {
            ;
        } catch (TangoErrorException e) {

        } catch (TangoInvalidException e) {

        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mark:
                mark();
                //markTextView.setText(Double.toString(location[0]) + "\nY:" +  Double.toString(location[1]) + "\nZ:" + Double.toString(location[2]));
                break;
            case R.id.manual:
                Intent startADIntent = new Intent(this, Manual.class);
                startActivity(startADIntent);

        }
    }

    public void mark() {
        location[0] = X;
        location[1] = Y;
        location[2] = Z;
    }

    private TangoConfig setTangoConfig(Tango tango, boolean isLearningMode, boolean isLoadAdf) {
        TangoConfig config = new TangoConfig();
        config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        // Check if learning mode
        if (isLearningMode) {
            // Set learning mode to config.
            config.putBoolean(TangoConfig.KEY_BOOLEAN_LEARNINGMODE, true);

        }
        // Check for Load ADF/Constant Space relocalization mode
        if (isLoadAdf) {
            ArrayList<String> fullUUIDList = new ArrayList<String>();
            // Returns a list of ADFs with their UUIDs
            fullUUIDList = tango.listAreaDescriptions();
            // Load the latest ADF if ADFs are found.
            if (fullUUIDList.size() > 0) {
                config.putString(TangoConfig.KEY_STRING_AREADESCRIPTION,
                        "520da138-f402-468c-bb15-0d059757ef71");
            }
        }
        return config;
    }

    public double getRotation(double x, double y){
        double diffx = (location[0] - x);// / (cube.z - camera.z);
        double diffy = (location[1] - y);
        double angle = (Math.atan(diffx / diffy))*(180/Math.PI);
        if(diffy <= 0){
            if(angle < 0){
                angle = angle + 360;
            }else{
                angle = angle;
            }

        } else {
            angle = angle + 180;
        }


        return (angle + 90)%360;

    }

    private void setUpTangoListeners() {

        // Set Tango Listeners for Poses Device wrt Start of Service, Device wrt
        // ADF and Start of Service wrt ADF
        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                TangoPoseData.COORDINATE_FRAME_DEVICE));
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE));

        mTango.connectListener(framePairs, new OnTangoUpdateListener() {
            @Override
            public void onXyzIjAvailable(TangoXyzIjData xyzij) {
                // Not using XyzIj data for this sample
            }

            // Listen to Tango Events
            @Override
            public void onTangoEvent(final TangoEvent event) {
            }

            @Override
            public void onPoseAvailable(TangoPoseData pose) {
                boolean safeMark = false;
                // Make sure to have atomic access to Tango Data so that
                // UI loop doesn't interfere while Pose call back is updating
                // the data.
                synchronized (mSharedLock) {
                    // Check for Device wrt ADF pose, Device wrt Start of Service pose,
                    // Start of Service wrt ADF pose (This pose determines if the device
                    // is relocalized or not).
                    if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION
                            && pose.targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {

                        if (mIsRelocalized) {
                            safeMark = true;
                        }
                    } else if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE
                            && pose.targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {
                        if (!mIsRelocalized) {
                            safeMark = true;
                        }

                    } else if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION
                            && pose.targetFrame == TangoPoseData
                            .COORDINATE_FRAME_START_OF_SERVICE) {
                        if (pose.statusCode == TangoPoseData.POSE_VALID) {
                            mIsRelocalized = true;
                            // Set the color to green
                        } else {
                            mIsRelocalized = false;
                            // Set the color blue
                        }
                    }
                }

                final double deltaTime = (pose.timestamp - mPreviousPoseTimeStamp) *
                        SECS_TO_MILLISECS;
                mPreviousPoseTimeStamp = pose.timestamp;
                mTimeToNextUpdate -= deltaTime;

                if (mTimeToNextUpdate < 0.0) {
                    mTimeToNextUpdate = UPDATE_INTERVAL_MS;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (mSharedLock) {
                                double dAngle = getRotation(X, Y);

                                mRelocalizationTextView.setText(mIsRelocalized ?
                                        "Localized\n X:" + Double.toString(X) + "\nY:" +  Double.toString(Y) + "\nZ:" + Double.toString(Z) :
                                        "Not Localized");
                                angleTextView.setText(Double.toString(dAngle));
                                double x = rot[0];
                                double y = rot[1];
                                double z = rot[2];
                                double w = rot[3];
                                double tAngle = Math.toDegrees(Math.atan2(2.0*(x*y+w*z),w*w +  x*x -z*z ))+ 180.0;

                                double angDif = dAngle - tAngle;
                                //if (angDif > 180.0 || angDif < 0){
                                //    markTextView.setText("left");
                                //}
                                //else{
                                 //   markTextView.setText("right");
                                //}
                                double check = tAngle-90;
                                if (check < 0){
                                    check = 360 + check;
                                }

                                markTextView.setText(Double.toString(check));




                            }
                        }
                    });
                }
                if (safeMark){
                    X = pose.translation[0];
                    Y = pose.translation[1];
                    Z = pose.translation[2];
                    coor = pose.getRotationAsFloats();
                    rot = pose.rotation;



                }


                }


            @Override
            public void onFrameAvailable(int cameraId) {
                // We are not using onFrameAvailable for this application.
            }
        });
    }


}
