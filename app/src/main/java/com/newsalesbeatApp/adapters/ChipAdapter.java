package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newsalesbeatApp.R;

import java.util.ArrayList;

public class ChipAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> chipList;

    public ChipAdapter(Context context, ArrayList<String> chipList) {
        super(context, 0, chipList);
        this.context = context;
        this.chipList = chipList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Inflate custom chip layout if not already created
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_list, parent, false);
        }

        // Get the current item from the list
        String chipText = chipList.get(position);

        // Bind data to views
        TextView chipLabel = convertView.findViewById(R.id.chipText);
        ImageView chipIcon = convertView.findViewById(R.id.chipIcon);

        chipLabel.setText(chipText);

        chipIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove item from the list and notify adapter
                chipList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}

