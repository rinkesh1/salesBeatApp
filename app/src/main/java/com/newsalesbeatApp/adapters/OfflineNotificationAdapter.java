package com.newsalesbeatApp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.OfflineNotificationModel;

import java.util.ArrayList;
import java.util.List;

public class OfflineNotificationAdapter extends RecyclerView.Adapter<OfflineNotificationAdapter.MyViewHolder> {

    private List<OfflineNotificationModel> moviesList;

    public OfflineNotificationAdapter(List<OfflineNotificationModel> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notif_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OfflineNotificationModel movie = moviesList.get(position);
        holder.genre.setText(movie.getStrPrayer());
        holder.title.setText(movie.getStrPrayerHeader());
        holder.year.setText(movie.getStrNumber());
        if (moviesList.get(position).getReadStatus().equals("read")) {
            holder.notif.setCardBackgroundColor(Color.parseColor("#191919"));
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void updateList(ArrayList<OfflineNotificationModel> list) {
        moviesList = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        private CardView notif;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
            notif = view.findViewById(R.id.notif_card);
        }
    }

}