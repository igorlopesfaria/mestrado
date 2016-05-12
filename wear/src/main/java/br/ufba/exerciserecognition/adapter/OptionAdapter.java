package br.ufba.exerciserecognition.adapter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufba.exerciserecognition.R;
import br.ufba.exerciserecognition.model.MenuItem;

/**
 * Created by igor.faria on 11/05/2016.
 */
public class OptionAdapter extends WearableListView.Adapter {
    private List<MenuItem> mDataset;
    private final Context mContext;
    private final LayoutInflater mInflater;

    // Provide a suitable constructor (depends on the kind of dataset)
    public OptionAdapter(Context context, List<MenuItem> dataset) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = dataset;
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView textView;
        private TextView subtitleView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            textView = (TextView) itemView.findViewById(R.id.titleTX);
            subtitleView= (TextView) itemView.findViewById(R.id.subtitleTX);
        }
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // Inflate our custom layout for list items
        return new ItemViewHolder(mInflater.inflate(R.layout.option_item, null));
    }

    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {
        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        MenuItem menuItem = mDataset.get(position);

        TextView view = itemHolder.textView;
        view.setText(menuItem.getTitle());

        TextView viewSubtitle = itemHolder.subtitleView;
        // replace text contents
        if(menuItem.getSubtitle()!= null){
            viewSubtitle.setVisibility(View.VISIBLE);
            viewSubtitle.setText(menuItem.getSubtitle());
        }else{
            viewSubtitle.setVisibility(View.GONE);
            viewSubtitle.setText(menuItem.getSubtitle());

        }


        // replace list item's metadata
        holder.itemView.setTag(position);
    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
