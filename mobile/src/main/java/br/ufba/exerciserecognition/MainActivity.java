package br.ufba.exerciserecognition;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import br.ufba.exerciserecognition.fragment.SplashFragment;

public class MainActivity extends BaseActivity {

    private Fragment fragment;
    private Toolbar mToolbar;
    private TextView titleTX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initScreen();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        if (savedInstanceState == null) {
            fragment =   SplashFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.container,fragment)
                    .commit();
        }else {
            fragment =  getFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    private void initScreen() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTX= (TextView) findViewById(R.id.titleTX);
        setProgressBox((LinearLayout) findViewById(R.id.progressBox));
        setProgressText((TextView) findViewById(R.id.progressText));

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/AndroidScratch.ttf");
        titleTX.setTypeface(font);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
