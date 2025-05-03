package com.newsalesbeatApp.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.utilityclass.BlurBuilder;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import java.io.InputStream;

/*
 * Created by MTC on 29-12-2015.
 */
public class ShopImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private String[] room_img_url;
    private LayoutInflater mLayoutInflater;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public ShopImagePagerAdapter(Context context, String[] r_img) {
        mContext = context;
        this.room_img_url = r_img;

        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(context);

        if (mImageLoader == null)
        {
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return room_img_url.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.shop_image_row, container, false);
        NetworkImageView imageView1 = (NetworkImageView) itemView.findViewById(R.id.imgShopPager);
        if (position == 0)
        {
            imageView1.setImageUrl(SbAppConstants.IMAGE_PREFIX_RETAILER_ORIGINAL + room_img_url[position], mImageLoader);
            new LoadProfileImage2(imageView1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    SbAppConstants.IMAGE_PREFIX_RETAILER_ORIGINAL + room_img_url[position]);
        } else
        {
            imageView1.setImageUrl(SbAppConstants.IMAGE_PREFIX_SHOPIMAGES_ORIGINAL + room_img_url[position], mImageLoader);
            new LoadProfileImage2(imageView1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    SbAppConstants.IMAGE_PREFIX_SHOPIMAGES_ORIGINAL + room_img_url[position]);
        }

        //@Umesh
//        if(room_img_url[position].length()>0 && room_img_url[position]!="default.jpeg")
//        {
//            byte[] imageBytes = Base64.decode(room_img_url[position], Base64.DEFAULT);
//            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//            imageView1.setDefaultImageBitmap(decodedImage);
//            new LoadProfileImage2(imageView1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,room_img_url[position]);
//        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private class LoadProfileImage2 extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        private LoadProfileImage2(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

                //@Umesh
//                if(urldisplay.length()>0 && urldisplay!="default.jpeg")
//                {
//                    byte[] imageBytes = Base64.decode(urldisplay, Base64.DEFAULT);
//                    mIcon11 = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(Bitmap result) {
            try {
                Bitmap blurredBitmap = BlurBuilder.blur(mContext, result);
                Drawable d = new BitmapDrawable(mContext.getResources(), blurredBitmap);
                bmImage.setBackground(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
