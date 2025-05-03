package com.newsalesbeatApp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.utilityclass.FileUtils;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/*
 * Created by Dhirendra Thakur on 02-12-2017.
 */

public class NewClaim extends Fragment {

    private static final int REQUEST_CHOOSER = 1234;
    private static final int CAMERA_REQUEST = 100;
    private CalendarView newClaimCalendarView;
    private SharedPreferences prefSFA, tempPref;
    private LinearLayout llTA, llDA, llOther, llTemp;
    private TextView tvTA, tvDA, tvOther, tvTodayDate;
    private ImageView imgTA, imgDA, imgOther;
    private String daType = "";
    private UtilityClass utilityClass;
    private ArrayList<String> path = new ArrayList<>();
    private LinearLayout llAddPath;
    private String date1 = "", date2 = "";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.new_claim_layout, parent, false);
        prefSFA = requireActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = requireActivity().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        newClaimCalendarView = view.findViewById(R.id.newClaimCalendarView);
        llTA = view.findViewById(R.id.llTA);
        llDA = view.findViewById(R.id.llDA);
        llOther = view.findViewById(R.id.llOther);
        llTemp = view.findViewById(R.id.llTe);
        tvTA = view.findViewById(R.id.tvTA);
        tvDA = view.findViewById(R.id.tvDA);
        tvOther = view.findViewById(R.id.tvOther);
        tvTodayDate = view.findViewById(R.id.tvCurrentDateClaim);
        imgTA = view.findViewById(R.id.imgTA);
        imgDA = view.findViewById(R.id.imgDA);
        imgOther = view.findViewById(R.id.imgOther);

        utilityClass = new UtilityClass(getContext());

        SimpleDateFormat sdff = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdff.format(Calendar.getInstance().getTime());

        tvTodayDate.setText(date);

        if (tempPref.getBoolean(getString(R.string.is_aplied_ta_key), false))
            imgTA.setImageResource(R.drawable.ic_done_white_36dp);

        if (tempPref.getBoolean(getString(R.string.is_aplied_da_key), false))
            imgDA.setImageResource(R.drawable.ic_done_white_36dp);

        if (tempPref.getBoolean(getString(R.string.is_aplied_other_key), false))
            imgOther.setImageResource(R.drawable.ic_done_white_36dp);


        llOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tempPref.getBoolean(getString(R.string.is_aplied_other_key), false)) {

                    Toast.makeText(getContext(), "You have already applied for today. Please contact support ",
                            Toast.LENGTH_SHORT).show();
                } else {
                    otherDialog();
                }
            }
        });


        llTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (tempPref.getBoolean(getString(R.string.is_aplied_ta_key), false)) {

                    Toast.makeText(getContext(), "You have already applied for today. Please contact support ",
                            Toast.LENGTH_SHORT).show();

                } else {

                    showTaDialog();

                }

            }
        });

        llDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (tempPref.getBoolean(getString(R.string.is_aplied_da_key), false)) {

                    Toast.makeText(getContext(), "You have already applied for today. Please contact support ",
                            Toast.LENGTH_SHORT).show();
                } else {
                    showDaDialog();
                }

            }
        });

        return view;
    }

    private void otherDialog() {

//        final Dialog dialog = new Dialog(getContext());
        final Dialog dialog = new Dialog(getContext(), R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        dialog.setContentView(R.layout.other_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText edtRemarks = dialog.findViewById(R.id.edtRemarks);
        final EditText edtAmount = dialog.findViewById(R.id.edtAmount);
        TextView tvCurrentDate = dialog.findViewById(R.id.tvCurrnetDateD);
        LinearLayout llUploadDocs = dialog.findViewById(R.id.llUploadDocs);
        LinearLayout llSubmit = dialog.findViewById(R.id.llSubmit);
        llAddPath = dialog.findViewById(R.id.llAddPath);

        SimpleDateFormat sdff = new SimpleDateFormat("dd MMM yyyy");
        String date = sdff.format(Calendar.getInstance().getTime());

        tvCurrentDate.setText(date);

        llUploadDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showOptionDailog();

            }
        });

        llSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    if (!tvOther.getText().toString().isEmpty() && !edtAmount.getText().toString().isEmpty()
                            && !edtRemarks.getText().toString().isEmpty())
                        submitPhone(dialog, tvOther.getText().toString(), edtAmount.getText().toString(),
                                edtRemarks.getText().toString());
                    else
                        Toast.makeText(getContext(), "Mandatory fields required", Toast.LENGTH_SHORT).show();

                } else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();

    }

    private void showDaDialog() {

        final Dialog dialog = new Dialog(getContext(), R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.da_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RadioButton rdHQ = dialog.findViewById(R.id.rdHQ);
        RadioButton rdExHQ = dialog.findViewById(R.id.rdExHQ);
        RadioButton rdUC = dialog.findViewById(R.id.rdUC);
        final EditText edtRemarks = dialog.findViewById(R.id.edtRemarks);
        final LinearLayout llUploadDocs = dialog.findViewById(R.id.llUploadDocs);
        LinearLayout llSubmit = dialog.findViewById(R.id.llSubmit);
        final LinearLayout llDateRange = dialog.findViewById(R.id.llDateRange);
        llAddPath = dialog.findViewById(R.id.llAddPath);
        TextView tvCurrentDate = dialog.findViewById(R.id.tvCurrnetDateD);
        final Button btnSelectDate1 = dialog.findViewById(R.id.btnSelectDate1);
        final Button btnSelectDate2 = dialog.findViewById(R.id.btnSelectDate2);

        llUploadDocs.setVisibility(View.GONE);
        llDateRange.setVisibility(View.GONE);

        SimpleDateFormat sdff = new SimpleDateFormat("dd MMM yyyy");
        String date = sdff.format(Calendar.getInstance().getTime());

        tvCurrentDate.setText(date);


        btnSelectDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDateCalendar(btnSelectDate1, 1);

            }
        });

        btnSelectDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateCalendar(btnSelectDate2, 2);
            }
        });

        rdHQ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    llUploadDocs.setVisibility(View.GONE);
                    llDateRange.setVisibility(View.GONE);
                    daType = "hq";
                }


            }
        });


        rdExHQ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    llUploadDocs.setVisibility(View.GONE);
                    llDateRange.setVisibility(View.GONE);
                    daType = "exhq";
                }

            }
        });


        rdUC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    llUploadDocs.setVisibility(View.VISIBLE);
                    llDateRange.setVisibility(View.VISIBLE);
                    daType = "uc";
                }

            }
        });

        llUploadDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showOptionDailog();

            }
        });


        llSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    if (!daType.isEmpty() && !edtRemarks.getText().toString().isEmpty()) {

                        if (daType.equalsIgnoreCase("uc")) {

                            if (!date1.isEmpty() && !date2.isEmpty())
                                submitDA(dialog, date1, date2, daType, edtRemarks.getText().toString());
                            else
                                Toast.makeText(getContext(), "Date required", Toast.LENGTH_SHORT).show();

                        } else {

                            SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
                            String date = sdff.format(Calendar.getInstance().getTime());
                            submitDA(dialog, date, date, daType, edtRemarks.getText().toString());
                        }

                    } else
                        Toast.makeText(getContext(), "Mandatory fields required", Toast.LENGTH_SHORT).show();

                } else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

    private void showDateCalendar(final Button btnSelectDate, final int v) {

        try {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            final int mYear = c.get(Calendar.YEAR);
            final int mMonth = c.get(Calendar.MONTH);
            final int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            btnSelectDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            if (v == 1)
                                date1 = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            else if (v == 2)
                                date2 = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void showTaDialog() {

        final Dialog dialog = new Dialog(getContext(), R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ta_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        if (dialog.getWindow() != null)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText edtFrom = dialog.findViewById(R.id.edtFrom);
        final EditText edtTo = dialog.findViewById(R.id.edtTo);
        final EditText edtKmsTravelled = dialog.findViewById(R.id.edtKmsTravelled);
        final EditText edtTotalFare = dialog.findViewById(R.id.edtTotalFare);
        final EditText edtRemarks = dialog.findViewById(R.id.edtRemarks);
        TextView tvCurrentDate = dialog.findViewById(R.id.tvCurrnetDateD);
        LinearLayout llUploadDocs = dialog.findViewById(R.id.llUploadDocs);
        LinearLayout llSubmit = dialog.findViewById(R.id.llSubmit);
        llAddPath = dialog.findViewById(R.id.llAddPath);


        SimpleDateFormat sdff = new SimpleDateFormat("dd MMM yyyy");
        String date = sdff.format(Calendar.getInstance().getTime());

        tvCurrentDate.setText(date);

        llUploadDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showOptionDailog();

            }
        });


        llSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    if (!edtFrom.getText().toString().isEmpty() && !edtTo.getText().toString().isEmpty()
                            && !edtKmsTravelled.getText().toString().isEmpty() && !edtTotalFare.getText().toString().isEmpty()
                            && !edtRemarks.getText().toString().isEmpty())
                        submitTA(dialog, edtFrom.getText().toString(), edtTo.getText().toString(), edtKmsTravelled.getText().toString(),
                                edtTotalFare.getText().toString(), edtRemarks.getText().toString());
                    else
                        Toast.makeText(getContext(), "Mandatory fields required", Toast.LENGTH_SHORT).show();

                } else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

    private void showOptionDailog() {

        final Dialog dialog = new Dialog(getContext(), R.style.DialogActivityTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.docs_chooser_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ImageView imgCamera = dialog.findViewById(R.id.imgCamera);
        ImageView imgGallery = dialog.findViewById(R.id.imgGallery);

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent(dialog);
            }
        });

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void dispatchTakePictureIntent(Dialog dialog) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
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
                        photoURI = FileProvider.getUriForFile(requireContext(), "com.newsalesbeat.fileprovider", photoFile);
                    } else {
                        // For older devices:
                        // Samsung Galaxy Tab 7" 2 (Samsung GT-P3113 Android 4.2.2, API 17)
                        // Samsung S3
                        photoURI = Uri.fromFile(photoFile);
                    }

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    dialog.dismiss();
                }

                // Continue only if the File was successfully created
