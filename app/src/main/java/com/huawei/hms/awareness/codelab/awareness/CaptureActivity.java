/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.huawei.hms.awareness.codelab.awareness;

import static com.huawei.hms.kit.awareness.status.BluetoothStatus.DEVICE_CAR;

import java.util.Arrays;
import java.util.List;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.awareness.codelab.Constant;
import com.huawei.hms.awareness.codelab.R;
import com.huawei.hms.awareness.codelab.logger.LogView;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.capture.AmbientLightResponse;
import com.huawei.hms.kit.awareness.capture.ApplicationStatusResponse;
import com.huawei.hms.kit.awareness.capture.BeaconStatusResponse;
import com.huawei.hms.kit.awareness.capture.BehaviorResponse;
import com.huawei.hms.kit.awareness.capture.BluetoothStatusResponse;
import com.huawei.hms.kit.awareness.capture.DarkModeStatusResponse;
import com.huawei.hms.kit.awareness.capture.HeadsetStatusResponse;
import com.huawei.hms.kit.awareness.capture.LocationResponse;
import com.huawei.hms.kit.awareness.capture.ScreenStatusResponse;
import com.huawei.hms.kit.awareness.capture.TimeCategoriesResponse;
import com.huawei.hms.kit.awareness.capture.WeatherStatusResponse;
import com.huawei.hms.kit.awareness.capture.WifiStatusResponse;
import com.huawei.hms.kit.awareness.status.AmbientLightStatus;
import com.huawei.hms.kit.awareness.status.ApplicationStatus;
import com.huawei.hms.kit.awareness.status.BeaconStatus;
import com.huawei.hms.kit.awareness.status.BehaviorStatus;
import com.huawei.hms.kit.awareness.status.BluetoothStatus;
import com.huawei.hms.kit.awareness.status.DarkModeStatus;
import com.huawei.hms.kit.awareness.status.DetectedBehavior;
import com.huawei.hms.kit.awareness.status.HeadsetStatus;
import com.huawei.hms.kit.awareness.status.ScreenStatus;
import com.huawei.hms.kit.awareness.status.TimeCategories;
import com.huawei.hms.kit.awareness.status.WeatherStatus;
import com.huawei.hms.kit.awareness.status.WifiStatus;
import com.huawei.hms.kit.awareness.status.weather.Situation;
import com.huawei.hms.kit.awareness.status.weather.WeatherSituation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private ScrollView mScrollView;

    private LogView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        mLogView = findViewById(R.id.logView);
        mScrollView = findViewById(R.id.root_scrollView);
        findViewById(R.id.capture_getLocation).setOnClickListener(this);
        findViewById(R.id.capture_getWeatherStatus).setOnClickListener(this);
        findViewById(R.id.clear_log).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture_getLocation:
                getLocation();
                break;
            case R.id.capture_getWeatherStatus:
                getWeatherStatus();
                break;
            case R.id.clear_log:
                mLogView.setText("");
                break;
            default:
                break;
        }
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Awareness.getCaptureClient(this).getLocation().addOnSuccessListener(new OnSuccessListener<LocationResponse>() {
            @Override
            public void onSuccess(LocationResponse locationResponse) {
                Location location = locationResponse.getLocation();
                mLogView.printLog("Longitude:" + location.getLongitude() + ",Latitude:" + location.getLatitude());
                scrollToBottom();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                mLogView.printLog("Failed to get the location.");
                Log.e(TAG, "Failed to get the location.", e);
            }
        });
    }


    private void getWeatherStatus() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Awareness.getCaptureClient(this)
                .getWeatherByDevice()
                .addOnSuccessListener(new OnSuccessListener<WeatherStatusResponse>() {
                    @Override
                    public void onSuccess(WeatherStatusResponse weatherStatusResponse) {
                        WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                        WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
                        Situation situation = weatherSituation.getSituation();
                        // For more weather information, please refer to the development guide.
                        String weatherInfoStr = "City:" + weatherSituation.getCity().getName() + "\n" + "Weather id is "
                                + situation.getWeatherId() + "\n" + "CN Weather id is " + situation.getCnWeatherId() + "\n"
                                + "Temperature is " + situation.getTemperatureC() + "℃" + "," + situation.getTemperatureF()
                                + "℉" + "\n" + "Wind speed is " + situation.getWindSpeed() + "km/h" + "\n"
                                + "Wind direction is " + situation.getWindDir() + "\n" + "Humidity is "
                                + situation.getHumidity() + "%";
                        mLogView.printLog(weatherInfoStr);
                        scrollToBottom();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        mLogView.printLog("Failed to get weather information.");
                        Log.e(TAG, "Failed to get weather information.");
                    }
                });
    }
    private void scrollToBottom() {
        mScrollView.postDelayed(() -> mScrollView.smoothScrollTo(0, mScrollView.getBottom()), 200);
    }
}
