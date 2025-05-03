package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.MyViewHolder> implements Filterable {

    private OnItemClickListener listener;
    private Context context;
    private int lastCheckedPosition = -1;

    public ArrayList<String> getAcitvityList() {
        return acitvityList;
    }

    public void setAcitvityList(ArrayList<String> acitvityList) {
        this.acitvityList = acitvityList;
    }

    public ArrayList<String> acitvityList;
    public ArrayList<String> acitvityList2;
    private ArrayList<Boolean> isChecked = new ArrayList<>();
    private boolean flagMulti;
    private boolean flag = false;

    public ActivityListAdapter(Context context, ArrayList<String> activityListArr,
                               OnItemClickListener listener, boolean flagMulti) {
        this.context = context;
        this.acitvityList = activityListArr;
        this.acitvityList2 = activityListArr;
        this.listener = listener;
        this.flagMulti = flagMulti;
        setFalse(acitvityList);
    }

    private void setFalse(ArrayList<String> acitvityList) {

        for (int i = 0; i < acitvityList.size(); i++) {
            isChecked.add(false);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvActivityName.setText(acitvityList.get(position));
        holder.bind(position, listener);

        if (position == 0 && acitvityList.get(0).equalsIgnoreCase("I will be on leave"))
            holder.tvActivityName.setTextColor(Color.parseColor("#F0544D"));


        if (flagMulti) {

            holder.llActList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!flag) {

                        flag = true;
                        holder.chbActSelect.setChecked(flag);
                        Log.e("ChkClick1",holder.tvActivityName.getText().toString());
                    } else {

                        flag = false;
                        holder.chbActSelect.setChecked(flag);
                        Log.e("ChkClick2",holder.tvActivityName.getText().toString());
                    }

                }
            });

            holder.chbActSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
//                        if(holder.tvActivityName.getText().toString().equals("MAHARAJGANJ"))
//                        {
//                            int indx = acitvityList2.indexOf(holder.tvActivityName.getText());
//                        }
                        listener.onItemClick(position);
                    } else {
                        listener.onItemClick2(position);
                    }
                }
            });

        } else {

            //@Umesh 20220908
            int indx = acitvityList2.indexOf(holder.tvActivityName.getText());

            holder.chbActSelect.setChecked(position == lastCheckedPosition);

            if (position == lastCheckedPosition)
                listener.onItemClick(indx);

            holder.chbActSelect.setOnClickListener(v -> {
                if (position == lastCheckedPosition) {
                    holder.chbActSelect.setChecked(false);
                    lastCheckedPosition = -1;
                    listener.onItemClick2(position);
                } else {
                    lastCheckedPosition = position;
                    notifyDataSetChanged();
                }
            });

            holder.llActList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == lastCheckedPosition) {
                        holder.chbActSelect.setChecked(false);
                        lastCheckedPosition = -1;
                        listener.onItemClick2(position);
                    } else {
                        lastCheckedPosition = position;
                        notifyDataSetChanged();
                    }

                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return acitvityList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    acitvityList = acitvityList2;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String row : acitvityList2) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    acitvityList = filteredList;
                    setAcitvityList(filteredList);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = acitvityList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                acitvityList = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvActivityName;
        CheckBox chbActSelect;
        LinearLayout llActList;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            chbActSelect = itemView.findViewById(R.id.chbActSelect);
            llActList = itemView.findViewById(R.id.llActList);
        }

        public void bind(int position, OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}
