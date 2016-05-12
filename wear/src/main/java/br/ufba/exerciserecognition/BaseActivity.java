package br.ufba.exerciserecognition;

import android.support.wearable.activity.WearableActivity;

import br.ufba.exerciserecognition.dao.AppPreference;

/**
 * Created by igor.faria on 12/05/2016.
 */
public class BaseActivity extends WearableActivity {

    private AppPreference appPref;


    public AppPreference getAppPreference() {
        if (appPref == null)
            appPref = new AppPreference(getApplicationContext());
        return appPref;
    }
}
