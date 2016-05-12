package br.ufba.exerciserecognition;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;

import java.util.ArrayList;
import java.util.List;

import br.ufba.exerciserecognition.adapter.OptionAdapter;
import br.ufba.exerciserecognition.model.MenuItem;

public class OptionExperimentActivity extends BaseActivity implements WearableListView.ClickListener {

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
        menuItem.setTitle(getString(R.string.collect_dataset_training));

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setTitle(getString(R.string.execute_experiment));

        elements.add(menuItem);
        elements.add(menuItem2);
        // Assign an adapter to the list
        listView.setAdapter(new OptionAdapter(this, elements));

        // Set a click listener
        listView.setClickListener(this);
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        getAppPreference().setKeyPrefsTypeExperiment(elements.get(tag).getTitle());

        this.finish();
    }

    @Override
    public void onTopEmptyRegionClick() {
    }
}
