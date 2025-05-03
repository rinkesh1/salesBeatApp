package com.newsalesbeatApp.fragments;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.AddDistributor;
import com.newsalesbeatApp.adapters.ChipAdapter;
import com.newsalesbeatApp.services.TempService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/*
 * Created by MTC on 23-08-2017.
 */

public class AddNewDistributor extends Fragment {

    private static final String TAG = "AddNewDistributor";
    private static final String GSTIN_MASK = "99AAAAA9999A9A9";
    private static final String PAN_MASK = "AAAAA9999A";
    private static final int CAMERA_REQUEST = 1888;
    private List<Double> ownerImageLatLong = new ArrayList<>();
    private List<Double> firmImageLatLong = new ArrayList<>();
    private ArrayList<String> newDistributorDetails = new ArrayList<>();
    private String address = "", city = "", state = "", pincode = "", locality = "",
            beatN = "", ownerImagePath = "", shopImagePath = "", strOpinionAboutDistributor = "";
    private ProgressDialog progressDialog;
    private int counter = 0;
    private double latitude, longitude;
    private ChipGroup chipGroup;

    private EditText edtNameOfCmny, edtCmnyAddress, edtPinode, edtCity, edtState,
            edtOwnerEmail, edtGstin, edtFsiNumber, edtPanNumber, edtBeat1, edtRemarkCmnt, edtOtherBrand;

    private EditText edtOwnerName, edtMobileNumber1, edtMobileNumber2, edtOtherPrsnName, edtOtherPrsnMobileNumber;

    private ImageView imgDoneGstin, imgDoneFsin, imgDonePanNo, imgTakePic1, imgTakePic2, imgPreview1, imgPreview2, imgAdd;

    private TextView tvTimeStamp1, tvTimeStamp2, tvCurrentLocP2, tvCurrentLocP1;

    private RadioButton rdBest1, rdGood1, rdAverage1, rdFresher1;
    private GridView gridView;

    private AppCompatButton btnAddDistributor;
    private ScrollView myScrollView;

    private CheckBox chbItem1, chbItem2, chbItem3, chbItem4;

    private ArrayList<String> stringList;
    private ChipAdapter adapter;
    private CountDownTimer countDownTimer;
    private static final long MAX_TIME_MS = 180000;

    //SharedPreferences myPref;

    private SalesBeatDb salesBeatDb;

    private List<String> listProductDivision = new ArrayList<>();
    private Button recordButton;
    private Button playButton;
    private ImageView img_delete;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private TextView tvTimer,tvTimerString;
    private String filePath;

    private static final int REQUEST_PERMISSION_CODE = 1001;
    private static final String LOG_TAG = "AudioRecorder";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classInitialization();

        startService();

        setViewListeners();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.new_distributor_search, parent, false);
        //myPref = getContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);

        filePath = getActivity().getExternalFilesDir(null).getAbsolutePath() + "/recorded_audio.m4a";
