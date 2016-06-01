package br.ufba.exerciserecognition;

import android.os.Environment;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.ufba.exerciserecognition.dao.AppPreference;
import br.ufba.exerciserecognition.model.SensorBase;

/**
 * Created by igor.faria on 12/11/2015.
 */
public class PhoneMessageListenerService extends WearableListenerService {
    private static final String WEAR_PATH = "/DATA_IDENTIFIER_PATH";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( WEAR_PATH ) ) {
            String identifier ;
            identifier = new String(messageEvent.getData());
            Log.v("TEST",identifier );
            AppPreference appPreference = new AppPreference(getApplicationContext());
            appPreference.setKeyPrefsIdentifier(identifier);

        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}
