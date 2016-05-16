package br.ufba.exerciserecognition;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.List;

import br.ufba.exerciserecognition.model.SensorBase;
import br.ufba.exerciserecognition.sensor.AccelerometerReader;
import br.ufba.exerciserecognition.sensor.GyroscopeReader;
import br.ufba.exerciserecognition.sensor.MagnetometerReader;

public class MainActivity extends BaseActivity  implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String PATH = "/DATA_SENSORS_PATH";


    private TextView typeExerciseTX, typeExperimentTX, identifierTX;
    private List<SensorBase> lAccelerometer;
    private List<SensorBase> lGyroscope;
    private List<SensorBase> lMagnetometer;
    private Chronometer chronometer;
    private AccelerometerReader accelerometerReader;
    private GyroscopeReader gyroscopeReader;
    private MagnetometerReader magnetometerReader;
    private GoogleApiClient mGoogleApiClient;
    private ImageButton settingsBTN, playBTN, stopBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

        accelerometerReader = new AccelerometerReader();
        gyroscopeReader = new GyroscopeReader();
        magnetometerReader = new MagnetometerReader();
        stopBTN.setVisibility(View.GONE);
        playBTN.setVisibility(View.VISIBLE);

        playBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identifier = identifierTX.getText().toString().trim();
                if("".equals(identifier) || getString(R.string.empty).equals(identifier)){
                    Toast.makeText(getApplicationContext(),getString(R.string.insert_identifier),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String typeExperiment =  typeExperimentTX.getText().toString().trim();
                if("".equals(typeExperiment) || getString(R.string.empty).equals(typeExperiment)){
                    Toast.makeText(getApplicationContext(),getString(R.string.select_type_experiment),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String typeExercise =  typeExerciseTX.getText().toString().trim();
                if("".equals(typeExercise) || getString(R.string.empty).equals(typeExercise)){
                    Toast.makeText(getApplicationContext(),getString(R.string.select_type_experiment),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                accelerometerReader.initialize(getApplication());
                gyroscopeReader.initialize(getApplication());
                magnetometerReader.initialize(getApplication());
                stopBTN.setVisibility(View.VISIBLE);
                playBTN.setVisibility(View.GONE);

            }
        });

        stopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                accelerometerReader.unregisterSensor();
                gyroscopeReader.unregisterSensor();
                magnetometerReader.unregisterSensor();

                lGyroscope = gyroscopeReader.getData();
                lAccelerometer = accelerometerReader.getData();
                lMagnetometer = magnetometerReader.getData();


                sendValues();

                stopBTN.setVisibility(View.GONE);
                playBTN.setVisibility(View.VISIBLE);

            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        settingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                startActivity(intent);
            }
        });


        //  getAppPreference().setKeyPrefsTypeExercise(elements[tag]);
    }




    private void init() {
        playBTN = (ImageButton)findViewById(R.id.playBTN);
        stopBTN = (ImageButton)findViewById(R.id.stopBTN);
        chronometer= (Chronometer)findViewById(R.id.chronometer);
        settingsBTN= (ImageButton)findViewById(R.id.settingsBTN);
        identifierTX= (TextView) findViewById(R.id.identifierTX);
        typeExerciseTX= (TextView) findViewById(R.id.typeExerciseTX);
        typeExperimentTX= (TextView)findViewById(R.id.typeExperimentTX);
    }

    private void sendValues() {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                for (int i = 0; i < result.getNodes().size(); i++) {
                    Node node = result.getNodes().get(i);
                    String nName = node.getDisplayName();
                    String nId = node.getId();
                    Log.d("MainActivity", "Node name and ID: " + nName + " | " + nId);

                    Wearable.MessageApi.addListener(mGoogleApiClient, new MessageApi.MessageListener() {
                        @Override
                        public void onMessageReceived(MessageEvent messageEvent) {
                            Log.d("MainActivity", "Message received: " + messageEvent);
                        }
                    });
                    Gson gson = new GsonBuilder().create();
                    JsonArray accelerometerArrayJSON = gson.toJsonTree(lAccelerometer).getAsJsonArray();
                    JsonArray gyroscopeArrayJSON = gson.toJsonTree(lGyroscope).getAsJsonArray();
                    JsonArray magnetometerArrayJSON = gson.toJsonTree(lMagnetometer).getAsJsonArray();

                    JsonObject identifier = new JsonObject();
                    identifier.addProperty("identifier",identifierTX.getText().toString());
                    JsonObject experimentType = new JsonObject();
                    identifier.addProperty("experimentType",typeExperimentTX.getText().toString());
                    JsonObject exerciseType = new JsonObject();
                    identifier.addProperty("exerciseType",typeExerciseTX.getText().toString());

                    JsonArray resultToSend = new JsonArray();
                    resultToSend.add(accelerometerArrayJSON);
                    resultToSend.add(gyroscopeArrayJSON);
                    resultToSend.add(magnetometerArrayJSON);
                    resultToSend.add(identifier);
                    resultToSend.add(experimentType);
                    resultToSend.add(exerciseType);

                    PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            PATH,
                            resultToSend.toString().getBytes());

                    messageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Status status = sendMessageResult.getStatus();
                            Log.d("MainActivity", "Status: " + status.toString());

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if(getAppPreference().getKeyPrefsIdentifier()!=null && !"".equalsIgnoreCase(getAppPreference().getKeyPrefsIdentifier())) {
            identifierTX.setText(getAppPreference().getKeyPrefsIdentifier());
        }

        if(getAppPreference().getKeyPrefsTypeExercise()!=null && !"".equalsIgnoreCase(getAppPreference().getKeyPrefsTypeExercise())) {
            typeExerciseTX.setVisibility(View.VISIBLE);
            typeExerciseTX.setText(getAppPreference().getKeyPrefsTypeExercise());
        }

        if(getAppPreference().getKeyPrefsTypeExperiment()!=null && !"".equalsIgnoreCase(getAppPreference().getKeyPrefsTypeExperiment())) {
            typeExperimentTX.setText(getAppPreference().getKeyPrefsTypeExperiment());
            if(getString(R.string.collect_dataset_training).equalsIgnoreCase(getAppPreference().getKeyPrefsTypeExperiment()))
                typeExerciseTX.setVisibility(View.VISIBLE);
            else
                typeExerciseTX.setVisibility(View.GONE);


        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
