package br.ufba.exerciserecognition;

import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.ufba.exerciserecognition.dao.AppPreference;

/**
 * Created by igor.faria on 12/05/2016.
 */
public class BaseActivity extends WearableActivity {

    private AppPreference appPref;
    private LinearLayout progressBox;
    private TextView progressText;



    public AppPreference getAppPreference() {
        if (appPref == null)
            appPref = new AppPreference(getApplicationContext());
        return appPref;
    }
    public void setProgressBox(LinearLayout progressBox) {
        this.progressBox = progressBox;
    }
    public void setProgressText(TextView progressText) {
        this.progressText = progressText;
    }

    public void showProgress(String message) {
        if (progressBox != null) {
            progressText.setText(message);
            progressBox.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (progressBox != null)
            progressBox.setVisibility(View.GONE);
    }
}
