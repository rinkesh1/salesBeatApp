package com.newsalesbeatApp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.vision.Frame;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.ImageRecAdaptor;
import com.newsalesbeatApp.customview.RoundedImageView;
import com.newsalesbeatApp.netwotkcall.VolleyMultipartRequest;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.newsalesbeatApp.utilityclass.SbAppConstants.IMAGE_PREFIX_RETAILER_ORIGINAL;

/**
 * Created by Dhirendra Thakur on 18-11-2017.
 */

public class UpdateRetailersActivity extends AppCompatActivity {

    private static final String GSTIN_MASK = "99 AAAAA9999A 9A9";
    private static final String PAN_MASK = "AAAAA9999A";
    String TAG = "UpdateRetailersActivity_update_1";
    ImageView retUpdateClose;

    RoundedImageView retailerImage;
    ProgressBar proressBarImageview;
    // ImageView    retailerImage;
    ImageView shopImage1, shopImage2, shopImage3, shopImage4, shopImage5, shopImage6, chaneImage,shopImage01;
    Bitmap retailerImageBitmap = null;
    Bitmap shopBitmap1 = null;
    Bitmap shopBitmap2 = null;
    Bitmap shopBitmap3 = null;
    Bitmap shopBitmap4 = null;
    Bitmap shopBitmap5 = null;
    Bitmap shopBitmap6 = null;
    UtilityClass utilityClass;
    GPSLocation locationProvider;
    SalesBeatDb salesBeatDb;
    RelativeLayout updateRetailerRL;
    private SharedPreferences prefSFA;
    LinearLayout llProfile;

    ArrayList<Uri> imgList = new ArrayList<>();
    ImageRecAdaptor adaptor;
    RecyclerView recyclerImage;

    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.retailer_info_dialog);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);

        llProfile = findViewById(R.id.llProfile);
        llProfile.setBackgroundResource(R.drawable.bg_polygon);

        updateRetailerRL = findViewById(R.id.updateRetailerRL);
        retailerImage = findViewById(R.id.retailerImage);
        ImageView imgEditGrade = findViewById(R.id.imgEditGrade);
        ImageView imgEditTarget = findViewById(R.id.imgEditTarget);
        ImageView imgEditName = findViewById(R.id.imgEditName);
        ImageView imgEditCity = findViewById(R.id.imgEditCity);
        ImageView imgEditState = findViewById(R.id.imgEditState);
        ImageView imgEditEmail = findViewById(R.id.imgEditMailId);
        ImageView imgEditWhatsAppNo = findViewById(R.id.imgEditWhatsAppNo);
        ImageView imgEditMobileNo = findViewById(R.id.imgEditMobileNo);
        ImageView imgEditGstin = findViewById(R.id.imgEditGSTIN);
        ImageView imgEditFssaino = findViewById(R.id.imgEditFSSAINo);
        ImageView imgEditPinNo = findViewById(R.id.imgEditPinNo);
        ImageView chaneImage = findViewById(R.id.chaneImage);
        ImageView shopImage01 = findViewById(R.id.shopImage01);
