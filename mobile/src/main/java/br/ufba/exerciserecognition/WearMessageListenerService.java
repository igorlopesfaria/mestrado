package br.ufba.exerciserecognition;

import android.os.Environment;
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
import java.util.List;

import br.ufba.exerciserecognition.model.SensorBase;

/**
 * Created by igor.faria on 12/11/2015.
 */
public class WearMessageListenerService extends WearableListenerService {
    private static final String WEAR_PATH = "/DATA_SENSORS_PATH";
    public static final String WEAR_DATA = "DATA_SENSORS";

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
            exportFile(accelerometer, gyroscope,magnetometer,  exerciseType, experimentType, identifier );

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
                typeExercise = "";

            }

            File folder = new File(Environment.getExternalStorageDirectory()+ "/ExcerciseRecognition/wearable"+"/"+folderTypeExperiment);

            if (!folder.exists())
                folder.mkdir();
            String SensorType = "";
            if(id==1)
                SensorType = "Accelerometer";
            else if(id==2)
                SensorType = "Gyroscope";
            else
                SensorType = "Magnetometer";


            final String filename = folder.getAbsolutePath().toString() + "/"+identifier+"_"+ SensorType+ typeExercise+".csv";

            String content="";

            FileOutputStream fOut = new FileOutputStream (new File(filename), true); // true will be same as Context.MODE_APPEND
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            for( SensorBase sensorBase: lSensor){

                for( int j=0; j<3; j++){
                    if( j==0)
                        content+=sensorBase.getX().toString();
                    else if(j==1)
                        content+=sensorBase.getY().toString();
                    else if(j==2)
                        content+=sensorBase.getZ().toString();


                    content += " ,";
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

    private void exportFile(List<SensorBase> lAccelerometer,
                            List<SensorBase> lGyroscope,
                            List<SensorBase> lMagnetometer, String typeExercise, String typeExperiment, String identifier){
        try {

            String folderTypeExperiment = "Dataset";
            if(!typeExperiment.equalsIgnoreCase(folderTypeExperiment)) {
                folderTypeExperiment = "Experimento";
                typeExercise = "";
            }

            File folder = new File(Environment.getExternalStorageDirectory()+ "/ExcerciseRecognition/wearable"+"/"+folderTypeExperiment);

            if (!folder.exists())
                folder.mkdir();


            int size = lAccelerometer.size();

            if(size>lGyroscope.size())
                size = lGyroscope.size();
            if(size>lMagnetometer.size())
                size = lMagnetometer.size();



            final String filename = folder.getAbsolutePath().toString() +"/"+identifier+"_"+"AllSensors"+ typeExercise+".csv";
            String content="";

            FileOutputStream fOut = new FileOutputStream (new File(filename), true); // true will be same as Context.MODE_APPEND
            OutputStreamWriter osw = new OutputStreamWriter(fOut);


            // Write the string to the file
            for(int i= 0 ; i< size; i++) {

                content += lAccelerometer.get(i).getX().toString();
                content += " ,";
                content += lAccelerometer.get(i).getY().toString();
                content += " ,";
                content += lAccelerometer.get(i).getZ().toString();
                content += " ,";

                content += lGyroscope.get(i).getX().toString();
                content += " ,";
                content += lGyroscope.get(i).getY().toString();
                content += " ,";
                content += lGyroscope.get(i).getZ().toString();
                content += " ,";

                content += lMagnetometer.get(i).getX().toString();
                content += " ,";
                content += lMagnetometer.get(i).getY().toString();
                content += " ,";
                content += lMagnetometer.get(i).getZ().toString();

                if (!typeExperiment.equalsIgnoreCase("Dataset"))
                    typeExercise = "?";


                content = content + " ," + typeExercise + "\n";
            }
            Log.v("AllSensors content is ", content);
            osw.write(content);
            osw.flush();
            osw.close();

        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
}
