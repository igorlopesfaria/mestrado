package br.ufba.exerciserecognition;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;


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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufba.exerciserecognition.model.SensorBase;

/**
 * Created by igor.faria on 12/11/2015.
 */
public class WearMessageListenerService extends WearableListenerService {
    private static final String WEAR_PATH = "/DATA_SENSORS_PATH";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( WEAR_PATH ) ) {

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new String(messageEvent.getData()));
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<SensorBase>accelerometer  = processLocale(jsonArray.get(0).getAsJsonArray());
            List<SensorBase>gyroscope  = processLocale(jsonArray.get(1).getAsJsonArray());
            List<SensorBase>magnetometer  = processLocale(jsonArray.get(2).getAsJsonArray());
            String identifier = jsonArray.get(3).getAsJsonObject().get("identifier").getAsString();
            String experimentType = jsonArray.get(3).getAsJsonObject().get("experimentType").getAsString();
            String exerciseType = jsonArray.get(3).getAsJsonObject().get("exerciseType").getAsString();

            exportFile(accelerometer,1, exerciseType,experimentType, identifier);
            exportFile(gyroscope,2, exerciseType, experimentType, identifier);
            exportFile(magnetometer,3, exerciseType, experimentType, identifier);

        } else {
            super.onMessageReceived( messageEvent );
        }
    }

    private List<SensorBase> processLocale(JsonArray array) {
        Type listType = new TypeToken<ArrayList<SensorBase>>() {
        }.getType();

        return new Gson().fromJson(array, listType);
    }

    private void exportFile(List<SensorBase> lSensor, int id, String typeExercise, String typeExperiment, String identifier){
        try {

            String folderTypeExperiment = "Dataset";
            if(!typeExperiment.equalsIgnoreCase(folderTypeExperiment)) {
                folderTypeExperiment = "Experimento";
            }

            File folder = new File(Environment.getExternalStorageDirectory()+ "/FitRecognition/smatwatch");

            if (!folder.exists())
                folder.mkdirs();
            String SensorType = "";
            if(id==1)
                SensorType = "Accelerometer";
            else if(id==2)
                SensorType = "Gyroscope";
            else
                SensorType = "Magnetometer";


            final String filename = folder.getAbsolutePath().toString() + "/"+identifier+"_"+folderTypeExperiment+"_"+ SensorType+"_"+ typeExercise+".csv";

            String content="";

            FileOutputStream fOut = new FileOutputStream (new File(filename), true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            lastTime = null;

            for( SensorBase sensorBase: lSensor){

                for( int j=0; j<4; j++){
                    if( j==0)
                        content+=sensorBase.getX().toString();
                    else if(j==1)
                        content+=sensorBase.getY().toString();
                    else if(j==2)
                        content+=sensorBase.getZ().toString();
                    else if(j==3)
                        content+=getDate(sensorBase.getTimestamp());


                    content += "; ";
                }
                if(!typeExperiment.equalsIgnoreCase("Dataset"))
                    typeExercise = "?";

                content= content+ typeExercise+"\n";

            }
            Log.v(SensorType+" content is ", content);
            osw.write(content);
            osw.flush();
            osw.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private Date lastTime;
    private Long getDate(Long timeStampStr){

        Date atualDate = new Date(timeStampStr);
        if(lastTime==null)
            lastTime = atualDate;

        return (atualDate.getTime() - lastTime.getTime())/1000;

    }

}