//                if (photoFile != null) {
//
//                    Uri photoURI = FileProvider.getUriForFile(getContext(), "com.newsalesbeat.fileprovider", photoFile);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
//                    dialog.dismiss();
//                }

            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir /* directory */);

        // Get the File path from the Uri
        String filename = image.getAbsolutePath();
        path.add(filename);
        TextView tvPath = new TextView(requireContext());
        tvPath.setText(filename);
        tvPath.setTextColor(Color.GRAY);
        if (llAddPath != null && tvPath != null)
            llAddPath.addView(tvPath);

        return image;
    }

    private void submitPhone(final Dialog dialog, String phone, String totalBill, String remarks) {

        dialog.dismiss();

        final Dialog loader = new Dialog(getContext(), R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        final JSONObject reqEntity = new JSONObject();

        try {

            reqEntity.put("claimtype", "other");
            reqEntity.put("expense", Double.valueOf(totalBill));
            reqEntity.put("remarks", remarks);
            reqEntity.put("transactionId", prefSFA.getString(getString(R.string.emp_id_key), "") + "_"
                    + Calendar.getInstance().getTimeInMillis());


            JSONArray attArr = new JSONArray();
            for (int i = 0; i < path.size(); i++) {
//                attArr.put(getStringImage(compressImage(path.get(i))));
                File compressedFile = compressImage(path.get(i));
                if (compressedFile != null) {
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
                    attArr.put(getStringImage(compressedBitmap));
                }
            }

            reqEntity.put("attatchments", attArr);

        } catch (Exception e) {
            Log.e("ClaimExpense", "==" + e.getMessage());
        }

        Log.e("ClaimExpense", "  Json: " + reqEntity.toString());


        JsonObjectRequest newlyAddedRetailerRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_UPLOAD_CLAIM_EXP, reqEntity, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loader.dismiss();
                Log.e("ClaimExpense", "OTHER=Response=" + response.toString());
                try {

                    //@Umesh 13-March-2022
                    if(response.getInt("status")==1)
                    {

                        imgOther.setImageResource(R.drawable.ic_done_white_36dp);

                        path.clear();
                        imgOther.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Record saved!!", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putBoolean(getString(R.string.is_aplied_other_key), true);
                        editor.apply();

                    }
                    else {
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                try {

                    error.printStackTrace();

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

        newlyAddedRetailerRequest.setShouldCache(false);
        newlyAddedRetailerRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(newlyAddedRetailerRequest);

        //new MyAsynchTask3().execute(phone, totalBill, remarks);
    }

    private void submitDA(final Dialog dialog, String date1, String date2, String hq, String remarks) {

        dialog.dismiss();

        final Dialog loader = new Dialog(getContext(), R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        final JSONObject reqEntity = new JSONObject();

        try {

            reqEntity.put("claimType", "da");
            reqEntity.put("daType", hq);
//            reqEntity.put("datefrom", date1);
//            reqEntity.put("dateto", date2);
            reqEntity.put("datefromstr", date1); //Umesh
            reqEntity.put("datetostr", date2); //Umesh
            reqEntity.put("remarks", remarks);
            reqEntity.put("transactionId", prefSFA.getString(getString(R.string.emp_id_key), "") + "_"
                    + Calendar.getInstance().getTimeInMillis());


            JSONArray attArr = new JSONArray();
            for (int i = 0; i < path.size(); i++) {
//                attArr.put(getStringImage(compressImage(path.get(i))));
                File compressedFile = compressImage(path.get(i));
                if (compressedFile != null) {
                    // Convert File to Bitmap
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());

                    // Now pass the Bitmap to getStringImage
                    attArr.put(getStringImage(compressedBitmap));
                }
            }

            //reqEntity.put("attatchments", attArr);
            //reqEntity.put("attatchments" + "[" + i + "]", getStringImage(compressImage(path.get(i))));

        } catch (Exception e) {
            Log.e("ClaimExpense", "==" + e.getMessage());
        }

        Log.e("ClaimExpense", "  Json: " + reqEntity.toString());


        JsonObjectRequest newlyAddedRetailerRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_UPLOAD_CLAIM_EXP, reqEntity, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loader.dismiss();
                Log.e("ClaimExpense", "DA=Response=" + response.toString());
                try {

                    //@Umesh 13-March-2022
                    if(response.getInt("status")==1)
                    {

                        imgDA.setImageResource(R.drawable.ic_done_white_36dp);

                        path.clear();
                        imgDA.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Record saved!!", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putBoolean(getString(R.string.is_aplied_da_key), true);
                        editor.apply();

                    }
                    else {
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                try {

                    error.printStackTrace();

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

        newlyAddedRetailerRequest.setShouldCache(false);
        newlyAddedRetailerRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(newlyAddedRetailerRequest);


        // new MyAsynchTask2().execute(hq, date1, date2, remarks);
    }

    private void submitTA(final Dialog dialog, final String from, final String to,
                          final String kmsTravelled, final String totalFare, final String remarks) {

        dialog.dismiss();

        final Dialog loader = new Dialog(getContext(), R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        final JSONObject reqEntity = new JSONObject();

        try {

            reqEntity.put("claimType", "ta");
            reqEntity.put("origin", from);
            reqEntity.put("destination", to);
            reqEntity.put("expense", Double.valueOf(totalFare));
            reqEntity.put("remarks", remarks);
            reqEntity.put("kmstravel", Integer.valueOf(kmsTravelled));
            reqEntity.put("transactionId", prefSFA.getString(getString(R.string.emp_id_key), "") + "_"
                    + Calendar.getInstance().getTimeInMillis());

            JSONArray attArr = new JSONArray();
            for (int i = 0; i < path.size(); i++) {
//                attArr.put(getStringImage(compressImage(path.get(i))));
                File compressedFile = compressImage(path.get(i));
                if (compressedFile != null) {
                    // Convert File to Bitmap
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());

                    // Now pass the Bitmap to getStringImage
                    attArr.put(getStringImage(compressedBitmap));
                }
            }

            reqEntity.put("attatchments", attArr);

        } catch (Exception e) {
            Log.e("ClaimExpense", "==" + e.getMessage());
        }


        Log.e("ClaimExpense", "  Json: " + reqEntity.toString());

        JsonObjectRequest newlyAddedRetailerRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_UPLOAD_CLAIM_EXP, reqEntity, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                loader.dismiss();
                Log.e("ClaimExpense", "TA=Response=" + response.toString());
                try {

                    //@Umesh 13-March-2022
                    if(response.getInt("status")==1)
                    {
                        path.clear();
                        imgTA.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Record Saved!!", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putBoolean(getString(R.string.is_aplied_ta_key), true);
                        editor.apply();
                        imgTA.setImageResource(R.drawable.ic_done_white_36dp);
                    }
                    else {
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader.dismiss();
                try {

                    error.printStackTrace();

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

        newlyAddedRetailerRequest.setShouldCache(false);
        newlyAddedRetailerRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(newlyAddedRetailerRequest);
        //new MyAsynchTask1().execute(from, to, kmsTravelled, totalFare, remarks);
    }

    /*public Bitmap compressImage(String filePath) {

        try {

            return new Compressor(getContext())
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(new File(filePath));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }*/

    public static File compressImage(String filePath) {
        try {
            // Load image dimensions without loading full bitmap into memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Compute the scale factor
            int inSampleSize = 1;
            while (originalWidth / inSampleSize > 640 || originalHeight / inSampleSize > 480) {
                inSampleSize *= 2;
            }

            // Load the scaled-down image
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            if (bitmap == null) return null;

            // Resize bitmap to exactly 640x480
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);

            // Create output file
            File compressedFile = new File(filePath.replace(".jpg", "_compressed.jpg"));
            FileOutputStream fileOutputStream = new FileOutputStream(compressedFile);

            // Compress image (JPEG, 75% quality)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

            // Recycle bitmaps to free memory
            bitmap.recycle();
            resizedBitmap.recycle();

            return compressedFile; // Return the compressed image file

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getStringImage(Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {

                    try {

                        final Uri uri = data.getData();
                        // Get the File path from the Uri
                        String filename = FileUtils.getPath(getContext(), uri);
                        path.add(filename);
                        TextView tvPath = new TextView(getContext());
                        tvPath.setText(filename);
                        tvPath.setTextColor(Color.GRAY);
                        llAddPath.addView(tvPath);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {

                    Log.e("New Claim", "" + requestCode + "  " + resultCode + " data: " + data);
                }
                break;
        }
    }
}
