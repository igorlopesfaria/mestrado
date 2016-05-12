package br.ufba.exerciserecognition;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import br.ufba.exerciserecognition.fragment.MainFragment;
import br.ufba.exerciserecognition.fragment.SplashFragment;

/**
 * Created by igor.faria on 20/12/2015.
 */

public class BaseActivity extends AppCompatActivity {

    public Fragment actualFragment;
    private ProgressBar progressBar;



    public void changeFragment(Fragment fragmentToChange) {

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragmentToChange, fragmentToChange.getClass().toString())
                .addToBackStack(null)
                .commit();

    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

    }
    @Override
    public void onBackPressed() {
        if (actualFragment instanceof SplashFragment ||
                actualFragment instanceof MainFragment )
            this.finish();
       else
            super.onBackPressed();
    }



}
