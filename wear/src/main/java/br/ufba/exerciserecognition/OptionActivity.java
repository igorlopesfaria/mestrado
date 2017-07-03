package br.ufba.exerciserecognition;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WearableListView;
import java.util.ArrayList;
import java.util.List;
import br.ufba.exerciserecognition.adapter.OptionAdapter;
import br.ufba.exerciserecognition.model.MenuItem;

public class OptionActivity extends BaseActivity implements WearableListView.ClickListener {

    private List<MenuItem> elements;
    private WearableListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_option);

        // Get the list component from the layout of the activity
        listView = (WearableListView) findViewById(R.id.wearable_list);

        // Set a click listener
        listView.setClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();

    }

    private void setAdapter() {
        elements = new ArrayList();

        MenuItem menuItem = new MenuItem();
        menuItem.setTitle(getString(R.string.identifier));
        if(getAppPreference().getKeyPrefsIdentifier()!=null)
            menuItem.setSubtitle(getAppPreference().getKeyPrefsIdentifier());

        MenuItem menuItem3 = new MenuItem();
        menuItem3.setTitle(getString(R.string.type_exercise));
        if(getAppPreference().getKeyPrefsTypeExercise()!=null)
         menuItem3.setSubtitle(getAppPreference().getKeyPrefsTypeExercise());

        elements.add(menuItem);
        elements.add(menuItem3);


        // Assign an adapter to the list
        listView.setAdapter(new OptionAdapter(this, elements));
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();

        if(tag ==0){
            displaySpeechRecognizer();

        } else if(tag ==1){
            Intent intent = new Intent(getApplicationContext(), OptionExerciseActivity.class);
            startActivity(intent);

        }
        // use this data to complete some action ...
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt");

        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            getAppPreference().setKeyPrefsIdentifier(spokenText);
            setAdapter();
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