//        ImageView imgEditLongtitudeR = (ImageView) findViewById(R.id.imgEditLongtitudeR);
        recyclerImage = findViewById(R.id.recyclerImage);

        adaptor = new ImageRecAdaptor(imgList);
        recyclerImage.setLayoutManager(new GridLayoutManager(UpdateRetailersActivity.this,4));
        recyclerImage.setAdapter(adaptor);

        shopImage1 = findViewById(R.id.shopImage1);
        ImageView addShopImage1 = findViewById(R.id.addShopImage1);
        shopImage2 = findViewById(R.id.shopImage2);
        ImageView addShopImage2 = findViewById(R.id.addShopImage2);
        shopImage3 = findViewById(R.id.shopImage3);
        ImageView addShopImage3 = findViewById(R.id.addShopImage3);
        shopImage4 = findViewById(R.id.shopImage4);
        ImageView addShopImage4 = findViewById(R.id.addShopImage4);
        shopImage5 = findViewById(R.id.shopImage5);
        ImageView addShopImage5 = findViewById(R.id.addShopImage5);
        shopImage6 = findViewById(R.id.shopImage6);
        proressBarImageview = findViewById(R.id.proressBarImageview);
        ImageView addShopImage6 = findViewById(R.id.addShopImage6);

        final TextView tvGrade = findViewById(R.id.tvGrade);
        final TextView tvTarget = findViewById(R.id.tvTarget);
        TextView tvLocality = findViewById(R.id.tvLocality);
        final TextView tvOwnerName = findViewById(R.id.tvOwnerName);
        final TextView tvCity = findViewById(R.id.tvCity);
        final TextView tvState = findViewById(R.id.tvState);
        final TextView tvEmail = findViewById(R.id.tvEmail);
        final TextView tvWhatsAppNo = findViewById(R.id.tvWhatsAppNo);
        final TextView tvMobileNo = findViewById(R.id.tvMobileNo);
        final TextView tvGstin = findViewById(R.id.tvGstin);
        final TextView tvFssaino = findViewById(R.id.tvFssaino);
        final TextView tvPinNo = findViewById(R.id.tvPinNo);
        final TextView tvLatitudeR = findViewById(R.id.tvLatitudeR);
        final TextView tvLongtitudeR = findViewById(R.id.tvLongtitudeR);

        final EditText edtGrade = findViewById(R.id.edtGrade);
        final EditText edtTarget = findViewById(R.id.edtTarget);
        final EditText edtOwnerName = findViewById(R.id.edtOwnerName);
        final EditText edtCity = findViewById(R.id.edtCity);
        final EditText edtState = findViewById(R.id.edtState);
        final EditText edtEmail = findViewById(R.id.edtEmail);
        final EditText edtMobileNo = findViewById(R.id.edtMobileNo);
        final EditText edtWhatsAppNo = findViewById(R.id.edtWhatsAppNo);
        final EditText edtGstin = findViewById(R.id.edtGstin);
        final EditText edtFssaino = findViewById(R.id.edtFssaino);
        final EditText edtPinNo = findViewById(R.id.edtPinNo);
        final EditText edtLatitudeR = findViewById(R.id.edtLatitudeR);
        final EditText edtLongtitudeR = findViewById(R.id.edtLongtitudeR);
        retUpdateClose = findViewById(R.id.retUpdateClose);

        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnUpgradeToPreferred = findViewById(R.id.btnUpgradeToPreferred);

        /*Intent intent = getIntent();
        String txtLat = intent.getStringExtra("txtLat");
        String txtLong = intent.getStringExtra("txtLong");

        Log.d(TAG, "Check onLoad Lat :"+txtLat);
        Log.d(TAG, "Check onLoad Long :"+txtLong);*/
        final RetailerItem retailerList = (RetailerItem) getIntent().getSerializableExtra("retailer");
        Log.d(TAG, "onCreate Gson: "+new Gson().toJson(retailerList));
        /*Glide.with(this)
                .load(IMAGE_PREFIX_RETAILER_ORIGINAL+retailerList.getRetailer_image())
                .asBitmap()
                .into(retailerImage);*/

        salesBeatDb = SalesBeatDb.getHelper(this);
        Log.d(TAG, "Retailer Image Path: "+IMAGE_PREFIX_RETAILER_ORIGINAL + retailerList.getRetailer_image().split(",")[0]);

        proressBarImageview.setVisibility(View.VISIBLE);
        /*Glide.with(this)
                .load(IMAGE_PREFIX_RETAILER_ORIGINAL + retailerList.getRetailer_image().split(",")[0])
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                        proressBarImageview.setVisibility(View.GONE);
                        retailerImage.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        proressBarImageview.setVisibility(View.GONE);
                        retailerImage.setImageDrawable(getResources().getDrawable(R.drawable.men_placeholder));

                    }
                });*/
        Glide.with(this)
                .asBitmap()
                .load(IMAGE_PREFIX_RETAILER_ORIGINAL + retailerList.getRetailer_image().split(",")[0])
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        proressBarImageview.setVisibility(View.GONE);
                        retailerImage.setImageDrawable(getResources().getDrawable(R.drawable.men_placeholder));
                        return false; // Return false to allow Glide to handle the error placeholder.
                    }


                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        proressBarImageview.setVisibility(View.GONE);
                        retailerImage.setImageBitmap(resource);
                        return true; // Return true if you've handled setting the image.
                    }
                })
                .into(retailerImage);
        Log.d("SaareImage", retailerList.getRetailer_image() + "");
        if (retailerList.getRetailer_grade() == null || retailerList.getRetailer_grade().equalsIgnoreCase("null")
                || retailerList.getRetailer_grade().isEmpty()) {
            tvGrade.setText("NA");
        } else {
            tvGrade.setText(retailerList.getRetailer_grade());
        }

        if (retailerList.getRetailerLocality() == null
                || retailerList.getRetailerLocality().equalsIgnoreCase("null")
                || retailerList.getRetailerLocality().isEmpty()) {
            tvLocality.setText("NA");
        } else {
            tvLocality.setText(retailerList.getRetailerLocality());
        }

        Log.d(TAG, "onCreate getRetailer_owner_name: "+retailerList.getRetailer_owner_name());
        if (retailerList.getRetailer_owner_name() == null
                || retailerList.getRetailer_owner_name().equalsIgnoreCase("null")
                || retailerList.getRetailer_owner_name().isEmpty()) {
            tvOwnerName.setText("NA");
        } else {
            tvOwnerName.setText(retailerList.getRetailer_owner_name());
        }

        if (retailerList.getRetailer_city() == null
                || retailerList.getRetailer_city().equalsIgnoreCase("null")
                || retailerList.getRetailer_city().isEmpty()) {
            tvCity.setText("NA");
        } else {
            tvCity.setText(retailerList.getRetailer_city());
        }

        if (retailerList.getRetailer_state() == null
                || retailerList.getRetailer_state().equalsIgnoreCase("null")
                || retailerList.getRetailer_state().isEmpty()) {
            tvState.setText("NA");
        } else {
            tvState.setText(retailerList.getRetailer_state());
        }

        if (retailerList.getReatialerTarget() == null
                || retailerList.getReatialerTarget().equalsIgnoreCase("null")
                || retailerList.getReatialerTarget().isEmpty()) {
            tvTarget.setText("NA");
        } else {
            tvTarget.setText(retailerList.getReatialerTarget());
        }

        if (retailerList.getRetailer_email() == null
                || retailerList.getRetailer_email().equalsIgnoreCase("null")
                || retailerList.getRetailer_email().isEmpty()) {
            tvEmail.setText("NA");
        } else {
            tvEmail.setText(retailerList.getRetailer_email());
        }

        if (retailerList.getRetailerPhone() == null
                || retailerList.getRetailerPhone().equalsIgnoreCase("null")
                || retailerList.getRetailerPhone().isEmpty()) {
            tvMobileNo.setText("NA");
        } else {
            tvMobileNo.setText(retailerList.getRetailerPhone());
        }

        if (retailerList.getReatilerWhatsAppNo() == null
                || retailerList.getReatilerWhatsAppNo().equalsIgnoreCase("null")
                || retailerList.getReatilerWhatsAppNo().isEmpty()) {
            tvWhatsAppNo.setText("NA");
        } else {
            tvWhatsAppNo.setText(retailerList.getReatilerWhatsAppNo());
        }

        if (retailerList.getRetailer_gstin() == null
                || retailerList.getRetailer_gstin().equalsIgnoreCase("null")
                || retailerList.getRetailer_gstin().isEmpty()) {
            tvGstin.setText("NA");
        } else {
            tvGstin.setText(retailerList.getRetailer_gstin());
        }

        if (retailerList.getRetailer_fssai() == null
                || retailerList.getRetailer_fssai().equalsIgnoreCase("null")
                || retailerList.getRetailer_fssai().isEmpty()) {
            tvFssaino.setText("NA");
        } else {
            tvFssaino.setText(retailerList.getRetailer_fssai());
        }

        if (retailerList.getRetailer_pin() == null
                || retailerList.getRetailer_pin().equalsIgnoreCase("null")
                || retailerList.getRetailer_pin().isEmpty()) {
            tvPinNo.setText("NA");
        } else {
            tvPinNo.setText(retailerList.getRetailer_pin());
        }

        if (retailerList.getLatitude() == null
                || String.valueOf(retailerList.getLatitude()).equalsIgnoreCase("null")
                || String.valueOf(retailerList.getLatitude()).isEmpty()) {
            tvLatitudeR.setText("NA");
        } else {
            tvLatitudeR.setText(String.valueOf(retailerList.getLatitude()));
        }

        if (retailerList.getLongtitude() == null
                || String.valueOf(retailerList.getLongtitude()).equalsIgnoreCase("null")
                || String.valueOf(retailerList.getLongtitude()).isEmpty()) {
            tvLongtitudeR.setText("NA");
        } else {
            tvLongtitudeR.setText(String.valueOf(retailerList.getLongtitude()));
        }

