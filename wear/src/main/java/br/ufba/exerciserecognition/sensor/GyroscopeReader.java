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
public class GyroscopeReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mGyroscope;
    private static List<SensorBase> lGyroscope;

    private Boolean started = false;

    public void initialize(Context context){
        lGyroscope = new ArrayList();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
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

        SensorBase gyroscope = new SensorBase();

        gyroscope.setX(event.values[0]);
        gyroscope.setY(event.values[1]);
        gyroscope.setZ(event.values[2]);

        long timeInMillis = (new Date()).getTime()
                + (event.timestamp - System.nanoTime()) / 1000000L;

        gyroscope.setTimestamp(timeInMillis);
        lGyroscope.add(gyroscope);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public List<SensorBase> getData(){
        return lGyroscope;
    }


}
