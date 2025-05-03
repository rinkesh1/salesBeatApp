package com.newsalesbeatApp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.services.TempService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPreferredRetailerActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    static String imageTimeStamp, tempRid;
    String TAG = "AddPreferredRetailerActivity";
    UtilityClass utilityClass;
    SampleResultReceiver resultReceiever;
    File storageDir, image;
    Dialog loader;
    ArrayList<String> disDidList = new ArrayList<>();
    ArrayList<String> disNameList = new ArrayList<>();
    int floorTypeVal = 0, fixtureTypeVal = 0;
    private SharedPreferences prefSFA, tempPref;
    private GPSLocation locationProvider;
    private SalesBeatDb salesBeatDb;
    private TextView tvPageTitle;
    private Button addPreferredRetailer;
    private EditText firmName, firmContactName1, firmContactName2, contactPersonPhone1, contactPersonPhone2,
            latLong, blockEt, districtEt, avgPerDayWalk, avgCustomerIncome, otherBusiness,
            sidePaneSize, counterSize, frontBoardSize, roofHeight, openFasciaWidth,
            frontSpace, totalShelf, spaceProvided, widthOfRoad, frontCounterH, frontCounterW, frontCounterL;
    private ImageView shopImage, cameraIcon, imageBack;
    private RadioGroup proposedCategoryRg, perMonthBusinessRg, retailerDistributionRg;
    private String shopImagePath = "", proposedCategory, perMonthBusiness, retailerDistribution, internalStaffCount,
            monthlyTurnover, brandSelected, latitude, longitude, didSelected;
    private Spinner monthlyTurnoverSpinner, internalStaffSpinner, brandListSpinner,
            retailerDistributionSpinner, distributorName, floortype, fixturetype;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_preferred_retailer);

        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);


        utilityClass = new UtilityClass(AddPreferredRetailerActivity.this);
        salesBeatDb = SalesBeatDb.getHelper(this);
        locationProvider = new GPSLocation(AddPreferredRetailerActivity.this);
        locationProvider.checkGpsStatus();

        initializeViews();
        initializeSpinners();


        String from = "";
        try {

            from = getIntent().getStringExtra("from");
            if (from.equalsIgnoreCase("UpdateRet")) {
                String rid = getIntent().getStringExtra("rid");
                Cursor cursor = salesBeatDb.getRetailer(rid);
                fillValues(cursor);
            }


        } catch (Exception e) {
            from = "";
            e.printStackTrace();
        }

        if (from.isEmpty())
            startService();


        tvPageTitle.setText("Add Preferred Retailer");

    }

    private void fillValues(Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

            firmName.setText(cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_RETAILER_NAME)));
            firmContactName1.setText(cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_OWNER_NAME)));
            contactPersonPhone1.setText(cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_OWNER_PHONE)));
            latitude = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_RETAILER_LAT));
            longitude = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_RETAILER_LONG));
            latLong.setText(latitude + "," + longitude);
            blockEt.setText(cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_LOCALITY)));
            districtEt.setText(cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DISTRICT)));

        }
    }

    private void initializeSpinners() {

        intializeDistributorList();

        ArrayList<String> monthlyTurnoverSizeList = new ArrayList<>();
        monthlyTurnoverSizeList.add("Below 5 Lakhs");
        monthlyTurnoverSizeList.add("5-7 Lakhs");
        monthlyTurnoverSizeList.add("7-10 Lakhs");
        monthlyTurnoverSizeList.add("Above 10 Lakhs");

        ArrayAdapter monthlyTurnoverAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, monthlyTurnoverSizeList);
        monthlyTurnoverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthlyTurnoverSpinner.setAdapter(monthlyTurnoverAdapter);

        ArrayList<String> internalStaffSizeList = new ArrayList<>();
        internalStaffSizeList.add("1");
        internalStaffSizeList.add("2");
        internalStaffSizeList.add("3");
        internalStaffSizeList.add("4");
        internalStaffSizeList.add("5");
        internalStaffSizeList.add("6");
        internalStaffSizeList.add("7");
        internalStaffSizeList.add("8");
        internalStaffSizeList.add("9");
        internalStaffSizeList.add("10");

        ArrayList<String> brandList = new ArrayList<>();
        brandList.add("Brand 1");
        brandList.add("Brand 2");
        brandList.add("Brand 3");
        brandList.add("Brand 4");

        ArrayAdapter internalStaffAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, internalStaffSizeList);
        internalStaffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        internalStaffSpinner.setAdapter(internalStaffAdapter);


        ArrayAdapter brandAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, brandList);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandListSpinner.setAdapter(brandAdapter);

        ArrayAdapter distributionBrandAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, brandList);
        distributionBrandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        retailerDistributionSpinner.setAdapter(distributionBrandAdapter);


        ArrayList<String> fixtureTypeList = new ArrayList<>();
        fixtureTypeList.add("Wooden");
        fixtureTypeList.add("Metal");
        fixtureTypeList.add("Mix");


        ArrayAdapter fixtureTypeAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, fixtureTypeList);
        fixtureTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fixturetype.setAdapter(fixtureTypeAdapter);

        ArrayList<String> floorTypeList = new ArrayList<>();
        floorTypeList.add("Cemented");
        floorTypeList.add("Mud");
        floorTypeList.add("Tile");
        floorTypeList.add("Wooden");
        floorTypeList.add("Other");


        ArrayAdapter floorTypeAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, floorTypeList);
        floorTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floortype.setAdapter(floorTypeAdapter);

        internalStaffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                internalStaffCount = internalStaffSpinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        monthlyTurnoverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                monthlyTurnover = monthlyTurnoverSpinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        retailerDistributionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                retailerDistribution = retailerDistributionSpinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        distributorName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                didSelected = disDidList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fixturetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fixtureTypeVal = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        floortype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                floorTypeVal = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void intializeDistributorList() {

        String townName = "";
        try {
            townName = tempPref.getString(getString(R.string.town_name_key), "");
            townName = URLEncoder.encode(townName, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObjectRequest getDistListReq = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_DISTRIBUTORS_2 + "?town=" + townName, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {

                        try {

                            Log.e(TAG, " Distributor list-1: " + res);
                            //@Umesh 02-Feb-2022
                            if (res.getInt("status") == 1) {
                                JSONArray distributors = res.getJSONArray("data");
                                for (int i = 0; i < distributors.length(); i++) {


                                    JSONObject obj = (JSONObject) distributors.get(i);

                                    // JSONObject zoneObj = obj.getJSONObject("zone");
                                    disDidList.add(obj.getString("did"));
                                    disNameList.add(obj.getString("name"));

                                }

                                if (res.getInt("status") == 1) {


                                    if (distributors.length() == 0) {

                                        //no data

                                    }


                                    if (disDidList.size() > 0) {

                                        ArrayAdapter disListAdapter = new ArrayAdapter(AddPreferredRetailerActivity.this,
                                                android.R.layout.simple_spinner_dropdown_item, disNameList);
                                        disListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        distributorName.setAdapter(disListAdapter);


                                    } else {

                                        //error

                                    }


                                } else {

                                    //dist error

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, error.toString());
                if (!utilityClass.isInternetConnected()) {
                    Log.e(TAG, "Distributor list: No Internet Error");
                    //tvLoadingMsg.setText("No Internet");


                } else {
                    error.printStackTrace();
                    //error
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("authorization", prefSFA.getString("token", ""));
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(this).add(getDistListReq);
    }

    private void initializeViews() {

        tvPageTitle = findViewById(R.id.pageTitle);

        firmName = findViewById(R.id.firmNameEt);
        firmContactName1 = findViewById(R.id.firmContactPersonName1Et);
        firmContactName2 = findViewById(R.id.firmContactPersonName2Et);
        distributorName = findViewById(R.id.distributorNameEt);
        contactPersonPhone1 = findViewById(R.id.distributorContactPersonName1Et);
        contactPersonPhone2 = findViewById(R.id.distributorContactPersonName2Et);
        latLong = findViewById(R.id.firmLatLongEt);
        blockEt = findViewById(R.id.firmBlockEt);
        districtEt = findViewById(R.id.firmDistrictEt);
        avgPerDayWalk = findViewById(R.id.perDayWalkInEt);
        avgCustomerIncome = findViewById(R.id.averageIncomeCustomersEt);
        otherBusiness = findViewById(R.id.otherBusinessEt);
        sidePaneSize = findViewById(R.id.sidePanelSizeEt);
        counterSize = findViewById(R.id.counterSizeEt);
        frontBoardSize = findViewById(R.id.frontBoardSizeEt);
        roofHeight = findViewById(R.id.edtRoofHeight);
        openFasciaWidth = findViewById(R.id.edtFasciaWidth);
        frontSpace = findViewById(R.id.edtFronSpaceShop);
        totalShelf = findViewById(R.id.edtTotalShelf);
        spaceProvided = findViewById(R.id.edtSpaceProvided);
        frontCounterH = findViewById(R.id.edtFrontHeight);
        frontCounterW = findViewById(R.id.edtFrontWidth);
        frontCounterL = findViewById(R.id.edtFrontLength);
        widthOfRoad = findViewById(R.id.edtWidthRoadPassing);

        monthlyTurnoverSpinner = findViewById(R.id.monthlyTurnoverSpinner);
        internalStaffSpinner = findViewById(R.id.internalStaffNumberSpinner);
        brandListSpinner = findViewById(R.id.brandsListSpinner);
        retailerDistributionSpinner = findViewById(R.id.retailerDistributionBrandSpinner);
        fixturetype = findViewById(R.id.fixturetype);
        floortype = findViewById(R.id.floortype);

        proposedCategoryRg = findViewById(R.id.proposedCategoryRg);
        perMonthBusinessRg = findViewById(R.id.perMonthBusinessRg);
        retailerDistributionRg = findViewById(R.id.retailerDistributionRg);

        shopImage = findViewById(R.id.shopImagePreferredRetailer);
        cameraIcon = findViewById(R.id.cameraIcon);
        imageBack = findViewById(R.id.imgBack);

        addPreferredRetailer = findViewById(R.id.addPreferredRetailerButton);


        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddPreferredRetailerActivity.this, RetailerActivity.class);
                startActivity(intent);
                finish();
            }
        });


        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIcon.setImageResource(R.drawable.refresh);
                dispatchTakePictureIntent();
            }
        });


        proposedCategoryRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == R.id.diamondRadioButton) {
                    proposedCategory = ((RadioButton) findViewById(R.id.diamondRadioButton)).getText().toString();
                } else if (selectedId == R.id.goldenRadioButton) {
                    proposedCategory = ((RadioButton) findViewById(R.id.goldenRadioButton)).getText().toString();
                } else if (selectedId == R.id.silverRadioButton) {
                    proposedCategory = ((RadioButton) findViewById(R.id.silverRadioButton)).getText().toString();
                } else if (selectedId == R.id.bronzeRadioButton) {
                    proposedCategory = ((RadioButton) findViewById(R.id.bronzeRadioButton)).getText().toString();
                }

