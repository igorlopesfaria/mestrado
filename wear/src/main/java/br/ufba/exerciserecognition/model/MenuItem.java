package br.ufba.exerciserecognition.model;

import java.io.Serializable;

/**
 * Created by igor.faria on 12/05/2016.
 */
public class MenuItem implements Serializable {

    private String  title;
    private String  subtitle;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
