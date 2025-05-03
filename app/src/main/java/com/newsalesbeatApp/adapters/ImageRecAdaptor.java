package com.newsalesbeatApp.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;

import java.util.ArrayList;

public class ImageRecAdaptor extends RecyclerView.Adapter<ImageRecAdaptor.ViewHolder>{

    private ArrayList<Uri> list;

    public ImageRecAdaptor(ArrayList<Uri> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ImageRecAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(R.layout.custom_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecAdaptor.ViewHolder holder, int position) {
        holder.imageView.setImageURI((Uri) list.get(position));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
