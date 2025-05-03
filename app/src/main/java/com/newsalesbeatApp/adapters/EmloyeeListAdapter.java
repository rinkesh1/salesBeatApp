package com.newsalesbeatApp.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;

import java.util.ArrayList;

/**
 * Created by Dhirendra Thakur on 15-01-2018.
 */

public class EmloyeeListAdapter extends RecyclerView.Adapter<EmloyeeListAdapter.MyViewHolder> {

    public static ArrayList<String> eidList = new ArrayList<>();
    public static ArrayList<String> empList = new ArrayList<>();
    Context context;
    private ArrayList<String> empId;
    private ArrayList<String> empName;
    private ArrayList<String> empPhone;

    public EmloyeeListAdapter(Context ctx, ArrayList<String> empId, ArrayList<String> empName, ArrayList<String> empPhone) {
        this.context = ctx;
        this.empId = empId;
        this.empName = empName;
        this.empPhone = empPhone;
        eidList.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.employee_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.chbEmpName.setText(empName.get(position));

        holder.chbEmpName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                try {

                    if (b) {
                        eidList.add(empId.get(position));
                        empList.add(empName.get(position));
                    } else {
                        eidList.remove(position);
                        empList.remove(position);
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });


        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phn = empPhone.get(position);

                if (phn != null && !phn.isEmpty() && !phn.equalsIgnoreCase("null")) {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phn));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(intent);
                    }

                } else {
                    Toast.makeText(context, "Number not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return empId.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox chbEmpName;
        ImageView imgCall;

        public MyViewHolder(View itemView) {
            super(itemView);
            chbEmpName = itemView.findViewById(R.id.chbEmpName);
            imgCall = itemView.findViewById(R.id.imgCall);
        }
    }

}
