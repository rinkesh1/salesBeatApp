package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.RoundedImageView;

import java.util.ArrayList;

/*
 * Created by MTC on 28-10-2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> dateList;
    private ArrayList<String> messageList;

    public MessageListAdapter(Context ctx, ArrayList<String> datelist, ArrayList<String> messagelist) {
        this.context = ctx;
        this.dateList = datelist;
        this.messageList = messagelist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvDate.setText(dateList.get(position));

        if (position != 0) {
            if (dateList.get((position - 1)).equalsIgnoreCase(dateList.get(position))) {
                holder.tvDate.setVisibility(View.GONE);
            }
        }

        holder.tvMessage.setText(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvMessage;
        RoundedImageView imgAdmin;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            imgAdmin = (RoundedImageView) itemView.findViewById(R.id.imgAdmin);
        }
    }
}
