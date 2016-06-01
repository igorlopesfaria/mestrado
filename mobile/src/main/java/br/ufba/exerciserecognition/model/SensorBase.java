package br.ufba.exerciserecognition.model;

import java.io.Serializable;

/**
 * Created by igor.faria on 05/04/2016.
 */
public class SensorBase implements Serializable {

    private Float x;
    private Float z;
    private Float Y;

    private Long timestamp;

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    public Float getY() {
        return Y;
    }

    public void setY(Float y) {
        Y = y;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
