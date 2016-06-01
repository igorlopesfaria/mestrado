package br.ufba.exerciserecognition.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufba.exerciserecognition.model.SensorBase;

/**
 * Created by igor.faria on 10/03/2016.
 */
public class AccelerometerReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    protected Boolean mInitialized, started = false;
    private static List<SensorBase> lAccelerometer;


    public void initialize(Context context){
        mInitialized = false;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

        SensorBase accelerometer = new SensorBase();

        if (!mInitialized) {
            lAccelerometer = new ArrayList();
            mInitialized = true;
        }

        accelerometer.setX(event.values[0]);
        accelerometer.setY(event.values[1]);
        accelerometer.setZ(event.values[2]);

        long timeInMillis = (new Date()).getTime()
                + (event.timestamp - System.nanoTime()) / 1000000L;

        accelerometer.setTimestamp(timeInMillis);
        lAccelerometer.add(accelerometer);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public List<SensorBase> getData(){
        return lAccelerometer;
    }


}
