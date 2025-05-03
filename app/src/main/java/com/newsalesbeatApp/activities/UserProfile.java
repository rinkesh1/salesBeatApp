package com.newsalesbeatApp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.newsalesbeatApp.BuildConfig;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.RoundedImageView;
import com.newsalesbeatApp.netwotkcall.VolleyMultipartRequest;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.BlurBuilder;
import com.newsalesbeatApp.utilityclass.Config;
//import com.newsalesbeat.utilityclass.FaceCropper2;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/*
 * Created by MTC on 03-11-2017.
 */

public class UserProfile extends AppCompatActivity implements NetworkChangeInterface {

    private static final int CAMERA_REQUEST = 1;
    Bitmap userImageEncoded = null;
    UtilityClass utilityClass;
    GPSLocation locationProvider;
    DownloadImage downloadImage;
    private String TAG = "UserProfile";
    private TextView tvEmpName;
    private TextView tvEmpEmailId;
    private TextView tvEmpPhoneNo;
    private TextView tvEmpReportingTo;
    private TextView tvEmpDesignation;
    private TextView tvEmpZone;
    private TextView tvEmpState;
    private TextView tvTokenValidTo;
    private RoundedImageView userImage;
    private TextView tvHeadquarter;
    private Button btnUpdate;
    private ImageView imgBack, chaneImage, imgBlurBack;
    private SharedPreferences prefSFA;
    private SwipeRefreshLayout swipeContainer;
    private int PICK_IMAGE_REQUEST = 2;
    private String mImageUri;
    // Network Error
    private Boolean updateUserError = false;
    private Bitmap updateUserBitmap;

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.user_profile);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        swipeContainer = findViewById(R.id.swipeContainer);
        tvEmpName = findViewById(R.id.tvEmpName);
        tvEmpEmailId = findViewById(R.id.tvEmpEmailId);
        tvEmpPhoneNo = findViewById(R.id.tvEmpPoneNo);
        tvEmpZone = findViewById(R.id.tvEmpZone);
        tvEmpState = findViewById(R.id.tvEmpState);
        tvEmpReportingTo = findViewById(R.id.tvEmpReportingTo);
        tvEmpDesignation = findViewById(R.id.tvEmpDesignation);
        TextView tvPageTitle = findViewById(R.id.pageTitle);
        tvHeadquarter = findViewById(R.id.tvHeadquarter);
        btnUpdate = findViewById(R.id.btnUpdate);
        userImage = findViewById(R.id.userProPic);
        imgBack = findViewById(R.id.imgBack);
        imgBlurBack = findViewById(R.id.imgBlurBack);
        chaneImage = findViewById(R.id.chaneImage);

        //@Umesh 10-08-2022
//            tvTokenValidTo = findViewById(R.id.tvTokenValidTo);
//            String TokenValidTo = UTCToLocal("yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd HH:mm:ss", prefSFA.getString("TokenValidTo", ""));
//            tvTokenValidTo.setText(TokenValidTo);

        //Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        //TextView tvPageTitle = (TextView) mToolbar.findViewById(R.id.pageTitle);

        //tvPageTitle.setText("Employee Profile");

        utilityClass = new UtilityClass(UserProfile.this);
        locationProvider = new GPSLocation(this);

        //locationProvider.unregisterReceiver();

        //check gps status if on/off
        //locationProvider.checkGpsStatus();

