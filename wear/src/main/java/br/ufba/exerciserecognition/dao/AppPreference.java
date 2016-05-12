package br.ufba.exerciserecognition.dao;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Igor Lopes de Faria on 16/04/15.
 */
public class AppPreference {

    private static final String APP_SHARED_PREFS = AppPreference.class.getSimpleName();

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public static final String KEY_PREFS_TYPE_EXPERIMENT = "typeExperiment";
    public static final String KEY_PREFS_TYPE_EXERCISE = "typeExercise";
    public static final String KEY_PREFS_IDENTIFIER = "identifier";



    public AppPreference(Context context) {
        this.sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
                Activity.MODE_PRIVATE);
        this.prefsEditor = sharedPrefs.edit();
    }


    public String getKeyPrefsTypeExperiment() {
        return sharedPrefs.getString(KEY_PREFS_TYPE_EXPERIMENT, null);
    }

    public void setKeyPrefsTypeExperiment(String token) {
        prefsEditor.putString(KEY_PREFS_TYPE_EXPERIMENT, token);
        prefsEditor.commit();
    }

    public String getKeyPrefsTypeExercise() {
        return sharedPrefs.getString(KEY_PREFS_TYPE_EXERCISE, null);
    }

    public void setKeyPrefsTypeExercise(String token) {
        prefsEditor.putString(KEY_PREFS_TYPE_EXERCISE, token);
        prefsEditor.commit();
    }

    public String getKeyPrefsIdentifier() {
        return sharedPrefs.getString(KEY_PREFS_IDENTIFIER, null);
    }

    public void setKeyPrefsIdentifier(String refreshToken) {
        prefsEditor.putString(KEY_PREFS_IDENTIFIER, refreshToken);
        prefsEditor.commit();
    }
}

