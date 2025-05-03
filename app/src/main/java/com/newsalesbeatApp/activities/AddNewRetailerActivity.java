package com.newsalesbeatApp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

//import com.azimolabs.maskformatter.MaskFormatter;
import com.bumptech.glide.Glide;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.snackbar.Snackbar;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.services.TempService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * Created by Dhirendra Thakur on 15-11-2017.
 */

public class AddNewRetailerActivity extends AppCompatActivity {

    private static final String GSTIN_MASK = "99AAAAA9999A9A9";
    private static final int CAMERA_REQUEST = 1888;
    static String imageTimeStamp, tempRid;
    String TAG = "AddNewRetailerActivity";
    UtilityClass utilityClass;
    int counter = 0;
    EditText edtShopName;
    EditText edtShopAddress;
    EditText edtShopPhone;
    EditText edtWhatsAppNo;
    EditText edtOwnerName;
    EditText edtOwnerPhn;
    EditText edtLocality;
    EditText edtState;
    EditText edtDistrict;
    EditText edtPincode;
    EditText edtEmailId;
    EditText edtGSTIN;
    EditText edtTarget;
    EditText edtFSSAINo;
    AutoCompleteTextView actvGrade;
    AutoCompleteTextView actvShopType;
    AutoCompleteTextView actvOutletChannel;
    Button btnAddRetailer;
    TextView tvPageTitle;
    Dialog loader;
    String ownerImagePath = "", shopImagePath1 = "", shopImagePath2 = "", shopImagePath3 = "", shopImagePath4 = "";
    String address, district, locality, pincode,state; //@Umesh
    GPSLocation locationProvider;
    SalesBeatDb salesBeatDb;
    SampleResultReceiver resultReceiever;
    RelativeLayout addNewRetailerRL;
    File storageDir, image;
    private SharedPreferences prefSFA, tempPref;
    private RelativeLayout ownerImageLayout, shopImage1Layout, shopImage2Layout, shopImage3Layout,
            shopImage4Layout, shopImage5Layout;
    private TextView shopImage2Title, shopImage3Title, shopImage4Title, shopImage5Title;
    private ImageView takePicOwner, takePicShop1, takePicShop2, takePicShop3, takePicShop4, takePicShop5,
            takePicAgainOwner, takePicAgainShop1, takePicAgainShop2, takePicAgainShop3,
            takePicAgainShop4, takePicAgainShop5, imgBack;
    private ImageView imgOwnerImagePreview, imgShopImagePreview1, imgShopImagePreview2, imgShopImagePreview3,
            imgShopImagePreview4, imgShopImagePreview5, imgGrade, imgOutletChannel, imgShopType;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.add_new_retailer_dialog);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        findViewByIdd();

        initializeClasssesAndVariables();

        startService();

        setViewListener();
    }

    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private void setViewListener() {

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //locationProvider.unregisterReceiver();
                Intent intent = new Intent(AddNewRetailerActivity.this, RetailerActivity.class);
                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        takePicOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                counter = 1;
                dispatchTakePictureIntent(counter);

            }
        });

        takePicAgainOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePicAgainOwner.setVisibility(View.GONE);
                takePicOwner.setVisibility(View.VISIBLE);
            }
        });

        takePicShop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                counter = 2;
                dispatchTakePictureIntent(counter);
            }
        });

        takePicAgainShop1.setOnClickListener(view -> {

            takePicAgainShop1.setVisibility(View.GONE);
            takePicShop1.setVisibility(View.VISIBLE);
        });

        takePicShop2.setOnClickListener(view -> {
            counter = 3;
            dispatchTakePictureIntent(counter);
            shopImage3Layout.setVisibility(View.VISIBLE);
        });

        takePicAgainShop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicAgainShop2.setVisibility(View.GONE);
                takePicShop2.setVisibility(View.VISIBLE);
            }
        });


        takePicShop3.setOnClickListener(view -> {
            counter = 4;
            dispatchTakePictureIntent(counter);
            shopImage4Layout.setVisibility(View.VISIBLE);
        });


        takePicAgainShop3.setOnClickListener(view -> {
            takePicAgainShop3.setVisibility(View.GONE);
            takePicShop3.setVisibility(View.VISIBLE);
        });


        takePicShop4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter = 5;
                dispatchTakePictureIntent(counter);
            }
        });


        takePicAgainShop4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicAgainShop4.setVisibility(View.GONE);
                takePicShop4.setVisibility(View.VISIBLE);
            }
        });


        actvGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvGrade.showDropDown();
            }
        });

        imgGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvGrade.showDropDown();
            }
        });


        actvShopType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvShopType.showDropDown();
            }
        });


        imgShopType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvShopType.showDropDown();
            }
        });


        actvOutletChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvOutletChannel.showDropDown();
            }
        });

        imgOutletChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvOutletChannel.showDropDown();
            }
        });

        btnAddRetailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "IsDuplicateRetailer check: "+edtOwnerPhn.getText().toString());


                if (!edtShopName.getText().toString().isEmpty() && !edtShopAddress.getText().toString().isEmpty()
                        && !edtOwnerName.getText().toString().isEmpty() && !edtOwnerPhn.getText().toString().isEmpty()
                        && !edtDistrict.getText().toString().isEmpty() && !edtLocality.getText().toString().isEmpty()
                        && !edtPincode.getText().toString().isEmpty() && !actvShopType.getText().toString().isEmpty()
                        && !actvGrade.getText().toString().isEmpty() && !actvOutletChannel.getText().toString().isEmpty()) {



                    if (!ownerImagePath.isEmpty()) {

                        if (edtOwnerPhn.getText().toString().length() == 10) {

                            if (edtEmailId.getText().toString().isEmpty() || edtEmailId.getText().toString().contains("@")) {

                                if (edtPincode.getText().length() == 6) {

                                    if (edtGSTIN.getText().toString().isEmpty() || (edtGSTIN.getText().length() == 15)) {

                                        if (edtFSSAINo.getText().toString().isEmpty() || edtFSSAINo.getText().length() == 14) {

                                            SQLiteDatabase db= null;
                                            Cursor res=null;
                                            String retNameOne = "";
                                            try{
                                                db = salesBeatDb.getReadableDatabase();
                                                res = db.rawQuery( "select * from new_retailers_list " +
                                                        " Where new_owner_phone= '"+edtOwnerPhn.getText().toString()+"'", null );

                                                if (res != null && res.moveToFirst()) {
                                                    // Get the retailer name using the column index
                                                    @SuppressLint("Range")
                                                    String retName = res.getString(res.getColumnIndex("new_owner_name"));
                                                    // Do something with the retailer name
                                                    Log.d(TAG, "onClick retName: "+retName);
                                                    retNameOne = retName;

                                                    // Close the cursor after use
                                                    res.close();
                                                }
                                                Log.d(TAG, "onClick Count: "+res.getCount());
                                                if(res.getCount()>0)
                                                {
                                                    Toast.makeText(getApplication(), "Retailer : '"+retNameOne+"' \n Mobile number already exist with retailer name and beat name!!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                res = db.rawQuery( "select * from retailers_list " +
                                                        " Where owner_phone= '"+edtOwnerPhn.getText().toString()+"'", null );
                                                if (res != null && res.moveToFirst()) {
                                                    // Get the retailer name using the column index
                                                    @SuppressLint("Range")
                                                    String retName = res.getString(res.getColumnIndex("retailer_name"));
                                                    // Do something with the retailer name
                                                    Log.d(TAG, "onClick retName: "+retName);
                                                    retNameOne = retName;

                                                    // Close the cursor after use
                                                    res.close();
                                                }
                                                Log.d(TAG, "onClick Count-1: "+res.getCount());
                                                Log.d(TAG, "IsDuplicateRetailer-3: "+res.getCount());
                                                if(res.getCount()>0)
                                                {
                                                    Toast.makeText(getApplication(), "Retailer : '"+retNameOne+"' \n Mobile number already exist with retailer name and beat name!!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                            catch (Exception ex)
                                            {
                                                Log.d(TAG, "onClick exception: "+ ex.getMessage());
                                            }
                                            finally {
                                                if (res != null)
                                                    res.close();
                                            }

                                            addNewRetailer(edtShopName.getText().toString(), edtShopAddress.getText().toString(),
                                                    edtShopPhone.getText().toString(), edtOwnerName.getText().toString(),
                                                    edtOwnerPhn.getText().toString(), edtWhatsAppNo.getText().toString(),
                                                    //prefSFA.getString(getString(R.string.state_key), ""),
                                                    edtState.getText().toString(), //@Umesh
                                                    //prefSFA.getString(getString(R.string.zone_key), ""),
                                                    edtState.getText().toString(), //@Umesh
                                                    edtLocality.getText().toString(), edtDistrict.getText().toString(),
                                                    edtPincode.getText().toString(), edtEmailId.getText().toString(),
                                                    edtGSTIN.getText().toString(), edtTarget.getText().toString(),
                                                    edtFSSAINo.getText().toString(), actvGrade.getText().toString(),
                                                    actvOutletChannel.getText().toString(), actvShopType.getText().toString());


                                        } else {

                                            Toast.makeText(AddNewRetailerActivity.this, "FSSAI number incorrect", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {

                                        Toast.makeText(AddNewRetailerActivity.this, "GSTIN format incorrect", Toast.LENGTH_SHORT).show();
                                    }

                                } else {

                                    Toast.makeText(AddNewRetailerActivity.this, "Pincode incorrect", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(AddNewRetailerActivity.this, "Mail format incorrect", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(AddNewRetailerActivity.this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(AddNewRetailerActivity.this, "Owner image mandatory", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(AddNewRetailerActivity.this, "Mandatory fields required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initializeClasssesAndVariables() {

        tvPageTitle.setText("Add New Retailer");
//        MaskFormatter ibanMaskFormatter = new MaskFormatter(GSTIN_MASK, edtGSTIN);
//        edtGSTIN.addTextChangedListener(ibanMaskFormatter);

        edtGSTIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String text = edtGSTIN.getText().toString();
                // Apply your custom masking logic here
                String masked = applyGSTINMask(text);
                edtGSTIN.setText(masked);
                edtGSTIN.setSelection(masked.length()); // Move the cursor to the end
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        utilityClass = new UtilityClass(AddNewRetailerActivity.this);
        //salesBeatDb = new SalesBeatDb(AddNewRetailerActivity.this);
        salesBeatDb = SalesBeatDb.getHelper(this);
        locationProvider = new GPSLocation(AddNewRetailerActivity.this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();


        //edtState.setText(prefSFA.getString(getString(R.string.state_key), "")); //@Umesh

        List<String> srn = new ArrayList<>();
        srn.add("A+");
        srn.add("A");
        srn.add("B");
        srn.add("C");
        srn.add("D");

        ArrayAdapter<String> srnAdapter = new ArrayAdapter<>(AddNewRetailerActivity.this, android.R.layout.simple_spinner_dropdown_item, srn);
        actvGrade.setAdapter(srnAdapter);

        List<String> srn2 = new ArrayList<>();
        srn2.add("MT");
        srn2.add("GT");
        srn2.add("GOVT");
        srn2.add("HORECA");

        ArrayAdapter<String> srnAdapter2 = new ArrayAdapter<>(AddNewRetailerActivity.this, android.R.layout.simple_spinner_dropdown_item, srn2);
        actvOutletChannel.setAdapter(srnAdapter2);

        List<String> srn1 = new ArrayList<>();
        srn1.add("Grocery Store");
        srn1.add("Modern Trade");
        srn1.add("Tea Stall");
        srn1.add("Loose Tea");
        srn1.add("Canteen");

        ArrayAdapter<String> srnAdapter1 = new ArrayAdapter<>(AddNewRetailerActivity.this, android.R.layout.simple_spinner_dropdown_item, srn1);
        actvShopType.setAdapter(srnAdapter1);

        try {

            String from = getIntent().getStringExtra("from");

            if (from!=null && from.equalsIgnoreCase("order")) {

                Bundle newRet = getIntent().getBundleExtra("newRet");
                String[] params = newRet.getStringArray("params");
                imageTimeStamp = newRet.getString("imageTime");
                ownerImagePath = newRet.getString("ownerImage");
                shopImagePath1 = newRet.getString("shopImage1");
                shopImagePath2 = newRet.getString("shopImage2");
                shopImagePath3 = newRet.getString("shopImage3");
                shopImagePath4 = newRet.getString("shopImage4");

                showOrderDialog(tempRid, imageTimeStamp, params);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private String applyGSTINMask(String input) {
        // Add your masking logic for GSTIN here
        // For example, you could mask it like: "XX-XX-XXXXXXX-X"
        return input.replaceAll("([A-Za-z]{2})(\\d{4})(\\d{4})([A-Za-z]{1})(\\d{1})", "$1-$2-$3-$4-$5");
    }

    private void findViewByIdd() {

        addNewRetailerRL = findViewById(R.id.addNewRetailerRL);
        btnAddRetailer = findViewById(R.id.btnAddRetailer);
        tvPageTitle = findViewById(R.id.pageTitle);
        takePicOwner = findViewById(R.id.takeImageOwner);
        takePicAgainOwner = findViewById(R.id.takeImageAgainOwner);
        takePicShop1 = findViewById(R.id.takeImageOwnerShop1);
        takePicAgainShop1 = findViewById(R.id.takeImageAgaiShop1);
        takePicShop2 = findViewById(R.id.takeImageOwnerShop2);
        takePicAgainShop2 = findViewById(R.id.takeImageAgaiShop2);
        takePicShop3 = findViewById(R.id.takeImageOwnerShop3);
        takePicAgainShop3 = findViewById(R.id.takeImageAgaiShop3);
        takePicShop4 = findViewById(R.id.takeImageOwnerShop4);
        takePicAgainShop4 = findViewById(R.id.takeImageAgaiShop4);
        takePicShop5 = findViewById(R.id.takeImageOwnerShop5);
        takePicAgainShop5 = findViewById(R.id.takeImageAgaiShop5);
        imgBack = findViewById(R.id.imgBack);
        imgOwnerImagePreview = findViewById(R.id.imgOwnerImagePreview);
        imgShopImagePreview1 = findViewById(R.id.imgShopImagePreview1);
        imgShopImagePreview2 = findViewById(R.id.imgShopImagePreview2);
        imgShopImagePreview3 = findViewById(R.id.imgShopImagePreview3);
        imgShopImagePreview4 = findViewById(R.id.imgShopImagePreview4);
        imgShopImagePreview5 = findViewById(R.id.imgShopImagePreview5);
        imgGrade = findViewById(R.id.imgGrade);
        imgShopType = findViewById(R.id.imgShopType);
        imgOutletChannel = findViewById(R.id.imgOutletChannel);
        edtShopName = findViewById(R.id.edtShopName);
        edtShopAddress = findViewById(R.id.edtShopAddress);
        edtShopPhone = findViewById(R.id.edtShopPhoneR);
        edtWhatsAppNo = findViewById(R.id.edtWhatsAppno);
        edtOwnerName = findViewById(R.id.edtOwnerNameR);
        edtOwnerPhn = findViewById(R.id.edtOwnerPhnR);
        edtLocality = findViewById(R.id.edtLocality);
        edtState = findViewById(R.id.edtState);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtPincode = findViewById(R.id.edtPinCode);
        edtEmailId = findViewById(R.id.edtMailId);
        edtGSTIN = findViewById(R.id.edtGSTIN);
        edtTarget = findViewById(R.id.edtTarget);
        edtFSSAINo = findViewById(R.id.edtFSSAINo);
        actvGrade = findViewById(R.id.actvGrade);
        actvShopType = findViewById(R.id.actvShopType);
        actvOutletChannel = findViewById(R.id.actvOutletChannel);

        ownerImageLayout = findViewById(R.id.ownerImageLayout);
        shopImage1Layout = findViewById(R.id.shopImage1Layout);
        shopImage2Layout = findViewById(R.id.shopImage2Layout);
        shopImage3Layout = findViewById(R.id.shopImage3Layout);
        shopImage4Layout = findViewById(R.id.shopImage4Layout);
        shopImage5Layout = findViewById(R.id.shopImage5Layout);

        shopImage2Title = findViewById(R.id.shopImage2Title);
        shopImage3Title = findViewById(R.id.shopImage3Title);
        shopImage4Title = findViewById(R.id.shopImage4Title);
        shopImage5Title = findViewById(R.id.shopImage5Title);

    }

    private void addNewRetailer(String shop_name, String shop_address, String shop_phone, String owner_name,
                                String owner_mobile_no, String whatsAppNo, String state, String zone,
                                String locality, String district, String pincode, String email_id,
                                String gstin, String target, String fssai_no, String grade,
                                String outlet_channel, String shop_type) {

        //@Umesh 07-07-2022
        if(IsDuplicateRetailer(shop_name,owner_name,shop_address,gstin,owner_mobile_no)==false)
        {
            new MyAsynchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, shop_name, shop_address, shop_phone,
                    owner_name, owner_mobile_no, whatsAppNo, state, zone,
               locality, district, pincode, email_id, gstin, target, fssai_no, grade, outlet_channel, shop_type);
        }
    }
    //@Umesh 07-07-2022
    private boolean IsDuplicateRetailer(String shop_name,String owner_name,String shop_address,String gstin,String owner_mobile_no)
    {
        boolean Duplicate=false;
        SQLiteDatabase db= null;
        Cursor res=null;
        try{
            db = salesBeatDb.getReadableDatabase();
            res = db.rawQuery( "select * from new_retailers_list " +
                    " Where new_retailer_name= '"+shop_name+"'"+
                    " and new_owner_name='"+owner_name+"'"+
                    " and new_retailer_address= '"+shop_address+"'"+
                    " and new_retailer_gstin= '"+gstin+"'"+
                    " and new_owner_phone='"+owner_mobile_no+"'", null );
            Log.d(TAG, "IsDuplicateRetailer-1: "+res.getCount());
            if(res.getCount()>0)
            {
                Duplicate=true;
                Toast.makeText(this, "Retailer Allready Exists!!", Toast.LENGTH_SHORT).show();
            }
            res = db.rawQuery( "select * from retailers_list " +
                    " Where retailer_name= '"+shop_name+"'"+
                    " and owner_name='"+owner_name+"'"+
                    " and retailer_address= '"+shop_address+"'"+
                    " and retailer_gstin= '"+gstin+"'"+
                    " and owner_phone='"+owner_mobile_no+"'", null );
            Log.d(TAG, "IsDuplicateRetailer-2: "+res.getCount());
            if(res.getCount()>0)
            {
                Duplicate=true;
                Toast.makeText(this, "Retailer Allready Exists!!", Toast.LENGTH_SHORT).show();
            }
            res = db.rawQuery( "select * from retailers_list " +
                    " Where owner_phone= '"+owner_mobile_no+"'", null );
            Log.d(TAG, "IsDuplicateRetailer-3: "+res.getCount());
            if(res.getCount()>0)
            {
                Duplicate=true;
                Toast.makeText(this, "Retailer Mobile Number Exists!!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            Duplicate=true;
            Toast.makeText(this,"AddNewRetailerActivity:"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            if (res != null)
                res.close();
        }
        return  Duplicate;
    }

    private void startService() {

        resultReceiever = new SampleResultReceiver(new Handler());
        //start service to download data
        Intent startIntent = new Intent(this, TempService.class);
        startIntent.putExtra("receiver", resultReceiever);
        startService(startIntent);

    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent(int counter) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {

                photoFile = createImageFile(counter);
                Uri photoURI = null;

                // Continue only if the File was successfully created
                if (photoFile != null) {
                   // photoURI = Uri.fromFile(photoFile);
                    photoURI = FileProvider.getUriForFile(
                            this,
                            "com.newsalesbeat.fileprovider",
                            photoFile
                        );
                }

                Log.e(TAG,"Photo URI"+photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile(int counter) throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        if (counter == 1)
            ownerImagePath = image.getAbsolutePath();
        else if (counter == 2)
            shopImagePath1 = image.getAbsolutePath();
        else if (counter == 3)
            shopImagePath2 = image.getAbsolutePath();
        else if (counter == 4)
            shopImagePath3 = image.getAbsolutePath();
        else if (counter == 5)
            shopImagePath4 = image.getAbsolutePath();

        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_CANCELED) {
                AddNewRetailerActivity.this.finishAffinity();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult counter: "+counter);
            if (counter == 1) {

                try {

                    Glide.with(this)
                            .load(new File(ownerImagePath))
                            .override(200, 200)
                            .into(imgOwnerImagePreview);

                    takePicAgainOwner.setVisibility(View.VISIBLE);
                    takePicOwner.setVisibility(View.GONE);

                } catch (Exception e) {
                    Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            } else if (counter == 2) {

                try {

                    Glide.with(this)
                            .load(new File(shopImagePath1))
                            .override(200, 200)
                            .into(imgShopImagePreview1);

                    takePicAgainShop1.setVisibility(View.VISIBLE);
                    takePicShop1.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (counter == 3) {

                try {

                    Glide.with(this)
                            .load(new File(shopImagePath2))
                            .override(200, 200)
                            .into(imgShopImagePreview2);

                    takePicAgainShop2.setVisibility(View.VISIBLE);
                    takePicShop2.setVisibility(View.GONE);
                    shopImage2Title.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (counter == 4) {

                try {

                    Glide.with(this)
                            .load(new File(shopImagePath3))
                            .override(200, 200)
                            .into(imgShopImagePreview3);

                    takePicAgainShop3.setVisibility(View.VISIBLE);
                    shopImage3Title.setVisibility(View.VISIBLE);
                    takePicShop3.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (counter == 5) {

                try {

                    Glide.with(this)
                            .load(new File(shopImagePath4))
                            .override(200, 200)
                            .into(imgShopImagePreview4);

                    takePicAgainShop4.setVisibility(View.VISIBLE);
                    takePicShop4.setVisibility(View.GONE);
                    shopImage4Title.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void detectFace(String ownerImagePath) {

        new LoadimageTask(ownerImagePath).execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        SbAppConstants.STOP_SYNC = true;
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    public void onBackPressed() {

        //locationProvider.unregisterReceiver();
        Intent intent = new Intent(AddNewRetailerActivity.this, RetailerActivity.class);
        startActivity(intent);
        finish();
        //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void showOrderDialog(final String tempRid, final String imageTimeStamp, final String[] params) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(AddNewRetailerActivity.this);
        builder.setTitle("Retailer added successfully!");
        builder.setCancelable(false);
        builder.setMessage("Do you want to take an order from newly added retailer?");

        builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
            //dialogInterface.dismiss();
            Bundle bundle = new Bundle();
            bundle.putStringArray("params", params);
            bundle.putString("imageTime", imageTimeStamp);
            bundle.putString("ownerImage", ownerImagePath);
            bundle.putString("shopImage1", shopImagePath1);
            bundle.putString("shopImage2", shopImagePath2);
            bundle.putString("shopImage3", shopImagePath3);
            bundle.putString("shopImage4", shopImagePath4);

            Intent intent = new Intent(AddNewRetailerActivity.this, RetailerOrderActivity.class);
            intent.putExtra("newRet", bundle);
            intent.putExtra("nrid", tempRid);
            intent.putExtra("Position", 0);
            intent.putExtra("orderType", "onNewShop");
            intent.putExtra("checkInTime", imageTimeStamp);
            startActivity(intent);
            //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        });

        builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
            dialogInterface.dismiss();
            noOrderReason(tempRid, params);


        });

        Dialog dialog = builder.create();
        dialog.show();

    }

    private void noOrderReason(final String tempRid, final String[] params) {

        final Dialog dialog = new Dialog(AddNewRetailerActivity.this, R.style.DialogActivityTheme);
        dialog.setContentView(R.layout.no_order_reason_dialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final RadioButton stockAvailable = dialog.findViewById(R.id.stockAvl);
        final RadioButton shopClosed = dialog.findViewById(R.id.shopClosed);
        final RadioButton competitorPriceLow = dialog.findViewById(R.id.competitorPriceLow);
        final RadioButton competitorSchemeBetter = dialog.findViewById(R.id.competitorSchemeBetter);
        final RadioButton delaySupply = dialog.findViewById(R.id.delayIrregularSupply);
        final RadioButton directOrder = dialog.findViewById(R.id.directOrderPlaced);
        final RadioButton keyPersonNotAvl = dialog.findViewById(R.id.keyPersonNotAvailable);
        final RadioButton lowRetailerMargin = dialog.findViewById(R.id.lowRetailerMargin);
        final RadioButton lowShelfOffTake = dialog.findViewById(R.id.lowShelfOffTake);
        final RadioButton noCustomerA = dialog.findViewById(R.id.noCustomerA);
        final RadioButton notInterestedIn = dialog.findViewById(R.id.notInterestedIn);
        final RadioButton paymentIssue = dialog.findViewById(R.id.paymentIssue);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> reason = new ArrayList<>();

                if (stockAvailable.isChecked())
                    reason.add(stockAvailable.getText().toString());
                if (shopClosed.isChecked())
                    reason.add(shopClosed.getText().toString());
                if (competitorPriceLow.isChecked())
                    reason.add(competitorPriceLow.getText().toString());
                if (delaySupply.isChecked())
                    reason.add(delaySupply.getText().toString());
                if (directOrder.isChecked())
                    reason.add(directOrder.getText().toString());
                if (keyPersonNotAvl.isChecked())
                    reason.add(keyPersonNotAvl.getText().toString());
                if (lowRetailerMargin.isChecked())
                    reason.add(lowRetailerMargin.getText().toString());
                if (lowShelfOffTake.isChecked())
                    reason.add(lowShelfOffTake.getText().toString());
                if (noCustomerA.isChecked())
                    reason.add(noCustomerA.getText().toString());
                if (notInterestedIn.isChecked())
                    reason.add(notInterestedIn.getText().toString());
                if (paymentIssue.isChecked())
                    reason.add(paymentIssue.getText().toString());
                if (competitorSchemeBetter.isChecked())
                    reason.add(competitorSchemeBetter.getText().toString());


                if (reason.size() > 0) {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                    String time2 = dateFormat.format(Calendar.getInstance().getTime());

                    String transactionId = prefSFA.getString(getString(R.string.emp_id_key), "") + "_" + String.valueOf(Calendar.getInstance().getTimeInMillis());


                    salesBeatDb.insertNewRetailerList(tempRid, params[0], params[1], params[2],
                            params[3], params[4], params[5], locationProvider.getLatitudeStr(),
                            locationProvider.getLongitudeStr(), params[6], params[7], params[8], params[9], params[10], params[11], params[12], params[13],
                            params[14], params[15], params[16],
                            params[17], ownerImagePath, imageTimeStamp, shopImagePath1,
                            shopImagePath2, shopImagePath3, shopImagePath4,
                            "", tempPref.getString(getString(R.string.dis_id_key), ""), tempPref.getString(getString(R.string.beat_id_key), ""), date, transactionId, date);

                    salesBeatDb.entryInOderPlacedByNewRetailersTable(tempRid,
                            tempPref.getString(getString(R.string.dis_id_key), ""), time2, "onNewShop", time2,
                            locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(),
                            reason.toString(), date, transactionId, date);

                    dialog.dismiss();

                    Intent intent = new Intent(AddNewRetailerActivity.this, RetailerActivity.class);
                    intent.putExtra("tabPosition", 1);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(AddNewRetailerActivity.this, "No reason selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }

    private class SampleResultReceiver extends ResultReceiver {

        public SampleResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case TempService.DOWNLOAD_ERROR:
                    break;

                case TempService.DOWNLOAD_SUCCESS:

                    address = resultData.getString("address");
                    locality = resultData.getString("locality");
                    district = resultData.getString("district");
                    pincode = resultData.getString("pincode");
                    state = resultData.getString("state");
                    //latitude = resultData.getDouble("latitude");
                    //longitude = resultData.getDouble("longitude");

                    if (address != null && !address.isEmpty() && !address.equalsIgnoreCase("null"))
                        edtShopAddress.setText(address);
                    if (locality != null && !locality.isEmpty() && !locality.equalsIgnoreCase("null"))
                        edtLocality.setText(locality);
                    if (pincode != null && !pincode.isEmpty() && !pincode.equalsIgnoreCase("null"))
                        edtPincode.setText(pincode);
                    if (district != null && !district.isEmpty() && !district.equalsIgnoreCase("null"))
                        edtDistrict.setText(district);
                    if (state != null && !state.isEmpty() && !state.equalsIgnoreCase("null")) //@Umesh
                        edtState.setText(state);

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

    private class MyAsynchTask extends AsyncTask<String, Void, Integer> {

        String[] params;

        protected void onPreExecute() {

            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            imageTimeStamp = dateFormat1.format(Calendar.getInstance().getTime());

            tempRid = String.valueOf(Calendar.getInstance().getTimeInMillis());

            loader = new Dialog(AddNewRetailerActivity.this, R.style.DialogActivityTheme);
            loader.setContentView(R.layout.loader);
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            loader.show();

        }

        @Override
        protected Integer doInBackground(String... params) {


            this.params = params;

            return 1;
        }

        protected void onPostExecute(Integer I) {
            loader.dismiss();
            if (I == 1) {

                showOrderDialog(tempRid, imageTimeStamp, params);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadimageTask extends AsyncTask<Void, Void, Bitmap> {

        String ownerImagePath = "";

        Snackbar snackbar = Snackbar
                .make(addNewRetailerRL, "Loading...", Snackbar.LENGTH_INDEFINITE);

        public LoadimageTask(String ownerImagePath) {
            this.ownerImagePath = ownerImagePath;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap myBitmap = BitmapFactory.decodeFile(ownerImagePath);

            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.RED);
            myRectPaint.setStyle(Paint.Style.STROKE);

            Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(myBitmap, 0, 0, null);

//            FaceDetector faceDetector = new FaceDetector.Builder(AddNewRetailerActivity.this).setTrackingEnabled(false).build();
            //Log.e("AddNewRet","####"+faceDetector.);
//            if (!faceDetector.isOperational()) {
////                Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show();
////                new AlertDialog.Builder(this).setMessage("Could not set up the face detector!").show();
//                return;
//            }

            /*Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frame);

            Log.e("AddNewRet", "####" + faces.size());

            if (faces.size() == 0) {

                faceDetector.release();
                return null;

            } else {

                for (int i = 0; i < faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }


                // Although detector may be used multiple times for different images, it should be released
                // when it is no longer needed in order to free native resources.
                faceDetector.release();

                return tempBitmap;
            }*/
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

                Toast.makeText(AddNewRetailerActivity.this, "Face not detected.Please take again", Toast.LENGTH_SHORT).show();


            } else {

                imgOwnerImagePreview.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

                takePicAgainOwner.setVisibility(View.VISIBLE);
                takePicOwner.setVisibility(View.GONE);
            }


        }
    }
}
