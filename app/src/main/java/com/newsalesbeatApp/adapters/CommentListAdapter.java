package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.ChatItem;

import java.util.ArrayList;

/*
 * Created by abc on 1/16/19.
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewholder> {

    private final String[] months = {"January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    ArrayList<ChatItem> chatUpdated;
    Context context;

    public CommentListAdapter(Context context, ArrayList<ChatItem> chatUpdated) {
        this.context = context;
        this.chatUpdated = chatUpdated;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comment_row, parent, false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {

        String[] date = chatUpdated.get(position).getDate().split(" ");
        String day = date[0];
        String mn = date[1];
        String yr = date[2];

        int month = 0;

        for (int pos = 0; pos < months.length; pos++) {

            if (months[pos].contains(mn)) {
                month = pos + 1;
            }
        }

        holder.tvEmpName.setText(chatUpdated.get(position).getEmpName());
        if (month < 10)
            holder.tvCommentDate.setText(day + "-0" + month + "-" + yr + " " + chatUpdated.get(position).getTimeStamp());
        else
            holder.tvCommentDate.setText(day + "-" + month + "-" + yr + " " + chatUpdated.get(position).getTimeStamp());

        holder.tvCommnet.setText(chatUpdated.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return chatUpdated.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {
        TextView tvEmpName, tvCommentDate, tvCommnet;

        public MyViewholder(View itemView) {
            super(itemView);
            tvEmpName = itemView.findViewById(R.id.tvEmpName);
            tvCommentDate = itemView.findViewById(R.id.tvCmntDate);
            tvCommnet = itemView.findViewById(R.id.tvEmpCmnt);
        }
    }
}
