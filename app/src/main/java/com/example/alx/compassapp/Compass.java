package com.example.alx.compassapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

public class Compass implements LocationListener, SensorEventListener, CoordinateInputView.Model {
    public enum CoordinateType {
        LATITUDE, LONGITUDE
    }
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;
    private double mDestLatitude = 0;
    private double mDestLongitude = 0;
    private double mDestBearing = 0;

    private double mMagneticBearing = 0;
    private double mGPSBearing = 0;
    private double mMagneticBearingWhenGPS = 0;
    private long mMagneticBearingWhenGPSTimestamp = 0;
    private long mGSPBearingTimestamp = 0;

    private View mNeedleView;
    private View mTargetView;

    private float mTemp[] = new float[2];

    private final static int GPS_TIMEOUT = 30000;
    private final static double ACCEPTABLE_SHIFT = Math.toRadians(10);

    private MainActivity ac;

    public Compass(View needle, View target, Activity activity){
        mNeedleView = needle;
        mTargetView = target;

        Location mock = new Location("NetGuruPoznan");
        mock.setLatitude(52.4082604);
        mock.setLongitude(16.9243316);
        onLocationChanged(mock);
    }

    public double getBearing(){
        if(System.currentTimeMillis() - mGSPBearingTimestamp < GPS_TIMEOUT
            && Math.abs(mMagneticBearing - mMagneticBearingWhenGPS) < ACCEPTABLE_SHIFT){
            return mGPSBearing;
        }else{
            return mMagneticBearing;
        }
    }

    public float getNorthRotation(){
        return (float) -Math.toDegrees(getBearing());
    }

    public float getTargetRotation(){
        return (float) -Math.toDegrees(getBearing() - mDestBearing);
    }

    public double getDestLatitude() {
        return mDestLatitude;
    }

    public void setDestLatitude(double destLatitude) {
        this.mDestLatitude = destLatitude;
    }

    public double getDestLongitude() {
        return mDestLongitude;
    }

    public void setDestLongitude(double destLongitude) {
        this.mDestLongitude = destLongitude;
    }


    private void update() {
        Location.distanceBetween(mMyLatitude, mMyLongitude, mDestLatitude, mDestLongitude, mTemp);
        mDestBearing = mTemp[1];

        mNeedleView.setRotation(getNorthRotation());
        mTargetView.setRotation(getTargetRotation());
    }

    @Override
    public void setCoordinate(double coordinate, CoordinateType coordinateType) {
        switch (coordinateType){
            case LATITUDE:
                mDestLatitude = coordinate;
                break;
            case LONGITUDE:
                mDestLongitude = coordinate;
                break;
        }
        update();
    }

    @Override
    public double getCoordinate(CoordinateType coordinateType) {
        switch (coordinateType){
            case LATITUDE:
                return mDestLatitude;
            case LONGITUDE:
                return mDestLongitude;
            default:
                return 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mMagneticBearing = Math.atan2(event.values[1], event.values[0]);
        if(mGSPBearingTimestamp > mMagneticBearingWhenGPSTimestamp){
            mMagneticBearingWhenGPSTimestamp = System.currentTimeMillis();
            mMagneticBearingWhenGPS = mMagneticBearing;
        }
        update();
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        if(location.hasBearing()) {
            mGPSBearing = location.getBearing();
            mGSPBearingTimestamp = System.currentTimeMillis();
        }
        update();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
