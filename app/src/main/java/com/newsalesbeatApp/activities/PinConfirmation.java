package com.newsalesbeatApp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.services.DownloadDataService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

;

/*
 * Created by MTC on 01-11-2017.
 */

public class PinConfirmation extends AppCompatActivity {

    String TAG = "PinConfirmation";
    SharedPreferences prefSFA, tempSfa;
    ArrayList<String> pin = new ArrayList<>();
    LinearLayout pinSetUpLayout;
    SalesBeatDb salesBeatDb;
    UtilityClass utilityClass;
    ServerCall serverCall;
    //NetworkImageView cmnyLogo;
    ImageView imgBackSpace, inputPin1, inputPin2, inputPin3, inputPin4, cmnyLogo;
    TextView tvCmnyName, tvOne, tvTwo, tvThree, tvFour, tvFive, tvSix, tvSeven, tvEight, tvNine, tvZero, tvDelete/*,tvSpace*/;
    int count = 0;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    TextView tvGoBack;
    private Handler mHandler;
    private long mInterval = 100;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pin_confirmation);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        pinSetUpLayout = findViewById(R.id.pinSetUpLayout);
        cmnyLogo = findViewById(R.id.cmnyLogo);

        inputPin1 = findViewById(R.id.inputPin1);
        inputPin2 = findViewById(R.id.inputPin2);
        inputPin3 = findViewById(R.id.inputPin3);
        inputPin4 = findViewById(R.id.inputPin4);
        imgBackSpace = findViewById(R.id.imgBackSpace);

        tvCmnyName = findViewById(R.id.tvCmnyName);
        tvOne = findViewById(R.id.tvOne);
        tvTwo = findViewById(R.id.tvTwo);
        tvThree = findViewById(R.id.tvThree);
        tvFour = findViewById(R.id.tvFour);
        tvFive = findViewById(R.id.tvFive);
        tvSix = findViewById(R.id.tvSix);
        tvSeven = findViewById(R.id.tvSeven);
        tvEight = findViewById(R.id.tvEight);
        tvNine = findViewById(R.id.tvNine);
        tvZero = findViewById(R.id.tvZero);
        tvDelete = findViewById(R.id.tvDelete);
        tvGoBack = findViewById(R.id.tvGoBack);

        utilityClass = new UtilityClass(this);
        salesBeatDb = SalesBeatDb.getHelper(this);
        serverCall = new ServerCall(this);
        mHandler = new Handler();
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        initializeViewAndClass();
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();

        try {

//            //start service
//            startServiceToDownloadData();

            registerReceiver(receiver, intentFilter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();
        try {

            unregisterReceiver(receiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {

        tvOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvOne);

            }
        });

        tvTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvTwo);
            }
        });

        tvThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvThree);
            }
        });

        tvFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvFour);
            }
        });

        tvFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvFive);
            }
        });

        tvSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvSix);
            }
        });

        tvSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvSeven);
            }
        });

        tvEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvEight);
            }
        });

        tvNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvNine);
            }
        });

        tvZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPin(tvZero);
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPin();
            }
        });

        imgBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePin();
            }
        });

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = prefSFA.edit();
                editor.remove(getString(R.string.saved_pin_key));
                editor.apply();

                Intent intent = new Intent(PinConfirmation.this, PinSetup.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeViewAndClass() {

        String cmnyName = prefSFA.getString("cmny_name", "");
        String cmnyLogoUrl = prefSFA.getString("logo_url", "");

        tvCmnyName.setText(cmnyName);

        cmnyLogo.setImageURI(Uri.parse(cmnyLogoUrl));


    }

    private void insertPin(TextView tvnumber) {

        if (pin.size() < 4) {
            pin.add(tvnumber.getText().toString());

            if (pin.size() == 1) {
                inputPin1.setBackgroundResource(R.drawable.circle_white);
            } else if (pin.size() == 2) {
                inputPin2.setBackgroundResource(R.drawable.circle_white);
            } else if (pin.size() == 3) {
                inputPin3.setBackgroundResource(R.drawable.circle_white);
            } else if (pin.size() == 4) {

                inputPin4.setBackgroundResource(R.drawable.circle_white);

                String p1 = pin.get(0);
                String p2 = pin.get(1);
                String p3 = pin.get(2);
                String p4 = pin.get(3);

                String pinStr = p1.concat(p2).concat(p3).concat(p4);
                int intPin = Integer.parseInt(pinStr);
                verifyUser(intPin);
            }
        }
    }


    private void clearPin() {
        pin.clear();
        inputPin1.setBackgroundResource(R.drawable.white_circle);
        inputPin2.setBackgroundResource(R.drawable.white_circle);
        inputPin3.setBackgroundResource(R.drawable.white_circle);
        inputPin4.setBackgroundResource(R.drawable.white_circle);
    }

    private void removePin() {
        try {
            if (pin.size() <= 4) {

                if (pin.size() == 1) {
                    inputPin1.setBackgroundResource(R.drawable.white_circle);
                    pin.remove(0);
                } else if (pin.size() == 2) {
                    inputPin2.setBackgroundResource(R.drawable.white_circle);
                    pin.remove(1);
                } else if (pin.size() == 3) {
                    inputPin3.setBackgroundResource(R.drawable.white_circle);
                    pin.remove(2);
                } else if (pin.size() == 4) {
                    inputPin4.setBackgroundResource(R.drawable.white_circle);
                    pin.remove(3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        SharedPreferences.Editor editor = prefSFA.edit();
        editor.remove(getString(R.string.saved_pin_key));
        editor.apply();
        Intent intent = new Intent(PinConfirmation.this, PinSetup.class);
        startActivity(intent);
        finish();
    }

    private void verifyUser(int userPin) {

        new CheckForPin(userPin).execute();
    }

    private void startServiceToDownloadData() {

        if (utilityClass != null && utilityClass.isInternetConnected()) {

            //start service to download data
            Intent startIntent = new Intent(PinConfirmation.this, DownloadDataService.class);
            startIntent.putExtra("appVersion", getAppVersion());
            startService(startIntent);
        }
    }

    private void stopRecursive() {

        if (mHandler != null) {

            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private Integer checkLogIn() {

        return prefSFA.getInt(getString(R.string.saved_pin_key), 0);
    }

    private String getAppVersion() {

        try {

            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            String verCode = String.valueOf(pInfo.versionCode);

            //to check live or test
            String app = "";
            if (getString(R.string.url_mode).equalsIgnoreCase("T"))
                app = "T";
            else if (getString(R.string.url_mode).equalsIgnoreCase("L"))
                app = "L";

            return version + app + "(" + verCode + ")";

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return "";
    }

    private void stepRecursive() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (count == 0) {

                    inputPin1.setBackgroundResource(R.drawable.circle_white);
                    inputPin2.setBackgroundResource(R.drawable.white_circle);
                    inputPin3.setBackgroundResource(R.drawable.white_circle);
                    inputPin4.setBackgroundResource(R.drawable.white_circle);
                    count = count + 1;

                } else if (count == 1) {

                    inputPin1.setBackgroundResource(R.drawable.white_circle);
                    inputPin2.setBackgroundResource(R.drawable.circle_white);
                    inputPin3.setBackgroundResource(R.drawable.white_circle);
                    inputPin4.setBackgroundResource(R.drawable.white_circle);
                    count = count + 1;

                } else if (count == 2) {

                    inputPin1.setBackgroundResource(R.drawable.white_circle);
                    inputPin2.setBackgroundResource(R.drawable.white_circle);
                    inputPin3.setBackgroundResource(R.drawable.circle_white);
                    inputPin4.setBackgroundResource(R.drawable.white_circle);
                    count = count + 1;

                } else if (count == 3) {

                    inputPin1.setBackgroundResource(R.drawable.white_circle);
                    inputPin2.setBackgroundResource(R.drawable.white_circle);
                    inputPin3.setBackgroundResource(R.drawable.white_circle);
                    inputPin4.setBackgroundResource(R.drawable.circle_white);
                    count = 0;

                }

                stepRecursive();
            }
        }, mInterval);
    }

    private class CheckForPin extends AsyncTask<Void, Void, Integer> {

        int userPin;

        public CheckForPin(int userPin) {
            this.userPin = userPin;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Integer doInBackground(Void... voids) {

            //functionsCall();
            //start service
            startServiceToDownloadData();

            int pinVal = 0;
            try {

                pinVal = checkLogIn();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return pinVal;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stepRecursive();
        }

        @Override
        protected void onPostExecute(Integer savedPin) {
            super.onPostExecute(savedPin);

            stopRecursive();

            //int savedPin = prefSFA.getInt(getString(R.string.saved_pin_key), 0);
            if (savedPin == userPin) {

                //setting user log in status.....................
                if (!prefSFA.getBoolean(getString(R.string.is_logged_in), false)) {

                    SharedPreferences.Editor editor = prefSFA.edit();
                    editor.putBoolean(getString(R.string.is_logged_in), true);
                    editor.apply();
                }

                Intent intent = new Intent(PinConfirmation.this, MainActivity.class);
                intent.putExtra("pinFrom", "confirmation");
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();

            } else {

                Toast.makeText(PinConfirmation.this, getString(R.string.incorrectpin), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = prefSFA.edit();
                editor.remove(getString(R.string.saved_pin_key));
                editor.apply();
                Intent intent = new Intent(PinConfirmation.this, PinSetup.class);
                intent.putExtra("fromLogin", true);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();

            }

        }
    }
}

