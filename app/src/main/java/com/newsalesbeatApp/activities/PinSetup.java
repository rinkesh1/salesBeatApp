package com.newsalesbeatApp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.AnimCheckBox;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.pojo.FcmTokenManager;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.FingerprintHandler;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


/*
 * Created by MTC on 17-07-2017.
 */

public class PinSetup extends AppCompatActivity
        implements FingerprintHandler.Callback {

    private static final String DEFAULT_KEY_NAME = "default_key";
    final int MY_PERMISSIONS_REQUEST_CALL = 99;
    String TAG = "PinSetup";
    ArrayList<String> pin = new ArrayList<>();
    LinearLayout pinSetUpLayout, llFingerPrint;
    RelativeLayout rlOr;
    AnimCheckBox animCheckBox;
    SalesBeatDb salesBeatDb;
    UtilityClass utilityClass;
    ServerCall serverCall;
    int count = 0;
    //Permission required at run time
    String[] permissionsRequired = new String[]{Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE
    };
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    FingerprintManager mFingerprintManager;
    KeyStore mKeyStore = null;
    KeyGenerator mKeyGenerator = null;
    KeyguardManager mKeyguardManager;
    private SharedPreferences prefSFA, tempSfa;
    private Handler mHandler;
    private long mInterval = 100;
    private ImageView imgBackSpace, inputPin1, inputPin2, inputPin3, inputPin4, cmnyLogo, fingerPrintIcon;
    private TextView tvCmnyName, tvOne, tvTwo, tvThree, tvFour, tvFive, tvSix, tvSeven, tvEight,
            tvNine, tvZero, tvDelete, tvPageTitle, tvForgotPin;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintHandler mFingerprintHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        utilityClass = new UtilityClass(PinSetup.this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        salesBeatDb = SalesBeatDb.getHelper(this);
        serverCall = new ServerCall(this);
        mHandler = new Handler();

        /*FirebaseApp.initializeApp(this);
        Log.e(TAG, "LoginScreen");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    FcmTokenManager.setFcmToken(token);
                });
*/
        if (!prefSFA.getString("token", "").contains("Bearer")) {

            SharedPreferences.Editor editor = prefSFA.edit();
            editor.putString("token", "Bearer " + prefSFA.getString("token", ""));
            editor.apply();

        }

        initPinSetUp();

        initializeViewsClasses();

        utilityClass.initailizeAndResetPrefAndDatabase();

        //initialize listeners
        setListeners();

        tvForgotPin.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initFingerPrint() {

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        Cipher defaultCipher;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        mFingerprintHelper = new FingerprintHandler(mFingerprintManager, this, this);

        if (!mKeyguardManager.isKeyguardSecure()) {
            Toast.makeText(this,
                    "Lock screen not set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }

        createKey(DEFAULT_KEY_NAME);

        if (initCipher(defaultCipher, DEFAULT_KEY_NAME)) {
            mCryptoObject = new FingerprintManager.CryptoObject(defaultCipher);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey(String keyName) {
        try {
            mKeyStore.load(null);


            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();

        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);


            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            Toast.makeText(this, "Keys are invalidated after created. Retry the purchase\n"
                            + e.getMessage(),
                    Toast.LENGTH_LONG).show();

            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            Toast.makeText(this, "Failed to init cipher", Toast.LENGTH_LONG).show();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onAuthenticated(boolean b) {
        if (b) {

            new DelayTask().execute();


        } else
            Toast.makeText(this, "Auth failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String s) {
//        Log.e(TAG,"--111 ->"+s);
//        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHelp(String s) {
        Toast.makeText(this, "Auth help message:" + s, Toast.LENGTH_LONG).show();
    }

    private void initPinSetUp() {

        setContentView(R.layout.pin_set_up_activity);
        pinSetUpLayout = findViewById(R.id.pinSetUpLayout);
        llFingerPrint = findViewById(R.id.llFingerPrint);
        rlOr = findViewById(R.id.rlOr);
        cmnyLogo = findViewById(R.id.cmnyLogo);
        inputPin1 = findViewById(R.id.inputPin1);
        inputPin2 = findViewById(R.id.inputPin2);
        inputPin3 = findViewById(R.id.inputPin3);
        inputPin4 = findViewById(R.id.inputPin4);
        fingerPrintIcon = findViewById(R.id.icon);
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
        tvPageTitle = findViewById(R.id.pageTitle);
        tvForgotPin = findViewById(R.id.tvForgotPin);
        animCheckBox = findViewById(R.id.animCheckBox);

        // Check if we're running on Android 6.0 (M) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)

            try {

                mKeyguardManager = getSystemService(KeyguardManager.class);
                mFingerprintManager = getSystemService(FingerprintManager.class);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if (mFingerprintManager != null && !mFingerprintManager.isHardwareDetected()) {
                    // Device doesn't support fingerprint authentication
                    llFingerPrint.setVisibility(View.GONE);
                    rlOr.setVisibility(View.GONE);

                } else {
                    // Everything is ready for fingerprint authentication
                    int savedPin = prefSFA.getInt(getString(R.string.saved_pin_key), 0);
                    if (savedPin == 0) {
                        llFingerPrint.setVisibility(View.GONE);
                        rlOr.setVisibility(View.GONE);
                    } else {

                        llFingerPrint.setVisibility(View.VISIBLE);
                        rlOr.setVisibility(View.VISIBLE);
                        initFingerPrint();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean flag1 = false, flag2 = true;
        for (int i = 0; i < grantResults.length; i++) {

            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                flag1 = true;
            } else {

                flag2 = false;
            }
        }

        if (flag1 && flag2) {

            initializeViewsClasses();

            utilityClass.initailizeAndResetPrefAndDatabase();
            //initialize listeners
            setListeners();
        } else {

            Toast.makeText(this, "All permission required", Toast.LENGTH_SHORT).show();
            finishAffinity();
        }

    }


    public void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResume() {
        super.onResume();
        try {
            //register receiver
            registerReceiver(receiver, intentFilter);

            if (mCryptoObject != null) {
                mFingerprintHelper.startAuthentication(mFingerprintManager, mCryptoObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();
        try {

            unregisterReceiver(receiver);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mFingerprintHelper.stopListening();
        } catch (NoClassDefFoundError | Exception error) {
            error.printStackTrace();
        }
    }

    public void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();

    }

    private void initializeViewsClasses() {

        String cmnyName = prefSFA.getString("cmny_name", "");
        String cmnyLogoUrl = prefSFA.getString("logo_url", "");

        tvCmnyName.setText(cmnyName);

        cmnyLogo.setImageURI(Uri.parse(cmnyLogoUrl));

        if (!prefSFA.getBoolean("login", false)) {

            tvForgotPin.setVisibility(View.VISIBLE);

        } else {

            tvPageTitle.setText("Please Enter Pin");
        }
    }

    private void setListeners() {

        tvForgotPin.setVisibility(View.VISIBLE);

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

        tvForgotPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showConfirmationDialog();
            }
        });
    }

    private void showConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PinSetup.this);
        builder.setTitle(getString(R.string.alert));
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //salesBeatDb.getMyDB();
                SharedPreferences.Editor editor = prefSFA.edit();
                editor.remove(getString(R.string.saved_pin_key));
                editor.putBoolean(getString(R.string.is_logged_in), false);
                editor.apply();

                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    intent = new Intent(PinSetup.this, SplashScreen.class);
                }
                startActivity(intent);
                finishAffinity();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

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

    private void insertPin(TextView tvNumber) {

        if (pin.size() < 4) {

            pin.add(tvNumber.getText().toString());

            if (pin.size() == 1) {
                inputPin1.setBackgroundResource(R.drawable.circle_white);
            } else if (pin.size() == 2) {
                inputPin2.setBackgroundResource(R.drawable.circle_white);
            } else if (pin.size() == 3) {
                inputPin3.setBackgroundResource(R.drawable.circle_white);
            } else if (pin.size() == 4) {

                inputPin4.setBackgroundResource(R.drawable.circle_white);
                //validate pin
                checkForPin(pin.get(0), pin.get(1), pin.get(2), pin.get(3));
            }
        }
    }

    private void checkForPin(String p1, String p2, String p3, String p4) {

        String pinStr = p1.concat(p2).concat(p3).concat(p4);
        int intPin = Integer.parseInt(pinStr);

        new CheckForPin(intPin).execute();
    }

    public void clearPin() {

        pin.clear();
        inputPin1.setBackgroundResource(R.drawable.white_circle);
        inputPin2.setBackgroundResource(R.drawable.white_circle);
        inputPin3.setBackgroundResource(R.drawable.white_circle);
        inputPin4.setBackgroundResource(R.drawable.white_circle);

    }

    public void onBackPressed() {

        PinSetup.this.finishAffinity();

    }

    private int checkLogIn(int intPin) {

        int savedPin = prefSFA.getInt(getString(R.string.saved_pin_key), 0);

        if (savedPin == intPin) {

            //setting user log in status.....................
            if (!prefSFA.getBoolean(getString(R.string.is_logged_in), false)) {

                SharedPreferences.Editor editor = prefSFA.edit();
                editor.putBoolean(getString(R.string.is_logged_in), true);
                editor.apply();
            }

            return 1;

        } else if (savedPin == 0) {

            SharedPreferences.Editor editor = prefSFA.edit();
            editor.putInt(getString(R.string.saved_pin_key), intPin);
            editor.apply();

            return 2;

        }

        return 0;

    }

    private void stopRecursive() {

        if (mHandler != null) {

            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void stepRecursive() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run check: "+count);
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

        int pin;

        public CheckForPin(int intPin) {
            this.pin = intPin;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected Integer doInBackground(Void... voids) {

            int pinVal = 0;
            try {

                pinVal = checkLogIn(pin);

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
        protected void onPostExecute(Integer returnVal) {
            super.onPostExecute(returnVal);

            stopRecursive();

            if (returnVal == 1) {

                new DelayTask().execute();
//                Intent intent = new Intent(PinSetup.this, MainActivity.class);
//                intent.putExtra("flag", "pin");
//                startActivity(intent);
//                PinSetup.this.finish();
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if (returnVal == 2) {

                Intent intent = new Intent(PinSetup.this, PinConfirmation.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();

            } else {

                Toast.makeText(PinSetup.this, getString(R.string.incorrectpin), Toast.LENGTH_SHORT).show();
                clearPin();

            }
        }
    }

    private class DelayTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearPin();
            animCheckBox.setVisibility(View.VISIBLE);
            fingerPrintIcon.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            animCheckBox.setChecked(true);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {

                    Intent intent = new Intent(PinSetup.this, MainActivity.class);
                    intent.putExtra("flag", "pin");
                    startActivity(intent);
                    PinSetup.this.finish();

                }
            }, 250);
        }
    }
}