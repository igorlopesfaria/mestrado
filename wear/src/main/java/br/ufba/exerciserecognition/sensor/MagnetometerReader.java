package br.ufba.exerciserecognition.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

import br.ufba.exerciserecognition.model.SensorBase;

/**
 * Created by igor.faria on 10/03/2016.
 */
public class MagnetometerReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    protected Boolean mInitialized;
    private static List<SensorBase> lMagnetometer;

    private Boolean started = false;

    public void initialize(Context context){
        mInitialized = false;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        started = true;
    }


    public void unregisterSensor() {
        mSensorManager.unregisterListener(this);
        started = false;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE){
            return;
        }

        if(!started)
            return;

        SensorBase magnetometer = new SensorBase();

        if (!mInitialized) {
            lMagnetometer = new ArrayList();
            mInitialized = true;
        }
        magnetometer.setX(event.values[0]);
        magnetometer.setY(event.values[1]);
        magnetometer.setZ(event.values[2]);
        lMagnetometer.add(magnetometer);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public List<SensorBase> getData(){
        return lMagnetometer;
    }


}
