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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
public class MainFragment extends BaseFragment {


    private List<SensorBase> lAccelerometer;
    private List<SensorBase> lGyroscope;
    private List<SensorBase> lMagnetometer;

    private Button onBTN, offBTN;
    private Chronometer chronometer;
    private AccelerometerReader accelerometerReader;
    private GyroscopeReader gyroscopeReader;
    private MagnetometerReader magnetometerReader;

    private ImageView arrow2IMG, exerciseIMG;
    private Spinner typeExperimentSP,typeExerciseSP;

    private EditText nameETX;
    private View view;
    Vibrator vibrator;

    private Boolean  waiting = false;

    public MainFragment() {
        // Required empty public constructor
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
                        getBaseActivity().showProgress("Aguarde... "+seconds+" seg");
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
                    exerciseIMG.setVisibility(View.GONE);
                }else{
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                    exerciseIMG.setVisibility(View.VISIBLE);
                    if(position==1)
                        exerciseIMG.setImageDrawable(getResources().getDrawable(R.drawable.running_small));
                    else
                        exerciseIMG.setImageDrawable(getResources().getDrawable(R.drawable.walking_small));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                // TODO Auto-generated method stub
                String typeExperiment = (String) typeExperimentSP.getSelectedItem();
                String currentTime = chronometer.getText().toString();
                boolean stop = false;
                if (typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training)) && currentTime.equals("02:00")){
//                    offBTN.performClick();
                    stop = true;
                }else if(typeExperiment.equalsIgnoreCase(getString(R.string.execute_experiment)) && currentTime.equals("00:05")) {
//                    offBTN.performClick();
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
//              exportFile(lAccelerometer, lGyroscope,lMagnetometer );
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
        exerciseIMG = (ImageView) view.findViewById(R.id.exerciseIMG);
        arrow2IMG = (ImageView) view.findViewById(R.id.arrow2IMG);

        nameETX = (EditText) view.findViewById(R.id.nameETX);
        
        String[] items = new String[]{getString(R.string.select_experiment), getString(R.string.collect_dataset_training), getString(R.string.execute_experiment)};
        ArrayAdapter<String> adapter = new ArrayAdapter(getBaseActivity(), R.layout.item_spiner, items);
        typeExperimentSP.setAdapter(adapter);


        String[] items2 = new String[]{getString(R.string.select_exercise), getString(R.string.running), getString(R.string.walking)};
        ArrayAdapter<String> adapter2 = new ArrayAdapter(getBaseActivity(), R.layout.item_spiner, items2);
        typeExerciseSP.setAdapter(adapter2);

        vibrator = (Vibrator) getBaseActivity().getSystemService(Context.VIBRATOR_SERVICE);


    }


    private void exportFile(List<SensorBase> lSensor, int id){
        try {

            if(!isExternalStorageWritable()){
                showSnackBar(view, getString(R.string.problem_generate_file), Snackbar.LENGTH_LONG, android.R.color.white, android.R.color.holo_red_dark);
                return;
            }

            String typeExercise = (String) typeExerciseSP.getSelectedItem();
            String typeExperiment = (String) typeExperimentSP.getSelectedItem();

            String folderTypeExperiment = "DataSet";
            if(!typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training))) {
                folderTypeExperiment = "Experimento";
            }

            File folder = new File(Environment.getExternalStorageDirectory()+ "/ExerciseRecognition/mobile");

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

            // Write the string to the file
            for( SensorBase sensorBase: lSensor){

                for( int j=0; j<3; j++){
                    if( j==0)
                        content+=sensorBase.getX().toString();
                    else if(j==1)
                        content+=sensorBase.getY().toString();
                    else if(j==2)
                        content+=sensorBase.getZ().toString();


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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
