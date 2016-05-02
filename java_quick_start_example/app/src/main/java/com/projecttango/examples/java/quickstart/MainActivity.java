/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.projecttango.examples.java.quickstart;

import com.google.atap.tangoservice.Tango;
import com.projecttango.experiments.javaquickstart.Localization;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener{
    public static final String LOAD_ADF = "com.projecttango.areadescriptionjava.loadadf";
    private Button mStartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartButton = (Button) findViewById(R.id.start);
        mStartButton.setOnClickListener(this);
        startActivityForResult(
                Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_ADF_LOAD_SAVE), 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startLocalization();
                break;

        }
    }

    private void startLocalization() {
        Intent startADIntent = new Intent(this, Localization.class);
        startActivity(startADIntent);
    }


    }



