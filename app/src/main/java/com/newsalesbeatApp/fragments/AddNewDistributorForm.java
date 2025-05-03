package com.newsalesbeatApp.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.JsonObject;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.AddDistributor;
import com.newsalesbeatApp.adapters.LocationViewModel;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.receivers.ConnectivityChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.services.TempService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class AddNewDistributorForm extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(AddNewDistributorForm.class);
    View view;
    private SharedPreferences myPref;
    private ClientInterface clientInterface;
    //    private LinearLayout formContainer;
    private int seconds;
    private Map<String, View> inputFields = new HashMap<>();

    private static final int REQUEST_IMAGE_SHOP = 102;
    private static final int REQUEST_IMAGE_OWNER = 103;

    private Uri shopImageUri;
    private Uri ownerImageUri;

    private ImageView shopImageView;
    private ImageView ownerImageView;

    private boolean isRecording = false;
    private boolean isPlaying = false;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private Button startRecordingButton, playButton;
    private ImageView deleteIcon;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private static final long MAX_TIME_MS = 60000;
    private static final int REQUEST_PERMISSION_CODE = 1001;
    private String address = "", city = "", state = "", pincode = "", locality = "",
            beatN = "", ownerImagePath = "", shopImagePath = "", strOpinionAboutDistributor = "";
    private double latitude, longitude;
    List<String> stringList = new ArrayList<>();
    private ArrayList<String> newDistributorDetails = new ArrayList<>();
    private List<String> listProductDivision = new ArrayList<>();
    private List<Double> ownerImageLatLong = new ArrayList<>();
    private List<Double> firmImageLatLong = new ArrayList<>();
    private Map<String, EditText> fieldMap = new HashMap<>();
    GPSLocation locationProvider;
    ChipGroup chipGroup;
    private SalesBeatDb salesBeatDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_add_new_distributor_form, container, false);
