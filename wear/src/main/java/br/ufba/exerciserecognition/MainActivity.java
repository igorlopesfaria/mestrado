package br.ufba.exerciserecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private Boolean  waiting = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();


        init();

        accelerometerReader = new AccelerometerReader();
        gyroscopeReader = new GyroscopeReader();
        magnetometerReader = new MagnetometerReader();
        stopBTN.setVisibility(View.GONE);
        playBTN.setVisibility(View.VISIBLE);

        playBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(waiting)
                    return;

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
                    Toast.makeText(getApplicationContext(),getString(R.string.select_type_exercise),
                            Toast.LENGTH_LONG).show();
                    return;
                }


                new CountDownTimer(15000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        waiting = true;
                        showProgress("Aguarde... "+seconds+" seg");
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        waiting= false;
                        hideProgress();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        vibrator.vibrate(1000);


                        accelerometerReader.initialize(getApplication());
                        gyroscopeReader.initialize(getApplication());
                        magnetometerReader.initialize(getApplication());
                        stopBTN.setVisibility(View.VISIBLE);
                        playBTN.setVisibility(View.GONE);
                    }

                }.start();


            }
        });

        stopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopChronometer();
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

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {

                String typeExperiment = typeExperimentTX.getText().toString();
                String currentTime = chronometer.getText().toString();
                boolean stop = false;
                if (typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training)) && currentTime.equals("01:00")){
                    stop = true;
                }else if(typeExperiment.equalsIgnoreCase(getString(R.string.execute_experiment)) && currentTime.equals("00:30")) {
                    stop = true;
                }

                if (stop) {
                    stopChronometer();
                }
            }
        });

    }


    private void stopChronometer(){
        chronometer.stop();
        vibrator.vibrate(1000);

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


    private void init() {
        playBTN = (ImageButton)findViewById(R.id.playBTN);
        stopBTN = (ImageButton)findViewById(R.id.stopBTN);
        chronometer= (Chronometer)findViewById(R.id.chronometer);
        settingsBTN= (ImageButton)findViewById(R.id.settingsBTN);
        identifierTX= (TextView) findViewById(R.id.identifierTX);
        typeExerciseTX= (TextView) findViewById(R.id.typeExerciseTX);
        typeExperimentTX= (TextView)findViewById(R.id.typeExperimentTX);

        setProgressBox((LinearLayout) findViewById(R.id.progressBox));
        setProgressText((TextView) findViewById(R.id.progressText));
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
            typeExerciseTX.setText(getAppPreference().getKeyPrefsTypeExercise());
        }

        if(getAppPreference().getKeyPrefsTypeExperiment()!=null && !"".equalsIgnoreCase(getAppPreference().getKeyPrefsTypeExperiment())) {
            typeExperimentTX.setText(getAppPreference().getKeyPrefsTypeExperiment());
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