//        FaceCropper2 mFaceCropper = new FaceCropper2(2f);
//        mFaceCropper.setFaceMinSize(0);
//        mFaceCropper.setDebug(true);

        try {

            if (!getResources().getBoolean(R.bool.isTablet)) {
                // Configure the refreshing colors
                swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        tvEmpName.setText(prefSFA.getString(getString(R.string.emp_name_key), ""));
        tvEmpDesignation.setText(prefSFA.getString(getString(R.string.emp_designation_key), ""));
        tvEmpEmailId.setText(prefSFA.getString(getString(R.string.emp_emailid_key), ""));
        tvEmpPhoneNo.setText(getString(R.string.phone_no) + " " + prefSFA.getString(getString(R.string.emp_phoneno_key), ""));
        tvEmpReportingTo.setText(prefSFA.getString(getString(R.string.emp_reportingto_key), ""));
        tvEmpZone.setText(prefSFA.getString(getString(R.string.zone_key), ""));
        tvEmpState.setText(prefSFA.getString(getString(R.string.state_key), ""));
        tvHeadquarter.setText(prefSFA.getString(getString(R.string.emp_headq_key), ""));
        btnUpdate.setVisibility(View.GONE);

        setProfilePic(prefSFA.getString(getString(R.string.emp_pic_url_key), ""));

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(UserProfile.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        if (utilityClass.isInternetConnected())
                            logInUser();
                        else
                            Toast.makeText(UserProfile.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }

                });


            }
        });

        chaneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage("Take Picture From");
                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        launchCamera();
                    }
                });

                builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        launchGallery();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfile.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }
    //@Umesh 10-08-2022
    public static String UTCToLocal(String dateFormatInPut, String dateFomratOutPut, String datesToConvert) {


        String dateToReturn = datesToConvert;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatInPut);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date gmt = null;

        SimpleDateFormat sdfOutPutToSend = new SimpleDateFormat(dateFomratOutPut);
        sdfOutPutToSend.setTimeZone(TimeZone.getDefault());

        try {

            gmt = sdf.parse(datesToConvert);
            dateToReturn = sdfOutPutToSend.format(gmt);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn; }

    @Override
    public void onResume() {
        super.onResume();
        //check gps status if on/off
        //locationProvider.checkGpsStatus();
    }

    public void onDestroy() {
        try {
            if (downloadImage != null)
                downloadImage.cancel(true);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        System.gc();
        super.onDestroy();
    }

    private void logInUser() {

        if (!prefSFA.getString("username", "").isEmpty() && !prefSFA.getString("password", "").isEmpty()) {

            JSONObject orderrrr = new JSONObject();
            try {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);

                Log.e("FIREBASE", "Firebase reg id: " + regId);



                orderrrr.put("auth", getString(R.string.apikey));
                orderrrr.put("cid", prefSFA.getString("cmny_id", ""));
                orderrrr.put("username", prefSFA.getString("username", ""));
                orderrrr.put("password", prefSFA.getString("password", ""));
                orderrrr.put("ismobileuser", true);
                orderrrr.put("app_version",  BuildConfig.VERSION_NAME);
                //orderrrr.put("os_version", String.valueOf(Build.VERSION.SDK_INT));
                orderrrr.put("os_version", Build.VERSION.RELEASE);
                orderrrr.put("model", Build.BRAND + " " + Build.MODEL);

                //TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                try {
//
//                    if (manager != null)
//                        orderrrr.put("imei", manager.getDeviceId());
//
//                } catch (SecurityException e) {
//                    orderrrr.put("imei", "restricted in Q");
//                }
                orderrrr.put("imei", "restricted in Q");
                orderrrr.put("token", regId);


            } catch (Exception e) {
                e.printStackTrace();
            }

            // Volley
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    SbAppConstants.API_USER_LOG_IN,
                    orderrrr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.e("TAG", "User Login===" + response);
                    swipeContainer.setRefreshing(false);
                    try {
                        JSONObject data = response.getJSONObject("data");

                        JSONObject authtoken = data.getJSONObject("authtoken");
                        String TokenValidTo=authtoken.getString("expiration");
                        String token =authtoken.getString("token");
                        SharedPreferences.Editor Teditor = prefSFA.edit();
                        Teditor.putString("TokenValidTo", TokenValidTo);
                        Teditor.apply();

                        JSONObject employee = data.getJSONObject("emp");
                        String cmny_id = employee.getString("cid");
                        String emp_id = employee.getString("eid");
                        String emp_name = employee.getString("name");
                        String username = employee.getString("username");
                        String emp_ph_no = employee.getString("phone1");
                        String emp_email = employee.getString("email1");
                        String headquarter = employee.getString("headquarter");
                        String zone = employee.getString("zone");
                        String zoneid = employee.getString("zoneid");
                        String state = employee.getString("state");
                        //String report_to = employee.getString("reportingTo");
                        String report_to = employee.getString("reportingToEuid"); //@Umesh
                        String designation = employee.getString("designation");
                        String emp_photo_url = employee.getString("profilePic");

                        if (emp_photo_url == null || emp_photo_url.isEmpty() || emp_photo_url.equalsIgnoreCase("null")) {
                            emp_photo_url = SbAppConstants.PLACEHOLDER_URL;
                        }
                        else{
                            emp_photo_url=SbAppConstants.IMAGE_PREFIX+emp_photo_url;
                        }

//                        String status = response.getString("status");
//                        String statusMsg = response.getString("statusMessage");

                        String status = String.valueOf(response.getInt("status")); //@Umesh
                        String statusMsg = response.getString("message");//@Umesh

                        //String token = response.getString("token");
//                        String token = employee.getString("fcmToken");//@Umesh




                        if (status.equalsIgnoreCase("1") && cmny_id.equalsIgnoreCase(prefSFA.getString("cmny_id", ""))) {

                            downloadImage = new DownloadImage(emp_id, emp_name, username, emp_ph_no, emp_email, headquarter, zone, zoneid,
                                    state, report_to, designation, emp_photo_url, token, statusMsg);

                            downloadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        } else {
                            Toast.makeText(UserProfile.this, statusMsg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeContainer.setRefreshing(false);
                    try {

                        SbLog.printError(TAG, "employee-reports/date/", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                                prefSFA.getString(getString(R.string.emp_id_key), ""));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (error.networkResponse.statusCode == 422) {
                            String responseBody = null;
                            try {

                                responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject object = new JSONObject(responseBody);
                                String message = object.getString("message");
                                JSONObject errorr = object.getJSONObject("errors");

                                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(UserProfile.this);
                                dialog.setTitle("Message!");
                                dialog.setMessage(message + "\n" + errorr.toString());

                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                Dialog dialog1 = dialog.create();
                                dialog1.show();

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(UserProfile.this).add(jsonObjectRequest);

        }

    }

    @Override
    public void connectionChange(boolean status) {

        if (status) {

            if (updateUserError) {

                updateUserDetails(updateUserBitmap);

            }

        }

    }

    private void setProfilePic(String employee_pic_url) {
        Log.d(TAG, "setProfilePic URL: "+employee_pic_url);
        try {

            if (!getResources().getBoolean(R.bool.isTablet))
            {

                Glide.with(this)
                        .asBitmap()
                        .load(new File(employee_pic_url))  // Load from file
                        .override(400, 400)  // Resize
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    // Apply blur effect
                                    Bitmap blurredBitmap = BlurBuilder.blur(UserProfile.this, resource);
                                    imgBlurBack.setImageBitmap(blurredBitmap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Set original image
                                userImage.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Optional: Handle cleanup if necessary
                            }
                        });
                /*Glide.with(this)
                        .asBitmap() // Explicitly request a Bitmap
                        .load(new File(employee_pic_url)) // Load the image from a File
                        .into(new CustomTarget<Bitmap>(400, 400) { // Use CustomTarget to handle the size
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                try {
                                    // Apply the blur effect
                                    Bitmap blurredBitmap = BlurBuilder.blur(UserProfile.this, resource);
                                    imgBlurBack.setImageBitmap(blurredBitmap); // Set the blurred bitmap
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Set the original bitmap
                                userImage.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(Drawable placeholder) {
                                // Handle cleanup or placeholder if needed
                                imgBlurBack.setImageDrawable(placeholder);
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                // Handle the failure case
                                userImage.setImageDrawable(getResources().getDrawable(R.drawable.placeholder3));
                            }
                        });*/

            } else {

                new SetImage(employee_pic_url).execute();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //@Umesh
        new SetImage(employee_pic_url).execute();
    }

    private void launchGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        try {


            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;

                try {

                    photoFile = createImageFile();

                    // Continue only if the File was successfully created
                    if (photoFile != null) {

                        Uri photoURI;
                        // N is for Nougat Api 24 Android 7
                        if (Build.VERSION_CODES.N <= android.os.Build.VERSION.SDK_INT) {
                            // FileProvider required for Android 7.  Sending a file URI throws exception.
                            photoURI = FileProvider.getUriForFile(this, "com.newsalesbeat.fileprovider", photoFile);
                        } else {
                            // For older devices:
                            // Samsung Galaxy Tab 7" 2 (Samsung GT-P3113 Android 4.2.2, API 17)
                            // Samsung S3
                            photoURI = Uri.fromFile(photoFile);
                        }

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    }

                } catch (IOException ex) {

                    ex.printStackTrace();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        mImageUri = image.getAbsolutePath();

        return image;
    }

    //called after camera intent finished
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("TAG", "onActivityResult profile image");
        /*if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && intent != null && intent.getData() != null) {

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
                showDialog(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == CAMERA_REQUEST
                && resultCode == RESULT_OK) {

            getImage();
        }

        super.onActivityResult(requestCode, resultCode, intent);*/
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);  // 80% quality for compression

                byte[] imageBytes = outputStream.toByteArray();

                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                showDialog(compressedBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            getImage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getImage() {

        try {

            Log.e("UserProfile", "===>" + mImageUri);
            File file = new File(mImageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            options.inSampleSize = calculateInSampleSize(options, 600, 600);

            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            showDialog(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UserProfile", "===File not found");
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void updateUserDetails2(final Bitmap userPic) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                SbAppConstants.API_UPDATE_EMP_INFO, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                updateUserError = false;

                Log.e("RESPONSE", "Update emp info===" + response);
                loader.dismiss();
                try {

                    JSONObject obj = new JSONObject(new String(response.data));
                    Log.e("RESPONSE", "Update emp info===" + obj.toString());

                    String status = obj.getString("status");
                    String url = obj.getString("profilePic");
                    String msg = obj.getString("statusMessage");

                    if (status.equalsIgnoreCase("success")) {


                        btnUpdate.setVisibility(View.INVISIBLE);

                        Toast.makeText(UserProfile.this, "Updated successfully",
                                Toast.LENGTH_SHORT).show();
                        new DownloadProfileImage(url).execute();

                    } else {

                        Toast.makeText(UserProfile.this, "" + msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                error.printStackTrace();
                Log.e("IMAGE", "" + error.networkResponse.statusCode);
                // TODO update user detail error
                if (!utilityClass.isInternetConnected()) {
                    updateUserBitmap = userPic;
                    updateUserError = true;
                } else {
                    updateUserError = false;
                }

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

                Map<String, String> empObj1 = new HashMap<>();
                empObj1.put("eid", prefSFA.getString(getString(R.string.emp_id_key), ""));
                /*empObj1.put("emp_dob", dob);*/
                return empObj1;
            }


            @Override
            protected Map<String, DataPart> getByteData() {

                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("emp_profile_pic", new DataPart(imagename + ".png", getFileDataFromDrawable(userPic)));
                return params;
            }

        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    //@Umesh
    private void updateUserDetails(final Bitmap userPic) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();

        JSONObject obj = new JSONObject();
        try {
            obj.put("eid", Integer.valueOf(prefSFA.getString(getString(R.string.emp_id_key), "")));
            obj.put("profilePicBase64", Base64.encodeToString(getFileDataFromDrawable(userPic), Base64.DEFAULT));
        }
        catch (Exception ex){}

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_UPDATE_EMP_INFO,obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                updateUserError = false;

                Log.e("RESPONSE", "Update emp info===" + response);
                loader.dismiss();
                try {

                    if (response.getInt("status")==1)
                    {
                        JSONObject data = response.getJSONObject("data");
                        String employee_pic_url = SbAppConstants.IMAGE_PREFIX+data.getString("profilePic");

                        btnUpdate.setVisibility(View.INVISIBLE);
                        Toast.makeText(UserProfile.this, "Updated successfully",
                                Toast.LENGTH_SHORT).show();
                       new SetImage( employee_pic_url).execute();

                        SharedPreferences.Editor editor = prefSFA.edit();
                        editor.putString(getString(R.string.emp_pic_url_key), employee_pic_url);
                        editor.apply();

                    } else {
                        Toast.makeText(UserProfile.this, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                error.printStackTrace();
                Log.e("IMAGE", "" + error.networkResponse.statusCode);
                // TODO update user detail error
                if (!utilityClass.isInternetConnected()) {
                    updateUserBitmap = userPic;
                    updateUserError = true;
                } else {
                    updateUserError = false;
                }

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

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //adding the request to volley
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void showDialog(Bitmap photo) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        final Dialog dialog = new Dialog(UserProfile.this, R.style.DialogActivityTheme);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.dimAmount = 0.75f;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.width = width;
        lp.height = height;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_preview);
        ImageView imgPreview = dialog.findViewById(R.id.imageView);
//        final CropperView imgPreview = (CropperView) dialog.findViewById(R.id.imageview);
        Button btnRotate = (Button) dialog.findViewById(R.id.image_button_rotate);
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);

        imgPreview.setImageBitmap(photo);


        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(imgPreview);
            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cropImage(imgPreview);
                try {

                    Bitmap blurredBitmap = BlurBuilder.blur(UserProfile.this, imgPreview.getDrawingCache());
                    //Drawable d = new BitmapDrawable(getResources(), blurredBitmap);
                    imgBlurBack.setImageBitmap(blurredBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                userImage.setImageBitmap(imgPreview.getDrawingCache());
                userImageEncoded = imgPreview.getDrawingCache();
                dialog.dismiss();

                if (userImageEncoded != null)

                    new PingServer(internet -> {
                        /* do something with boolean response */
                        if (!internet) {
                            Toast.makeText(UserProfile.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                        } else {
                            updateUserDetails(userImageEncoded);
                        }

                    });


                else
                    Toast.makeText(UserProfile.this, "Please select image", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void rotateImage(ImageView imgPreview) {
        Bitmap mBitmap = rotateBitmap(imgPreview.getDrawingCache(), 90);
        imgPreview.setImageBitmap(mBitmap);
    }

    public void onBackPressed() {
        UserProfile.this.finish();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private class DownloadImage extends AsyncTask<Void, Void, String> {

        String emp_id, emp_name, username, emp_ph_no, emp_email, headquarter, zone, zoneid, state, report_to,
                designation, emp_photo_url, token, statusMsg;

        public DownloadImage(String emp_id, String emp_name, String username, String emp_ph_no, String emp_email,
                             String headquarter, String zone, String zoneid, String state, String report_to, String
                                     designation, String emp_photo_url, String token, String statusMsg) {

            this.emp_id = emp_id;
            this.emp_name = emp_name;
            this.username = username;
            this.emp_ph_no = emp_ph_no;
            this.emp_email = emp_email;
            this.headquarter = headquarter;
            this.zone = zone;
            this.zoneid = zoneid;
            this.state = state;
            this.report_to = report_to;
            this.designation = designation;
            this.emp_photo_url = emp_photo_url;
            this.token = token;
            this.statusMsg = statusMsg;

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {


                String PATH = Environment.getExternalStorageDirectory() + "/CMNY/";
                File folder = new File(PATH);

                if (!folder.exists()) {
                    folder.mkdir();
                }

                URL downloadURL = new URL(emp_photo_url);
                HttpURLConnection conn = (HttpURLConnection) downloadURL
                        .openConnection();
                int responseCode = conn.getResponseCode();

                if (responseCode != 200)
                    throw new Exception("Error in connection");
                InputStream is = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(PATH + emp_id);
                byte buffer[] = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteCount);
                }
                os.close();
                is.close();

                return folder.getPath() + "/" + emp_id;


            } catch (Exception e) {

                e.printStackTrace();
            }

            return "";
        }


        @Override
        protected void onPostExecute(String filepath) {
            super.onPostExecute(filepath);

            Log.e(TAG, "==" + filepath);

            if (!filepath.isEmpty()) {

                SharedPreferences.Editor editor = prefSFA.edit();
                editor.putString(getString(R.string.emp_id_key), emp_id);
                editor.putString(getString(R.string.emp_name_key), emp_name);
                editor.putString(getString(R.string.emp_phoneno_key), emp_ph_no);
                editor.putString(getString(R.string.emp_emailid_key), emp_email);
                editor.putString(getString(R.string.zone_key), zone);
                editor.putString(getString(R.string.zone_id_key), zoneid);
                editor.putString(getString(R.string.state_key), state);
                editor.putString(getString(R.string.emp_headq_key), headquarter);
                editor.putString(getString(R.string.emp_reportingto_key), report_to);
                editor.putString(getString(R.string.emp_designation_key), designation);
                editor.putString(getString(R.string.emp_pic_url_key), filepath);
                editor.putString("token", "Bearer " + token);
                editor.apply();

                tvEmpName.setText(prefSFA.getString(getString(R.string.emp_name_key), ""));
                tvEmpDesignation.setText(prefSFA.getString(getString(R.string.emp_designation_key), ""));
                tvEmpEmailId.setText(prefSFA.getString(getString(R.string.emp_emailid_key), ""));
                tvEmpPhoneNo.setText(getString(R.string.phone_no) + " " + prefSFA.getString(getString(R.string.emp_phoneno_key), ""));
                tvEmpReportingTo.setText(prefSFA.getString(getString(R.string.emp_reportingto_key), ""));
                tvEmpZone.setText(prefSFA.getString(getString(R.string.zone_key), ""));
                tvEmpState.setText(prefSFA.getString(getString(R.string.state_key), ""));
                tvHeadquarter.setText(prefSFA.getString(getString(R.string.emp_headq_key), ""));

                setProfilePic(prefSFA.getString(getString(R.string.emp_pic_url_key), ""));

                Toast.makeText(UserProfile.this, statusMsg, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class SetImage extends AsyncTask<Void, Void, Bitmap> {
        String filePath = "file://";

        public SetImage(String employee_pic_url) {
            //filePath = filePath + employee_pic_url;
            filePath =  employee_pic_url; //@Umesh
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bm = null;
            InputStream is = null;
            BufferedInputStream bis = null;
            try {
                URLConnection conn = new URL(filePath).openConnection();
                conn.connect();
                if(conn.getDoOutput()) { //@Umesh 20220903
                    is = conn.getInputStream();
                    bis = new BufferedInputStream(is, 8192);
                    bm = BitmapFactory.decodeStream(bis);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bm;
        }

        public void onPostExecute(Bitmap resource) {

            try {

                if(resource!=null) { //@Umesh 20220903
                    Bitmap blurredBitmap = BlurBuilder.blur(UserProfile.this, resource);
                    //Drawable d = new BitmapDrawable(getResources(), blurredBitmap);
                    imgBlurBack.setImageBitmap(blurredBitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            userImage.setImageBitmap(resource);
        }
    }

    private class DownloadProfileImage extends AsyncTask<Void, Void, String> {

        String imageUrl;
        String emp_id;

        public DownloadProfileImage(String url) {
            this.imageUrl = url;
            emp_id = prefSFA.getString(getString(R.string.emp_id_key), "");
        }

        @Override
        protected void onPostExecute(String filepath) {
            super.onPostExecute(filepath);
            SharedPreferences.Editor editor = prefSFA.edit();
            editor.putString(getString(R.string.emp_pic_url_key), filepath);
            editor.apply();

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {


                String PATH = Environment.getExternalStorageDirectory() + "/CMNY/";
                File folder = new File(PATH);

                if (!folder.exists()) {
                    folder.mkdir();
                }

                String imagePath = folder.getPath() + "/" + emp_id;

                File file = new File(imagePath);
                if (file.exists())
                    file.delete();

                URL downloadURL = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) downloadURL
                        .openConnection();
                int responseCode = conn.getResponseCode();

                Log.e(TAG, " ====>>>" + responseCode);

                if (responseCode != 200)
                    throw new Exception("Error in connection");
                InputStream is = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(PATH + emp_id );
                byte buffer[] = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteCount);
                }
                os.close();
                is.close();

                return folder.getPath() + "/" + emp_id;


            } catch (Exception e) {
                SbLog.printException("LoginScreen", "employeeLogin", e.getMessage(), "0");
                e.printStackTrace();
            }

            return "";
        }

    }
}