//        myPref = getActivity().getSharedPreferences(getActivity().getString(R.string.pref_name), Context.MODE_PRIVATE);
        salesBeatDb = SalesBeatDb.getHelper(requireContext());
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        Log.d("TAG", "AddNewDistributorForm :"+myPref.getString("token",""));
//        formContainer = view.findViewById(R.id.formContainer);
        locationProvider = new GPSLocation(getActivity());
        getFormResponse();

        startService();

        Log.d("TAG", "Address from service: "+latitude);

        return view;
    }

    private void startService() {
        SampleResultReceiver resultReceiever = new SampleResultReceiver(new Handler());
        //start service to download data
        Intent startIntent = new Intent(getContext(), TempService.class);
        startIntent.putExtra("receiver", resultReceiever);
        requireActivity().startService(startIntent);
    }

    private void getFormResponse() {
        clientInterface = RetrofitClient.getClient().create(ClientInterface.class);
        String strToken = myPref.getString("token", "");
        Log.d("TAG", "getSkuList Token: " + strToken);
        Call<JsonObject> jsonObjectCall = clientInterface.getDisJsonForm(strToken);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("TAG", "Response Sku List==:" + response.body().toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray fieldsArray = jsonObject.getJSONArray("fields");

                        /*String jsonString = "{"
                                + "\"formTitle\": \"New Distributor Registration\","
                                + "\"fields\": ["
                                + "{ \"id\": \"firmName\", \"label\": \"Name of Firm\", \"type\": \"text\", \"required\": true, \"validation\": { \"minLength\": 2, \"maxLength\": 100 } },"
                                + "{ \"id\": \"firmAddress\", \"label\": \"Company/Firm Address\", \"type\": \"text\", \"required\": true, \"validation\": { \"minLength\": 5, \"maxLength\": 255 } },"
                                + "{ \"id\": \"pinCode\", \"label\": \"Pincode/ZipCode\", \"type\": \"number\", \"required\": true, \"validation\": { \"pattern\": \"^[1-9][0-9]{5}$\" } },"
                                + "{ \"id\": \"city\", \"label\": \"Town/City/District\", \"type\": \"text\", \"required\": true, \"validation\": { \"minLength\": 2, \"maxLength\": 100 } },"
                                + "{ \"id\": \"state\", \"label\": \"State\", \"type\": \"text\", \"required\": true },"
                                + "{ \"id\": \"ownerName\", \"label\": \"Owner Name\", \"type\": \"text\", \"required\": true, \"validation\": { \"pattern\": \"^[A-Za-z ]{2,100}$\" } },"
                                + "{ \"id\": \"ownerMobile1\", \"label\": \"Mobile Number 1\", \"type\": \"number\", \"required\": true, \"validation\": { \"pattern\": \"^[6-9][0-9]{9}$\" } },"
                                + "{ \"id\": \"ownerMobile2\", \"label\": \"Mobile Number 2\", \"type\": \"number\", \"required\": false, \"validation\": { \"pattern\": \"^[6-9][0-9]{9}$\" } },"
                                + "{ \"id\": \"email\", \"label\": \"Owner Email\", \"type\": \"text\", \"required\": true, \"validation\": { \"pattern\": \"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$\" } },"
                                + "{ \"id\": \"gstin\", \"label\": \"GSTIN\", \"type\": \"text\", \"required\": true, \"validation\": { \"pattern\": \"^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$\" } },"
                                + "{ \"id\": \"fsi\", \"label\": \"FSSAI Reg No/Licence No\", \"type\": \"text\", \"required\": true, \"validation\": { \"pattern\": \"^[0-9]{14}$\" } },"
                                + "{ \"id\": \"pan\", \"label\": \"PAN No\", \"type\": \"text\", \"required\": true, \"validation\": { \"pattern\": \"^[A-Z]{5}[0-9]{4}[A-Z]{1}$\" } },"
                                + "{ \"id\": \"otherBrand\", \"label\": \"Other Brand\", \"type\": \"dynamicText\", \"required\": false, \"icon\": \"plus\", \"placeholder\": \"Enter brand name\" },"
                                + "{ \"id\": \"beatName\", \"label\": \"beat Name\", \"type\": \"text\", \"required\": true, \"validation\": { \"pattern\": \"^[A-Za-z ]{2,100}$\" } },"
                                + "{ \"id\": \"otherContactPersonNames\", \"label\": \"Contact Person Name\", \"type\": \"text\", \"required\": true, \"validation\": { \"pattern\": \"^[A-Za-z ]{2,100}$\" } },"
                                + "{ \"id\": \"otherContactPersonPhones\", \"label\": \"Contact Person Number\", \"type\": \"number\", \"required\": true, \"validation\": { \"pattern\": \"^[6-9][0-9]{9}$\" } },"
                                + "{ \"id\": \"productDivision\", \"label\": \"Product Division\", \"type\": \"checkbox\", \"required\": true, \"options\": [ \"Real Gold Select\", \"Real Gold\", \"Real Taste\", \"Raid\" ] },"
                                + "{ \"id\": \"opinion\", \"label\": \"Your Opinion about distributor\", \"type\": \"radio\", \"required\": true, \"options\": [ \"Best\", \"Good\", \"Average\", \"Fresher\" ] }"
                                + "]"
                                + "}";

                                JSONObject jo = new JSONObject(jsonString);
                                JSONArray fieldsArray = jo.getJSONArray("fields");
                                generateForm(fieldsArray);


                                */

                        generateForm(fieldsArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("TAG", "Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("TAG", "Failed Get Sku List " + t.getMessage());
            }
        });
    }

    @SuppressLint("ResourceType")
    private void generateForm(JSONArray fieldsArray) {
        LinearLayout formLayout = view.findViewById(R.id.formContainer);
        formLayout.removeAllViews(); // Clear old views
        int fieldIndex = 1;

        for (int i = 0; i < fieldsArray.length(); i++) {
            try {
                JSONObject field = fieldsArray.getJSONObject(i);
                String fieldType = field.getString("type");
                String fieldId = field.getString("id");
                String label = field.getString("label");
                boolean isRequired = field.optBoolean("required", false);

                // Create Label (Heading)
                TextView textView = new TextView(getActivity());
                textView.setText(label);
                textView.setTextSize(16);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setPadding(0, 10, 0, 5);
                formLayout.addView(textView);

                if (fieldType.equals("text") || fieldType.equals("number")) {
                    EditText editText = new EditText(getActivity());
                    editText.setHint(label);
                    editText.setTextSize(16);
                    editText.setTextColor(Color.BLACK);
                    editText.setHintTextColor(Color.GRAY);
                    editText.setPadding(20, 20, 20, 20);
                    editText.setBackgroundResource(R.drawable.edit_text_bg);
                    editText.setTag(fieldId);

                    if (fieldType.equals("number")) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }

                    fieldMap.put(fieldId, editText);

                    if (field.has("validation")) {
                        JSONObject validation = field.getJSONObject("validation");
                        int maxLength = validation.optInt("maxLength", Integer.MAX_VALUE);
                        String pattern = validation.optString("pattern", "");

                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

                        if (!pattern.isEmpty()) {
                            addDynamicValidation(editText, pattern);
                        }
                    }

                    if (fieldId.equalsIgnoreCase("pan")) {
                        addPANValidation(editText);
                    } else if (fieldId.equalsIgnoreCase("gstin")) {
                        addGSTINValidation(editText);
                    }

                    if (isRequired) {
                        editText.setTag(R.id.required, true);
                    }
                    formLayout.addView(editText);

                }else
                    if (fieldType.equals("dynamicText")) {
                    LinearLayout brandSectionLayout = new LinearLayout(getActivity());
                    brandSectionLayout.setOrientation(LinearLayout.VERTICAL);
                    brandSectionLayout.setPadding(10, 10, 10, 10);

                    // FrameLayout to overlay icon inside EditText
                    FrameLayout inputContainer = new FrameLayout(getActivity());

                    // Brand EditText
                    EditText brandEditText = new EditText(getActivity());
                    brandEditText.setHint("Enter brand name");
                    brandEditText.setTextSize(16);
                    brandEditText.setTextColor(Color.BLACK);
                    brandEditText.setHintTextColor(Color.GRAY);
                    brandEditText.setPadding(20, 20, 60, 20); // Right padding to avoid overlap with icon
                    brandEditText.setBackgroundResource(R.drawable.edit_text_bg);
                    brandEditText.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    ));
                    brandEditText.setTag(fieldId);

                    ImageView addIcon = new ImageView(getActivity());
                    addIcon.setImageResource(R.drawable.ic_add);
                    addIcon.setBackground(getRectDrawable("#3F51B5"));
                    addIcon.setClickable(true);
                    addIcon.setFocusable(true);
                    addIcon.setPadding(0, 0, 0, 0);
                    addIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                    FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(
                            iconSize, iconSize, Gravity.END | Gravity.CENTER_VERTICAL);
                    iconParams.setMargins(0, 0, 10, 0);
                    addIcon.setLayoutParams(iconParams);

                    inputContainer.setClipChildren(true);
                    inputContainer.setClipToPadding(true);

                    // Add views to FrameLayout
                    inputContainer.addView(brandEditText);
                    inputContainer.addView(addIcon);

                    // Add FrameLayout to parent
                    brandSectionLayout.addView(inputContainer);

                    // ChipGroup for added brands
                    chipGroup = new ChipGroup(getActivity());
                    chipGroup.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    chipGroup.setChipSpacing(10);
                    chipGroup.setPadding(0, 10, 0, 10);
                    brandSectionLayout.addView(chipGroup);

                    formLayout.addView(brandSectionLayout);

                    // Add brand logic
                    addIcon.setOnClickListener(v -> {
                        String brandName = brandEditText.getText().toString().trim();

                        if (!brandName.isEmpty()) {
                            if (stringList.contains(brandName)) {
                                Toast.makeText(getActivity(), "Brand already added!", Toast.LENGTH_SHORT).show();
                            } else {
                                stringList.add(brandName);
                                addChipToGroup(brandName);
                                brandEditText.setText("");
                            }
                        }
                    });
                } else
                    if (fieldType.equals("checkbox")) {
                        JSONArray options = field.getJSONArray("options");
                        int itemsPerRow = 2;
                        LinearLayout rowLayout = null;

                        for (int j = 0; j < options.length(); j++) {
                            JSONObject optionObj = options.getJSONObject(j);
                            String product = optionObj.getString("product");
                            String imageBase64 = optionObj.optString("image");

                            if (j % itemsPerRow == 0) {
                                rowLayout = new LinearLayout(getActivity());
                                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));
                                rowLayout.setPadding(0, 8, 0, 8);
                                formLayout.addView(rowLayout);
                            }

                            // Parent layout for one item
                            LinearLayout itemLayout = new LinearLayout(getActivity());
                            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                            itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                            itemLayout.setPadding(16, 16, 16, 16);
                            itemLayout.setBackgroundResource(R.drawable.checkbox_item_bg); // Optional background

                            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1f
                            );
                            itemParams.setMargins(8, 8, 8, 8);
                            itemLayout.setLayoutParams(itemParams);

                            // ImageView for product image
                            ImageView productImage = new ImageView(getActivity());
                            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(64, 64);
                            imageParams.setMargins(0, 0, 16, 0);
                            productImage.setLayoutParams(imageParams);

                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                try {
                                    String base64Image = imageBase64.split(",")[1];
                                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    productImage.setImageBitmap(decodedByte);
                                } catch (Exception e) {
                                    productImage.setImageResource(R.drawable.ic_noimage);
                                }
                            } else {
                                productImage.setImageResource(R.drawable.ic_noimage);
                            }

                            // Checkbox for product
                            CheckBox checkBox = new CheckBox(getActivity());
                            checkBox.setText(product);
                            checkBox.setTag(fieldId);

                            // ✅ Text color that adapts to light/dark mode
                            /*TypedValue typedValue = new TypedValue();
                            Context context = getActivity();
                            context.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
                            int textColor = ContextCompat.getColor(context, typedValue.resourceId);
                            checkBox.setTextColor(textColor);

                            // ✅ Button (tick) tint adapting to light/dark
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                checkBox.setButtonTintList(
                                        AppCompatResources.getColorStateList(context, R.drawable.checkbox_tint)
                                );
                            }*/

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                checkBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            }

                            // ✅ Text color (optional - match checkbox color)
                            checkBox.setTextColor(Color.BLACK);

                            // ✅ OnChecked logic
                            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    if (!listProductDivision.contains(product)) {
                                        listProductDivision.add(product);
                                    }
                                } else {
                                    listProductDivision.remove(product);
                                }
                                Log.d("SelectedCheckboxes", "Current selection: " + listProductDivision.toString());
                            });

                            itemLayout.addView(productImage);
                            itemLayout.addView(checkBox);
                            rowLayout.addView(itemLayout);
                        }
                    }
                    else
                    if (fieldType.equals("radio")) {
                    JSONArray options = field.getJSONArray("options");
                    RadioGroup radioGroup = new RadioGroup(getActivity());
                    radioGroup.setTag(fieldId);

                        if (isRequired) {
                            radioGroup.setTag(R.id.required, true);
                        }

                    for (int j = 0; j < options.length(); j++) {
                        String option = options.getString(j);
                        RadioButton radioButton = new RadioButton(getActivity());
                        radioButton.setText(option);
                        radioButton.setTag(fieldId);
                        radioGroup.addView(radioButton);
                    }

                    formLayout.addView(radioGroup);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        timerTextView = new TextView(getActivity());
        timerTextView.setText("00:00");
        timerTextView.setTextSize(24);
        timerTextView.setTextColor(Color.RED);
        timerTextView.setTypeface(null, Typeface.BOLD);
        timerTextView.setGravity(Gravity.CENTER);
        timerTextView.setVisibility(View.GONE);
        timerTextView.setPadding(10, 20, 10, 20);
        formLayout.addView(timerTextView);

        LinearLayout recordingLayout = new LinearLayout(getActivity());
        recordingLayout.setOrientation(LinearLayout.HORIZONTAL);
        recordingLayout.setGravity(Gravity.CENTER);
        recordingLayout.setPadding(5, 20, 5, 20);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(10, 0, 10, 0);

// START REC Button
        startRecordingButton = new Button(getActivity());
        startRecordingButton.setText("START REC");
        startRecordingButton.setBackground(getRoundedCardDrawable("#4CAF50")); // Set card background
        startRecordingButton.setTextColor(Color.WHITE);
        startRecordingButton.setTextSize(16);
        startRecordingButton.setPadding(25, 20, 25, 20);
        startRecordingButton.setLayoutParams(buttonParams);
        recordingLayout.addView(startRecordingButton);

// PLAY REC Button (Initially Hidden)
        playButton = new Button(getActivity());
        playButton.setText("PLAY REC");
        playButton.setBackground(getRoundedCardDrawable("#FF9800")); // Orange color
        playButton.setTextColor(Color.WHITE);
        playButton.setTextSize(16);
        playButton.setPadding(25, 20, 25, 20);
        playButton.setLayoutParams(buttonParams);
        playButton.setVisibility(View.GONE);
        recordingLayout.addView(playButton);

// Delete Icon (Initially Hidden)
        deleteIcon = new ImageView(getActivity());
        deleteIcon.setImageResource(R.drawable.ic_delete);
        deleteIcon.setPadding(5, 20, 5, 20);
        deleteIcon.setLayoutParams(buttonParams);
        deleteIcon.setVisibility(View.GONE);
        recordingLayout.addView(deleteIcon);

        formLayout.addView(recordingLayout);

        startRecordingButton.setOnClickListener(v -> {

            if (!checkPermissions()) {
                requestPermissions();
                return;
            }

            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
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

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri fileUri = getLatestAudioFileUri();
                Log.d("TAG", "Delete path: "+fileUri.getPath());
                if (fileUri != null) {
                    ContentResolver resolver = getActivity().getContentResolver();
                    int deletedRows = resolver.delete(fileUri, null, null);

                    if (deletedRows > 0) {
                        if (isPlaying) {
                            stopPlaying();
                        }

                        timerTextView.setVisibility(View.GONE);
//                        tvTimerString.setVisibility(View.GONE);
                        startRecordingButton.setVisibility(View.VISIBLE);
                        startRecordingButton.setText("START REC");
                        playButton.setText("PLAY REC");
                        playButton.setVisibility(View.GONE);
                        deleteIcon.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LinearLayout imageContainer = new LinearLayout(getActivity());
        imageContainer.setOrientation(LinearLayout.HORIZONTAL);
        imageContainer.setGravity(Gravity.CENTER);
        imageContainer.setPadding(10, 20, 10, 20);

// Define LayoutParams with weight and margin for spacing
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        imageParams.setMargins(20, 0, 20, 0); // Adds horizontal space

// --- Owner Image Section ---
        LinearLayout ownerLayout = new LinearLayout(getActivity());
        ownerLayout.setOrientation(LinearLayout.VERTICAL);
        ownerLayout.setLayoutParams(imageParams);

// Owner Header
        TextView ownerLabel = new TextView(getActivity());
        ownerLabel.setText("Owner Image");
        ownerLabel.setTextSize(16);
        ownerLabel.setTypeface(null, Typeface.BOLD);
        ownerLabel.setGravity(Gravity.CENTER);
        ownerLabel.setPadding(0, 0, 0, 10);
        ownerLayout.addView(ownerLabel);

// Owner ImageView
        ownerImageView = new ImageView(getActivity());
        ownerImageView.setImageResource(R.drawable.ic_menu_camera);
        ownerImageView.setPadding(40, 40, 40, 40);
        ownerImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_rounded_bg));
        ownerImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ownerImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 300
        ));
        ownerImageView.setOnClickListener(v -> openCameraForOwner());
        ownerLayout.addView(ownerImageView);

// --- Shop Image Section ---
        LinearLayout shopLayout = new LinearLayout(getActivity());
        shopLayout.setOrientation(LinearLayout.VERTICAL);
        shopLayout.setLayoutParams(imageParams);

// Shop Header
        TextView shopLabel = new TextView(getActivity());
        shopLabel.setText("Shop Image");
        shopLabel.setTextSize(16);
        shopLabel.setTypeface(null, Typeface.BOLD);
        shopLabel.setGravity(Gravity.CENTER);
        shopLabel.setPadding(0, 0, 0, 10);
        shopLayout.addView(shopLabel);

// Shop ImageView
        shopImageView = new ImageView(getActivity());
        shopImageView.setImageResource(R.drawable.ic_menu_camera);
        shopImageView.setPadding(40, 40, 40, 40);
        shopImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_rounded_bg));
        shopImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        shopImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 300
        ));
        shopImageView.setOnClickListener(v -> openCameraForShop());
        shopLayout.addView(shopImageView);

