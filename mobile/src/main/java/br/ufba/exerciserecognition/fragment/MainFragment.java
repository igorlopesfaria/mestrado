package br.ufba.exerciserecognition.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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

    private Boolean  isRunning;

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
                if(getString(R.string.collect_dataset_training).equalsIgnoreCase(typeExperiment) && getString(R.string.select_exercise).equalsIgnoreCase(typeExercise)){
                    showSnackBar(v, getString(R.string.select_type_exercise), Snackbar.LENGTH_LONG, android.R.color.white, android.R.color.holo_red_dark);
                    return;
                }

                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                typeExperimentSP.setEnabled(false);
                typeExerciseSP.setEnabled(false);
                nameETX.setEnabled(false);
                accelerometerReader.initialize(getBaseActivity());
                gyroscopeReader.initialize(getBaseActivity());
                magnetometerReader.initialize(getBaseActivity());
                offBTN.setVisibility(View.VISIBLE);
                onBTN.setVisibility(View.GONE);

            }
        });

        offBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                typeExperimentSP.setEnabled(true);
                typeExerciseSP.setEnabled(true);
                nameETX.setEnabled(true);

                accelerometerReader.unregisterSensor();
                gyroscopeReader.unregisterSensor();
                magnetometerReader.unregisterSensor();

                lGyroscope = gyroscopeReader.getData();
                lAccelerometer = accelerometerReader.getData();
                lMagnetometer = magnetometerReader.getData();

                exportFile(lAccelerometer,1);
                exportFile(lGyroscope,2);
                exportFile(lMagnetometer,3);

                exportFile(lAccelerometer, lGyroscope,lMagnetometer );
                offBTN.setVisibility(View.GONE);
                onBTN.setVisibility(View.VISIBLE);

            }
        });


        typeExperimentSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.LTGRAY);
                    typeExerciseSP.setVisibility(View.GONE);
                    arrow2IMG.setVisibility(View.GONE);

                }else{
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                    if(position==1) {
                        typeExerciseSP.setVisibility(View.VISIBLE);
                        arrow2IMG.setVisibility(View.VISIBLE);
                    }else {
                        typeExerciseSP.setVisibility(View.GONE);
                        exerciseIMG.setVisibility(View.GONE);
                        arrow2IMG.setVisibility(View.GONE);

                    }


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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseActivity(), R.layout.item_spiner, items);
        typeExperimentSP.setAdapter(adapter);


        String[] items2 = new String[]{getString(R.string.select_exercise), getString(R.string.running), getString(R.string.walking)};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getBaseActivity(), R.layout.item_spiner, items2);
        typeExerciseSP.setAdapter(adapter2);



    }


    private void exportFile(List<SensorBase> lSensor, int id){
        try {
            getBaseActivity().getProgressBar().setVisibility(View.VISIBLE);
            String typeExercise = (String) typeExerciseSP.getSelectedItem();
            String typeExperiment = (String) typeExperimentSP.getSelectedItem();

            String folderTypeExperiment = "DataSet";
            if(!typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training))) {
                folderTypeExperiment = "Experiment";
                typeExercise = "";

            }

            File folder = new File(Environment.getExternalStorageDirectory()+ "/ExcerciseRecognition/mobile"+"/"+folderTypeExperiment);

            if (!folder.exists())
                folder.mkdir();
            String identifier = nameETX.getText().toString().trim();
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
        }
        getBaseActivity().getProgressBar().setVisibility(View.GONE);

    }

    private void exportFile(List<SensorBase> lAccelerometer,
                            List<SensorBase> lGyroscope,
                            List<SensorBase> lMagnetometer) {

        try {
            getBaseActivity().getProgressBar().setVisibility(View.VISIBLE);
            String typeExercise = (String) typeExerciseSP.getSelectedItem();
            String typeExperiment = (String) typeExperimentSP.getSelectedItem();

            String folderTypeExperiment = "DataSet";
            if(!typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training))) {
                folderTypeExperiment = "Experiment";
                typeExercise = "";
            }

            File folder = new File(Environment.getExternalStorageDirectory()+ "/ExcerciseRecognition/mobile"+"/"+folderTypeExperiment);

            if (!folder.exists())
                folder.mkdir();


            int size = lAccelerometer.size();

            if(size>lGyroscope.size())
                size = lGyroscope.size();
            if(size>lMagnetometer.size())
                size = lMagnetometer.size();
            String identifier = nameETX.getText().toString().trim();


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

                if (!typeExperiment.equalsIgnoreCase(getString(R.string.collect_dataset_training)))
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
        getBaseActivity().getProgressBar().setVisibility(View.GONE);

    }
}
