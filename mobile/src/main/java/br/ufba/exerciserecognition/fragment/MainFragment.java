package br.ufba.exerciserecognition.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import br.ufba.exerciserecognition.R;
import br.ufba.exerciserecognition.model.SensorBase;
import br.ufba.exerciserecognition.sensor.AccelerometerReader;
import br.ufba.exerciserecognition.sensor.GyroscopeReader;
import br.ufba.exerciserecognition.sensor.MagnetometerReader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends BaseFragment implements         DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String PATH = "/DATA_IDENTIFIER_PATH";


    private List<SensorBase> lAccelerometer;
    private List<SensorBase> lGyroscope;
    private List<SensorBase> lMagnetometer;
    private GoogleApiClient mGoogleApiClient;

    private Button onBTN, offBTN;
    private Chronometer chronometer;
    private AccelerometerReader accelerometerReader;
    private GyroscopeReader gyroscopeReader;
    private MagnetometerReader magnetometerReader;
    private ImageButton syncWatchBTN;
    private Spinner typeExperimentSP,typeExerciseSP;

    private EditText nameETX;
    private View view;
    private Vibrator vibrator;

    private Boolean  waiting = false;

    public MainFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        init(view);

        accelerometerReader = new AccelerometerReader();
        gyroscopeReader = new GyroscopeReader();
        magnetometerReader = new MagnetometerReader();
        offBTN.setVisibility(View.GONE);
        onBTN.setVisibility(View.VISIBLE);
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        onBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(waiting)
                    return;

                getBaseActivity().hideKeyboard();
                String identifier = nameETX.getText().toString().trim();
                if("".equals(identifier)){
                    showSnackBar(v, getString(R.string.insert_identifier), Snackbar.LENGTH_LONG, android.R.color.white, android.R.color.holo_red_dark);
                    return;
                }

                String typeExperiment = (String) typeExperimentSP.getSelectedItem();
                if(getString(R.string.select_experiment).equalsIgnoreCase(typeExperiment)){
                    showSnackBar(v, getString(R.string.select_type_experiment), Snackbar.LENGTH_LONG, android.R.color.white, android.R.color.holo_red_dark);
                    return;
                }

                String typeExercise = (String) typeExerciseSP.getSelectedItem();
                if(getString(R.string.select_exercise).equalsIgnoreCase(typeExercise)){
                    showSnackBar(v, getString(R.string.select_type_exercise), Snackbar.LENGTH_LONG, android.R.color.white, android.R.color.holo_red_dark);
                    return;
                }

                new CountDownTimer(15000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        waiting = true;

                        getBaseActivity().showProgress(getString(R.string.wait)+seconds+
                                ((seconds!=1)?getString(R.string.wait_plural):getString(R.string.wait_singular)));
                    }

                    public void onFinish() {
                        waiting= false;
                        getBaseActivity().hideProgress();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        vibrator.vibrate(1000);

                        typeExperimentSP.setEnabled(false);
                        typeExerciseSP.setEnabled(false);
                        nameETX.setEnabled(false);
                        accelerometerReader.initialize(getBaseActivity());
                        gyroscopeReader.initialize(getBaseActivity());
                        magnetometerReader.initialize(getBaseActivity());
                        offBTN.setVisibility(View.VISIBLE);
                        onBTN.setVisibility(View.GONE);

                    }

                }.start();

            }
        });

        offBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               stopChronometer();
            }
        });


        typeExperimentSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.LTGRAY);
                }else{
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        typeExerciseSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.LTGRAY);
                }else{
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        syncWatchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIdentifier();
            }
        });
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {

                String typeExperiment = (String) typeExperimentSP.getSelectedItem();
                String currentTime = chronometer.getText().toString();
                boolean stop = false;
                if (typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training)) && currentTime.equals("02:00")){
                    stop = true;
                }else if(typeExperiment.equalsIgnoreCase(getString(R.string.execute_experiment)) && currentTime.equals("01:00")) {
                    stop = true;
                }

                if (stop) {
                    stopChronometer();
                }
            }
        });
    }

    private void stopChronometer() {
        chronometer.stop();
        vibrator.vibrate(1000);

        typeExperimentSP.setEnabled(true);
        typeExerciseSP.setEnabled(true);
        nameETX.setEnabled(true);

        getBaseActivity().showProgress(getString(R.string.stopping_collect));

        accelerometerReader.unregisterSensor();
        gyroscopeReader.unregisterSensor();
        magnetometerReader.unregisterSensor();

        lGyroscope = gyroscopeReader.getData();
        lAccelerometer = accelerometerReader.getData();
        lMagnetometer = magnetometerReader.getData();

        getBaseActivity().showProgress(getString(R.string.creating_accelerometer));
        exportFile(lAccelerometer,1);
        getBaseActivity().showProgress(getString(R.string.creating_gyroscope));
        exportFile(lGyroscope,2);
        getBaseActivity().showProgress(getString(R.string.creating_magnetometer));
        exportFile(lMagnetometer,3);
        getBaseActivity().showProgress(getString(R.string.creating_all_sensor_data));
        offBTN.setVisibility(View.GONE);
        onBTN.setVisibility(View.VISIBLE);
        getBaseActivity().hideProgress();

    }


    private void init(View view) {
        onBTN = (Button) view.findViewById(R.id.onBTN);
        offBTN = (Button) view.findViewById(R.id.offBTN);

        chronometer = (Chronometer) view.findViewById(R.id.chronometer);

        typeExperimentSP  = (Spinner) view.findViewById(R.id.typeExperimentSP);
        typeExerciseSP  = (Spinner) view.findViewById(R.id.typeExerciseSP);

        nameETX = (EditText) view.findViewById(R.id.nameETX);
        
        String[] items = new String[]{getString(R.string.select_experiment), getString(R.string.collect_dataset_training), getString(R.string.execute_experiment)};
        ArrayAdapter<String> adapter = new ArrayAdapter(getBaseActivity(), R.layout.item_spiner, items);
        typeExperimentSP.setAdapter(adapter);


        String[] items2 = new String[]{
                getString(R.string.select_exercise),
                getString(R.string.biceps),
                getString(R.string.chester),
                getString(R.string.shoulder),
                getString(R.string.back)};
        ArrayAdapter<String> adapter2 = new ArrayAdapter(getBaseActivity(), R.layout.item_spiner, items2);
        typeExerciseSP.setAdapter(adapter2);

        vibrator = (Vibrator) getBaseActivity().getSystemService(Context.VIBRATOR_SERVICE);
        syncWatchBTN= (ImageButton) view.findViewById(R.id.syncWatchBTN);

    }


    private void exportFile(List<SensorBase> lSensor, int id){
        try {

            if(!isExternalStorageWritable()){
                showSnackBar(view, getString(R.string.problem_generate_file), Snackbar.LENGTH_LONG, android.R.color.white, android.R.color.holo_red_dark);
                return;
            }

            String typeExercise = (String) typeExerciseSP.getSelectedItem();
            String typeExperiment = (String) typeExperimentSP.getSelectedItem();

            String folderTypeExperiment = "Dataset";
            if(!typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training))) {
                folderTypeExperiment = "Experimento";
            }

            File folder = new File(Environment.getExternalStorageDirectory()+ "/FitRecognition/smartphone");

            if (!folder.exists())
                folder.mkdirs();

            String identifier = nameETX.getText().toString().trim();

            String SensorType;
            if(id==1)
                SensorType = "Accelerometer";
            else if(id==2)
                SensorType = "Gyroscope";
            else
                SensorType = "Magnetometer";


            final String filename = folder.getAbsolutePath().toString() + "/"+identifier+"_"+folderTypeExperiment+"_"+ SensorType+"_"+ typeExercise+".csv";

            String content="";

            FileOutputStream fOut = new FileOutputStream (new File(filename), true); // true will be same as Context.MODE_APPEND
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            lastTime = null;
            // Write the string to the file
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
                if(!typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training)))
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
            showSnackBar(view, getString(R.string.problem_generate_file), Snackbar.LENGTH_LONG, android.R.color.white, android.R.color.holo_red_dark);

        }

    }

    private Date lastTime;

    private Long getDate(Long timeStampStr){

        Date atualDate = new Date(timeStampStr);
        if(lastTime==null)
            lastTime = atualDate;

        return (atualDate.getTime() - lastTime.getTime())/1000;

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private void sendIdentifier() {
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

                    String identifier = nameETX.getText().toString();


                    PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            PATH,
                            identifier.getBytes());

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
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPause() {
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
