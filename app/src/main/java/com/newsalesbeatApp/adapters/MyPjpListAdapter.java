package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MyPjp;

import java.util.ArrayList;

/*
 * Created by abc on 9/21/18.
 */

public class MyPjpListAdapter extends RecyclerView.Adapter<MyPjpListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<MyPjp> myPjpArrayList;

    public MyPjpListAdapter(Context ctx, ArrayList<MyPjp> myPjps) {
        this.context = ctx;
        this.myPjpArrayList = myPjps;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pjp_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {

        // 1 Retailing
        // 2 Meeting
        // 3 Joint Working
        // 4 Distributor Search
        // 5 Leave
        // 6 Weak Off
        // 7 Market Survey
        // 8 Holiday
        // 9 Office Visit

        viewHolder.tvTownName.setText(myPjpArrayList.get(position).getTownName());
        viewHolder.tvDistributor.setText(myPjpArrayList.get(position).getDistributorName());
        viewHolder.tvBeatName.setText(myPjpArrayList.get(position).getBeatName());
        viewHolder.tvJwWith.setText(myPjpArrayList.get(position).getJointworkingwith());

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("1")) {
            viewHolder.tvActivity.setText("Retailing");
            viewHolder.tvTC.setText(myPjpArrayList.get(position).getTc());
            viewHolder.tvPC.setText(myPjpArrayList.get(position).getPc());
            viewHolder.tvSale.setText(myPjpArrayList.get(position).getSale());

            viewHolder.llSale.setVisibility(View.VISIBLE);
            viewHolder.llPC.setVisibility(View.VISIBLE);
            viewHolder.llTC.setVisibility(View.VISIBLE);

        } else {

            viewHolder.llSale.setVisibility(View.GONE);
            viewHolder.llPC.setVisibility(View.GONE);
            viewHolder.llTC.setVisibility(View.GONE);
        }

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("2"))
            viewHolder.tvActivity.setText("Meeting");

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("3"))
            viewHolder.tvActivity.setText("Joint Working");

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("4"))
            viewHolder.tvActivity.setText("Distributor Search");

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("5")) {
            viewHolder.tvActivity.setText("Leave");
            viewHolder.tvTownName.setText("N/A");
        }

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("6")) {
            viewHolder.tvActivity.setText("Weak Off");
            viewHolder.tvTownName.setText("N/A");
        }

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("7"))
            viewHolder.tvActivity.setText("Market Survey");

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("8")) {
            viewHolder.tvActivity.setText("Holiday");
            viewHolder.tvTownName.setText("N/A");
        }

        if (myPjpArrayList.get(position).getActivity().equalsIgnoreCase("9")) {
            viewHolder.tvActivity.setText("Office Visit");
            viewHolder.tvTownName.setText("N/A");
        }


        if (myPjpArrayList.get(position).getAssigneeEmp() != null
                && !myPjpArrayList.get(position).getAssigneeEmp().equalsIgnoreCase("null")
                && !myPjpArrayList.get(position).getAssigneeEmp().isEmpty()) {

            viewHolder.tvAssigneeEmp.setText(myPjpArrayList.get(position).getAssigneeEmp());

        } else if (myPjpArrayList.get(position).getAssigneeAdmin() != null
                && !myPjpArrayList.get(position).getAssigneeAdmin().equalsIgnoreCase("null")
                && !myPjpArrayList.get(position).getAssigneeAdmin().isEmpty()) {

            viewHolder.tvAssigneeEmp.setText(myPjpArrayList.get(position).getAssigneeAdmin());
        } else {

            viewHolder.tvAssigneeEmp.setText("N/A");
        }


        viewHolder.tvRemarks.setText(myPjpArrayList.get(position).getRemarks());


        if (myPjpArrayList.get(position).getDistributorName() == null
                || myPjpArrayList.get(position).getDistributorName().isEmpty())
            viewHolder.llDis.setVisibility(View.GONE);

        if (myPjpArrayList.get(position).getBeatName() == null
                || myPjpArrayList.get(position).getBeatName().isEmpty())
            viewHolder.llBeat.setVisibility(View.GONE);


        if (myPjpArrayList.get(position).getJointworkingwith() == null
                || myPjpArrayList.get(position).getJointworkingwith().isEmpty())
            viewHolder.llJointWorking.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return myPjpArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTownName;
        TextView tvDistributor;
        TextView tvBeatName;
        TextView tvAssigneeEmp;
        TextView tvJwWith;
        TextView tvActivity;
        TextView tvTC;
        TextView tvPC;
        TextView tvSale;
        TextView tvRemarks;


        LinearLayout llDis, llBeat, llJointWorking, llTC, llPC, llSale;

        public MyViewHolder(View row) {
            super(row);
            tvTownName = row.findViewById(R.id.tvTownName);
            tvDistributor = row.findViewById(R.id.tvDistributorName);
            tvBeatName = row.findViewById(R.id.tvBeatName);
            tvAssigneeEmp = row.findViewById(R.id.tvAssineeName);
            tvJwWith = row.findViewById(R.id.tvJwWith);
            tvActivity = row.findViewById(R.id.tvActivity);
            tvTC = row.findViewById(R.id.tvTC);
            tvPC = row.findViewById(R.id.tvPC);
            tvSale = row.findViewById(R.id.tvSale);
            tvRemarks = row.findViewById(R.id.tvRemarks);
            llDis = row.findViewById(R.id.llD);
            llBeat = row.findViewById(R.id.llB);
            llTC = row.findViewById(R.id.llTC);
            llPC = row.findViewById(R.id.llPC);
            llSale = row.findViewById(R.id.llSale);
            llJointWorking = row.findViewById(R.id.llJw);
        }
    }
}
