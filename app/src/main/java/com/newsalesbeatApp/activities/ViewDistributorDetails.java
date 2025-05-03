package com.newsalesbeatApp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.ClaimHistoryItem;

public class ViewDistributorDetails extends AppCompatActivity implements OnMapReadyCallback {

    TextView tvFirmName, tvFirmAddress, tvCity, tvState, tvPincode, tvOwnerName, tvMobile1, tvMobile2, tvEmailId,
            tvGstin, tvFssai, tvPanNo, tvMonthlyTurnOver, tvBeatName, tvNoOfShopInBeat, tvInvestmentPlan, tvProductDivision,tvOtherBrands,
            tvWorkingSince, tvContactPersonName, tvContactPersonMobile, tvYourOpinion, tvRemarks;

    ClaimHistoryItem distributorHistoryList;

    LinearLayout llShareDistributor;

    SharedPreferences tempPref;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    double lat, lon;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.view_distributor_details);
        tvFirmName = findViewById(R.id.tvFirmNameD);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        tvFirmAddress = findViewById(R.id.tvFirmAddressD);
        tvCity = findViewById(R.id.tvTownCityD);
        tvState = findViewById(R.id.tvStateDD);
        tvPincode = findViewById(R.id.tvPincodeD);
        tvOwnerName = findViewById(R.id.tvOwnerNameD);
        tvMobile1 = findViewById(R.id.tvMobile1D);
        tvMobile2 = findViewById(R.id.tvMobile2D);
        tvEmailId = findViewById(R.id.tvEmailIdD);
        tvGstin = findViewById(R.id.tvGstinD);
        tvFssai = findViewById(R.id.tvFssaiD);
        tvPanNo = findViewById(R.id.tvPanNoD);
        tvMonthlyTurnOver = findViewById(R.id.tvMonthlyTurnoverD);
        tvBeatName = findViewById(R.id.tvBeatNameD);
        tvNoOfShopInBeat = findViewById(R.id.tvNoOfShopInBeatD);
        tvInvestmentPlan = findViewById(R.id.tvInvestmentPlanD);
        tvProductDivision = findViewById(R.id.tvProductDivisionD);
        tvOtherBrands = findViewById(R.id.tvOtherBrands);
        tvWorkingSince = findViewById(R.id.tvWorkingSinceD);
        tvContactPersonName = findViewById(R.id.tvOtherContactPersonNameD);
        tvContactPersonMobile = findViewById(R.id.tvOtherContactPersonMobileD);
        tvYourOpinion = findViewById(R.id.tvYourOpinionDistributor);
        tvRemarks = findViewById(R.id.tvRemarksDD);
        llShareDistributor = findViewById(R.id.llShareDistributor);

        Toolbar mToolbar = findViewById(R.id.toolbar3);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        setSupportActionBar(mToolbar);

        tvPageTitle.setText("View Distributor Details");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        distributorHistoryList = (ClaimHistoryItem) getIntent().getSerializableExtra("data");
        String imgLatLong = getIntent().getStringExtra("imgLat");
        Log.d("TAG", "show more getLatlong: "+imgLatLong);
        Log.d("TAG", "show more history: "+new Gson().toJson(distributorHistoryList));

        if (imgLatLong != null && !imgLatLong.isEmpty()) {
            String[] latLong = imgLatLong.split(",");

            if (latLong.length == 2 && !latLong[0].isEmpty() && !latLong[1].isEmpty()) {
                String latitude = latLong[0].trim();
                String longitude = latLong[1].trim();

                lat = Double.parseDouble(latitude);
                lon = Double.parseDouble(longitude);

            }
        }


        if (distributorHistoryList.getFirmName() != null
                && !distributorHistoryList.getFirmName().equalsIgnoreCase("null")
                && !distributorHistoryList.getFirmName().isEmpty()) {

            tvFirmName.setText(distributorHistoryList.getFirmName());
        }

        if (distributorHistoryList.getFirmAddress() != null
                && !distributorHistoryList.getFirmAddress().equalsIgnoreCase("null")
                && !distributorHistoryList.getFirmAddress().isEmpty()) {

            tvFirmAddress.setText(distributorHistoryList.getFirmAddress());
        }

        if (distributorHistoryList.getCity() != null
                && !distributorHistoryList.getCity().equalsIgnoreCase("null")
                && !distributorHistoryList.getCity().isEmpty()) {

            tvCity.setText(distributorHistoryList.getCity());
        }

        if (distributorHistoryList.getState() != null
                && !distributorHistoryList.getState().equalsIgnoreCase("null")
                && !distributorHistoryList.getState().isEmpty()) {

            tvState.setText(distributorHistoryList.getState());
        }

        if (distributorHistoryList.getPin() != null
                && !distributorHistoryList.getPin().equalsIgnoreCase("null")
                && !distributorHistoryList.getPin().isEmpty()) {

            tvPincode.setText(distributorHistoryList.getPin());
        }

        if (distributorHistoryList.getOwnerName() != null
                && !distributorHistoryList.getOwnerName().equalsIgnoreCase("null")
                && !distributorHistoryList.getOwnerName().isEmpty()) {

            tvOwnerName.setText(distributorHistoryList.getOwnerName());
        }

        if (distributorHistoryList.getMobile1() != null
                && !distributorHistoryList.getMobile1().equalsIgnoreCase("null")
                && !distributorHistoryList.getMobile1().isEmpty()) {

            tvMobile1.setText(distributorHistoryList.getMobile1());
        }

        if (distributorHistoryList.getMobile2() != null
                && !distributorHistoryList.getMobile2().equalsIgnoreCase("null")
                && !distributorHistoryList.getMobile2().isEmpty()) {

            tvMobile2.setText(distributorHistoryList.getMobile2());
        }

        if (distributorHistoryList.getOwnerEmail() != null
                && !distributorHistoryList.getOwnerEmail().equalsIgnoreCase("null")
                && !distributorHistoryList.getOwnerEmail().isEmpty()) {

            tvEmailId.setText(distributorHistoryList.getOwnerEmail());
        }

        if (distributorHistoryList.getGstin() != null
                && !distributorHistoryList.getGstin().equalsIgnoreCase("null")
                && !distributorHistoryList.getGstin().isEmpty()) {

            tvGstin.setText(distributorHistoryList.getGstin());
        }

        if (distributorHistoryList.getFssai() != null
                && !distributorHistoryList.getFssai().equalsIgnoreCase("null")
                && !distributorHistoryList.getFssai().isEmpty()) {

            tvFssai.setText(distributorHistoryList.getFssai());
        }

        if (distributorHistoryList.getPan() != null
                && !distributorHistoryList.getPan().equalsIgnoreCase("null")
                && !distributorHistoryList.getPan().isEmpty()) {

            tvPanNo.setText(distributorHistoryList.getPan());
        }

        if (distributorHistoryList.getMonthlyTurnOver() != null
                && !distributorHistoryList.getMonthlyTurnOver().equalsIgnoreCase("null")
                && !distributorHistoryList.getMonthlyTurnOver().isEmpty()) {

            tvMonthlyTurnOver.setText(distributorHistoryList.getMonthlyTurnOver());
        }


        if (distributorHistoryList.getBeat1() != null
                && !distributorHistoryList.getBeat1().contains("null")
                && !distributorHistoryList.getBeat1().isEmpty()) {

            tvBeatName.setText(distributorHistoryList.getBeat1());
        }

        if (distributorHistoryList.getNoOfShop() != null
                && !distributorHistoryList.getNoOfShop().equalsIgnoreCase("null")
                && !distributorHistoryList.getNoOfShop().isEmpty()) {

            tvNoOfShopInBeat.setText(distributorHistoryList.getNoOfShop());
        }


        if (distributorHistoryList.getInvestmentPlan() != null
                && !distributorHistoryList.getInvestmentPlan().equalsIgnoreCase("null")
                && !distributorHistoryList.getInvestmentPlan().isEmpty()) {

            tvInvestmentPlan.setText(distributorHistoryList.getInvestmentPlan());
        }

        if (distributorHistoryList.getProduct() != null
                && !distributorHistoryList.getProduct().contains("null")
                && !distributorHistoryList.getProduct().isEmpty()) {

            tvProductDivision.setText(distributorHistoryList.getProduct());
        }

        if (distributorHistoryList.getOtherBrands() != null
                && !distributorHistoryList.getOtherBrands().contains("null")
                && !distributorHistoryList.getOtherBrands().isEmpty()) {

            tvOtherBrands.setText(distributorHistoryList.getOtherBrands());
        }

        if (distributorHistoryList.getWorkingSince() != null
                && !distributorHistoryList.getWorkingSince().equalsIgnoreCase("null")
                && !distributorHistoryList.getWorkingSince().isEmpty()) {

            tvWorkingSince.setText(distributorHistoryList.getWorkingSince());
        }

        if (distributorHistoryList.getOtherPerson() != null
                && !distributorHistoryList.getOtherPerson().equalsIgnoreCase("null")
                && !distributorHistoryList.getOtherPerson().isEmpty()) {

            tvContactPersonName.setText(distributorHistoryList.getOtherPerson());
        }

        if (distributorHistoryList.getOtherPersonMob() != null
                && !distributorHistoryList.getOtherPersonMob().equalsIgnoreCase("null")
                && !distributorHistoryList.getOtherPersonMob().isEmpty()) {

            tvContactPersonMobile.setText(distributorHistoryList.getOtherPersonMob());
        }


        if (distributorHistoryList.getOpDis() != null
                && !distributorHistoryList.getOpDis().equalsIgnoreCase("null")
                && !distributorHistoryList.getOpDis().isEmpty()) {

            tvYourOpinion.setText(distributorHistoryList.getOpDis());
        }

        if (distributorHistoryList.getRemarks() != null
                && !distributorHistoryList.getRemarks().equalsIgnoreCase("null")
                && !distributorHistoryList.getRemarks().isEmpty()) {

            tvRemarks.setText(distributorHistoryList.getRemarks());
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = tempPref.edit();
                editor.putBoolean("Flag", true);
                editor.apply();

                ViewDistributorDetails.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });


        llShareDistributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDetails();
            }
        });

    }

    public void onBackPressed() {

        SharedPreferences.Editor editor = tempPref.edit();
        editor.putBoolean("Flag", true);
        editor.apply();

        ViewDistributorDetails.this.finish();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void shareDetails() {

        String text = "";
        text = text.concat("Distributor Name : " + tvFirmName.getText().toString());
        text = text.concat("\n");
        text = text.concat("Distributor Address : " + tvFirmAddress.getText().toString());
        text = text.concat("\n");
        text = text.concat("City : " + tvCity.getText().toString());
        text = text.concat("\n");
        text = text.concat("State : " + tvState.getText().toString());
        text = text.concat("\n");
        text = text.concat("Pincode : " + tvPincode.getText().toString());
        text = text.concat("\n");
        text = text.concat("Owner Name : " + tvOwnerName.getText().toString());
        text = text.concat("\n");
        text = text.concat("Owner Mobile No.: " + tvMobile1.getText().toString());
        text = text.concat("\n");
        text = text.concat("Distributor Email id : " + tvEmailId.getText().toString());
        text = text.concat("\n");
        text = text.concat("Distributor GSTIN : " + tvGstin.getText().toString());
        text = text.concat("\n");
        text = text.concat("Distributor FSSAI : " + tvFssai.getText().toString());
        text = text.concat("\n");
        text = text.concat("Distributor PAN : " + tvPanNo.getText().toString());
        text = text.concat("\n");
//        text = text.concat("Monthly Turn Over : " + tvMonthlyTurnOver.getText().toString());
//        text = text.concat("\n");
        text = text.concat("Beat Name : " + tvBeatName.getText().toString());
        text = text.concat("\n");
//        text = text.concat("No. of shop in a beat : " + tvNoOfShopInBeat.getText().toString());
//        text = text.concat("\n");
//        text = text.concat("Investment Plan : " + tvInvestmentPlan.getText().toString());
//        text = text.concat("\n");
        text = text.concat("Product Division : " + tvProductDivision.getText().toString());
        text = text.concat("\n");
//        text = text.concat("Working Since : " + tvWorkingSince.getText().toString());
//        text = text.concat("\n");
        text = text.concat("Other Contact Person Name : " + tvContactPersonName.getText().toString());
        text = text.concat("\n");
        text = text.concat("Other Contact Person Mobile : " + tvContactPersonMobile.getText().toString());
        text = text.concat("\n");
        text = text.concat("Opinion About Distributor : " + tvYourOpinion.getText().toString());
        text = text.concat("\n");
        text = text.concat("Comments : " + tvRemarks.getText().toString());
        text = text.concat("\n");

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Distributor");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share Distributor"));

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get the current location
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
//                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng currentLocation = new LatLng(lat, lon);
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                } else {
                    Toast.makeText(getApplication(), "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, reload the map
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
