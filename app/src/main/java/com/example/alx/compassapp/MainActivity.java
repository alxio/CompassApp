package com.example.alx.compassapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private LocationManager mLocationManager;
    private SensorManager mSensorManager;
    private Compass mCompassModel;
    private CoordinateInputView mLatitudePicker;
    private CoordinateInputView mLongitudePicker;
    private View mNeedleView;
    private View mTargetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLatitudePicker = (CoordinateInputView) findViewById(R.id.latitudePicker);
        mLongitudePicker = (CoordinateInputView) findViewById(R.id.longitudePicker);
        mNeedleView = findViewById(R.id.needle);
        mTargetView = findViewById(R.id.target);
        initCompass();
        mLatitudePicker.init(mCompassModel, Compass.CoordinateType.LATITUDE);
        mLongitudePicker.init(mCompassModel, Compass.CoordinateType.LONGITUDE);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mCompassModel.setDestLatitude(sharedPref.getFloat(getString(R.string.latitude), 30f));
        mCompassModel.setDestLongitude(sharedPref.getFloat(getString(R.string.longitude), -110f));
    }

    @Override
    protected void onDestroy(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getString(R.string.latitude), (float) mCompassModel.getDestLatitude());
        editor.putFloat(getString(R.string.longitude), (float) mCompassModel.getDestLongitude());
        editor.commit();
        super.onDestroy();
    }

    public void selectLatitude(View v) {
        mLatitudePicker.show();
    }

    public void selectLongitude(View v) {
        mLongitudePicker.show();
    }

    private void initCompass() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCompassModel = new Compass(mNeedleView, mTargetView, this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.no_gps, Toast.LENGTH_LONG).show();
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mCompassModel);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mCompassModel);
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mCompassModel);
            mLocationManager.removeUpdates(mCompassModel);
        }
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mCompassModel, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
