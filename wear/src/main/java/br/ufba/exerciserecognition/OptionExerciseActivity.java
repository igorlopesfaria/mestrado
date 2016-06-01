package br.ufba.exerciserecognition;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufba.exerciserecognition.adapter.OptionAdapter;
import br.ufba.exerciserecognition.model.MenuItem;

public class OptionExerciseActivity extends BaseActivity implements WearableListView.ClickListener {

    private List<MenuItem> elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_option);

        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) findViewById(R.id.wearable_list);
        elements = new ArrayList();

        MenuItem menuItem = new MenuItem();
        menuItem.setTitle(getString(R.string.biceps));

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setTitle(getString(R.string.chester));

        MenuItem menuItem3 = new MenuItem();
        menuItem3.setTitle(getString(R.string.shoulder));

        MenuItem menuItem4 = new MenuItem();
        menuItem4.setTitle(getString(R.string.back));

        elements.add(menuItem);
        elements.add(menuItem2);
        elements.add(menuItem3);
        elements.add(menuItem4);

        // Assign an adapter to the list
        listView.setAdapter(new OptionAdapter(this, elements));

        // Set a click listener
        listView.setClickListener(this);
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        // use this data to complete some action ...

        getAppPreference().setKeyPrefsTypeExercise(elements.get(tag).getTitle());

        this.finish();
    }

    @Override
    public void onTopEmptyRegionClick() {
    }
}
