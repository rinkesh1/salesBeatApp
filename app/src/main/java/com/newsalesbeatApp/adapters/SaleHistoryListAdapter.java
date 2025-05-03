package com.newsalesbeatApp.adapters;

/*
 * Created by Dhirendra Thakur on 29-03-2018.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.RoundedImageView;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class SaleHistoryListAdapter extends RecyclerView.Adapter<SaleHistoryListAdapter.MyViewHolder> {

    Context ctx;
    private ArrayList<Item> listEmp;

    public SaleHistoryListAdapter(Context context, ArrayList<Item> listEmp) {
        this.ctx = context;
        this.listEmp = listEmp;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.sale_history_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tvEmpName.setText(listEmp.get(position).getItem2());
        holder.tvTotalSale.setText(listEmp.get(position).getItem6() + "Kg");

        String url = listEmp.get(position).getItem3();
        if (url == null || url.equalsIgnoreCase("null") || url.isEmpty())
            url = SbAppConstants.PLACEHOLDER_URL;
        else
            url = SbAppConstants.IMAGE_PREFIX + url;

        /*Glide.with(ctx)
                .asBitmap() // Explicitly request a Bitmap
                .load(url) // Load the image from the URL
                .into(new CustomTarget<Bitmap>(300, 300) { // Use CustomTarget and specify size (300x300)
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        holder.empPic.setImageBitmap(resource); // Set the bitmap to the ImageView
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup or placeholder if needed
                        holder.empPic.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        // Handle failure case by setting a default or error drawable
                        holder.empPic.setImageDrawable(ctx.getResources().getDrawable(R.drawable.placeholder3));
                    }
                });*/


        /*Glide.with(ctx)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(300, 300) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        holder.empPic.setImageBitmap(resource);
                    }
                });
*/

        RequestOptions requestOptions = new RequestOptions()
                .override(300, 300)  // Resize the image to 300x300
                .diskCacheStrategy(DiskCacheStrategy.ALL);  // Optional: Cache the image

        Glide.with(ctx)
                .load(url)  // Load image from URL
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failure (e.g., display an error image)
//                        holder.empPic.setImageDrawable(errorDrawable);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Set the resource (image) to the ImageView when ready
                        holder.empPic.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(holder.empPic);


        /*Glide.with(ctx)
                .load(url)  // Load image from URL
                .override(300, 300)  // Resize the image to 300x300
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Optional: Cache the image
                .into(new CustomTarget<Bitmap>(300, 300) {  // Use CustomTarget instead of SimpleTarget
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the Bitmap resource to ImageView when the image is ready
                        holder.empPic.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Optional: Handle clearing the image (e.g., when the view is no longer visible)
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        // Optional: Handle load failure (e.g., display an error image)
                        holder.empPic.setImageDrawable(errorDrawable);
                    }
                });*/

    }

    @Override
    public int getItemCount() {
        return listEmp.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView empPic;
        TextView tvEmpName,/*tvEmpDes,*/
                tvTotalSale;

        public MyViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(ctx,itemView);
            empPic = itemView.findViewById(R.id.empPic);
            tvEmpName = (TextView) itemView.findViewById(R.id.tvEmpNameH);
            //tvEmpDes = (TextView) itemView.findViewById(R.id.tvEmpDes);
            tvTotalSale = (TextView) itemView.findViewById(R.id.tvTotalSale);
        }
    }
}
