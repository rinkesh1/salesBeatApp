package com.newsalesbeatApp.adapters;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.OrderDetails;
import com.newsalesbeatApp.pojo.RetailerVisitItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
 * Created by Dhirendra Thakur on 26-12-2017.
 */

public class RetailerVisitHistoryAdapter extends RecyclerView.Adapter<RetailerVisitHistoryAdapter.ViewHolder> {

    String TAG = "RetailerVisitHistoryAdapter";
    private Context context;
    private ArrayList<RetailerVisitItem> retailerItem;
    private String from = "";
    private String name = "";

    public RetailerVisitHistoryAdapter(Context ctx, ArrayList<RetailerVisitItem> retailerItem, String from, String name) {
        this.context = ctx;
        this.retailerItem = retailerItem;
        this.from = from;
        this.name = name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.retailer_visit_history_row, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tvOCounter.setText(String.valueOf(position + 1));

        String[] date = retailerItem.get(position).getCheckIn().split(" ");
        String strDate = date[0];

        SimpleDateFormat month_date = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String month_name = "";
        try {
            Date dater = sdf.parse(strDate);
            month_name = month_date.format(dater);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        String chekInTime = date[1];
//        String[] date2 = retailerItem.get(position).getCheckOut().split(" ");
//        String checkOut = date2[1];

        //@Umesh 13-March-2022
        String chekInTime = date[0];
        String[] date2 = retailerItem.get(position).getCheckOut().split(" ");
        String checkOut = date2[0];

        if (holder.tvOrderTakenDate.getText().toString().isEmpty()
                || !holder.tvOrderTakenDate.getText().toString().contains(strDate)) {

            holder.tvOrderTakenDate.setText(month_name);
            holder.tvOrderTakenDate.setVisibility(View.VISIBLE);

        } else {
            holder.tvOrderTakenDate.setVisibility(View.INVISIBLE);
        }

        if (chekInTime.equalsIgnoreCase("null")
                || chekInTime.isEmpty()) {
            holder.tvCheckIn.setText("NA");
        } else {

            holder.tvCheckIn.setText(get12HourFormat(chekInTime));
        }

        if (checkOut.equalsIgnoreCase("null")
                || checkOut.isEmpty()) {
            holder.tvCheckOut.setText("NA");
        } else {

            holder.tvCheckOut.setText(get12HourFormat(checkOut));
        }

        Log.e(TAG, "====>>>" + retailerItem.get(position).getComment());

        if (retailerItem.get(position).getComment() == null
                || retailerItem.get(position).getComment().equalsIgnoreCase("null")
                || retailerItem.get(position).getComment().isEmpty()
                || retailerItem.get(position).getComment().equalsIgnoreCase("----------")) {

            holder.tvComment.setText("NA");
            holder.llCommentHis.setVisibility(View.INVISIBLE);

        } else {

            String cmnt = retailerItem.get(position).getComment().replaceAll("\\]", "");
            cmnt = cmnt.replaceAll("\\[", "");
            holder.tvComment.setText(cmnt);
            if (retailerItem.get(position).getOrderList().size() == 0)
                holder.btnViewOrderDetails.setVisibility(View.GONE);
            else
                holder.btnViewOrderDetails.setVisibility(View.VISIBLE);

        }

        if (retailerItem.get(position).getEmpName().equalsIgnoreCase("null")
                || retailerItem.get(position).getEmpName().isEmpty()) {

            holder.tvEmpName.setText("NA");

        } else {

            holder.tvEmpName.setText(retailerItem.get(position).getEmpName());

        }

        if (retailerItem.get(position).getEmpPhone().equalsIgnoreCase("null")
                || retailerItem.get(position).getEmpPhone().isEmpty()) {

            holder.tvEmpPhone.setText("NA");

        } else {

            holder.tvEmpPhone.setText(retailerItem.get(position).getEmpPhone());

        }

        if (retailerItem.get(position).getEmpEmail().equalsIgnoreCase("null")
                || retailerItem.get(position).getEmpEmail().isEmpty()) {

            holder.tvEmpMail.setText("NA");

        } else {

            holder.tvEmpMail.setText(retailerItem.get(position).getEmpEmail());

        }


        if (from.equalsIgnoreCase("distributor_s"))
            holder.btnViewOrderDetails.setText("View Stock Details");

        if (from.equalsIgnoreCase("dis_closing"))
            holder.btnViewOrderDetails.setText("View Closing Details");


        holder.btnViewOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (retailerItem.get(position).getOrderList() != null && retailerItem.get(position).getOrderList().size() > 0)
                {

                    Intent intent = new Intent(context, OrderDetails.class);
                    intent.putExtra("list", retailerItem.get(position).getOrderList());
                    intent.putExtra("from", from);
                    intent.putExtra("name", name);
                    intent.putExtra("date", retailerItem.get(position).getCheckIn());
                    intent.putExtra("empName", retailerItem.get(position).getEmpName());
                    intent.putExtra("empMob", retailerItem.get(position).getEmpPhone());
                    context.startActivity(intent);

                } else {
                    Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.imgCallH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phn = retailerItem.get(position).getEmpPhone();

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

    private String get12HourFormat(String chekInTime) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(chekInTime);
            return new SimpleDateFormat("K:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return retailerItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCheckIn, tvCheckOut, tvComment, tvEmpName, tvEmpPhone, tvEmpMail,/*tvEmpZone,*/
                tvOrderTakenDate, tvOCounter;
        ImageView imgCallH;

        Button btnViewOrderDetails;

        LinearLayout llCommentHis;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCheckIn = itemView.findViewById(R.id.tvCheckIn);
            tvCheckOut = itemView.findViewById(R.id.tvCheckOut);
            tvComment = itemView.findViewById(R.id.tvComments);
            tvEmpName = itemView.findViewById(R.id.tvEmpName);
            tvEmpPhone = itemView.findViewById(R.id.tvEmpPhone);
            tvEmpMail = itemView.findViewById(R.id.tvEmpEmail);
            tvOCounter = itemView.findViewById(R.id.tvOCounter);
            //tvEmpZone = (TextView) itemView.findViewById(R.id.tvZone);
            tvOrderTakenDate = itemView.findViewById(R.id.tvOrderTakenDate);
            imgCallH = itemView.findViewById(R.id.imgCallH);
            btnViewOrderDetails = itemView.findViewById(R.id.btnViewOrderDetails);
            llCommentHis = itemView.findViewById(R.id.llCommentHis);

        }
    }
}
