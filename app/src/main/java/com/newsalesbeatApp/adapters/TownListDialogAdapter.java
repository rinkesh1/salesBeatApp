package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.pojo.TownItem;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;

/**
 * Created by abc on 11/12/18.
 */

public class TownListDialogAdapter extends RecyclerView.Adapter<TownListDialogAdapter.ViewHolder> {

    private final int TIME_OUT_VALUE = 50000;
    private final OnItemClickListener listener;
    UtilityClass utilityClassObj;
    private String TAG = "TownListAdapter";
    private Context context;
    private SalesBeatDb salesBeatDb;
    private ArrayList<TownItem> townList;
    private SharedPreferences tempPref, myPref;
    private RequestQueue requestQueue;
    private ServerCall serverCall;

    public TownListDialogAdapter(Context ctx, ArrayList<TownItem> tList, OnItemClickListener listener) {
        this.context = ctx;
        this.townList = tList;
        tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        myPref = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(context);

        //salesBeatDb = new SalesBeatDb(ctx);
        salesBeatDb = SalesBeatDb.getHelper(ctx);
        serverCall = new ServerCall(ctx);
        utilityClassObj = new UtilityClass(ctx);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.town_list_row_d, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.bind(townList.get(position), listener);
        holder.tvTownName.setText(townList.get(position).getTownName());
    }

    @Override
    public int getItemCount() {
        return townList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(TownItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTownName;

        public ViewHolder(View itemView) {
            super(itemView);
            //TextViews
            tvTownName = itemView.findViewById(R.id.tvTownName);

        }

        public void bind(final TownItem item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