//        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VoiceRecordings");
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        filePath = directory + "/recorded_audio.mp3";
        Log.d("TAG", "filePath : " + filePath);

        Uri fileUri = getSavedAudioFileUri();

        if (fileUri != null) {
            ContentResolver resolver = getActivity().getContentResolver();
            int deletedRows = resolver.delete(fileUri, null, null);
        }

        findViewById(view);

        return view;
    }


    private void startService() {

        SampleResultReceiver resultReceiever = new SampleResultReceiver(new Handler());
        //start service to download data
        Intent startIntent = new Intent(getContext(), TempService.class);
        startIntent.putExtra("receiver", resultReceiever);
        requireActivity().startService(startIntent);

    }

    private void setViewListeners() {

        imgTakePic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "Click Image-1");
                try {

                    counter = 1;
                    dispatchTakePictureIntent(counter);

                } catch (Exception e) {
                    Log.d("TAG", "Camera Exception: " + e.getMessage());
                    //Toast.makeText(getContext(), "Start camera first", Toast.LENGTH_SHORT).show();
                }


            }
        });

        imgTakePic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    counter = 2;
                    dispatchTakePictureIntent(counter);

                } catch (Exception e) {
                    //Toast.makeText(getContext(), "Start camera first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        myScrollView.fullScroll(ScrollView.FOCUS_UP);

        btnAddDistributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String base64Audio = "";

                Uri fileUri = getSavedAudioFileUri();

                if (fileUri != null) {
                    File file = getFileFromUri(fileUri); // Convert URI to File

                    if (file != null && file.exists()) {
                        uploadFile(file);
                    } else {
                        Toast.makeText(getActivity(), "Recording not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Recording not found.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        edtMobileNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtMobileNumber1.getText().toString().length() == 10) {
                    //imgDoneMobileOne.setVisibility(View.VISIBLE);
                } else {
                    //imgDoneMobileOne.setVisibility(View.GONE);
                }
            }
        });


        edtMobileNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtMobileNumber2.getText().toString().length() == 10) {
                    //imgDoneMobileTwo.setVisibility(View.VISIBLE);
                } else {
                    //imgDoneMobileTwo.setVisibility(View.GONE);
                }
            }
        });


        edtGstin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtGstin.getText().toString().length() == 15) {
                    imgDoneGstin.setVisibility(View.VISIBLE);
                } else {
                    imgDoneGstin.setVisibility(View.GONE);
                }
            }
        });


        edtFsiNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String text = edtFsiNumber.getText().toString();

                // Allow only numbers and ensure max 14 digits
                if (text.length() > 14) {
                    edtFsiNumber.setText(text.substring(0, 14));
                    edtFsiNumber.setSelection(14);
                    return;
                }

                // Show/hide icon based on length
                if (text.length() == 14 && isValidFssaiNumber(text)) {
                    imgDoneFsin.setVisibility(View.VISIBLE);
                } else {
                    imgDoneFsin.setVisibility(View.GONE);
                }
            }
        });

        edtPanNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtPanNumber.getText().toString().length() == 10) {
                    imgDonePanNo.setVisibility(View.VISIBLE);
                } else {
                    imgDonePanNo.setVisibility(View.GONE);
                }
            }
        });

        edtOtherPrsnMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtOtherPrsnMobileNumber.getText().toString().length() == 10) {
                    //imgDoneMobile2.setVisibility(View.VISIBLE);
                } else {
                    //imgDoneMobile2.setVisibility(View.GONE);
                }
            }
        });

        chbItem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listProductDivision.add(chbItem1.getText().toString());
                } else {
                    listProductDivision.remove(chbItem1.getText().toString());
                }
            }
        });


        chbItem2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listProductDivision.add(chbItem2.getText().toString());
                } else {
                    listProductDivision.remove(chbItem2.getText().toString());
                }
            }
        });

        chbItem3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listProductDivision.add(chbItem3.getText().toString());
                } else {
                    listProductDivision.remove(chbItem3.getText().toString());
                }
            }
        });

        chbItem4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listProductDivision.add(chbItem4.getText().toString());
                } else {
                    listProductDivision.remove(chbItem4.getText().toString());
                }
            }
        });

        rdBest1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    strOpinionAboutDistributor = rdBest1.getText().toString();
                }
            }
        });

        rdGood1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    strOpinionAboutDistributor = rdGood1.getText().toString();
                }
            }
        });

        rdAverage1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    strOpinionAboutDistributor = rdAverage1.getText().toString();
                }
            }
        });

        rdFresher1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    strOpinionAboutDistributor = rdFresher1.getText().toString();
                }
            }
        });

    }

    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                file = new File(getActivity().getCacheDir(), "temp_audio.m4a"); // Copy to cache folder
                copyFile(fd, file);
                pfd.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void copyFile(FileDescriptor sourceFD, File destFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(sourceFD);
        FileOutputStream outputStream = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }


    private boolean isValidFssaiNumber(String fssai) {
        return fssai.matches("^[0-9]{14}$"); // Only 14-digit numbers allowed
    }

    public void uploadFile(File file) {
        OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mpeg"), file);

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url("http://testsalesbeat.rungtatea.in/api/File")
                .post(requestBody)
                .addHeader("accept", "/")
                .addHeader("Content-Type", "multipart/form-data")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Mp3 Response: " + responseBody);
                    getActivity().runOnUiThread(() -> updateApiCall(responseBody));
                } else {
                    System.out.println("Error: " + response.code());
                }
            }
        });
    }

    private void updateApiCall(String base64Audio) {
        Set<String> uniqueSet = new LinkedHashSet<>(stringList);
        ArrayList<String> uniqueList = new ArrayList<>(uniqueSet);

        String sendBrandName = String.join(", ", uniqueList);
        Log.d(TAG, "get Brand List String format: " + sendBrandName);
        String cmnyName = edtNameOfCmny.getText().toString();

        if (!cmnyName.isEmpty()) {

            String cmnyAddress = edtCmnyAddress.getText().toString();
            String pincode = edtPinode.getText().toString();

            if (!cmnyAddress.isEmpty() && !pincode.isEmpty()) {

                city = edtCity.getText().toString();
                state = edtState.getText().toString();

                if (!city.isEmpty() && !state.isEmpty()) {

                    String ownerName = edtOwnerName.getText().toString();
                    String phone1 = edtMobileNumber1.getText().toString();
                    String email = edtOwnerEmail.getText().toString();
                    String phone2 = edtMobileNumber2.getText().toString();

                    if (!ownerName.isEmpty() && !phone1.isEmpty()) {

                        if (phone1.length() == 10) {

                            String gstn = edtGstin.getText().toString();
                            String fsin = edtFsiNumber.getText().toString();
                            String panNo = edtPanNumber.getText().toString();
                            boolean flag = validateValues(gstn, fsin, panNo);

                            if (flag) {

                                if (!edtBeat1.getText().toString().isEmpty())
                                    beatN = edtBeat1.getText().toString();

                                String otherContactPerson = edtOtherPrsnName.getText().toString();
                                String otherPhone = edtOtherPrsnMobileNumber.getText().toString();

                                if (otherContactPerson.isEmpty() && otherPhone.isEmpty()) {
                                    Log.e(TAG, "insert one :"+base64Audio);
                                    addDistributor(cmnyName, cmnyAddress, pincode, ownerName, phone1,
                                            email, phone2, "",
                                            "", otherContactPerson,
                                            otherPhone, gstn, fsin, panNo, "", sendBrandName, base64Audio);

                                } else {

                                    if (!otherContactPerson.isEmpty()) {

                                        if (!otherPhone.isEmpty() && otherPhone.length() == 10) {
                                            Log.e(TAG, "insert two :"+base64Audio);
                                            addDistributor(cmnyName, cmnyAddress, pincode, ownerName, phone1,
                                                    email, phone2, "", "", otherContactPerson,
                                                    otherPhone, gstn, fsin, panNo, "", sendBrandName, base64Audio);

                                        } else {
                                            Toast.makeText(getContext(), "Please enter 10 digit number", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Please enter other contact person name", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }

                        } else {
                            Toast.makeText(getContext(), "Please enter 10 digit number", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        edtOwnerName.requestFocus();
                        Toast.makeText(getContext(), "Owner Name & Mobile Mandatory", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    edtCity.requestFocus();
                    Toast.makeText(getContext(), "City & State Mandatory", Toast.LENGTH_SHORT).show();
                }

            } else {

                edtCmnyAddress.requestFocus();
                Toast.makeText(getContext(), "Company address & Pin Mandatory", Toast.LENGTH_SHORT).show();
            }

        } else {

            edtNameOfCmny.requestFocus();
            Toast.makeText(getContext(), "Company Name Mandatory", Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissLoader() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String convertFileToBase64(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytesArray = new byte[(int) file.length()];
            fileInputStream.read(bytesArray);
            String base64String = Base64.encodeToString(bytesArray, Base64.DEFAULT);
            fileInputStream.close();
            return "data:audio/mp4;base64," + base64String;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String convertFileToBase64New(String filePath) {
        File file = new File(filePath);

        // Validate the file
        if (!file.exists() || !file.isFile()) {
            System.err.println("File does not exist or is not a valid file: " + filePath);
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // Create a byte array to hold the file data
            byte[] bytesArray = new byte[(int) file.length()];

            // Read file data into the byte array
            if (fileInputStream.read(bytesArray) != -1) {
                // Convert byte array to Base64-encoded string
                String base64String = Base64.encodeToString(bytesArray, Base64.DEFAULT);

                // Prepend the required prefix for MP3 files
                return "data:audio/mpeg;base64," + base64String.trim();
            } else {
                System.err.println("Failed to read file: " + filePath);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateValues(String gstn, String fsin, String panNo) {

        if (!gstn.isEmpty() || !fsin.isEmpty() || !panNo.isEmpty()) {

            if (!gstn.isEmpty() && gstn.length() != 15) {

                Toast.makeText(getContext(), "Please enter valid gstin number", Toast.LENGTH_SHORT).show();
                return false;

            }

            if (!fsin.isEmpty() && fsin.length() != 14) {

                Toast.makeText(getContext(), "Please enter valid fssai number", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!panNo.isEmpty() && panNo.length() != 10) {

                Toast.makeText(getContext(), "Please enter valid pan number", Toast.LENGTH_SHORT).show();
                return false;

            }

        } else {

            return true;

        }

        return true;

    }

   /* private void dispatchTakePictureIntent(int counter) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {

                photoFile = createImageFile(counter);

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
                }

            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }*/

    private String currentPhotoPath;

    private void dispatchTakePictureIntent(int counter) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(counter);
                if (photoFile != null) {
                    Uri photoURI;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        photoURI = FileProvider.getUriForFile(requireContext(), "com.newsalesbeat.fileprovider", photoFile);
                    } else {
                        photoURI = Uri.fromFile(photoFile);
                    }
                    currentPhotoPath = photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile(int counter) throws IOException {
        // Create an image file name
        try {
            File image = null;
            File storageDir;
            if (counter == 1) {

                String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
                String imageFileName = "Dis_" + timeStamp;
                storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                image = File.createTempFile(imageFileName, ".jpg", storageDir);
                // Save a file: path for use with ACTION_VIEW intents
                ownerImagePath = image.getAbsolutePath();

                return image;

            } else if (counter == 2) {

                String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
                String imageFileName = "Shop_" + timeStamp;
                storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                image = File.createTempFile(imageFileName, ".jpg", storageDir);
                // Save a file: path for use with ACTION_VIEW intents
                shopImagePath = image.getAbsolutePath();

                return image;
            }
        } catch (IOException e) {
            Log.e(TAG, "createImageFile: " + e.getMessage());
        }


        return null;
    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            if (counter == 1) {

                ownerImageLatLong.clear();

                try {

                    Log.e("AddNewDist", "Owner Img :" + ownerImagePath);
                    Glide.with(requireContext())
                            .load(new File(ownerImagePath))
                            .override(200, 200)
                            .into(imgPreview1);

                    imgTakePic1.setVisibility(View.GONE);

                    ownerImageLatLong.add(latitude);
                    ownerImageLatLong.add(longitude);

                    tvCurrentLocP1.setText(address + "," + locality + " " + pincode);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
                    String date = sdf.format(Calendar.getInstance().getTime());
                    tvTimeStamp1.setText(date);
                    tvCurrentLocP1.setVisibility(View.GONE);
                    tvTimeStamp1.setVisibility(View.GONE);

                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult: " + e.getMessage());
                    e.printStackTrace();
                }

            } else if (counter == 2) {

                firmImageLatLong.clear();

                try {

                    Log.e("AddNewDist", "Shop Img :" + shopImagePath);
                    Glide.with(requireContext())
                            .load(new File(shopImagePath))
                            .override(200, 200)
                            .into(imgPreview2);

                    imgTakePic2.setVisibility(View.GONE);

                    firmImageLatLong.add(latitude);
                    firmImageLatLong.add(longitude);

                    tvCurrentLocP2.setText(address + "," + locality + " " + pincode);


                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
                    String date = sdf.format(Calendar.getInstance().getTime());
                    tvTimeStamp2.setText(date);
                    tvCurrentLocP2.setVisibility(View.GONE);
                    tvTimeStamp2.setVisibility(View.GONE);

                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

    private void classInitialization() {

        edtGstin.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false; // Prevent recursive calls

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isEditing) return;

                isEditing = true;
                String text = edtGstin.getText().toString().toUpperCase(); // Convert to uppercase
                Log.d(TAG, "onTextChanged GST: " + text);

                String masked = applyGSTINMask(text);
                edtGstin.setText(masked);
                edtGstin.setSelection(masked.length()); // Move cursor to the end

                if (!isValidGSTIN(masked)) {
                    edtGstin.setError("Invalid GSTIN format");
                }

                isEditing = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });



        edtPanNumber.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false; // Prevent recursive calls

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isEditing) return;

                isEditing = true;
                String text = edtPanNumber.getText().toString().toUpperCase(); // Convert to uppercase
                Log.d(TAG, "onTextChanged PAN: " + text);

                // Apply mask and limit to 10 characters
                String masked = applyPanMask(text);
                if (masked.length() > 10) {
                    masked = masked.substring(0, 10); // Ensure max 10 characters
                }

                edtPanNumber.setText(masked);
                edtPanNumber.setSelection(masked.length()); // Move cursor safely

                if (!isValidPAN(masked)) {
                    edtPanNumber.setError("Invalid PAN format");
                }

                isEditing = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });



        UtilityClass utilityClass = new UtilityClass(requireContext());
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(requireContext());
//        locationProvider = new GPSLocation(getContext());


//        String address = locationProvider.getAddressLine();
//        String district = locationProvider.getLocality();
//        String locality = locationProvider.getSubLocality();
//        String pincode = locationProvider.getPostalCode();
//        String state = locationProvider.getState();

//        edtCmnyAddress.setText(address);
//        edtCity.setText(locality);
//        edtPinode.setText(pincode);
//        edtState.setText(state);

        //cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    private boolean isValidPAN(String pan) {
        String panPattern = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";
        return pan.length() == 10 && pan.matches(panPattern);
    }

    private boolean isValidGSTIN(String gstin) {
        String gstinPattern = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$";
        return gstin.length() == 15 && gstin.matches(gstinPattern);
    }

    private String applyPanMask(String input) {
        return input.replaceAll("[^A-Z0-9]", ""); // Remove special characters
    }

    private String applyGSTINMask(String input) {
        return input.replaceAll("([A-Za-z]{2})(\\d{4})(\\d{4})([A-Za-z]{1})(\\d{1})", "$1-$2-$3-$4-$5");
    }

    private void findViewById(View view) {

        myScrollView = view.findViewById(R.id.myScrollView);
        //llViewContainer = view.findViewById(R.id.llViewContainer);
        edtNameOfCmny = view.findViewById(R.id.edtNameOfCmny);
        edtCmnyAddress = view.findViewById(R.id.edtCmnyAddress);
        edtPinode = view.findViewById(R.id.edtPincode);
        edtCity = view.findViewById(R.id.edtCity);
        edtState = view.findViewById(R.id.edtState);
        edtOwnerName = view.findViewById(R.id.edtOwnerName);
        edtMobileNumber1 = view.findViewById(R.id.edtMobileNumber1);
        edtMobileNumber2 = view.findViewById(R.id.edtMobileNumber2);
        edtOwnerEmail = view.findViewById(R.id.edtOwnerEmail);
        edtGstin = view.findViewById(R.id.edtGstin);
        edtFsiNumber = view.findViewById(R.id.edtFsiNumber);
        edtPanNumber = view.findViewById(R.id.edtPanNumber);
        edtBeat1 = view.findViewById(R.id.edtBeat1);
        //edtBeat2 = view.findViewById(R.id.edtBeat2);
        edtOtherPrsnName = view.findViewById(R.id.edtOtherPrsnName);
        edtOtherPrsnMobileNumber = view.findViewById(R.id.edtOtherPrsnMobileNumber);
        edtRemarkCmnt = view.findViewById(R.id.edtRemarkCmnt);
        tvTimeStamp1 = view.findViewById(R.id.tvTimeStamp1);
        tvTimeStamp2 = view.findViewById(R.id.tvTimeStamp2);
        tvCurrentLocP1 = view.findViewById(R.id.tvTimeCurrentLoc1);
        tvCurrentLocP2 = view.findViewById(R.id.tvTimeCurrentLoc2);
        //btnAddMore = view.findViewById(R.id.btnAddMore);
        btnAddDistributor = view.findViewById(R.id.btnAddNewDistributor);
        imgTakePic1 = view.findViewById(R.id.imgTakePic1);
        imgPreview1 = view.findViewById(R.id.imgPreview1);
        imgTakePic2 = view.findViewById(R.id.imgTakePic2);
        imgPreview2 = view.findViewById(R.id.imgPreview2);
        imgAdd = view.findViewById(R.id.imgAdd);
        imgDoneGstin = view.findViewById(R.id.imgDoneGstin);
        imgDoneFsin = view.findViewById(R.id.imgDoneFsin);
        imgDonePanNo = view.findViewById(R.id.imgDonePanNo);
        this.chbItem1 = view.findViewById(R.id.chbItem1);
        this.chbItem2 = view.findViewById(R.id.chbItem2);
        this.chbItem3 = view.findViewById(R.id.chbItem3);
        this.chbItem4 = view.findViewById(R.id.chbItem4);
        this.rdBest1 = view.findViewById(R.id.rdBest1);
        this.rdGood1 = view.findViewById(R.id.rdGood1);
        this.rdAverage1 = view.findViewById(R.id.rdAverage1);
        this.rdFresher1 = view.findViewById(R.id.rdFresher1);

        tvTimer = view.findViewById(R.id.tvTimer);
        tvTimerString = view.findViewById(R.id.tvTimerString);
        gridView = view.findViewById(R.id.gridView);
        edtOtherBrand = view.findViewById(R.id.edtOtherBrand);
        chipGroup = view.findViewById(R.id.chipGroup);
        recordButton = view.findViewById(R.id.record_button);
        playButton = view.findViewById(R.id.play_button);
        img_delete = view.findViewById(R.id.delete_icon);


        stringList = new ArrayList<>();
        adapter = new ChipAdapter(getActivity(), stringList);
        gridView.setAdapter(adapter);

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri fileUri = getSavedAudioFileUri();

                if (fileUri != null) {
                    ContentResolver resolver = getActivity().getContentResolver();
                    int deletedRows = resolver.delete(fileUri, null, null);

                    if (deletedRows > 0) {
                        if (isPlaying) {
                            stopPlaying();
                        }

                        tvTimer.setVisibility(View.GONE);
                        tvTimerString.setVisibility(View.GONE);
                        recordButton.setVisibility(View.VISIBLE);
                        recordButton.setText("Start Recording");
                        playButton.setVisibility(View.GONE);
                        img_delete.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
                }
            }
        });



        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Brand Name: " + edtOtherBrand.getText().toString());
                String value = edtOtherBrand.getText().toString();
                if (!value.isEmpty()) {
                    addChipToGroup(value);
                    stringList.add(value);
                    adapter.notifyDataSetChanged();
                    edtOtherBrand.setText("");
                }
            }
        });

        ((ImageView) view.findViewById(R.id.imgInfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Information");
                builder.setMessage(" Add names of brands that the distributor is working for | डिस्ट्रीब्यूटर जिन ब्रांडों के लिए काम कर रहा है, उनके नाम जोड़ें।");

                // Add an OK button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                positiveButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));

//                positiveButton.setTextColor(getResources().getColor(R.color.your_color));
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    stopPlaying();
                } else {
                    startPlaying();
                }
            }
        });
    }


    private Uri createAudioFile() {
        ContentResolver resolver = getActivity().getContentResolver();
        String folderName = "MyRecordings";
        String fileName = "recorded_audio.m4a"; // Fixed filename to always overwrite

        // Check if the file already exists
        Uri existingFileUri = null;
        String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=? AND " + MediaStore.MediaColumns.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{Environment.DIRECTORY_MUSIC + "/" + folderName, fileName};

        Cursor cursor = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.MediaColumns._ID},
                    selection,
                    selectionArgs,
                    null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            // File exists, get its URI
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            existingFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }
        if (cursor != null) cursor.close();

        // If file exists, return existing URI to overwrite
        if (existingFileUri != null) {
            return existingFileUri;
        }

        // Otherwise, create a new file
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/" + folderName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        }

        return null; // If all else fails, return null to indicate failure
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissions() {
        int recordAudio = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO);
        int storage = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return recordAudio == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || storage == PackageManager.PERMISSION_GRANTED);
    }


    private void startRecording() {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        Uri audioUri = createAudioFile();
        if (audioUri == null) {
            Toast.makeText(getActivity(), "Failed to create file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(getActivity().getContentResolver().openFileDescriptor(audioUri, "w").getFileDescriptor());

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            // UI updates
            tvTimer.setVisibility(View.VISIBLE);
            tvTimerString.setVisibility(View.VISIBLE);
            recordButton.setText("Stop Recording");
            recordButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.GONE);
            img_delete.setVisibility(View.GONE);
            startCountdownTimer();

            Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Recording failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                recordButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
                img_delete.setVisibility(View.VISIBLE);

                // Stop the countdown timer if running
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                // Notify MediaStore to make the file visible in Downloads
                notifyMediaStoreUpdate();

                Toast.makeText(getActivity(), "Recording stopped", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Recording error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startPlaying() {
        Uri audioUri = getSavedAudioFileUri();
        if (audioUri == null) {
            Toast.makeText(getActivity(), "Audio file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getActivity(), audioUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            playButton.setText("Stop Playing");
            Toast.makeText(getActivity(), "Playing started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error playing audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyMediaStoreUpdate() {
        Uri audioUri = getSavedAudioFileUri();
        if (audioUri != null) {
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(audioUri);
            getActivity().sendBroadcast(scanIntent);
        }
    }


    private void startCountdownTimer() {
        fadeInView(tvTimer); // Fade in the timer
        countDownTimer = new CountDownTimer(MAX_TIME_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) (millisUntilFinished / 1000 / 60);
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                stopRecording();
            }
        }.start();
    }

    private void fadeInView(View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(300); // Animation duration
        fadeIn.start();
    }

    private Uri getSavedAudioFileUri() {
        ContentResolver resolver = getActivity().getContentResolver();
        String folderName = "MyRecordings";
        String fileName = "recorded_audio.m4a"; // Fixed filename

        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // Correct MediaStore collection

        // Correct RELATIVE_PATH Query
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{fileName, Environment.DIRECTORY_MUSIC + "/" + folderName + "/"};

        Log.d("TAG", "getSavedAudioFileUri: ");
        Cursor cursor = resolver.query(collection,
                new String[]{MediaStore.MediaColumns._ID},
                selection,
                selectionArgs,
                null);

        Uri fileUri = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            fileUri = ContentUris.withAppendedId(collection, id);
        }

        if (cursor != null) cursor.close();

        return fileUri;
    }



    private void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        isPlaying = false;
        playButton.setText("Play Recording");
        img_delete.setVisibility(View.VISIBLE);
//        Toast.makeText(getActivity(), "Playing stopped", Toast.LENGTH_SHORT).show();
    }


    private void addChipToGroup(String text) {
        // Create a ContextThemeWrapper with the custom Chip style
        Context context1 = new ContextThemeWrapper(getActivity(), R.style.CustomChipStyle);

        // Create the Chip with the context
        Chip chip = new Chip(context1);
        chip.setText(text);
        chip.setCloseIconVisible(true);  // Show the close icon

        // Set a close icon (optional)
        chip.setCloseIconResource(R.drawable.ic_close); // Use a close icon drawable

        // Add the chip value to the list
        stringList.add(text); // Add chip value to the list

        // Set an OnCloseIconClickListener to remove the chip
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);  // Remove chip from ChipGroup
                stringList.remove(text);      // Remove chip value from the list
            }
        });

        // Optional: Set layout parameters if needed
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);  // Set margins around the chip
        chip.setLayoutParams(params);  // Apply layout parameters

        // Add the chip to the ChipGroup
        chipGroup.addView(chip);
    }

    private void addDistributor(String cmnyName, String cmnyAddress, String pincode, String ownerName,
                                String phone1, String email, String phone2, String investmentPlan, String workingSince,
                                String otherContactPerson, String otherPhone, String gstn, String fsin, String panNo,
                                String monthlyTurnOver, String brandName, String rec) {

        if (!strOpinionAboutDistributor.isEmpty()) {

            if (!edtRemarkCmnt.getText().toString().isEmpty()) {

                if (ownerImagePath != null && !ownerImagePath.isEmpty()) {


                    newDistributorDetails.add(cmnyName);
                    newDistributorDetails.add(cmnyAddress);
                    newDistributorDetails.add(pincode);
                    newDistributorDetails.add(city);
                    newDistributorDetails.add(state);
                    newDistributorDetails.add(ownerName);
                    newDistributorDetails.add(phone1);
                    newDistributorDetails.add(email);
                    newDistributorDetails.add(phone2);
                    newDistributorDetails.add(gstn);
                    newDistributorDetails.add(fsin);
                    newDistributorDetails.add(panNo);
                    newDistributorDetails.add(monthlyTurnOver);
                    newDistributorDetails.add(ownerImagePath);
                    newDistributorDetails.add(tvTimeStamp1.getText().toString());
                    newDistributorDetails.add("");
                    newDistributorDetails.add(investmentPlan);
                    newDistributorDetails.add(workingSince);
                    newDistributorDetails.add(otherContactPerson);
                    newDistributorDetails.add(otherPhone);
                    newDistributorDetails.add(shopImagePath);
                    newDistributorDetails.add(tvTimeStamp2.getText().toString());
                    newDistributorDetails.add(strOpinionAboutDistributor);
                    newDistributorDetails.add(edtRemarkCmnt.getText().toString());

                    Log.d(TAG, "add Distributor: " + new Gson().toJson(newDistributorDetails));
                    Log.d(TAG, "Send Recording Distributor: " + rec);
                    //insert in database
                    String tempDid = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    salesBeatDb.insertInNewDistributorTable(tempDid, newDistributorDetails,
                            /*listBeatName*/beatN, listProductDivision, ownerImageLatLong, firmImageLatLong, brandName, rec);

                    Toast.makeText(getContext(), "Distributor saved successfully ", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), AddDistributor.class);
                    intent.putExtra("tabPos", 1);
                    intent.putExtra("page_title", "New Distributor");
                    startActivity(intent);
                    AddNewDistributor.this.getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                    //AddDistributor.distributorTab.getTabAt(1).select();

                } else {

                    Toast.makeText(getContext(), "Owner Image Mandatory", Toast.LENGTH_SHORT).show();
                }

            } else {

                //edtRemarkCmnt.requestFocus();
                Toast.makeText(getContext(), "Remarks mandatory", Toast.LENGTH_SHORT).show();
            }

        } else {

            //rdBest1.requestFocus();
            Toast.makeText(getContext(), "Opinion about distributor mandatory", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (isRecording) {
            stopRecording();
        }
        if (isPlaying) {
            stopPlaying();
        }
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
                    city = resultData.getString("district");
                    pincode = resultData.getString("pincode");
                    state = resultData.getString("state");
                    latitude = resultData.getDouble("latitude");
                    longitude = resultData.getDouble("longitude");

                    if (address != null && !address.isEmpty() && !address.equalsIgnoreCase("null"))
                        edtCmnyAddress.setText(address);
                    if (locality != null && !locality.isEmpty() && !locality.equalsIgnoreCase("null"))
                        edtCity.setText(locality);
                    if (pincode != null && !pincode.isEmpty() && !pincode.equalsIgnoreCase("null"))
                        edtPinode.setText(pincode);
                    if (state != null && !state.isEmpty() && !state.equalsIgnoreCase("null"))
                        edtState.setText(state);
                    if (city != null && !city.isEmpty() && !city.equalsIgnoreCase("null"))
                        edtCity.setText(city);

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }
}
