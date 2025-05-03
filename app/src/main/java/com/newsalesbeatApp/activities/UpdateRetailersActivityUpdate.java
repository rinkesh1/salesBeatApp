package com.newsalesbeatApp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.RoundedImageView;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

public class UpdateRetailersActivityUpdate extends AppCompatActivity {

    private static final String GSTIN_MASK = "99 AAAAA9999A 9A9";
    private static final String PAN_MASK = "AAAAA9999A";
    String TAG = "UpdateRetailersActivity";
    ImageView retUpdateClose;

    RoundedImageView retailerImage;
    ProgressBar proressBarImageview;
    // ImageView    retailerImage;
    ImageView shopImage1, shopImage2, shopImage3, shopImage4, shopImage5, shopImage6, chaneImage;
    Bitmap retailerImageBitmap = null, shopBitmap1 = null, shopBitmap2 = null, shopBitmap3 = null,
            shopBitmap4 = null, shopBitmap5 = null, shopBitmap6 = null;
    UtilityClass utilityClass;
    GPSLocation locationProvider;
    SalesBeatDb salesBeatDb;
    RelativeLayout updateRetailerRL;
    private SharedPreferences prefSFA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_retailers_update);
    }
}