// Add both layouts to the container
        imageContainer.addView(ownerLayout);
        imageContainer.addView(shopLayout);

// Add the container to the form
        formLayout.addView(imageContainer);

        /*LinearLayout imageLayout = new LinearLayout(getActivity());
        imageLayout.setOrientation(LinearLayout.HORIZONTAL);
        imageLayout.setGravity(Gravity.CENTER);
        imageLayout.setPadding(10, 20, 10, 20);

// Define LayoutParams with weight and margin for spacing
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(0, 300, 1);
        imageParams.setMargins(20, 0, 20, 0); // Adds horizontal space

// Create Owner ImageView
        ownerImageView = new ImageView(getActivity());
        ownerImageView.setImageResource(R.drawable.ic_menu_camera);
        ownerImageView.setPadding(40, 40, 40, 40);
        ownerImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_rounded_bg));
        ownerImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ownerImageView.setLayoutParams(imageParams);
        ownerImageView.setOnClickListener(v -> openCameraForOwner());

        shopImageView = new ImageView(getActivity());
        shopImageView.setImageResource(R.drawable.ic_menu_camera);
        shopImageView.setPadding(40, 40, 40, 40);
        shopImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_rounded_bg));
        shopImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        shopImageView.setLayoutParams(imageParams);
        shopImageView.setOnClickListener(v -> openCameraForShop());

        imageLayout.addView(ownerImageView);
        imageLayout.addView(shopImageView);
        formLayout.addView(imageLayout);*/


        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int seconds = 0;
            @Override
            public void run() {
                seconds++;
                int minutes = seconds / 60;
                int secs = seconds % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, secs));
                handler.postDelayed(this, 1000);
            }
        };

        // Submit Button
        Button submitButton = new Button(getActivity());
        submitButton.setText("ADD DISTRIBUTOR");
        submitButton.setBackgroundColor(Color.parseColor("#4CAF50"));
        submitButton.setTextColor(Color.WHITE);
        submitButton.setTextSize(18);
        submitButton.setTypeface(null, Typeface.BOLD);
        submitButton.setPadding(30, 20, 30, 20);
        submitButton.setBackground(getRoundedButtonDrawable());