//        MaskFormatter gstinMaskFormatter = new MaskFormatter(GSTIN_MASK, edtGstin);
//        edtGstin.addTextChangedListener(gstinMaskFormatter);

        edtGstin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String text = edtGstin.getText().toString();
                // Apply your custom masking logic here
                String masked = applyGSTINMask(text);
                edtGstin.setText(masked);
                edtGstin.setSelection(masked.length()); // Move the cursor to the end
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        utilityClass = new UtilityClass(UpdateRetailersActivity.this);
        locationProvider = new GPSLocation(this);

        //check gps status if on/off
        locationProvider.checkGpsStatus();

        retUpdateClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UpdateRetailersActivity.this, RetailerActivity.class);
                startActivity(intent);
                UpdateRetailersActivity.this.finish();
            }
        });

        ((TextView) findViewById(R.id.txtUpdateLoc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvLatitudeR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        Log.d("TAG", "onClick Lat");
                        getIntent().getSerializableExtra("retailer");
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        i.putExtra("retailerId",retailerList.getRetailerId());
                        startActivity(i);
//                tvLatitudeR.setText(String.valueOf(locationProvider.getLatitude()));
//                tvLongtitudeR.setText(String.valueOf(locationProvider.getLongitude()));
                        return true;
                    }
                }
                return false;
            }
        });

        tvLongtitudeR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        Log.d("TAG", "onClick Lat");
                        getIntent().getSerializableExtra("retailer");
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        i.putExtra("retailerId",retailerList.getRetailerId());
                        startActivity(i);
