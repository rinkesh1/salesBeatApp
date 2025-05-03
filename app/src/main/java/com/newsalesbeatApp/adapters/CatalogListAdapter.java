package com.newsalesbeatApp.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.FullScreenImageActivity;
import com.newsalesbeatApp.utilityclass.BlurBuilder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/*
 * Created by MTC on 28-09-2017.
 */

public class CatalogListAdapter extends RecyclerView.Adapter<CatalogListAdapter.MyViewHolder> {

    private Context mContext;
    private String[] filepath;
    private ArrayList<String> desc;

    public CatalogListAdapter(Context context, String[] filePathStrings, ArrayList<String> fileNameStrings) {
        this.mContext = context;
        filepath = filePathStrings;
        desc = fileNameStrings;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.catalog_list_row2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.catalogThumbImage.setImageURI(Uri.parse(filepath[position]));
        if (desc != null && desc.size() > 0)
            holder.catalogDescription.setText(desc.get(position));

        new LoadProfileImage2(holder.catalogBackImage).execute(filepath[position]);

        holder.catalogThumbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FullScreenImageActivity.class);
                intent.putExtra("Position", position);
                intent.putExtra("image_list", filepath);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filepath.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView catalogDescription;
        ImageView catalogThumbImage, catalogBackImage;


        public MyViewHolder(View itemView) {
            super(itemView);
            catalogDescription = itemView.findViewById(R.id.catalogDescription);
            catalogThumbImage = itemView.findViewById(R.id.catalogThumbImage);
            catalogBackImage = itemView.findViewById(R.id.catalogBackImage);
        }
    }

    private class LoadProfileImage2 extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        private LoadProfileImage2(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            Bitmap getBitmap = null;
            try {
                InputStream image_stream;
                try {
                    image_stream = mContext.getContentResolver().openInputStream(Uri.parse("file://" + urls[0]));
                    getBitmap = BitmapFactory.decodeStream(image_stream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getBitmap;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(Bitmap result) {
            try {

                Bitmap blurredBitmap = BlurBuilder.blur(mContext, result);
                bmImage.setImageBitmap(blurredBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