//        submitButton.setOnClickListener(v -> handleSubmit(formLayout));
        submitButton.setOnClickListener(v -> {
            Log.d("TAG", "ownerImageUri Image Path1: "+ownerImageUri);
            Log.d("TAG", "shopImageUri Image Path1: "+ownerImageUri);

            if (validateFields(formLayout)) {
                handleSubmit(formLayout);
            }
        });
        formLayout.addView(submitButton);
    }

    private boolean validateFields(LinearLayout formLayout) {
        boolean isValid = true;
        int fieldCounter = 1;

        for (int i = 0; i < formLayout.getChildCount(); i++) {
            View child = formLayout.getChildAt(i);

            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                String value = editText.getText().toString().trim();

                // Check if field is marked required
                Boolean isRequired = (Boolean) editText.getTag(R.id.required);
                String fieldId = (String) editText.getTag(R.id.field_id); // e.g., "pan", "gstin"

                if (isRequired != null && isRequired && TextUtils.isEmpty(value)) {
                    editText.setError(fieldCounter + ". This field is required");
                    isValid = false;
                } else if (!TextUtils.isEmpty(value)) {
                    if ("pan".equalsIgnoreCase(fieldId) && !isValidPAN(value)) {
                        editText.setError(fieldCounter + ". Invalid PAN format");
                        isValid = false;
                    }

                    if ("gstin".equalsIgnoreCase(fieldId) && !isValidGSTIN(value)) {
                        editText.setError(fieldCounter + ". Invalid GSTIN format");
                        isValid = false;
                    }
                }

                fieldCounter++;
            } else if (child instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup) child;
                Boolean isRequired = (Boolean) radioGroup.getTag(R.id.required);

                if (isRequired != null && isRequired && radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getActivity(), fieldCounter + ". Please select Distributor Opinion", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }

                fieldCounter++;
            } else if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                if (checkBox.isChecked()) {
                    listProductDivision.add(checkBox.getText().toString());
                }
            }
        }

        if (ownerImageUri == null) {
            Toast.makeText(getActivity(), fieldCounter + ". Owner image is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        fieldCounter++;

        if (shopImageUri == null) {
            Toast.makeText(getActivity(), fieldCounter + ". Shop image is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }


    private boolean isValidPAN(String pan) {
        return pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    }

    private boolean isValidGSTIN(String gstin) {
        return gstin.matches("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1}");
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

        // Set an OnCloseIconClickListener to remove the chip
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);  // Remove chip from ChipGroup
            stringList.remove(text);      // Remove chip value from the list
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

    private void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        isPlaying = false;
        playButton.setText("PLAY REC");
        deleteIcon.setVisibility(View.VISIBLE);
//        Toast.makeText(getActivity(), "Playing stopped", Toast.LENGTH_SHORT).show();
    }

    private void startPlaying() {
        Uri audioUri = getLatestAudioFileUri(); // Get the latest file
        if (audioUri == null) {
            Toast.makeText(getActivity(), "No audio file found", Toast.LENGTH_SHORT).show();
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

    private Uri getLatestAudioFileUri() {
        ContentResolver resolver = getActivity().getContentResolver();
        String folderName = "MyRecordings";

        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // Query to get the most recent file from the directory
        String selection = MediaStore.Audio.Media.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{Environment.DIRECTORY_MUSIC + "/" + folderName + "/"};

        Cursor cursor = resolver.query(collection,
                new String[]{MediaStore.Audio.Media._ID},
                selection,
                selectionArgs,
                MediaStore.Audio.Media.DATE_ADDED + " DESC"); // Sort by newest first

        Uri fileUri = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            fileUri = ContentUris.withAppendedId(collection, id);
        }

        if (cursor != null) cursor.close();
        return fileUri;
    }

    private Drawable getRoundedCardDrawable(String colorHex) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(colorHex));
        shape.setCornerRadius(50);
        return shape;
    }

    private Drawable getRectDrawable(String colorHex) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(colorHex));
        shape.setCornerRadius(20);
        return shape;
    }

    private void openCameraForShop() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = createImageFile("SHOP");
            if (photoFile != null) {
                shopImageUri = FileProvider.getUriForFile(
                        getActivity(),
                        "com.newsalesbeat.fileprovider",
                        photoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, shopImageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_SHOP);
            }
        }
    }

    private void openCameraForOwner() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = createImageFile("OWNER");
            if (photoFile != null) {
                ownerImageUri = FileProvider.getUriForFile(
                        getActivity(),
                        "com.newsalesbeat.fileprovider",
                        photoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ownerImageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_OWNER);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_SHOP && shopImageUri != null) {
                firmImageLatLong.clear();
                shopImageView.setImageURI(shopImageUri);

                firmImageLatLong.add(locationProvider.getLatitude());
                firmImageLatLong.add(locationProvider.getLongitude());

            } else if (requestCode == REQUEST_IMAGE_OWNER && ownerImageUri != null) {
                ownerImageLatLong.clear();
                ownerImageView.setImageURI(ownerImageUri);
                ownerImageLatLong.add(locationProvider.getLatitude());
                ownerImageLatLong.add(locationProvider.getLongitude());
            }
        }
    }

    private File createImageFile(String type) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = type + "_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Drawable getRoundedButtonDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#4CAF50"));
        drawable.setCornerRadius(20); // Adjust radius as needed
        return drawable;
    }

    private void startRecording() {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        Uri audioUri = createAudioFile(); // Generate a new unique file
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
            timerTextView.setVisibility(View.VISIBLE);

            // UI updates
            startRecordingButton.setText("STOP REC");
            startRecordingButton.setBackground(getRoundedCardDrawable("#F44336"));

            playButton.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            deleteIcon.setVisibility(View.GONE);
            playButton.setText("PLAY REC");

            startCountdownTimer();

            Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Recording failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }

    private Uri createAudioFile() {
        ContentResolver resolver = getActivity().getContentResolver();
        String folderName = "MyRecordings";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "recorded_audio_" + timeStamp + ".m4a";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/" + folderName);

        return resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
    }


    private Uri getExistingAudioFileUri(String fileName, String folderName) {
        ContentResolver resolver = getActivity().getContentResolver();
        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{fileName, Environment.DIRECTORY_MUSIC + "/" + folderName + "/"};

        Cursor cursor = resolver.query(collection, new String[]{MediaStore.MediaColumns._ID}, selection, selectionArgs, null);
        Uri fileUri = null;

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            fileUri = ContentUris.withAppendedId(collection, id);
        }

        if (cursor != null) cursor.close();
        return fileUri;
    }

    private boolean checkPermissions() {
        int recordAudio = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO);
        int storage = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return recordAudio == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || storage == PackageManager.PERMISSION_GRANTED);
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                startRecordingButton.setText("START REC");
                startRecordingButton.setBackground(getRoundedCardDrawable("#4CAF50"));
                playButton.setVisibility(View.VISIBLE);
                deleteIcon.setVisibility(View.VISIBLE);

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

    private void notifyMediaStoreUpdate() {
        Uri audioUri = getSavedAudioFileUri();
        if (audioUri != null) {
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(audioUri);
            getActivity().sendBroadcast(scanIntent);
        }
    }

    private void startCountdownTimer() {
        fadeInView(timerTextView); // Fade in the timer
        countDownTimer = new CountDownTimer(MAX_TIME_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) (millisUntilFinished / 1000 / 60);
                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                stopRecording();
            }
        }.start();
    }

    private void fadeInView(View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(300); // Animation duration
        fadeIn.start();
    }

    /*private Uri getSavedAudioFileUri() {
        ContentResolver resolver = getActivity().getContentResolver();
        String fileName = "recorded_audio.m4a"; // Fixed filename
        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // Selection for all Android versions
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{fileName};

        Log.d("TAG", "Searching for: " + fileName);

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

        // If fileUri is null, check manually in storage
        if (fileUri == null) {
            File manualFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MyRecordings/" + fileName);
            if (manualFile.exists()) {
                fileUri = Uri.fromFile(manualFile);
            }
        }

        return fileUri;
    }*/

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

    private void addDynamicValidation(EditText editText, String regexPattern) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().toUpperCase(); // Convert input to uppercase
                if (!input.matches(regexPattern)) {
                    editText.setError("Invalid format");
                }
            }
        });
    }

    private void addPANValidation(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)}); // PAN is exactly 10 characters

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String pan = s.toString().toUpperCase(); // Convert to uppercase

                // Set the text back to the EditText in uppercase
                if (!s.toString().equals(pan)) {
                    editText.setText(pan);
                    editText.setSelection(pan.length()); // Maintain cursor position
                }

                String panPattern = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";

                if (!pan.matches(panPattern)) {
                    editText.setError("Invalid PAN format");
                }
            }
        });
    }

    private void addGSTINValidation(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); // GSTIN is exactly 15 characters

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String gstin = s.toString().toUpperCase(); // Convert to uppercase

                // Set the text back to the EditText in uppercase
                if (!s.toString().equals(gstin)) {
                    editText.setText(gstin);
                    editText.setSelection(gstin.length()); // Maintain cursor position
                }

                String gstinPattern = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";

                if (!gstin.matches(gstinPattern)) {
                    editText.setError("Invalid GSTIN format");
                }
            }
        });
    }

    private void handleSubmit(LinearLayout formLayout) {

        Log.d("TAG", "ownerImageUri Image Path: "+ownerImageUri);
        Log.d("TAG", "shopImageUri Image Path: "+shopImageUri);

        String audioFile = "";

        Uri fileUri = getLatestAudioFileUri();
        Log.d("TAG", "SAve File Path: "+fileUri.getPath());

        if (fileUri != null) {
            File file = getFileFromUri(fileUri);

            if (file != null && file.exists()) {
                uploadFile(file,formLayout);
            } else {
                Toast.makeText(getActivity(), "Recording not found-1.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Recording not found-2.", Toast.LENGTH_SHORT).show();
        }

    }


    private void addDistributor(String cmnyName, String cmnyAddress, String pincode, String ownerName,
                                String phone1, String email, String phone2, String investmentPlan, String workingSince,
                                String otherContactPerson, String otherPhone, String gstn, String fsin, String panNo,
                                String monthlyTurnOver, String brandName, String rec,String productDivision) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        String date = sdf.format(Calendar.getInstance().getTime());
        Log.d("TAG", "addDistributor ownerImagePath: "+ownerImageUri);
        Log.d("TAG", "addDistributor ownerImagePath: "+ownerImageUri);
        Log.d("TAG", "MOdel ownerImagePath: "+ownerImagePath);
        Log.d("TAG", "MOdel shopImagePath: "+shopImagePath);

        if (ownerImageUri != null) {

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
            newDistributorDetails.add(convertUriToBase64(ownerImageUri));
//            newDistributorDetails.add(ownerImageUri.toString());
            newDistributorDetails.add(date);
            newDistributorDetails.add("");
            newDistributorDetails.add(investmentPlan);
            newDistributorDetails.add(workingSince);
            newDistributorDetails.add(otherContactPerson);
            newDistributorDetails.add(otherPhone);
            newDistributorDetails.add(convertUriToBase64(shopImageUri));
            newDistributorDetails.add(date);
            newDistributorDetails.add(strOpinionAboutDistributor);
//            newDistributorDetails.add(edtRemarkCmnt.getText().toString());
            newDistributorDetails.add("ok");

            Set<String> uniqueSet = new HashSet<>(listProductDivision);
            List<String> uniqueItemsList = new ArrayList<>(uniqueSet);
            Collections.sort(uniqueItemsList);

            String tempDid = String.valueOf(Calendar.getInstance().getTimeInMillis());
            salesBeatDb.insertInNewDistributorTable(tempDid, newDistributorDetails,
                    /*listBeatName*/beatN, uniqueItemsList, ownerImageLatLong, firmImageLatLong, brandName, rec);

//            salesBeatDb.insertInNewDistributorTable(tempDid, newDistributorDetails,
//                    /*listBeatName*/beatN, listProductDivision, ownerImageLatLong, firmImageLatLong, brandName, rec);

            Toast.makeText(getContext(), "Distributor saved successfully ", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), AddDistributor.class);
            intent.putExtra("tabPos", 1);
            intent.putExtra("page_title", "New Distributor");
            startActivity(intent);
            getActivity().finish();
        }

    }

    private String convertUriToBase64(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) return null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream); // e.g., quality = 50
            byte[] compressedBytes = outputStream.toByteArray();
            outputStream.close();

            return Base64.encodeToString(compressedBytes, Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void uploadFile(File file,LinearLayout formLayout) {
        final String[] responseBody = {""};
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

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody= response.body().string();
                    Log.d("TAG", "Mp3 Response: " + responseBody);
                    getActivity().runOnUiThread(() -> updateApiCall(responseBody,formLayout));
                } else {
                    System.out.println("Error: " + response.code());
                }
            }
        });
    }

    private void updateApiCall(String audioFile,LinearLayout formLayout) {
        String sendBrandName = TextUtils.join(",", stringList);
        JSONObject formData = new JSONObject();

        for (int i = 0; i < formLayout.getChildCount(); i++) {
            View view = formLayout.getChildAt(i);

            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                try {
                    formData.put(editText.getTag().toString(), editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                try {
                    formData.put(checkBox.getTag().toString(), checkBox.isChecked());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (view instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup) view;
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton radioButton = radioGroup.findViewById(selectedId);
                    try {
                        formData.put(radioGroup.getTag().toString(), radioButton.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        String gstn = formData.optString("gstin", "");
        String panNo = formData.optString("pan", "");
        String fsi = formData.optString("fsi", "");
        String firmName = formData.optString("firmName", "");
        String firmAddress = formData.optString("firmAddress", "");
        String pinCode = formData.optString("pinCode", "");
        String city = formData.optString("city", "");
        String state = formData.optString("state", "");
        String ownerName = formData.optString("ownerName", "");
        String ownerMobile1 = formData.optString("ownerMobile1", "");
        String ownerMobile2 = formData.optString("ownerMobile2", "");
        String email = formData.optString("email", "");
        String otherContactPersonNames = formData.optString("otherContactPersonNames", "");
        String otherContactPersonPhones = formData.optString("otherContactPersonPhones", "");
        String productDivision = listProductDivision.toString();
//        String productDivision = formData.optString("productDivision", "");
        String opinion = formData.optString("opinion", "");
        beatN = formData.optString("beatName","");

        Log.d("TAG", "GSTN: " + gstn);
        Log.d("TAG", "PAN Number: " + panNo);
        Log.d("TAG", "opinion: " + opinion);
        Log.d("TAG", "productDivision: " + productDivision);
        Log.d("TAG", "email: " + email);

        addDistributor(firmName, firmAddress, pinCode, ownerName, ownerMobile1,
                email, ownerMobile2, "", "", otherContactPersonNames,
                otherContactPersonPhones, gstn, fsi, panNo, "", sendBrandName, audioFile,productDivision);

//        Log.d("TAG", "Submitted Data: " + formData.toString());
    }

    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                file = new File(getActivity().getCacheDir(), "temp_audio.m4a"); // Copy file to cache
                copyFile(fd, file);
                pfd.close();
                Log.d("DEBUG", "Copied file path: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", "Failed to copy file: " + e.getMessage());
        }
        return file;
    }

    private void copyFile(FileDescriptor sourceFD, File destFile) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(sourceFD);
             FileOutputStream outputStream = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            Log.d("DEBUG", "File copied successfully to: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("ERROR", "Failed to copy file: " + e.getMessage());
            throw e;
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
                    updateFormFields();

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

    private void updateFormFields() {
        if (fieldMap.containsKey("firmAddress") && fieldMap.get("firmAddress") != null) {
            fieldMap.get("firmAddress").setText(address);
        }
        if (fieldMap.containsKey("locality") && fieldMap.get("locality") != null) {
            fieldMap.get("locality").setText(locality);
        }
        if (fieldMap.containsKey("city") && fieldMap.get("city") != null) {
            fieldMap.get("city").setText(city);
        }
        if (fieldMap.containsKey("pinCode") && fieldMap.get("pinCode") != null) {
            fieldMap.get("pinCode").setText(pincode);
        }
        if (fieldMap.containsKey("state") && fieldMap.get("state") != null) {
            fieldMap.get("state").setText(state);
        }
        latitude = latitude;
        longitude = longitude;
    }

}