//                tvLatitudeR.setText(String.valueOf(locationProvider.getLatitude()));
//                tvLongtitudeR.setText(String.valueOf(locationProvider.getLongitude()));
                        return true;
                    }
                }
                return false;
            }
        });

        ((ImageView) findViewById(R.id.imgRefressLat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick Lat");
                getIntent().getSerializableExtra("retailer");
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                i.putExtra("retailerId",retailerList.getRetailerId());
                startActivity(i);
//                tvLatitudeR.setText(String.valueOf(locationProvider.getLatitude()));
//                tvLongtitudeR.setText(String.valueOf(locationProvider.getLongitude()));
            }
        });

        ((ImageView) findViewById(R.id.imgRefressLong)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick long");
//                tvLongtitudeR.setText(String.valueOf(locationProvider.getLongitude()));
//                tvLatitudeR.setText(String.valueOf(locationProvider.getLatitude()));
            }
        });

        imgEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvOwnerName.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvOwnerName.setVisibility(View.GONE);
                tvOwnerName.setText(str);
                tvOwnerName.setVisibility(View.VISIBLE);
                tvOwnerName.requestFocus();
            }
        });

        imgEditCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvCity.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvCity.setVisibility(View.GONE);
                edtCity.setText(str);
                edtCity.setVisibility(View.VISIBLE);
                edtCity.requestFocus();
            }
        });

        tvGrade.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        tvGrade.setFocusable(true);
                        tvGrade.setFocusableInTouchMode(true);
                        tvGrade.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        tvTarget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        tvTarget.setFocusable(true);
                        tvTarget.setFocusableInTouchMode(true);
                        tvTarget.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        tvEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        tvEmail.setFocusable(true);
                        tvEmail.setFocusableInTouchMode(true);
                        tvEmail.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        tvMobileNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        tvMobileNo.setFocusable(true);
                        tvMobileNo.setFocusableInTouchMode(true);
                        tvMobileNo.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        tvWhatsAppNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        Log.d(TAG, "onTouch grade");
                        tvWhatsAppNo.setFocusable(true);
                        tvWhatsAppNo.setFocusableInTouchMode(true);
                        tvWhatsAppNo.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        tvGstin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        Log.d(TAG, "onTouch grade");
                        tvGstin.setFocusable(true);
                        tvGstin.setFocusableInTouchMode(true);
                        tvGstin.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        tvFssaino.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        Log.d(TAG, "onTouch grade");
                        tvFssaino.setFocusable(true);
                        tvFssaino.setFocusableInTouchMode(true);
                        tvFssaino.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        tvPinNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Detect if the touch is within the bounds of the drawableRight (or drawableEnd for RTL)
                    if (event.getRawX() >= (tvLatitudeR.getRight() - tvLatitudeR.getCompoundDrawables()[2].getBounds().width())) {
                        Log.d(TAG, "onTouch grade");
                        tvPinNo.setFocusable(true);
                        tvPinNo.setFocusableInTouchMode(true);
                        tvPinNo.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    }
                }
                return false;
            }
        });

        imgEditState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvState.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvState.setVisibility(View.GONE);
                edtState.setText(str);
                edtState.setVisibility(View.VISIBLE);
                edtState.requestFocus();
            }
        });


        imgEditGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvGrade.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvGrade.setVisibility(View.GONE);
                edtGrade.setText(str);
                edtGrade.setVisibility(View.VISIBLE);
                edtGrade.requestFocus();
            }
        });


        imgEditTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvTarget.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvTarget.setVisibility(View.GONE);
                edtTarget.setText(str);
                edtTarget.setVisibility(View.VISIBLE);
                edtTarget.requestFocus();
            }
        });


        imgEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvEmail.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvEmail.setVisibility(View.GONE);
                edtEmail.setText(str);
                edtEmail.setVisibility(View.VISIBLE);
                edtEmail.requestFocus();
            }
        });


        imgEditMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvMobileNo.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvMobileNo.setVisibility(View.GONE);
                edtMobileNo.setText(str);
                edtMobileNo.setVisibility(View.VISIBLE);
                edtMobileNo.requestFocus();
            }
        });


        imgEditWhatsAppNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvWhatsAppNo.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvWhatsAppNo.setVisibility(View.GONE);
                edtWhatsAppNo.setText(str);
                edtWhatsAppNo.setVisibility(View.VISIBLE);
                edtWhatsAppNo.requestFocus();
            }
        });


        imgEditGstin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvGstin.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvGstin.setVisibility(View.GONE);
                edtGstin.setText(str);
                edtGstin.setVisibility(View.VISIBLE);
                edtGstin.requestFocus();
            }
        });


        imgEditFssaino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvFssaino.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvFssaino.setVisibility(View.GONE);
                edtFssaino.setText(str);
                edtFssaino.setVisibility(View.VISIBLE);
                edtFssaino.requestFocus();
            }
        });


        imgEditPinNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = tvPinNo.getText().toString();
                if (str.equalsIgnoreCase("NA"))
                    str = "";
                tvPinNo.setVisibility(View.GONE);
                edtPinNo.setText(str);
                edtPinNo.setVisibility(View.VISIBLE);
                edtPinNo.requestFocus();
            }
        });

        shopImage01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick shopImage01");
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 10);
            }
        });

        addShopImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });

        addShopImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 2);
            }
        });


        addShopImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 3);
            }
        });


        addShopImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 4);
            }
        });


        addShopImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 5);
            }
        });


        addShopImage6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 6);
            }
        });


        chaneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });

        btnUpgradeToPreferred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Update Information");
                Intent intent = new Intent(UpdateRetailersActivity.this, AddPreferredRetailerActivity.class);
                intent.putExtra("from", "UpdateRet");
                intent.putExtra("rid", retailerList.getRetailerId());
                startActivity(intent);
                finish();
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    new PingServer(internet -> {
                        /* do something with boolean response */
                        if (!internet) {
                            Toast.makeText(UpdateRetailersActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "update information");
                            String retailerId = retailerList.getRetailerId();
                            if (!UpdateRetailersActivity.this.isFinishing()) {

//                                String ret_email = edtEmail.getText().toString();
                                String ret_email = tvEmail.getText().toString();
                                /*Log.d("TAG", "Email Address: "+ret_email);
//                                if (ret_email.isEmpty())
//                                    ret_email = tvEmail.getText().toString();

//                                if (ret_email.isEmpty())
                                if(isValidEmail(ret_email)){
                                    ret_email = tvEmail.getText().toString();
                                }else {
                                    Toast.makeText(UpdateRetailersActivity_update_1.this, "Please Enter valid Email address", Toast.LENGTH_SHORT).show();
                                    return;
                                }*/

//                                String ret_mobile = edtMobileNo.getText().toString();
                                String ret_mobile = tvMobileNo.getText().toString();
                                Log.d("TAG", "Mobile no: "+ret_mobile);
                                /*if (ret_mobile.isEmpty())
                                    ret_mobile = tvMobileNo.getText().toString();*/

//                                if (ret_mobile.isEmpty())
                                if(isValidMobileNumber(ret_mobile)){
                                    ret_mobile = tvMobileNo.getText().toString();
                                }else {
                                    Toast.makeText(UpdateRetailersActivity.this, "Please Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                                    return;
                                }

//                                String ret_whatsAppNo = edtWhatsAppNo.getText().toString();
                                String ret_whatsAppNo = tvWhatsAppNo.getText().toString();
                                if (ret_whatsAppNo.isEmpty())
                                    ret_whatsAppNo = tvWhatsAppNo.getText().toString();

//                                String ret_gstno = edtGstin.getText().toString();
                                String ret_gstno = tvGstin.getText().toString();
                                if (ret_gstno.isEmpty())
                                    ret_gstno = tvGstin.getText().toString();

//                                String ret_fssaiNo = edtFssaino.getText().toString();
                                String ret_fssaiNo = tvFssaino.getText().toString();
                                if (ret_fssaiNo.isEmpty())
                                    ret_fssaiNo = tvFssaino.getText().toString();

//                                String ret_pincode = edtPinNo.getText().toString();
                                String ret_pincode = tvPinNo.getText().toString();
                                if (ret_pincode.isEmpty())
                                    ret_pincode = tvPinNo.getText().toString();

                                String ret_grade = tvGrade.getText().toString();
//                                String ret_grade = edtGrade.getText().toString();
                                if (ret_grade.isEmpty())
                                    ret_grade = tvGrade.getText().toString();


//                                String ret_target = edtTarget.getText().toString();
                                String ret_target = tvTarget.getText().toString();
                                if (ret_target.isEmpty())
                                    ret_target = tvTarget.getText().toString();

                                Log.e(TAG, " Mob No.: " + ret_mobile);

//                                updateRetailer(retailerId, /*edtOwnerName.getText().toString(), edtCity.getText().toString(),*/
//                                        /*edtState.getText().toString(),*/ ret_email, ret_mobile, ret_whatsAppNo, ret_gstno,
//                                        ret_fssaiNo, ret_pincode, /*edtLatitudeR.getText().toString(), edtLongtitudeR.getText().toString(),*/
//                                        ret_grade, ret_target);

                                //@Umesh
                                updateRetailer2(retailerId, /*edtOwnerName.getText().toString(), edtCity.getText().toString(),*/
                                        /*edtState.getText().toString(),*/ ret_email, ret_mobile, ret_whatsAppNo, ret_gstno,
                                        ret_fssaiNo, ret_pincode, /*edtLatitudeR.getText().toString(), edtLongtitudeR.getText().toString(),*/
                                        ret_grade, ret_target);
                            }


                        }

                    });


                } else {

                    Toast.makeText(UpdateRetailersActivity.this, "Can't update..not connected to internet", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private String applyGSTINMask(String input) {
        // Add your masking logic for GSTIN here
        // For example, you could mask it like: "XX-XX-XXXXXXX-X"
        return input.replaceAll("([A-Za-z]{2})(\\d{4})(\\d{4})([A-Za-z]{1})(\\d{1})", "$1-$2-$3-$4-$5");
    }

    public static boolean isValidEmail(String email) {
//        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static boolean isValidMobileNumber(String mobileNumber) {
        Pattern pattern = Pattern.compile("^[6-9]\\d{9}$");
        return pattern.matcher(mobileNumber).matches();
    }

    public void onBackPressed() {
        Intent intent = new Intent(UpdateRetailersActivity.this, RetailerActivity.class);
        startActivity(intent);
        UpdateRetailersActivity.this.finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (requestCode == 1000) {
                if (resultCode == Activity.RESULT_OK) {
                    //String result=data.getStringExtra("result");

                    //GPSLocation.builder1 = null;
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //GPSLocation.builder1 = null;
                    UpdateRetailersActivity.this.finishAffinity();
                }
            }

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            if(data.getClipData() != null){
                int x=data.getClipData().getItemCount();
                for(int i=0;i<x;i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imgList.add(imageUri);
                }
                adaptor.notifyDataSetChanged();
            }else if(data.getData()!=null){
                Uri imageUri = data.getData();
                imgList.add(imageUri);
                adaptor.notifyDataSetChanged();
            }else if (data.getExtras() != null) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    Uri imageUri = saveBitmapAndGetUri(photo);
                    imgList.add(imageUri);
                    adaptor.notifyDataSetChanged();
                }
            }


            detectFace(photo);

            if (imgList.size() > 0) {
                shopBitmap1 = getBitmapFromUri(imgList.get(0));
            }
            if (imgList.size() > 1) {
                shopBitmap2 = getBitmapFromUri(imgList.get(1));
            }
            if (imgList.size() > 2) {
                shopBitmap3 = getBitmapFromUri(imgList.get(2));
            }
            if (imgList.size() > 3) {
                shopBitmap4 = getBitmapFromUri(imgList.get(3));
            }
            if (imgList.size() > 4) {
                shopBitmap5 = getBitmapFromUri(imgList.get(4));
            }
            if (imgList.size() > 5) {
                shopBitmap6 = getBitmapFromUri(imgList.get(5));
            }


            Log.d(TAG, "image List Count: "+imgList.size());

            /*if (requestCode == 0 && resultCode == RESULT_OK) {

                detectFace(photo);

            } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                shopImage1.setImageBitmap(photo);
                shopBitmap1 = photo;
            } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                shopImage2.setImageBitmap(photo);
                shopBitmap2 = photo;
            } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
                shopImage3.setImageBitmap(photo);
                shopBitmap3 = photo;
            } else if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
                shopImage4.setImageBitmap(photo);
                shopBitmap4 = photo;
            } else if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
                shopImage5.setImageBitmap(photo);
                shopBitmap5 = photo;
            } else if (requestCode == 6 && resultCode == Activity.RESULT_OK) {
                shopImage6.setImageBitmap(photo);
                shopBitmap6 = photo;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Uri saveBitmapAndGetUri(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return FileProvider.getUriForFile(this, "com.newsalesbeat.fileprovider", file);
//        FileProvider.getUriForFile(this, "com.newsalesbeat.fileprovider", photoFile);
    }

    @Override
    public void onResume() {
        super.onResume();
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    //@Umesh
    private void updateRetailer2(final String retailerId,/* final String name, final String city, final String state,*/
                                final String email, final String mobno, final String whatsppno, final String gstin,
                                final String fssaino, final String pin_no, /*final String lat, final String longt,*/ final String grade,
                                final String target) {


            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("rid", Integer.valueOf(retailerId));
                // jsonObject.put("owner_name", name);
                jsonObject.put("grade", grade);
                jsonObject.put("target", target);
                // jsonObject.put("city", city);
                //  jsonObject.put("state", state);
                jsonObject.put("email", email);
                // jsonObject.put("mobile_no", mobno);
                jsonObject.put("ownersPhone1", mobno); //@Umesh
//                    jsonObject.put("whats_app_no", whatsppno);
                jsonObject.put("whatsappNo", whatsppno); //@Umesh
                jsonObject.put("gstin", gstin);
//                    jsonObject.put("fssai_no", fssaino);
                jsonObject.put("fssai", fssaino); //@Umesh
//                    jsonObject.put("pin_no", pin_no);
                jsonObject.put("pin", pin_no); //@Umesh
                // jsonObject.put("latitude", lat);
                // jsonObject.put("longtitude", longt);
                //jsonObject.put("base64image",  Base64.encodeToString(getFileDataFromDrawable(retailerImageBitmap), Base64.DEFAULT));
                if (retailerImageBitmap != null)
                    jsonObject.put("image",  Base64.encodeToString(getFileDataFromDrawable(retailerImageBitmap), Base64.DEFAULT));
                if (shopBitmap1 != null)
                    jsonObject.put("ShopImage0",  Base64.encodeToString(getFileDataFromDrawable(shopBitmap1), Base64.DEFAULT));
                if (shopBitmap2 != null)
                    jsonObject.put("ShopImage1",  Base64.encodeToString(getFileDataFromDrawable(shopBitmap2), Base64.DEFAULT));
                if (shopBitmap3 != null)
                    jsonObject.put("ShopImage2",  Base64.encodeToString(getFileDataFromDrawable(shopBitmap3), Base64.DEFAULT));
                if (shopBitmap4 != null)
                    jsonObject.put("ShopImage3",  Base64.encodeToString(getFileDataFromDrawable(shopBitmap4), Base64.DEFAULT));
//                if (shopBitmap5 != null)
//                    jsonObject.put("ShopImage4",  Base64.encodeToString(getFileDataFromDrawable(shopBitmap5), Base64.DEFAULT));
//                if (shopBitmap6 != null)
//                    jsonObject.put("ShopImage5",  Base64.encodeToString(getFileDataFromDrawable(shopBitmap6), Base64.DEFAULT));
            }
            catch (Exception ex)
            {

            }
            Log.e(TAG, " jsonObject: " + jsonObject.toString());
            JsonObjectRequest updateRetailer = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_UPDATE_RETAILER_INFO,jsonObject,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                //@Umesh
                                if(response.getInt("status")==1)
                                {
                                    JSONObject object = response.getJSONObject("data");
                                    Toast.makeText(UpdateRetailersActivity.this, "Information updated", Toast.LENGTH_SHORT).show();
                                    boolean flag = salesBeatDb.updateRetailerList2(retailerId, email, mobno, whatsppno, gstin,
                                            fssaino, pin_no, grade, target);

                                    if (flag) {

                                        Intent intent = new Intent(UpdateRetailersActivity.this, RetailerActivity.class);
                                        startActivity(intent);
                                        UpdateRetailersActivity.this.finish();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("authorization", prefSFA.getString("token", ""));
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

        updateRetailer.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        Volley.newRequestQueue(this).add(updateRetailer);
    }

    private void updateRetailer(final String retailerId,/* final String name, final String city, final String state,*/
                                final String email, final String mobno, final String whatsppno, final String gstin,
                                final String fssaino, final String pin_no, /*final String lat, final String longt,*/ final String grade,
                                final String target) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();

        try {
            final Map<String, String> jsonObject = new HashMap<>();

            //our custom volley request
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    SbAppConstants.API_UPDATE_RETAILER_INFO, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    loader.dismiss();
                    JSONObject obj = null;
                    try {

                        obj = new JSONObject(new String(response.data));
                        Log.e("RESPONSE", "update Retailer====" + obj.toString());

                        String status = obj.getString("status");

                        File file;
                        if (status.equalsIgnoreCase("success")) {

                            Toast.makeText(UpdateRetailersActivity.this, "Information updated", Toast.LENGTH_SHORT).show();
                            boolean flag = salesBeatDb.updateRetailerList2(retailerId, email, mobno, whatsppno, gstin,
                                    fssaino, pin_no, grade, target);

                            if (flag) {

                                Intent intent = new Intent(UpdateRetailersActivity.this, RetailerActivity.class);
                                startActivity(intent);
                                UpdateRetailersActivity.this.finish();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, error -> {
                loader.dismiss();
                error.printStackTrace();
                Log.e("Update Ret", " ...." + error.getMessage());
                SbLog.printError(TAG, "updateRetailerInfo", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                        prefSFA.getString(getString(R.string.emp_id_key), ""));
                try {

                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("ERRR", "===== " + responseBody);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("authorization", prefSFA.getString("token", ""));
                    headers.put("Accept", "application/json");
                    return headers;
                }


                @Override
                protected Map<String, String> getParams() {

                    jsonObject.put("rid", retailerId);
                    // jsonObject.put("owner_name", name);
                    jsonObject.put("grade", grade);
                    jsonObject.put("target", target);
                    // jsonObject.put("city", city);
                    //  jsonObject.put("state", state);
                    jsonObject.put("email", email);
                   // jsonObject.put("mobile_no", mobno);
                    jsonObject.put("ownersPhone1", mobno); //@Umesh
//                    jsonObject.put("whats_app_no", whatsppno);
                    jsonObject.put("whatsappNo", whatsppno); //@Umesh
                    jsonObject.put("gstin", gstin);
//                    jsonObject.put("fssai_no", fssaino);
                    jsonObject.put("fssai", fssaino); //@Umesh
//                    jsonObject.put("pin_no", pin_no);
                    jsonObject.put("pin", pin_no); //@Umesh
                    // jsonObject.put("latitude", lat);
                    // jsonObject.put("longtitude", longt);

                    return jsonObject;
                }


                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();

                    if (retailerImageBitmap != null)
                        params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(retailerImageBitmap)));

                    if (shopBitmap1 != null)
                        params.put("shop_image[0]", new DataPart("shopImage1" + ".png", getFileDataFromDrawable(shopBitmap1)));

                    if (shopBitmap2 != null)
                        params.put("shop_image[1]", new DataPart("shopImage2" + ".png", getFileDataFromDrawable(shopBitmap2)));

                    if (shopBitmap3 != null)
                        params.put("shop_image[2]", new DataPart("shopImage3" + ".png", getFileDataFromDrawable(shopBitmap3)));

                    if (shopBitmap4 != null)
                        params.put("shop_image[3]", new DataPart("shopImage4" + ".png", getFileDataFromDrawable(shopBitmap4)));

                    if (shopBitmap5 != null)
                        params.put("shop_image[4]", new DataPart("shopImage5" + ".png", getFileDataFromDrawable(shopBitmap5)));

                    return params;
                }
            };

            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //adding the request to volley
            Volley.newRequestQueue(this).add(volleyMultipartRequest);
        } catch (Exception e) {

        }

    }

    private void detectFace(Bitmap ownerImagePath) {

        new LoadimageTask(ownerImagePath).execute();

    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadimageTask extends AsyncTask<Void, Void, Bitmap> {

        Bitmap ownerImagePath = null;

        Snackbar snackbar = Snackbar
                .make(updateRetailerRL, "Loading...", Snackbar.LENGTH_INDEFINITE);

        public LoadimageTask(Bitmap ownerImagePath) {
            this.ownerImagePath = ownerImagePath;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inMutable = true;
//            Bitmap myBitmap = BitmapFactory.decodeFile(ownerImagePath);

            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.RED);
            myRectPaint.setStyle(Paint.Style.STROKE);

            Bitmap tempBitmap = Bitmap.createBitmap(ownerImagePath.getWidth(), ownerImagePath.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(ownerImagePath, 0, 0, null);

//            FaceDetector faceDetector = new FaceDetector.Builder(UpdateRetailersActivity_update_1.this).setTrackingEnabled(false).build();
            //Log.e("AddNewRet","####"+faceDetector.);
//            if (!faceDetector.isOperational()) {
////                Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show();
////                new AlertDialog.Builder(this).setMessage("Could not set up the face detector!").show();
//                return;
//            }

//            Frame frame = new Frame.Builder().setBitmap(ownerImagePath).build();
//            SparseArray<Face> faces = faceDetector.detect(frame);

           /* Log.e("AddNewRet", "####" + faces.size());

            if (faces.size() == 0) {

                faceDetector.release();
                return null;

            } else {

                for (int i = 0; i < faces.size(); ++i) {
                    Face face = faces.valueAt(i);
                    float x = face.getPosition().x;
                    float y = face.getPosition().y;
                    float width = face.getWidth();
                    float height = face.getHeight();
                    // ... (other face properties)
                }

                */
            /*for (int i = 0; i < faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }*/
            /*
                // Although detector may be used multiple times for different images, it should be released
                // when it is no longer needed in order to free native resources.
                faceDetector.release();

                retailerImageBitmap = tempBitmap;
                return tempBitmap;
            }*/
            retailerImageBitmap = tempBitmap;
            return tempBitmap;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            snackbar.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            snackbar.dismiss();
            if (bitmap == null) {

                Toast.makeText(UpdateRetailersActivity.this, "Face not detected.Please take again", Toast.LENGTH_SHORT).show();


            } else {

                Log.d(TAG, "onPostExecute Bitmap");
//                retailerImage.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                Glide.with(getApplicationContext())
                        .load(ownerImagePath) // If it's a URL
                        .into(retailerImage);

            }
        }
    }
}