//                switch (radioGroup.getCheckedRadioButtonId()) {
//                    case R.id.diamondRadioButton:
//                        proposedCategory = ((RadioButton) findViewById(R.id.diamondRadioButton)).getText().toString();
//                        break;
//                    case R.id.goldenRadioButton:
//                        proposedCategory = ((RadioButton) findViewById(R.id.goldenRadioButton)).getText().toString();
//                        break;
//                    case R.id.silverRadioButton:
//                        proposedCategory = ((RadioButton) findViewById(R.id.silverRadioButton)).getText().toString();
//                        break;
//                    case R.id.bronzeRadioButton:
//                        proposedCategory = ((RadioButton) findViewById(R.id.bronzeRadioButton)).getText().toString();
//                        break;
//                }
            }
        });

        perMonthBusinessRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == R.id.expectedRange1RadioButton) {
                    perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange1RadioButton)).getText().toString();
                } else if (selectedId == R.id.expectedRange2RadioButton) {
                    perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange2RadioButton)).getText().toString();
                } else if (selectedId == R.id.expectedRange3RadioButton) {
                    perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange3RadioButton)).getText().toString();
                } else if (selectedId == R.id.expectedRange4RadioButton) {
                    perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange4RadioButton)).getText().toString();
                }


//                switch (radioGroup.getCheckedRadioButtonId()) {
//                    case R.id.expectedRange1RadioButton:
//                        perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange1RadioButton)).getText().toString();
//                        break;
//
//                    case R.id.expectedRange2RadioButton:
//                        perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange2RadioButton)).getText().toString();
//                        break;
//
//                    case R.id.expectedRange3RadioButton:
//                        perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange3RadioButton)).getText().toString();
//                        break;
//
//                    case R.id.expectedRange4RadioButton:
//                        perMonthBusiness = ((RadioButton) findViewById(R.id.expectedRange4RadioButton)).getText().toString();
//                        break;
//                }
            }
        });

        retailerDistributionRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == R.id.retailerDoDistributionRadioButton) {
                    retailerDistributionSpinner.setVisibility(View.VISIBLE);
                } else if (selectedId == R.id.retailerNoDistributionRadioButton) {
                    retailerDistributionSpinner.setVisibility(View.GONE);
                }

//                switch (radioGroup.getCheckedRadioButtonId()) {
//                    case R.id.retailerDoDistributionRadioButton:
//                        retailerDistributionSpinner.setVisibility(View.VISIBLE);
//                        break;
//                    case R.id.retailerNoDistributionRadioButton:
//                        retailerDistributionSpinner.setVisibility(View.GONE);
//                        break;
//                }
            }
        });

        addPreferredRetailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!firmName.getText().toString().isEmpty() && !firmContactName1.getText().toString().isEmpty() &&
                        !distributorName.toString().isEmpty() && !contactPersonPhone1.getText().toString().isEmpty() &&
                        !proposedCategory.isEmpty() && !perMonthBusiness.isEmpty() &&
                        !retailerDistribution.isEmpty() && !monthlyTurnover.isEmpty() &&
                        !internalStaffCount.isEmpty() && !shopImagePath.isEmpty()) {


                    if (contactPersonPhone1.getText().toString().length() == 10) {


                        if (contactPersonPhone2.getText().toString().isEmpty()) {

                            ArrayList<String> selectedBrands = getSelectedBrands();

                            String otherBusinessS = otherBusiness.getText().toString();
                            String roofHeightS = roofHeight.getText().toString();
                            String openFasciaWidthS = openFasciaWidth.getText().toString();
                            String frontSp = frontSpace.getText().toString();
                            String totalSf = totalShelf.getText().toString();
                            String spaceProvidedS = spaceProvided.getText().toString();
                            String roadPassingWidth = widthOfRoad.getText().toString();
                            String frontH = frontCounterH.getText().toString();
                            String frontW = frontCounterW.getText().toString();
                            String frontL = frontCounterL.getText().toString();

                            String frontM = frontH + "," + frontW + "," + frontL;


                            addNewPreferredRetailer(firmName.getText().toString(), firmContactName1.getText().toString(),
                                    firmContactName2.getText().toString(), didSelected,
                                    contactPersonPhone1.getText().toString(), contactPersonPhone2.getText().toString(),
                                    latitude, longitude, blockEt.getText().toString(), districtEt.getText().toString(),
                                    proposedCategory, perMonthBusiness, retailerDistribution, monthlyTurnover, internalStaffCount,
                                    avgPerDayWalk.getText().toString(), avgCustomerIncome.getText().toString(),
                                    otherBusinessS, shopImagePath, roofHeightS, openFasciaWidthS, fixtureTypeVal,
                                    frontSp, totalSf, spaceProvidedS, frontM, roadPassingWidth, floorTypeVal,
                                    selectedBrands.toString(), sidePaneSize.getText().toString(), counterSize.getText().toString(),
                                    frontBoardSize.getText().toString());


                        } else {

                            if (contactPersonPhone2.getText().toString().length() == 10) {

                                ArrayList<String> selectedBrands = getSelectedBrands();


                                String otherBusinessS = otherBusiness.getText().toString();
                                String roofHeightS = roofHeight.getText().toString();
                                String openFasciaWidthS = openFasciaWidth.getText().toString();
                                String frontSp = frontSpace.getText().toString();
                                String totalSf = totalShelf.getText().toString();
                                String spaceProvidedS = spaceProvided.getText().toString();
                                String roadPassingWidth = widthOfRoad.getText().toString();
                                String frontH = frontCounterH.getText().toString();
                                String frontW = frontCounterW.getText().toString();
                                String frontL = frontCounterL.getText().toString();

                                String frontM = frontH + "," + frontW + "," + frontL;


                                addNewPreferredRetailer(firmName.getText().toString(), firmContactName1.getText().toString(),
                                        firmContactName2.getText().toString(), didSelected,
                                        contactPersonPhone1.getText().toString(), contactPersonPhone2.getText().toString(),
                                        latitude, longitude, blockEt.getText().toString(), districtEt.getText().toString(),
                                        proposedCategory, perMonthBusiness, retailerDistribution, monthlyTurnover, internalStaffCount,
                                        avgPerDayWalk.getText().toString(), avgCustomerIncome.getText().toString(),
                                        otherBusinessS, shopImagePath, roofHeightS, openFasciaWidthS, fixtureTypeVal,
                                        frontSp, totalSf, spaceProvidedS, frontM, roadPassingWidth, floorTypeVal,
                                        selectedBrands.toString(), sidePaneSize.getText().toString(), counterSize.getText().toString(),
                                        frontBoardSize.getText().toString());

                            } else {

                                Toast.makeText(getBaseContext(), "Contact Person Phone2 is not valid", Toast.LENGTH_SHORT).show();

                            }

                        }

                    } else {

                        Toast.makeText(getBaseContext(), "Contact Person Phone1 is not valid", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getBaseContext(), "Mandatory field required!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private ArrayList<String> getSelectedBrands() {
        ArrayList<String> brands = new ArrayList<>();
        LinearLayout checkboxLayout = findViewById(R.id.brandsListLinearLayout);

        for (int i = 0; i < checkboxLayout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) checkboxLayout.getChildAt(i);
            if (checkBox.isChecked())
                brands.add(checkBox.getText().toString());
        }
        return brands;
    }

    private void addNewPreferredRetailer(String firmName, String firmContact1, String firmContact2, String distributorName,
                                         String distributorContactName1, String distributorContactName2, String latitude, String longitude,
                                         String block, String district, String proposedCategory, String perMonthBusiness, String retailerDistributionBrandName,
                                         String monthlyTurnover, String internalStaffCount,
                                         String avgPerDayWalk, String avgCustomerIncome, String otherBusiness, String shopImagePath,
                                         String roofHeight, String openFasciaWidth, int fixtureType, String frontSpace, String totalShelf,
                                         String spaceProvided, String frontCounterMeasurement, String widthOfRoad,
                                         int floorType, String brandPosting, String sidePaneSize, String counterSize, String frontBoardSize) {

        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, firmName, firmContact1, firmContact2, distributorName,
                distributorContactName1, distributorContactName2, latitude, longitude, block, district, proposedCategory,
                perMonthBusiness, retailerDistributionBrandName, monthlyTurnover, internalStaffCount, avgPerDayWalk,
                avgCustomerIncome, otherBusiness, shopImagePath, roofHeight, openFasciaWidth, String.valueOf(fixtureType),
                frontSpace, totalShelf, spaceProvided, frontCounterMeasurement, widthOfRoad, String.valueOf(floorType),
                brandPosting, sidePaneSize, counterSize, frontBoardSize);

    }

    private void startService() {

        resultReceiever = new SampleResultReceiver(new Handler());
        //start service to download data
        Intent startIntent = new Intent(this, TempService.class);
        startIntent.putExtra("receiver", resultReceiever);
        startService(startIntent);

    }

    private void savePreferredDataLocally(String tempRid, String[] params) {


    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
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


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir /* directory */);

        // Save a file: path for use with ACTION_VIEW intents

        shopImagePath = image.getAbsolutePath();


        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            try {

                Glide.with(this)
                        .load(new File(shopImagePath))
                        .override(200, 200)
                        .into(shopImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void orderPopUp(String name, String[] params) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String checkInTimeStamp = dateFormat.format(Calendar.getInstance().getTime());

        AlertDialog.Builder orderPopUp = new AlertDialog.Builder(this);
        orderPopUp.setTitle("Alert!");
        orderPopUp.setMessage("Do you want to take order from newly added preferred retailer?");
        orderPopUp.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                Bundle bundle = new Bundle();
                bundle.putStringArray("params", params);
//                bundle.putString("imageTime", imageTimeStamp);
//                bundle.putString("ownerImage", ownerImagePath);
                bundle.putString("shopImage1", shopImagePath);
//                bundle.putString("shopImage2", shopImagePath2);
//                bundle.putString("shopImage3", shopImagePath3);
//                bundle.putString("shopImage4", shopImagePath4);

//                Intent intent = new Intent(AddNewRetailerActivity.this, RetailerOrderActivity.class);
//                intent.putExtra("newRet", bundle);
//                intent.putExtra("nrid", tempRid);
//                intent.putExtra("Position", 0);
//                intent.putExtra("orderType", "onNewShop");
//                intent.putExtra("checkInTime", imageTimeStamp);
//                startActivity(intent);

                Intent intent = new Intent(AddPreferredRetailerActivity.this, RetailerOrderActivity.class);
                intent.putExtra("newRet", bundle);
                intent.putExtra("tabPosition", 0);
                intent.putExtra("nrid", tempRid);
                intent.putExtra("retName", name);
                intent.putExtra("orderType", getString(R.string.pretailer));
                intent.putExtra("checkInTime", checkInTimeStamp);
                startActivity(intent);
            }
        });

        orderPopUp.setNegativeButton("No", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            noOrderReason(tempRid, checkInTimeStamp, params);
        });

        orderPopUp.show();

    }

    public void noOrderReason(String tempRid, final String checkInTimeStamp, String[] params) {

        final Dialog dialog = new Dialog(AddPreferredRetailerActivity.this, R.style.DialogActivityTheme);
        dialog.setContentView(R.layout.no_order_reason_dialog);

        if (dialog.getWindow() != null)
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
                reason.clear();

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
                    Calendar cal = Calendar.getInstance();
                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                    String time2 = dateFormat.format(Calendar.getInstance().getTime());

                    String transactionId = prefSFA.getString(getString(R.string.emp_id_key), "") + "_" + String.valueOf(Calendar.getInstance().getTimeInMillis());

                    salesBeatDb.insertNewPreferredRetailer(tempRid, params[0], params[1], params[2], params[3], params[4],
                            params[5], params[6], params[7], params[8], params[9], params[10], params[11], params[12], params[13],
                            params[14], params[15], params[16], params[17], params[18], params[19], params[20], params[21], params[22], params[23],
                            params[24], params[25], params[26], params[27], params[28], params[29], params[30], params[31],
                            tempPref.getString(getString(R.string.dis_id_key), ""), tempPref.getString(getString(R.string.beat_id_key), ""),
                            date, transactionId, date);


                    salesBeatDb.entryInOderPlacedByPreferredRetailersTable(AddPreferredRetailerActivity.tempRid,
                            tempPref.getString(getString(R.string.dis_id_key), ""), time2, getString(R.string.pretailer), time2,
                            locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(),
                            reason.toString(), date, transactionId, date);

                    dialog.dismiss();

                    Intent intent = new Intent(AddPreferredRetailerActivity.this, RetailerActivity.class);
                    intent.putExtra("tabPosition", 1);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(AddPreferredRetailerActivity.this, "No reason selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();

    }

    public void onBackPressed() {

        AlertDialog.Builder backAlert = new AlertDialog.Builder(this);
        backAlert.setTitle("Alert!");
        backAlert.setMessage("If you go back ur data will be lost.Are you sure?");
        backAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //locationProvider.unregisterReceiver();
                Intent intent = new Intent(AddPreferredRetailerActivity.this, RetailerActivity.class);
                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        backAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        backAlert.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationProvider.checkGpsStatus();

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

                    String block, district;

                    // address = resultData.getString("address");
                    block = resultData.getString("locality");
                    district = resultData.getString("district");
                    latitude = Double.toString(resultData.getDouble("latitude"));
                    longitude = Double.toString(resultData.getDouble("longitude"));

                    if (latitude != null && !latitude.isEmpty() && !latitude.equalsIgnoreCase("null")
                            && longitude != null && !longitude.isEmpty() && !longitude.equalsIgnoreCase("null"))
                        latLong.setText(latitude + "," + longitude);
                    if (block != null && !block.isEmpty() && !block.equalsIgnoreCase("null"))
                        blockEt.setText(block);
                    if (district != null && !district.isEmpty() && !district.equalsIgnoreCase("null"))
                        districtEt.setText(district);

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

    private class MyAsyncTask extends AsyncTask<String, Void, Integer> {

        String[] params;

        protected void onPreExecute() {

            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            imageTimeStamp = dateFormat1.format(Calendar.getInstance().getTime());

            tempRid = String.valueOf(Calendar.getInstance().getTimeInMillis());

            loader = new Dialog(AddPreferredRetailerActivity.this, R.style.DialogActivityTheme);
            loader.setContentView(R.layout.loader);
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            loader.show();


        }

        @Override
        protected Integer doInBackground(String... params) {


            this.params = params;

            //savePreferredDataLocally(tempRid, params);

            return 1;
        }

        protected void onPostExecute(Integer I) {
            loader.dismiss();
            if (I == 1) {

                Toast.makeText(AddPreferredRetailerActivity.this, "Retailer added successfully", Toast.LENGTH_SHORT).show();

                orderPopUp(params[0], params);

                //  showOrderDialog(tempRid, imageTimeStamp, params);
            }
        }
    }
}
