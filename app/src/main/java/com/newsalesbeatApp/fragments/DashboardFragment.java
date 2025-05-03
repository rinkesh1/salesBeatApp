package com.newsalesbeatApp.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELEPHONY_SERVICE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.newsalesbeatApp.BuildConfig;
import com.newsalesbeatApp.activities.LoginScreen;
import com.newsalesbeatApp.pojo.Employee;
import com.newsalesbeatApp.services.IsActiveService;
import com.newsalesbeatApp.utilityclass.Config;
import com.newsalesbeatApp.utilityclass.MockLocationChecker;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.MainActivity;
import com.newsalesbeatApp.activities.MyClaimExpense;
import com.newsalesbeatApp.activities.OrderBookingRetailing;
import com.newsalesbeatApp.activities.SaleHistory;
import com.newsalesbeatApp.activities.TomorrowPlanDetails;
import com.newsalesbeatApp.adapters.ActivityListAdapter;
import com.newsalesbeatApp.adapters.PromotionPagerAdapter;
import com.newsalesbeatApp.adapters.ViewPagerAdapter1;
import com.newsalesbeatApp.customview.Animation;
import com.newsalesbeatApp.customview.BarChartView;
import com.newsalesbeatApp.customview.BarSet;
import com.newsalesbeatApp.customview.RippleObjectView;
import com.newsalesbeatApp.customview.RoundedImageView;
import com.newsalesbeatApp.customview.Tooltip;
import com.newsalesbeatApp.interfaces.OnItemClickListener;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.netwotkcall.VolleyMultipartRequest;
import com.newsalesbeatApp.pojo.ClaimHistoryItem;
import com.newsalesbeatApp.pojo.SkuItem;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.services.MarkAttendanceService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.BlurBuilder;
//import com.newsalesbeat.utilityclass.FaceCropper2;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.newsalesbeatApp.activities.MainActivity.mainActivity;

import io.sentry.Sentry;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

/*
 * Created by MTC on 18-07-2017.
 */
public class DashboardFragment extends Fragment implements NetworkChangeInterface {

    private static final String VEHCLE_NUMBER = "AA99AA9999";
    private static int REQUEST_CODE_MY_PICK = 202;
    String TAG = "DashboardFragment";
    ArrayList<Integer> viewIdList = new ArrayList<>();
    int notListed = 0;
    private SharedPreferences tempPref, myPref;
    private ServerCall serverCall;
    private Button btnCheckOut, btnStartDay, btnApplyLeave,
            btnShareSummary, btnShareClaim, btnBookOrder, btnOk, btnTomorrowsPlan, btnShareStartWorkingSummary,
            btnHistory, btnTodaysSummary;

    private Handler handler1;
    private Runnable runnable, runnable1;
    private FrameLayout overlayLayout;
    private ImageView recognizeAudio;
    private LinearLayout llCurrentStatus, llMarkAttendance, llShareReport, llShareStartWorkingSummary;
    private UtilityClass utilityClass;
    private ViewPagerAdapter1 adapter = null;
    private TextView tvAttendanceStatus, tvActType, tvCheckInTime,
            tvCheckOutTime, tvTimer, tvWorkingTown, tvLeaveReason, tvLeaveReasonTitle;
    //Fragment fragment;
    private ViewPager promotionViewPager, empPerformanceViewPager;
    private PromotionPagerAdapter pagerAdapter;
    private ArrayList<String> marketName = new ArrayList<>();
    private ArrayList<String> town = new ArrayList<>();
    private ArrayList<String> beatName = new ArrayList<>();
    private ArrayList<String> contact = new ArrayList<>();
    private ArrayList<String> totalOpening = new ArrayList<>();
    private ArrayList<String> totalSecondary = new ArrayList<>();
    private ArrayList<String> totalClosing = new ArrayList<>();
    private ArrayList<ArrayList<SkuItem>> stocksList2 = new ArrayList<>();
    private GPSLocation locationProvider;
    private ScrollView scDashboard;
    private boolean loaded = false;
    private boolean townListEmpty = false;
    private SalesBeatDb salesBeatDb;
    /*Leaderboard*/
    private ArrayList<String> tEid = new ArrayList<>();
    private ArrayList<String> tEmpName = new ArrayList<>();
    private ArrayList<String> profilePic = new ArrayList<>();
    private ArrayList<String> tcData = new ArrayList<>();
    private ArrayList<String> pcData = new ArrayList<>();
    private ArrayList<String> salesData = new ArrayList<>();
    private BarChartView leaderboardChart;
    private TextView emp1, emp2, emp3, emp4, emp5;
    private TextView val1;
    private TextView val2;
    private TextView val3;
    private TextView val4;
    private TextView val5;
    private String[] mLabels3 = new String[5];
    private float[] mSalesValues = new float[5];
    private double valueInKg1 = 0, valueInKg2 = 0, valueInKg3 = 0, valueInKg4 = 0, valueInKg5 = 0;
    private RoundedImageView emp1Pic, emp2Pic, emp3Pic, emp4Pic, emp5Pic;
    private TextView tvNoInterNet;
    private ImageView noCampaignPic;
    private CardView campaignLoader;
    //ImageView imgHistory;
    private ImageView errorCampaign;
    private Snackbar snackbar;
    RelativeLayout rlActivityList1;
    private ImageLoader mImageLoader;
    private ImageView audioRecordingLoader;
    //    private FaceCropper2 mFaceCropper;
    RelativeLayout rlTownList1;

    //ImageView imgRefreshTown;

    //    private ArrayList<String> bids = new ArrayList<>();
//    private ArrayList<String> bidUpdatedAt = new ArrayList<>();
//    private ArrayList<String> retId = new ArrayList<>();
//    private ArrayList<String> ridUpdatedAt = new ArrayList<>();
    private RelativeLayout rlFront, rlBack;
    private LoadProfileImage2 loadProfileImage2;
    private ArrayList<String> activities = new ArrayList<>();
    //ArrayList<String> activitiesId = new ArrayList<>();
    private String workingTown = "";
    private String did = "";
    private String disN = "";
    private String distributorName = "";
    private String comment = "";
    private String empName = "";
    public String jointwrkempid = "";
    public String jointwrkempname = "";
    private String meeting = "";
    private String activity = "", activity2 = "";
    RelativeLayout rlDistributorList1;
    private String tempText, languagePreferred = "en";
    private Boolean allowSpeech = true;
    private int actCount = 0;
    private RecyclerView rvActivityList, rvTownList, rvDistributorList, rvEmployeeList, rvMeetingList;
    RelativeLayout rlEmpList1;
    RelativeLayout rlMeetingList1;
    //private boolean stopStepCount = false;
    private String selectedActivity = "";
    private Dialog activityListDialog;
    private ActivityListAdapter tempAdapter;
    // Network chnage Distributor List
    private Boolean downloadDistributorList = false;
    private Boolean downloadEmployeeList = false;
    private LinearLayout disllNewStartWork, disllNext;
    private LinearLayout empllNewStartWork, empllNext;
    private TextView distvLoadingMsgm, distvNotListed, distvDisNotListed;
    private AutoCompleteTextView emptvEmpNotListed;
    private Boolean disFlag;

    //@Umesh 20220908
    private LinearLayout llTownBtn;
    private TextView tvOtherTown,tvRegularTown;
    private LinearLayout llNext;
    private LinearLayout llNewStartWorkList;
    private TextView tvNotListed;
    private TextView tvDistNotListed;
    private boolean IsOther=false;
    ArrayList<String> acitvityList = new ArrayList<>();

    private NetworkChangeReceiver receiver = new NetworkChangeReceiver();

//    private NetworkChangeReceiver receiver = new NetworkChangeReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            resetImage();
//            callAsynctask();
//            try {
//                leaderboardChart.reset();
//            } catch (Exception e) {
//                //e.printStackTrace();
//            }
//            setValues();
//        }
//    };

    public static void hideKeyboardwithoutPopulate(EditText edtV, Activity activity, boolean b) {
        if (edtV != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {

                if (b)
                    imm.showSoftInput(edtV, InputMethodManager.SHOW_IMPLICIT);
                else
                    imm.hideSoftInputFromWindow(edtV.getWindowToken(), 0);
            }

        }
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void showActivityList(boolean flagTomorrowPlan) {

        activityListDialog = new Dialog(requireContext(), R.style.DialogActivityTheme);
        activityListDialog.setContentView(R.layout.activity_list);

        if (activityListDialog.getWindow() != null) {

            activityListDialog.getWindow().setGravity(Gravity.BOTTOM);
        }


        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(activityListDialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.99f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.75f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        activityListDialog.getWindow().setAttributes(layoutParams);

        //activityListDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        activityListDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        activityListDialog.setCancelable(false);
        activityListDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        rvActivityList = activityListDialog.findViewById(R.id.rvActivityList);
        rvTownList = activityListDialog.findViewById(R.id.rvTownList);
        rvDistributorList = activityListDialog.findViewById(R.id.rvDistributorList);
        rvEmployeeList = activityListDialog.findViewById(R.id.rvEmployeeList);
        rvMeetingList = activityListDialog.findViewById(R.id.rvMeetingList);
        rlActivityList1 = activityListDialog.findViewById(R.id.rlActivityList);
        rlTownList1 = activityListDialog.findViewById(R.id.rlTownList);
        rlDistributorList1 = activityListDialog.findViewById(R.id.rlDistributorList);
        rlEmpList1 = activityListDialog.findViewById(R.id.rlEmployeeList);
        rlMeetingList1 = activityListDialog.findViewById(R.id.rlMeetingList);
        LinearLayout llCommentAct = activityListDialog.findViewById(R.id.llCommentAct);
        LinearLayout llTravelling = activityListDialog.findViewById(R.id.llTravelling);
        LinearLayout llVanSales = activityListDialog.findViewById(R.id.llVanSales);

        llNext = activityListDialog.findViewById(R.id.llNext);
        llNewStartWorkList = activityListDialog.findViewById(R.id.llNewStartWorkList);
        LinearLayout llDone = activityListDialog.findViewById(R.id.llDone);
        LinearLayout llTownDist = activityListDialog.findViewById(R.id.llTownDist);
        RelativeLayout rlEmpList = activityListDialog.findViewById(R.id.rlEmpL);
        RelativeLayout rlMeetingList = activityListDialog.findViewById(R.id.rlMeetingL);
        RelativeLayout rlTownList = activityListDialog.findViewById(R.id.rlTownL);
        //LinearLayout llWillBeOnLeave = activityListDialog.findViewById(R.id.llWillbeOnLeave);
        EditText edtComment = activityListDialog.findViewById(R.id.edtComment);
        EditText edtFromNS = activityListDialog.findViewById(R.id.edtFromNS);
        EditText edtToNS = activityListDialog.findViewById(R.id.edtToNS);
        EditText edtVanNumber = activityListDialog.findViewById(R.id.edtVanNumber);
        EditText edtDriverName = activityListDialog.findViewById(R.id.edtDriverName);
        //EditText edtTownName = activityListDialog.findViewById(R.id.edtTownName);
        EditText edtDistributorName = activityListDialog.findViewById(R.id.edtDistributorName);
        EditText edtTownName = activityListDialog.findViewById(R.id.edtTownName);
        AutoCompleteTextView actvEmpList = activityListDialog.findViewById(R.id.actvEmpList);
        AutoCompleteTextView actvMeetingList = activityListDialog.findViewById(R.id.actvMeetingList);
        //AutoCompleteTextView actvTownList = activityListDialog.findViewById(R.id.actvTownList);
        TextView tvTitle = activityListDialog.findViewById(R.id.tvTitleForToady);
        TextView tvNext = activityListDialog.findViewById(R.id.tvNext);
        TextView tvLoadingActListMsg = activityListDialog.findViewById(R.id.tvLoadingActListMsg);
        TextView tvLoadingTownMsg = activityListDialog.findViewById(R.id.tvLoadingTownMsg);
        TextView tvLoadingDistributorMsg = activityListDialog.findViewById(R.id.tvLoadingDistributorMsg);
        TextView tvLoadingEmpListMsg = activityListDialog.findViewById(R.id.tvLoadingEmpListMsg);
        TextView tvLoadingMeetingListMsg = activityListDialog.findViewById(R.id.tvLoadingMeetingListMsg);
        tvNotListed = activityListDialog.findViewById(R.id.tvNotListed);
        tvDistNotListed = activityListDialog.findViewById(R.id.tvDistNotListed);
        ImageView imgEmpList = activityListDialog.findViewById(R.id.imgEmpList);
        ImageView imgMeetingList = activityListDialog.findViewById(R.id.imgMeetingList);
        //ImageView imgTownList = activityListDialog.findViewById(R.id.imgTownList);
        ImageView searchImage = activityListDialog.findViewById(R.id.searchImage);
        ImageView backImageSearch = activityListDialog.findViewById(R.id.backPageSearch);
        EditText edtSearch = activityListDialog.findViewById(R.id.edtSearchText);
        LinearLayout llSearch = activityListDialog.findViewById(R.id.llSearch);
        Button btnClearAll = activityListDialog.findViewById(R.id.btnClearAll);
        SpeechRecognizer speechRecognizer;
        Intent speechRecognitionIntent;
        recognizeAudio = activityListDialog.findViewById(R.id.recordAudioImageView);
        audioRecordingLoader = activityListDialog.findViewById(R.id.recordingAudioLoader);
        //ImageView imgBackPage = activityListDialog.findViewById(R.id.backPage);
        //CheckBox chbWillBeOnLeave = activityListDialog.findViewById(R.id.chbWillbeOnLeave);
        //VoiceRippleView voiceRipple = activityListDialog.findViewById(R.id.imgRecordAudio);
        RippleObjectView rippleObjectView = activityListDialog.findViewById(R.id.rippleView);

        //@Umesh 20220908
        llTownBtn= activityListDialog.findViewById(R.id.llTownBtn);
        tvOtherTown= activityListDialog.findViewById(R.id.tvOtherTown);
        tvRegularTown= activityListDialog.findViewById(R.id.tvRegularTown);
        tvOtherTown.setOnClickListener(v -> {IsOther=true; getTownList();});
        tvRegularTown.setOnClickListener(v -> {IsOther=false; getTownList();});

        activity = "";
        activity2 = "";
        final boolean[] disNotListed = {false};
        final boolean[] flagTownNotListed = {false};

        if (flagTomorrowPlan)
            tvTitle.setText("What will be your activity tomorrow?");

        hideAllRv();
        initActivityList(llNext, flagTomorrowPlan, tvNext, tvNotListed);


        searchImage.setOnClickListener(view -> {

            if (rvActivityList.isShown()
                    || rvTownList.isShown()
                    || rvDistributorList.isShown()
                    || rvEmployeeList.isShown()
                    || rvMeetingList.isShown()) {

                llSearch.setVisibility(View.VISIBLE);
                searchImage.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);

                edtSearch.requestFocus();

                hideKeyboardwithoutPopulate(edtSearch, requireActivity(), true);
            }
        });


        backImageSearch.setOnClickListener(view -> {

            llSearch.setVisibility(View.GONE);
            searchImage.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);

            RecyclerView rv = null;
            if (rvActivityList.isShown())
                rv = rvActivityList;
            else if (rvTownList.isShown())
                rv = rvTownList;
            else if (rvDistributorList.isShown())
                rv = rvDistributorList;
            else if (rvEmployeeList.isShown())
                rv = rvEmployeeList;
            else if (rvMeetingList.isShown())
                rv = rvMeetingList;


            if (rv != null) {

                ActivityListAdapter adapter = (ActivityListAdapter) rv.getAdapter();
                adapter.getFilter().filter("");
            }

            edtSearch.setText("");

            hideKeyboardwithoutPopulate(edtSearch, requireActivity(), false);

        });

        btnClearAll.setVisibility(View.GONE);

        btnClearAll.setOnClickListener(view -> edtComment.getText().clear());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                RecyclerView rv = null;
                if (rvActivityList.isShown())
                    rv = rvActivityList;
                else if (rvTownList.isShown())
                    rv = rvTownList;
                else if (rvDistributorList.isShown())
                    rv = rvDistributorList;
                else if (rvEmployeeList.isShown())
                    rv = rvEmployeeList;
                else if (rvMeetingList.isShown())
                    rv = rvMeetingList;


                if (rv != null) {

                    ActivityListAdapter adapter = (ActivityListAdapter) rv.getAdapter();
                    adapter.getFilter().filter(charSequence);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // to remove emoticons from copied text while pasting
                try {

                    clipEmoticonsFromCopiedText();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {

                    llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                    btnClearAll.setVisibility(View.VISIBLE);

                } else if (charSequence.length() == 0) {

                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                    btnClearAll.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtComment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clipEmoticonsFromCopiedText();
                return false;
            }
        });
        // to disable user to add emoticon
        edtComment.setFilters(new InputFilter[]{new EmoticonExcludeFilter()});

        //speech to text function

        rippleObjectView.setDuration(8000);
        rippleObjectView.setStyle(Paint.Style.STROKE);
        rippleObjectView.setColor(Color.BLACK);
        rippleObjectView.setSpeed(500);
        rippleObjectView.setInterpolator(new DecelerateInterpolator());
        rippleObjectView.start();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                tempText = edtComment.getText().toString().trim();
//                edtComment.setText("");
//                edtComment.setHint("Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e(TAG, "onBeginningOfSpeech: ");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                Log.e(TAG, "onEndOfSpeech: ");
                allowSpeech = true;
            }

            @Override
            public void onError(int i) {
                Log.e(TAG, "onError: " + i);
                if (i == 3) {
                    Toast.makeText(getContext(), "Please allow Audio Permission", Toast.LENGTH_SHORT).show();
                    allowSpeech = true;
                }
                if (i == 8) {
                    speechRecognizer.cancel();
                    speechRecognizer.startListening(speechRecognitionIntent);
                }
                if (i == 6 || i == 7)
                    allowSpeech = true;

                audioRecordingLoader.setVisibility(View.GONE);
                recognizeAudio.setImageResource(R.drawable.mic_black);
            }

            @Override
            public void onResults(Bundle bundle) {
                audioRecordingLoader.setVisibility(View.GONE);
                recognizeAudio.setImageResource(R.drawable.mic_black);
                ArrayList<String> audioResult = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                assert audioResult != null;
                edtComment.setText(tempText + " " + audioResult.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        recognizeAudio.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isCallActive(requireContext())) {
                    if (allowSpeech) {
                        allowSpeech = false;
                        rippleObjectView.stopImmediately();
                        recognizeAudio.setImageResource(R.drawable.mic_red);
                        Glide.with(getContext()).load(R.drawable.record_gif).into(audioRecordingLoader);
                        audioRecordingLoader.setVisibility(View.VISIBLE);
                        speechRecognizer.startListening(speechRecognitionIntent);
                    }
                } else {
                    Toast.makeText(getContext(), "Mic already in use in another app", Toast.LENGTH_SHORT).show();
                }

            }
            return false;
        });

        final int[] stepCount = {1};

        //stopStepCount = false;


        tvNext.setOnClickListener(view -> {

//            tvTitle.setVisibility(View.VISIBLE);
            edtSearch.setText("");
            llSearch.setVisibility(View.GONE);
            searchImage.setVisibility(View.VISIBLE);
            hideKeyboardwithoutPopulate(edtSearch, requireActivity(), false);
//
//            // ArrayList<String> actList = getActivityList();
//
            if (selectedActivity.isEmpty()) {

                if (activities.size() == 1) {
                    activity = activities.get(0);
                    selectedActivity = activity;
                } else if (activities.size() == 2) {
                    activity = activities.get(0);
                    activity2 = activities.get(1);
                    selectedActivity = activity + " & " + activity2;
                }

            }

            if (rvActivityList.isShown())
                viewIdList.add(rvActivityList.getId());
            else if (rvTownList.isShown())
                viewIdList.add(rvTownList.getId());
            else if (rvDistributorList.isShown())
                viewIdList.add(rvDistributorList.getId());
            else if (rvEmployeeList.isShown())
                viewIdList.add(rvEmployeeList.getId());
            else if (rvMeetingList.isShown())
                viewIdList.add(rvMeetingList.getId());
            else if (llCommentAct.isShown())
                viewIdList.add(llCommentAct.getId());

            notListed = 0;

            DateFormat timeFormat = new SimpleDateFormat(getString(R.string.timeformat), Locale.ENGLISH);
            String checkInTime = timeFormat.format(Calendar.getInstance().getTime());
//
//            Log.e(TAG, "Selected activity: " + selectedActivity);
//
////            if (!stopStepCount)
////                stepCount[0]++;
//
            String from = edtFromNS.getText().toString();
            String to = edtToNS.getText().toString();
            String vanNumber = edtFromNS.getText().toString();
            String driverName = edtToNS.getText().toString();
//            edtComment.getText().clear();
            comment = edtComment.getText().toString();
//            stopStepCount = false;


            switch (selectedActivity) {


                case SbAppConstants.ACTIVITY1:
                case SbAppConstants.ACTIVITY14:
                case SbAppConstants.ACTIVITY16:
                case SbAppConstants.ACTIVITY17:
                case SbAppConstants.ACTIVITY19:
                case SbAppConstants.ACTIVITY41:
                case SbAppConstants.ACTIVITY61:
                case SbAppConstants.ACTIVITY71:
                case SbAppConstants.ACTIVITY91:

//                    Log.e(TAG, " ---->" + stepCount[0]);
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg, llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showPlanDetailsView(llCommentAct, edtComment,
                                    llNext, tvTitle, tvNext, searchImage,
                                    flagTomorrowPlan);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (llCommentAct.isShown()) {

                        if (!comment.isEmpty()) {
                            // stopStepCount = true;
                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment);
                            else
                                startService("present", "-----",
                                        checkInTime, "-----", "");
                        } else {
                            Toast.makeText(getContext(), "Comment is mandatory", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
                case SbAppConstants.ACTIVITY2:
                case SbAppConstants.ACTIVITY12:
                case SbAppConstants.ACTIVITY21:
                case SbAppConstants.ACTIVITY24:
                case SbAppConstants.ACTIVITY26:
                case SbAppConstants.ACTIVITY27:
                case SbAppConstants.ACTIVITY29:
                case SbAppConstants.ACTIVITY42:
                case SbAppConstants.ACTIVITY62:
                case SbAppConstants.ACTIVITY72:
                case SbAppConstants.ACTIVITY92:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg,
                                    tvNotListed, tvTitle, actvEmpList, false);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (rvEmployeeList.isShown()) {
                        if (!empName.isEmpty()) {
                            hideAllRv();
                            showPlanDetailsView(llCommentAct, edtComment, llNext,
                                    tvTitle, tvNext, searchImage, flagTomorrowPlan);
                        } else {
                            showToast("Please select employee working with");
                        }
                    } else if (llCommentAct.isShown()) {
                        if (!comment.isEmpty()) {

                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment);
                            else
                                startService("present", "JW with employee: " + jointwrkempname,
                                        checkInTime, "-----", "");
                        } else {
                            Toast.makeText(getContext(), "Comment is mandatory", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY3:
                case SbAppConstants.ACTIVITY13:
                case SbAppConstants.ACTIVITY31:
                case SbAppConstants.ACTIVITY34:
                case SbAppConstants.ACTIVITY36:
                case SbAppConstants.ACTIVITY37:
                case SbAppConstants.ACTIVITY39:
                case SbAppConstants.ACTIVITY43:
                case SbAppConstants.ACTIVITY63:
                case SbAppConstants.ACTIVITY73:
                case SbAppConstants.ACTIVITY93:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showMeetingList(llNext, tvNotListed, tvTitle,
                                    actvMeetingList, false);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (rvMeetingList.isShown()) {
                        if (!meeting.isEmpty()) {
                            hideAllRv();
                            showPlanDetailsView(llCommentAct, edtComment,
                                    llNext, tvTitle, tvNext, searchImage, flagTomorrowPlan);
                        } else {
                            showToast("Please select meeting type");
                        }
                    } else if (llCommentAct.isShown()) {
                        if (!comment.isEmpty()) {
                            // stopStepCount = true;
                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment);
                            else
                                startService("present", "Meeting with type: " + meeting,
                                        checkInTime, "-----", "");
                        } else {
                            Toast.makeText(getContext(), "Comment is mandatory", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY4:
                case SbAppConstants.ACTIVITY6:
                case SbAppConstants.ACTIVITY7:
                case SbAppConstants.ACTIVITY9:
                case SbAppConstants.ACTIVITY46:
                case SbAppConstants.ACTIVITY47:
                case SbAppConstants.ACTIVITY49:
                case SbAppConstants.ACTIVITY64:
                case SbAppConstants.ACTIVITY67:
                case SbAppConstants.ACTIVITY69:
                case SbAppConstants.ACTIVITY74:
                case SbAppConstants.ACTIVITY76:
                case SbAppConstants.ACTIVITY79:
                case SbAppConstants.ACTIVITY94:
                case SbAppConstants.ACTIVITY96:
                case SbAppConstants.ACTIVITY97:

                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            showPlanDetailsView(llCommentAct, edtComment,
                                    llNext, tvTitle, tvNext, searchImage, flagTomorrowPlan);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (llCommentAct.isShown()) {
                        if (!comment.isEmpty()) {
                            //  stopStepCount = true;
                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment);
                            else
                                startService("present", "-----",
                                        checkInTime, "-----", "");
                        } else {
                            Toast.makeText(getContext(), "Comment is mandatory", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY5:
                    hideAllRv();
                    // stopStepCount = true;
                    if (!from.isEmpty()) {

                        if (!to.isEmpty()) {

                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                            else
                                startService("present", "Travelling from: " + from + " to " + to,
                                        checkInTime, "-----", "");

                        } else {

                            Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SbAppConstants.ACTIVITY8:
                    hideAllRv();
                    //  stopStepCount = true;
                    if (!vanNumber.isEmpty()) {

                        if (!driverName.isEmpty()) {

                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment + " Van number is: " + vanNumber + " with driver " + driverName);
                            else
                                startService("present", "Van number is: " + vanNumber + " with driver " + driverName,
                                        checkInTime, "-----", "");

                        } else {

                            Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case SbAppConstants.ACTIVITY15:
                case SbAppConstants.ACTIVITY51:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            //  stopStepCount = true;
                            if (!from.isEmpty()) {

                                if (!to.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                                    else
                                        startService("present", "Travelling from: " + from + " to " + to,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select working distributor");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY18:
                case SbAppConstants.ACTIVITY81:

                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {

                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            // stopStepCount = true;
                            if (!vanNumber.isEmpty()) {

                                if (!driverName.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Van number is: " + vanNumber + " with driver " + driverName);
                                    else
                                        startService("present", "Van number is: " + vanNumber + " with driver " + driverName,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select working distributor");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY23:
                case SbAppConstants.ACTIVITY32:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showEmployeeList(llNewStartWorkList, llNext,
                                    tvLoadingEmpListMsg,
                                    tvNotListed, tvTitle, actvEmpList, false);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (rvEmployeeList.isShown()) {
                        if (empName.isEmpty()) {
                            hideAllRv();
                            showMeetingList(llNext, tvNotListed, tvTitle,
                                    actvMeetingList, false);
                        } else {
                            showToast("Please select employee working with");
                        }
                    } else if (rvMeetingList.isShown()) {
                        if (!meeting.isEmpty()) {
                            hideAllRv();
                            showPlanDetailsView(llCommentAct, edtComment,
                                    llNext, tvTitle, tvNext, searchImage, flagTomorrowPlan);
                        } else {
                            showToast("Please select meeting type");
                        }
                    } else if (llCommentAct.isShown()) {
                        if (!comment.isEmpty()) {
                            ///  stopStepCount = true;
                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment);
                            else
                                startService("present", "JW with employee: " + empName,
                                        checkInTime, "-----", "");
                        } else {
                            showToast("Comment is mandatory");
                        }
                    }

                    break;
                case SbAppConstants.ACTIVITY25:
                case SbAppConstants.ACTIVITY52:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg,
                                    tvNotListed, tvTitle, actvEmpList, false);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (rvEmployeeList.isShown()) {
                        if (!empName.isEmpty()) {
                            hideAllRv();
                            //     stopStepCount = true;
                            if (!from.isEmpty()) {

                                if (!to.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                                    else
                                        startService("present", "Travelling from: " + from + " to " + to,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select employee working with");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY28:
                case SbAppConstants.ACTIVITY82:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg,
                                    tvNotListed, tvTitle, actvEmpList, false);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (rvEmployeeList.isShown()) {
                        if (!empName.isEmpty()) {
                            hideAllRv();
                            // stopStepCount = true;
                            if (!vanNumber.isEmpty()) {

                                if (!driverName.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Van number is: " + vanNumber + " with driver " + driverName);
                                    else
                                        startService("present", "Van number is: " + vanNumber + " with driver " + driverName,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select employee working with");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY35:
                case SbAppConstants.ACTIVITY53:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showMeetingList(llNext, tvNotListed, tvTitle,
                                    actvMeetingList, false);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (rvMeetingList.isShown()) {
                        if (!meeting.isEmpty()) {
                            hideAllRv();
                            // stopStepCount = true;
                            if (!from.isEmpty()) {

                                if (!to.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                                    else
                                        startService("present", "Travelling from: " + from + " to " + to,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select meeting type");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY38:
                case SbAppConstants.ACTIVITY83:
                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                            showDistributorList(llNewStartWorkList, tvDistNotListed,
                                    llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                    flagTownNotListed[0]);
                        } else {
                            showToast("Please select working town");
                        }
                    } else if (rvDistributorList.isShown()) {
                        if (!did.isEmpty()) {
                            hideAllRv();
                            showMeetingList(llNext, tvNotListed, tvTitle,
                                    actvMeetingList, false);
                        } else {
                            showToast("Please select working distributor");
                        }
                    } else if (rvMeetingList.isShown()) {
                        if (!meeting.isEmpty()) {
                            hideAllRv();
                            // stopStepCount = true;
                            if (!vanNumber.isEmpty()) {

                                if (!driverName.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Van number is: " + vanNumber + " with driver " + driverName);
                                    else
                                        startService("present", "Van number is: " + vanNumber + " with driver " + driverName,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select meeting type");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY45:
                case SbAppConstants.ACTIVITY54:
                case SbAppConstants.ACTIVITY56:
                case SbAppConstants.ACTIVITY57:
                case SbAppConstants.ACTIVITY59:
                case SbAppConstants.ACTIVITY65:
                case SbAppConstants.ACTIVITY75:
                case SbAppConstants.ACTIVITY95:

                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            // stopStepCount = true;
                            if (!from.isEmpty()) {

                                if (!to.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                                    else
                                        startService("present", "Travelling from: " + from + " to " + to,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select working town");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY48:
                case SbAppConstants.ACTIVITY68:
                case SbAppConstants.ACTIVITY78:
                case SbAppConstants.ACTIVITY84:
                case SbAppConstants.ACTIVITY86:
                case SbAppConstants.ACTIVITY87:
                case SbAppConstants.ACTIVITY89:
                case SbAppConstants.ACTIVITY98:

                    if (rvActivityList.isShown()) {
                        hideAllRv();
                        flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                        showTownList(llNext, tvDistNotListed, tvNotListed,
                                tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                llNewStartWorkList);
                    } else if (rvTownList.isShown()) {
                        if (!workingTown.isEmpty()) {
                            hideAllRv();
                            // stopStepCount = true;
                            if (!vanNumber.isEmpty()) {

                                if (!driverName.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Van number is: " + vanNumber + " with driver " + driverName);
                                    else
                                        startService("present", "Van number is: " + vanNumber + " with driver " + driverName,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("Please select working town");
                        }
                    }
                    break;
                case SbAppConstants.ACTIVITY58:
                case SbAppConstants.ACTIVITY85:
                    hideAllRv();

                    // stopStepCount = true;
                    if (!from.isEmpty()) {

                        if (!to.isEmpty()) {

                            if (!vanNumber.isEmpty()) {

                                if (!driverName.isEmpty()) {

                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to +
                                                "Van number is: " + vanNumber + " with driver " + driverName);
                                    else
                                        startService("present", "Travelling from: " + from + " to " + to +
                                                        "Van number is: " + vanNumber + " with driver " + driverName,
                                                checkInTime, "-----", "");

                                } else {

                                    Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
                    }

                    break;

                default:

                    if (activity.isEmpty())
                        Toast.makeText(getContext(), "Please select atleast one activity", Toast.LENGTH_SHORT).show();
                    else if (flagTomorrowPlan && activity.equalsIgnoreCase("I will be on leave"))
                        submitTomorrowPlan(comment);

                    break;
            }

        });


        imgEmpList.setOnClickListener(view -> {
            hideAllRv();
            Log.e(TAG, "imgEmpList clicked");
            //simpleDialog = null;
            showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg, tvNotListed, tvTitle, actvEmpList, true);
            //rvEmployeeList.setVisibility(View.VISIBLE);

        });


        imgMeetingList.setOnClickListener(view -> {
            hideAllRv();
            Log.e(TAG, "imgMeetingList clicked");
            //simpleDialog = null;
            showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, true);
            //rvMeetingList.setVisibility(View.VISIBLE);
            //simpleDialog.show();
        });


        tvDistNotListed.setOnClickListener(view -> {

            disNotListed[0] = true;

            if (rvActivityList.isShown())
                viewIdList.add(rvActivityList.getId());
            else if (rvTownList.isShown())
                viewIdList.add(rvTownList.getId());
            else if (rvDistributorList.isShown())
                viewIdList.add(rvDistributorList.getId());
            else if (rvEmployeeList.isShown())
                viewIdList.add(rvEmployeeList.getId());
            else if (rvMeetingList.isShown())
                viewIdList.add(rvMeetingList.getId());


            if (tvDistNotListed.isShown())
                notListed = tvDistNotListed.getId();

            if (activity.isEmpty()) {

                ArrayList<String> actList = getActivityList();

                if (actList.size() > 0)
                    activity = actList.get(0);

                if (actList.size() > 1)
                    activity2 = actList.get(1);
            }
            ;

            if (flagTomorrowPlan)
                tvTitle.setText("Explain tomorrow's plan in details.");
            else
                tvTitle.setText("Explain today's plan in details.");

            hideAllRv();

            tvNotListed.setVisibility(View.GONE);
            llNext.setVisibility(View.GONE);
            llDone.setVisibility(View.VISIBLE);
            llTownDist.setVisibility(View.VISIBLE);
            llCommentAct.setVisibility(View.VISIBLE);
            rlTownList.setVisibility(View.GONE);

            switch (activity) {

                case SbAppConstants.ACTIVITY2:

                    //showEmployeeList(llNext, tvLoadingMsg, tvNotListed, tvTitle, actvEmpList, true);
                    rlEmpList.setVisibility(View.VISIBLE);
                    edtDistributorName.setVisibility(View.VISIBLE);

                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY3)) {

                        // showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, true);
                        rlMeetingList.setVisibility(View.VISIBLE);

                    } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5)) {

                        llTravelling.setVisibility(View.VISIBLE);

                    } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8)) {

                        llVanSales.setVisibility(View.VISIBLE);
                    }


                    break;

                case SbAppConstants.ACTIVITY3:

                    //showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, true);
                    rlMeetingList.setVisibility(View.VISIBLE);

                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5)) {

                        llTravelling.setVisibility(View.VISIBLE);

                    } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8)) {

                        llVanSales.setVisibility(View.VISIBLE);
                    }

                    break;

                case SbAppConstants.ACTIVITY5:

                    llTravelling.setVisibility(View.VISIBLE);
                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8))
                        llVanSales.setVisibility(View.VISIBLE);

                    break;

                case SbAppConstants.ACTIVITY8:

                    llVanSales.setVisibility(View.VISIBLE);

                    break;

                default:
                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5))
                        llTravelling.setVisibility(View.VISIBLE);
                    else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8))
                        llVanSales.setVisibility(View.VISIBLE);
                    break;
            }
        });

        tvNotListed.setOnClickListener(view -> {

            disNotListed[0] = false;
            //getUnListedCityList(actvTownList);

            if (rvActivityList.isShown())
                viewIdList.add(rvActivityList.getId());
            else if (rvTownList.isShown())
                viewIdList.add(rvTownList.getId());
            else if (rvDistributorList.isShown())
                viewIdList.add(rvDistributorList.getId());
            else if (rvEmployeeList.isShown())
                viewIdList.add(rvEmployeeList.getId());
            else if (rvMeetingList.isShown())
                viewIdList.add(rvMeetingList.getId());


            if (tvNotListed.isShown())
                notListed = tvNotListed.getId();


            if (activity.isEmpty()) {

                ArrayList<String> actList = getActivityList();

                if (actList.size() > 0)
                    activity = actList.get(0);

                if (actList.size() > 1)
                    activity2 = actList.get(1);
            }

            if (flagTomorrowPlan)
                tvTitle.setText("Explain tomorrow's plan in details.");
            else
                tvTitle.setText("Explain today's plan in details.");

            hideAllRv();

            tvNotListed.setVisibility(View.GONE);
            llNext.setVisibility(View.GONE);
            llDone.setVisibility(View.VISIBLE);
            llTownDist.setVisibility(View.VISIBLE);
            llCommentAct.setVisibility(View.VISIBLE);

            switch (activity) {

                case SbAppConstants.ACTIVITY2:

                    //showEmployeeList(llNext, tvLoadingMsg, tvNotListed, tvTitle, actvEmpList, true);
                    rlEmpList.setVisibility(View.VISIBLE);
                    edtDistributorName.setVisibility(View.VISIBLE);

                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY3)) {

                        // showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, true);
                        rlMeetingList.setVisibility(View.VISIBLE);

                    } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5)) {

                        llTravelling.setVisibility(View.VISIBLE);

                    } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8)) {

                        llVanSales.setVisibility(View.VISIBLE);
                    }


                    break;

                case SbAppConstants.ACTIVITY3:

                    //showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, true);
                    rlMeetingList.setVisibility(View.VISIBLE);

                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5)) {

                        llTravelling.setVisibility(View.VISIBLE);

                    } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8)) {

                        llVanSales.setVisibility(View.VISIBLE);
                    }

                    break;

                case SbAppConstants.ACTIVITY5:

                    llTravelling.setVisibility(View.VISIBLE);
                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8))
                        llVanSales.setVisibility(View.VISIBLE);

                    break;

                case SbAppConstants.ACTIVITY8:

                    llVanSales.setVisibility(View.VISIBLE);

                    break;

                default:
                    if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5))
                        llTravelling.setVisibility(View.VISIBLE);
                    else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8))
                        llVanSales.setVisibility(View.VISIBLE);
                    break;
            }

        });


        llDone.setOnClickListener(view -> {

            if (!disNotListed[0])
                workingTown = edtTownName.getText().toString();
            distributorName = edtDistributorName.getText().toString();
            empName = actvEmpList.getText().toString();
            meeting = actvMeetingList.getText().toString();
            comment = edtComment.getText().toString();
            String from = edtFromNS.getText().toString();
            String to = edtToNS.getText().toString();
            String vanNumber = edtVanNumber.getText().toString();
            String driverName = edtDriverName.getText().toString();


            ArrayList<String> actList = getActivityList();

            String activity = "";
            if (actList.size() > 0)
                activity = actList.get(0);

            String activity2 = "";
            if (actList.size() > 1)
                activity2 = actList.get(1);

            DateFormat timeFormat = new SimpleDateFormat(getString(R.string.timeformat), Locale.ENGLISH);
            String checkInTime = timeFormat.format(Calendar.getInstance().getTime());

            if (!workingTown.isEmpty()) {

                switch (activity) {

                    case SbAppConstants.ACTIVITY2:

                        if (!distributorName.isEmpty()) {

                            if (!empName.isEmpty()) {


                                if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY3)) {


                                    if (!meeting.isEmpty())
                                        if (!comment.isEmpty())
                                            if (flagTomorrowPlan)
                                                submitTomorrowPlan(comment);
                                            else
                                                startService("present", comment, checkInTime, "------", "");
                                        else
                                            Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(requireContext(), "Meeting type is empty", Toast.LENGTH_SHORT).show();


                                } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5)) {

                                    if (!from.isEmpty())
                                        if (!to.isEmpty())
                                            if (!comment.isEmpty())
                                                if (flagTomorrowPlan)
                                                    submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                                                else
                                                    startService("present", comment + " Travelling from: " + from + " to " + to,
                                                            checkInTime, "------", "");
                                            else
                                                Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(requireContext(), "To is empty", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(requireContext(), "From is empty", Toast.LENGTH_SHORT).show();

                                } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8)) {

                                    if (!vanNumber.isEmpty())
                                        if (!driverName.isEmpty())
                                            if (!comment.isEmpty())
                                                if (flagTomorrowPlan)
                                                    submitTomorrowPlan(comment + " Van number is: " + vanNumber + " with driver " + driverName);
                                                else
                                                    startService("present", comment + " Van number is: " + vanNumber + " with driver "
                                                            + driverName, checkInTime, "------", "");
                                            else
                                                Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(requireContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(requireContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                                } else {

                                    if (!comment.isEmpty())
                                        if (flagTomorrowPlan)
                                            submitTomorrowPlan(comment);
                                        else
                                            startService("present", comment, checkInTime, "------", "");
                                    else
                                        Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                }


                            } else {

                                Toast.makeText(requireContext(), "Employee name is empty", Toast.LENGTH_SHORT).show();
                            }


                        } else {

                            Toast.makeText(requireContext(), "Distributor is empty", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case SbAppConstants.ACTIVITY3:

                        if (!meeting.isEmpty()) {

                            if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY5)) {

                                if (!from.isEmpty())
                                    if (!to.isEmpty())
                                        if (!comment.isEmpty())
                                            if (flagTomorrowPlan)
                                                submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                                            else
                                                startService("present", comment + " Travelling from: " + from + " to " + to,
                                                        checkInTime, "------", "");
                                        else
                                            Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(requireContext(), "To is empty", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(requireContext(), "From is empty", Toast.LENGTH_SHORT).show();

                            } else if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8)) {

                                if (!vanNumber.isEmpty())
                                    if (!driverName.isEmpty())
                                        if (!comment.isEmpty())
                                            if (flagTomorrowPlan)
                                                submitTomorrowPlan(comment + " Van number is: " + vanNumber
                                                        + " with driver " + driverName);
                                            else
                                                startService("present", comment + " Van number is: " + vanNumber
                                                        + " with driver " + driverName, checkInTime, "------", "");
                                        else
                                            Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(requireContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(requireContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
                            } else {

                                if (!comment.isEmpty())
                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment);
                                    else
                                        startService("present", comment, checkInTime, "------", "");
                                else
                                    Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                            }


                        } else {

                            Toast.makeText(requireContext(), "Meeting type is empty", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case SbAppConstants.ACTIVITY5:

                        if (!from.isEmpty())
                            if (!to.isEmpty())
                                if (activity2.equalsIgnoreCase(SbAppConstants.ACTIVITY8)) {

                                    if (!vanNumber.isEmpty())
                                        if (!driverName.isEmpty())
                                            if (!comment.isEmpty())
                                                if (flagTomorrowPlan)
                                                    submitTomorrowPlan(comment + " Van number is: " + vanNumber
                                                            + " with driver " + driverName + " Travelling from: " + from + " to " + to);
                                                else
                                                    startService("present", comment + " Van number is: " + vanNumber
                                                                    + " with driver " + driverName + " Travelling from: " + from + " to " + to,
                                                            checkInTime, "------", "");
                                            else
                                                Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(requireContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(requireContext(), "Van number is empty", Toast.LENGTH_SHORT).show();

                                } else {

                                    if (!comment.isEmpty())
                                        if (flagTomorrowPlan)
                                            submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
                                        else
                                            startService("present", comment + " Travelling from: " + from + " to " + to,
                                                    checkInTime, "------", "");
                                    else
                                        Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                                }
                            else
                                Toast.makeText(requireContext(), "To is empty", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(requireContext(), "From is empty", Toast.LENGTH_SHORT).show();

                        break;

                    case SbAppConstants.ACTIVITY8:

                        if (!vanNumber.isEmpty())
                            if (!driverName.isEmpty())
                                if (!comment.isEmpty())
                                    if (flagTomorrowPlan)
                                        submitTomorrowPlan(comment + " Van number is: " + vanNumber
                                                + " with driver " + driverName);
                                    else
                                        startService("present", comment + " Van number is: " + vanNumber
                                                + " with driver " + driverName, checkInTime, "------", "");
                                else
                                    Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(requireContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(requireContext(), "Van number is empty", Toast.LENGTH_SHORT).show();

                        break;

                    default:


                        if (!comment.isEmpty())
                            if (flagTomorrowPlan)
                                submitTomorrowPlan(comment);
                            else
                                startService("present", comment,
                                        checkInTime, "------", "");
                        else
                            Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();


                        break;
                }


            } else {
                Toast.makeText(getContext(), "Town is empty", Toast.LENGTH_SHORT).show();
            }
        });


        activityListDialog.setOnKeyListener((arg0, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            {

                //@Umesh 20220908
                activities.clear();
                workingTown = "";
                did = "";
                distributorName = "";
                comment = "";
                empName = "";
                meeting = "";
                actCount = 0;
                selectedActivity = "";
                IsOther=false;
                activityListDialog.dismiss();



                // tvLoadingMsg.setVisibility(View.GONE);
//                if (stepCount[0] != 0)
//                    stepCount[0]--;

//                if (rvActivityList.isShown())
//                    activityListDialog.dismiss();
//                else if (rvTownList.isShown())
//                    rvTownList.setVisibility(View.GONE);
//                else if (rvDistributorList.isShown())
//                    rvDistributorList.setVisibility(View.GONE);
//                else if (rvEmployeeList.isShown())
//                    rvEmployeeList.setVisibility(View.GONE);
//                else if (rvMeetingList.isShown())
//                    rvMeetingList.setVisibility(View.GONE);
//                else if (llCommentAct.isShown())
//                    llCommentAct.setVisibility(View.GONE);
//
//                if (llCommentAct.isShown() && rvTownList.isShown())
//                    searchImage.setVisibility(View.GONE);
//                else
//                    searchImage.setVisibility(View.VISIBLE);
//
//
//                if (viewIdList.size() != 0) {
//
//                    int index = viewIdList.size() - 1;
//                    Log.e(TAG, " Size--->" + viewIdList.size() + " index" + index);
//                    int id = viewIdList.get(index);
//
//                    tvNext.setText("Next");
//
//                    if (rvActivityList.getId() == id) {
//
//                        if (flagTomorrowPlan)
//                            tvTitle.setText("What will be your activity tomorrow?");
//                        else
//                            tvTitle.setText("What will be your activity today?");
//
//                        rvActivityList.setVisibility(View.VISIBLE);
//                        //To remove previous checks when user press back
//                        rvActivityList.setAdapter(tempAdapter);
//                        Log.e("ACtivity--->", "showActivityList: " + viewIdList);
//
//                        activity = "";
//                        if (townListEmpty) {
//                            llNewStartWorkList.setVisibility(View.VISIBLE);
//                            tvLoadingMsg.setVisibility(View.GONE);
//                        }
//                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
//
//                    } else if (rvTownList.getId() == id) {
//
//                        tvTitle.setText("Select working town.");
//                        rvTownList.setVisibility(View.VISIBLE);
//                    } else if (rvDistributorList.getId() == id) {
//
//                        tvTitle.setText("Select distributor name.");
//                        rvDistributorList.setVisibility(View.VISIBLE);
//                    } else if (rvEmployeeList.getId() == id) {
//
//                        tvTitle.setText("Select employee name.");
//                        rvEmployeeList.setVisibility(View.VISIBLE);
//                    } else if (rvMeetingList.getId() == id) {
//
//                        tvTitle.setText("Select meeting type.");
//                        rvMeetingList.setVisibility(View.VISIBLE);
//
//                    }
//
//                    viewIdList.remove(index);
//                }
//
//
//                if (notListed == tvNotListed.getId()) {
//
//                    tvNotListed.setVisibility(View.VISIBLE);
//                    tvDistNotListed.setVisibility(View.GONE);
//                    llNext.setVisibility(View.VISIBLE);
//                    llDone.setVisibility(View.GONE);
//                    llTownDist.setVisibility(View.GONE);
//                    llCommentAct.setVisibility(View.GONE);
//                    disNotListed[0] = false;
//
//                    notListed = 0;
//
//                } else if (notListed == tvDistNotListed.getId()) {
//
//                    tvDistNotListed.setVisibility(View.VISIBLE);
//                    tvNotListed.setVisibility(View.GONE);
//                    llNext.setVisibility(View.VISIBLE);
//                    llDone.setVisibility(View.GONE);
//                    llTownDist.setVisibility(View.GONE);
//                    llCommentAct.setVisibility(View.GONE);
//                    disNotListed[0] = false;
//
//                    notListed = tvNotListed.getId();
//
//                } else {
//
//                    if (tvDistNotListed.isShown() && rvTownList.isShown()) {
//
//                        tvNotListed.setVisibility(View.VISIBLE);
//                        tvDistNotListed.setVisibility(View.GONE);
//                    } else if (tvNotListed.isShown()) {
//                        tvNotListed.setVisibility(View.GONE);
//                        tvDistNotListed.setVisibility(View.GONE);
//                    }
//
//                }
//
//
//                activities.clear();
//                workingTown = "";
//                did = "";
//                distributorName = "";
//                comment = "";
//                empName = "";
//                meeting = "";
//                actCount = 0;

//                DateFormat timeFormat = new SimpleDateFormat(getString(R.string.timeformat), Locale.ENGLISH);
//                String checkInTime = timeFormat.format(Calendar.getInstance().getTime());
//
//                String from = edtFromNS.getText().toString();
//                String to = edtToNS.getText().toString();
//                String vanNumber = edtFromNS.getText().toString();
//                String driverName = edtToNS.getText().toString();

                if (rvActivityList.isShown()) {
                    activities.clear();
                    workingTown = "";
                    did = "";
                    distributorName = "";
                    comment = "";
                    empName = "";
                    meeting = "";
                    actCount = 0;
                    selectedActivity = "";
                    activityListDialog.dismiss();
                } else if (rvTownList.isShown()) {
                    activities.clear();
                    selectedActivity = "";
                    tvNext.setText("Next");
                    llDone.setVisibility(View.GONE);
                    llNext.setVisibility(View.VISIBLE);
                    if (llSearch.isShown())
                        llSearch.setVisibility(View.GONE);
                    if (flagTomorrowPlan)
                        tvTitle.setText("What will be your activity tomorrow?");
                    else
                        tvTitle.setText("What will be your activity today?");

                    initActivityList(llNext, flagTomorrowPlan, tvNext, tvNotListed);

                    edtSearch.setText("");
                    llSearch.setVisibility(View.GONE);
                    searchImage.setVisibility(View.GONE);

                } else {
                    tvNext.setText("Next");
                    llDone.setVisibility(View.GONE);
                    llNext.setVisibility(View.VISIBLE);
                    edtSearch.setText("");
                    llSearch.setVisibility(View.GONE);
                    searchImage.setVisibility(View.VISIBLE);
                    switch (selectedActivity) {
                        case SbAppConstants.ACTIVITY1:
                        case SbAppConstants.ACTIVITY14:
                        case SbAppConstants.ACTIVITY16:
                        case SbAppConstants.ACTIVITY17:
                        case SbAppConstants.ACTIVITY19:
                        case SbAppConstants.ACTIVITY41:
                        case SbAppConstants.ACTIVITY61:
                        case SbAppConstants.ACTIVITY71:
                        case SbAppConstants.ACTIVITY91:

                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg, llNewStartWorkList);
                            } else if (llCommentAct.isShown()) {
                                hideAllRv();
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed, flagTownNotListed[0]);
                            } /*else if (rvDistributorList.isShown()) {
                                hideAllRv();
                                showPlanDetailsView(llCommentAct, edtComment, llNext,
                                        tvTitle, tvNext, searchImage, flagTomorrowPlan);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY2:
                        case SbAppConstants.ACTIVITY12:
                        case SbAppConstants.ACTIVITY21:
                        case SbAppConstants.ACTIVITY24:
                        case SbAppConstants.ACTIVITY26:
                        case SbAppConstants.ACTIVITY27:
                        case SbAppConstants.ACTIVITY29:
                        case SbAppConstants.ACTIVITY42:
                        case SbAppConstants.ACTIVITY62:
                        case SbAppConstants.ACTIVITY72:
                        case SbAppConstants.ACTIVITY92:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } else if (rvEmployeeList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            } else if (llCommentAct.isShown()) {
                                hideAllRv();
                                showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg,
                                        tvNotListed, tvTitle, actvEmpList, false);
                            } /*else if (rvEmployeeList.isShown()) {
                                hideAllRv();
                                showPlanDetailsView(llCommentAct, edtComment, llNext, tvTitle, tvNext,
                                        searchImage, flagTomorrowPlan);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY3:
                        case SbAppConstants.ACTIVITY13:
                        case SbAppConstants.ACTIVITY31:
                        case SbAppConstants.ACTIVITY34:
                        case SbAppConstants.ACTIVITY36:
                        case SbAppConstants.ACTIVITY37:
                        case SbAppConstants.ACTIVITY39:
                        case SbAppConstants.ACTIVITY43:
                        case SbAppConstants.ACTIVITY63:
                        case SbAppConstants.ACTIVITY73:
                        case SbAppConstants.ACTIVITY93:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } else if (rvMeetingList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            } else if (llCommentAct.isShown()) {
                                hideAllRv();
                                showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, false);
                            } /*else if (rvMeetingList.isShown()) {
                                hideAllRv();
                                showPlanDetailsView(llCommentAct, edtComment, llNext,
                                        tvTitle, tvNext, searchImage, flagTomorrowPlan);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY4:
                        case SbAppConstants.ACTIVITY6:
                        case SbAppConstants.ACTIVITY7:
                        case SbAppConstants.ACTIVITY9:
                        case SbAppConstants.ACTIVITY46:
                        case SbAppConstants.ACTIVITY47:
                        case SbAppConstants.ACTIVITY49:
                        case SbAppConstants.ACTIVITY64:
                        case SbAppConstants.ACTIVITY67:
                        case SbAppConstants.ACTIVITY69:
                        case SbAppConstants.ACTIVITY74:
                        case SbAppConstants.ACTIVITY76:
                        case SbAppConstants.ACTIVITY79:
                        case SbAppConstants.ACTIVITY94:
                        case SbAppConstants.ACTIVITY96:
                        case SbAppConstants.ACTIVITY97:

                            if (llCommentAct.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } /*else if (rvTownList.isShown()) {
                                hideAllRv();
                                showPlanDetailsView(llCommentAct, edtComment,
                                        llNext, tvTitle, tvNext, searchImage, flagTomorrowPlan);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY5:
                            hideAllRv();
                            //if (stepCount[0] == 2) {

//                        stopStepCount = true;
//                        if (!from.isEmpty()) {
//
//                            if (!to.isEmpty()) {
//
//                                if (flagTomorrowPlan)
//                                    submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to);
//                                else
//                                    startService("present", "Travelling from: " + from + " to " + to,
//                                            checkInTime, "-----", "");
//
//                            } else {
//
//                                Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//
//                            Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
//                        }
                            //}
                            break;
                        case SbAppConstants.ACTIVITY8:
                            hideAllRv();
                            //if (stepCount[0] == 2) {

//                        stopStepCount = true;
//                        if (!vanNumber.isEmpty()) {
//
//                            if (!driverName.isEmpty()) {
//
//                                if (flagTomorrowPlan)
//                                    submitTomorrowPlan(comment + " Van number is: " + vanNumber + " with driver " + driverName);
//                                else
//                                    startService("present", "Van number is: " + vanNumber + " with driver " + driverName,
//                                            checkInTime, "-----", "");
//
//                            } else {
//
//                                Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//
//                            Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
//                        }
                            //}
                            break;

                        case SbAppConstants.ACTIVITY15:
                        case SbAppConstants.ACTIVITY51:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } /*else if (rvTownList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY18:
                        case SbAppConstants.ACTIVITY81:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } /*else if (rvTownList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY23:
                        case SbAppConstants.ACTIVITY32:

                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } else if (rvEmployeeList.isShown()) {
                                hideAllRv();
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            } else if (rvMeetingList.isShown()) {
                                hideAllRv();
                                showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg,
                                        tvNotListed, tvTitle, actvEmpList, false);
                            } else if (llCommentAct.isShown()) {
                                hideAllRv();
                                showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, false);
                            } /*else if (rvMeetingList.isShown()) {
                                hideAllRv();
                                showPlanDetailsView(llCommentAct, edtComment, llNext,
                                        tvTitle, tvNext, searchImage, flagTomorrowPlan);
                            }*/

                            break;
                        case SbAppConstants.ACTIVITY25:
                        case SbAppConstants.ACTIVITY52:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } else if (rvEmployeeList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            } /*else if (rvDistributorList.isShown()) {
                                hideAllRv();
                                showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg,
                                        tvNotListed, tvTitle, actvEmpList, false);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY28:
                        case SbAppConstants.ACTIVITY82:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } else if (rvEmployeeList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            } /*else if (rvDistributorList.isShown()) {
                                hideAllRv();
                                showEmployeeList(llNewStartWorkList, llNext, tvLoadingEmpListMsg,
                                        tvNotListed, tvTitle, actvEmpList, false);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY35:
                        case SbAppConstants.ACTIVITY53:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } else if (rvMeetingList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            } /*else if (rvDistributorList.isShown()) {
                                hideAllRv();
                                showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, false);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY38:
                        case SbAppConstants.ACTIVITY83:
                            if (rvDistributorList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            } else if (rvMeetingList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showDistributorList(llNewStartWorkList, tvDistNotListed,
                                        llNext, tvLoadingDistributorMsg, tvTitle, tvNotListed,
                                        flagTownNotListed[0]);
                            } /*else if (rvDistributorList.isShown()) {
                                hideAllRv();
                                showMeetingList(llNext, tvNotListed, tvTitle, actvMeetingList, false);
                            }*/
                            break;
                        case SbAppConstants.ACTIVITY45:
                        case SbAppConstants.ACTIVITY54:
                        case SbAppConstants.ACTIVITY56:
                        case SbAppConstants.ACTIVITY57:
                        case SbAppConstants.ACTIVITY59:
                        case SbAppConstants.ACTIVITY65:
                        case SbAppConstants.ACTIVITY75:
                        case SbAppConstants.ACTIVITY95:
                            if (rvActivityList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            }
                            break;
                        case SbAppConstants.ACTIVITY48:
                        case SbAppConstants.ACTIVITY68:
                        case SbAppConstants.ACTIVITY78:
                        case SbAppConstants.ACTIVITY84:
                        case SbAppConstants.ACTIVITY86:
                        case SbAppConstants.ACTIVITY87:
                        case SbAppConstants.ACTIVITY89:
                        case SbAppConstants.ACTIVITY98:
                            if (rvActivityList.isShown()) {
                                hideAllRv();
                                flagTownNotListed[0] = hideShowTownNotListed(selectedActivity);
                                showTownList(llNext, tvDistNotListed, tvNotListed,
                                        tvTitle, flagTownNotListed[0], tvLoadingTownMsg,
                                        llNewStartWorkList);
                            }
                            break;
                        case SbAppConstants.ACTIVITY58:
                        case SbAppConstants.ACTIVITY85:
                            hideAllRv();
                            //if (stepCount[0] == 2) {
//                        stopStepCount = true;
//                        if (!from.isEmpty()) {
//
//                            if (!to.isEmpty()) {
//
//                                if (!vanNumber.isEmpty()) {
//
//                                    if (!driverName.isEmpty()) {
//
//                                        if (flagTomorrowPlan)
//                                            submitTomorrowPlan(comment + " Travelling from: " + from + " to " + to +
//                                                    "Van number is: " + vanNumber + " with driver " + driverName);
//                                        else
//                                            startService("present", "Travelling from: " + from + " to " + to +
//                                                            "Van number is: " + vanNumber + " with driver " + driverName,
//                                                    checkInTime, "-----", "");
//
//                                    } else {
//
//                                        Toast.makeText(getContext(), "Driver name is empty", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                } else {
//
//                                    Toast.makeText(getContext(), "Van number is empty", Toast.LENGTH_SHORT).show();
//                                }
//
//                            } else {
//
//                                Toast.makeText(getContext(), "To is empty", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//
//                            Toast.makeText(getContext(), "From is empty", Toast.LENGTH_SHORT).show();
//                        }

                            //}
                            break;

                        default:

                            if (activity.isEmpty())
                                Toast.makeText(getContext(), "Please select atleast one activity", Toast.LENGTH_SHORT).show();
                            else if (flagTomorrowPlan && activity.equalsIgnoreCase("I will be on leave"))
                                submitTomorrowPlan(comment);
                            break;
                    }
                }


            }
            return false;
        });


//        activityListDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//
//
//            }
//        });


        activityListDialog.show();

    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadCampaign() {

        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> contentt = new ArrayList<>();
        ArrayList<String> filePathStrings = new ArrayList<>();
        Cursor cursor = null;
        filePathStrings.clear();
        contentt.clear();

        boolean noCampaign = false;
        boolean errorLoading = false;

        try {

            cursor = salesBeatDb.gettCampaignDetails();
            // Log.e(TAG, "######Cursor---->"+cursor.getCount());

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    String imgUrl = cursor.getString(cursor.getColumnIndex("campaign_img"));
                    String campContent = cursor.getString(cursor.getColumnIndex("campaign_content"));
                    //Log.e(TAG, "######---->" + campContent);

                    if (campContent.equals("no campaign available")) {
                        noCampaign = true;
                        break;
                    } else if (campContent.equals("Error in loading")) {
                        errorLoading = true;
                        break;
                    }

                    filePathStrings.add(imgUrl);
                    contentt.add(campContent);

                } while (cursor.moveToNext());

            } else if (cursor == null || cursor.getCount() == 0) {

                noCampaign = true;
            }

            // Log.e("Camp", "===>" + loaded + "  no camp: " + noCampaign + "  err: " + errorLoading);

            if (noCampaign) {
                promotionViewPager.setVisibility(View.INVISIBLE);
                noCampaignPic.setVisibility(View.VISIBLE);
                campaignLoader.setVisibility(View.GONE);
            } else if (errorLoading) {
                promotionViewPager.setVisibility(View.INVISIBLE);
                errorCampaign.setVisibility(View.VISIBLE);
                campaignLoader.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();

            list.add(0, filePathStrings);
            list.add(1, contentt);

            try {

                ArrayList<String> fileList = list.get(0);
                ArrayList<String> contentList = list.get(1);

                if (fileList.size() > 0) {

                    //Log.e("Camp File IN", "===>" + fileList.size());
                    loaded = true;
                    campaignLoader.setVisibility(View.INVISIBLE);
                    promotionViewPager.setVisibility(View.VISIBLE);
                    errorCampaign.setVisibility(View.GONE);
                    noCampaignPic.setVisibility(View.GONE);

                    pagerAdapter = new PromotionPagerAdapter(requireContext(), fileList, contentList);

                    if (pagerAdapter.getCount() > 1) {

                        promotionViewPager.setClipToPadding(false);
                        promotionViewPager.setPadding(0, 0, 100, 0);
                    }

                    promotionViewPager.setAdapter(pagerAdapter);


//                    promotionViewPager.setVisibility(View.VISIBLE);

                    if (promotionViewPager.getAdapter() != null)
                        promotionViewPager.getAdapter().notifyDataSetChanged();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void clipEmoticonsFromCopiedText() {

        ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String dataCopied = "";

        if (clipboardManager != null && clipboardManager.hasPrimaryClip()
                && clipboardManager.getPrimaryClip() != null) {

            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
            dataCopied = item.getText().toString();

            dataCopied = dataCopied.replaceAll("\\p{So}+", "");
            ClipData clip = ClipData.newPlainText("simple text", dataCopied);
            clipboardManager.setPrimaryClip(clip);
        }
    }

    private ArrayList<String> getActivityList() {

        ArrayList<String> actList = new ArrayList<>();
        ActivityListAdapter adapter = (ActivityListAdapter) rvActivityList.getAdapter();
        for (int position = 0; position < adapter.getItemCount(); position++) {

            ActivityListAdapter.MyViewHolder myViewHolder = (ActivityListAdapter.MyViewHolder)
                    rvActivityList.findViewHolderForAdapterPosition(position);
            if (myViewHolder != null) {

                CheckBox checkBox = myViewHolder.itemView.findViewById(R.id.chbActSelect);
                TextView tvActivityName = myViewHolder.itemView.findViewById(R.id.tvActivityName);
                if (checkBox.isChecked())
                    actList.add(tvActivityName.getText().toString());
            }

        }

        return actList;
    }

    @SuppressLint("SetTextI18n")
    private void showVanSalesView(LinearLayout llVanSales, LinearLayout llCommentAct,
                                  TextView tvTitle, TextView tvNext, boolean flagTomorrowPlan) {

        llVanSales.setVisibility(View.VISIBLE);
        llCommentAct.setVisibility(View.VISIBLE);

        if (flagTomorrowPlan)
            tvTitle.setText("Explain tomorrow's van sales plan in details.");
        else
            tvTitle.setText("Explain today's van sales plan in details.");

        tvNext.setText("Done");
    }

    @SuppressLint("SetTextI18n")
    private void showTravellingView(LinearLayout llTravelling, LinearLayout llCommentAct,
                                    TextView tvTitle, TextView tvNext, boolean flagTomorrowPlan) {

        llTravelling.setVisibility(View.VISIBLE);
        llCommentAct.setVisibility(View.VISIBLE);

        if (flagTomorrowPlan)
            tvTitle.setText("Explain tomorrow's travelling plan in details.");
        else
            tvTitle.setText("Explain today's travelling plan in details.");

        tvNext.setText("Done");

    }

//    @SuppressLint("SetTextI18n")
//    private void getUnListedCityList(AutoCompleteTextView actvTownList) {
//
//        String state = myPref.getString(getString(R.string.state_key), "");
//
//        try {
//            state = URLEncoder.encode(state, "utf-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//            // return "error";
//        }
//
//        actvTownList.setHint("Loading...");
//
//        StringRequest getCityListReq = new StringRequest(Request.Method.GET,
//                "https://indian-cities-api-nocbegfhqg.now.sh/cities?State=" + state,
//                response -> {
//
//                    Log.e(TAG, "------>" + response);
//                    //actvTownList.setText("Select town");
//
//                    try {
//                        JSONArray data = new JSONArray(response);
//                        ArrayList<String> cityList = new ArrayList<>();
//
//                        for (int i = 0; i < data.length(); i++) {
//                            JSONObject obj = (JSONObject) data.get(i);
//                            String city = obj.getString("City");
//                            cityList.add(city);
//                        }
//
//                        // showSimpleListDialog(actvTownList, cityList, "Select town.");
//
//                        ArrayAdapter<String> cityListAdapter = new ArrayAdapter<>(requireContext(),
//                                android.R.layout.simple_list_item_1, cityList);
//
//                        actvTownList.setAdapter(cityListAdapter);
//                        actvTownList.setHint("Search town");
//                        actvTownList.setBackgroundColor(Color.WHITE);
//                        //actvTownList.showDropDown();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }, error -> {
//            actvTownList.setHint("Please type town name");
//            actvTownList.setEnabled(true);
//            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
//            error.printStackTrace();
//        });
//
//        getCityListReq.setRetryPolicy(
//
//                new DefaultRetryPolicy(
//                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                )
//        );
//
//
//        Volley.newRequestQueue(requireContext()).add(getCityListReq);
//
//
////        showSimpleListDialog(actvMeetingList, meetingListArr, "Select meeting type.");
//    }

    @SuppressLint("SetTextI18n")
    private void showMeetingList(LinearLayout llNext, TextView tvNotListed,
                                 TextView tvTitle, AutoCompleteTextView actvMeetingList, boolean b) {

        rlMeetingList1.setVisibility(View.VISIBLE);
        ArrayList<String> meetingListArr = new ArrayList<>();
        meetingListArr.add("Office Meeting");
        meetingListArr.add("Distributor Meeting");
        meetingListArr.add("Others");

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));


        if (b) {


            showSimpleListDialog(actvMeetingList, meetingListArr, "Select meeting type.");


        } else {

            tvTitle.setText("Select meeting type.");

            ActivityListAdapter activityListAdapter = new ActivityListAdapter(getContext(), meetingListArr,
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                            meeting = meetingListArr.get(position);
                            llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                            llNext.setClickable(true);
                            tvNotListed.setClickable(false);
                            tvNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));
                        }

                        @Override
                        public void onItemClick2(int position) {
                            //AnimCheckBox animCheckBox =
                            meeting = "";
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            llNext.setClickable(false);
                            tvNotListed.setClickable(true);
                            tvNotListed.setBackgroundColor(Color.parseColor("#F0544D"));

                        }
                    }, false);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            int resId = R.anim.layout_animation_fall_down;
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
            rvMeetingList.setLayoutAnimation(animation);
            rvMeetingList.setLayoutManager(layoutManager);
            rvMeetingList.setAdapter(activityListAdapter);

            rvMeetingList.setVisibility(View.VISIBLE);

        }

    }

    @SuppressLint("SetTextI18n")
    private void showTodaysSummary() {

        Dialog todaySummaryDialog = new Dialog(requireContext(), R.style.DialogActivityTheme);
        todaySummaryDialog.setContentView(R.layout.todays_summary);

        if (todaySummaryDialog.getWindow() != null) {

            todaySummaryDialog.getWindow().setGravity(Gravity.BOTTOM);
        }


        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(todaySummaryDialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.99f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.75f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        todaySummaryDialog.getWindow().setAttributes(layoutParams);

        todaySummaryDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        todaySummaryDialog.setCancelable(true);
        todaySummaryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        TextView tvtodaySummaryCaption = todaySummaryDialog.findViewById(R.id.todaySummaryCaption);
        tvtodaySummaryCaption.setText("Today's Summary As On..."+ DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));

        TextView tvTodaysTC = todaySummaryDialog.findViewById(R.id.tvTodaysTC);
        TextView tvTodaysPC = todaySummaryDialog.findViewById(R.id.tvTodaysPC);
        TextView tvTodaysNC = todaySummaryDialog.findViewById(R.id.tvTodaysNC);


        //@Umesh 20221004
        TextView tvDhamaka = todaySummaryDialog.findViewById(R.id.tvDhamaka);
        TextView tvRaid = todaySummaryDialog.findViewById(R.id.tvRaid);
        TextView tvReal_Gold = todaySummaryDialog.findViewById(R.id.tvReal_Gold);
        TextView tvReal_Gold_Classic = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Classic);
        TextView tvReal_Gold_Dust = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Dust);
        TextView tvReal_Gold_Perfact = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Perfact);
        TextView tvReal_Gold_Select = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Select);
        TextView tvReal_Lite = todaySummaryDialog.findViewById(R.id.tvReal_Lite);
        TextView tvReal_Taste = todaySummaryDialog.findViewById(R.id.tvReal_Taste);
        TextView tvROZ = todaySummaryDialog.findViewById(R.id.tvROZ);
        TextView tvElaichi = todaySummaryDialog.findViewById(R.id.tvElaichi);

        LinearLayout layout_Dhamaka = todaySummaryDialog.findViewById(R.id.layout_Dhamaka);
        LinearLayout layout_Raid = todaySummaryDialog.findViewById(R.id.layout_Raid);
        LinearLayout layout_Real_Gold = todaySummaryDialog.findViewById(R.id.layout_Real_Gold);
        LinearLayout layout_Real_Gold_Classic = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Classic);
        LinearLayout layout_Real_Gold_Dust = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Dust);
        LinearLayout layout_Real_Gold_Perfact = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Perfact);
        LinearLayout layout_Real_Gold_Select = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Select);
        LinearLayout layout_Real_Lite = todaySummaryDialog.findViewById(R.id.layout_Real_Lite);
        LinearLayout layout_Real_Taste = todaySummaryDialog.findViewById(R.id.layout_Real_Taste);
        LinearLayout layout_ROZ = todaySummaryDialog.findViewById(R.id.layout_ROZ);
        LinearLayout layout_Elaichi = todaySummaryDialog.findViewById(R.id.layout_Elaichi);

        layout_Dhamaka.setVisibility(View.GONE);
        layout_Raid.setVisibility(View.GONE);
        layout_Real_Gold.setVisibility(View.GONE);
        layout_Real_Gold_Classic.setVisibility(View.GONE);
        layout_Real_Gold_Dust.setVisibility(View.GONE);
        layout_Real_Gold_Perfact.setVisibility(View.GONE);
        layout_Real_Gold_Select.setVisibility(View.GONE);
        layout_Real_Lite.setVisibility(View.GONE);
        layout_Real_Taste.setVisibility(View.GONE);
        layout_ROZ.setVisibility(View.GONE);
        layout_Elaichi.setVisibility(View.GONE);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        JsonObjectRequest townListRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.GET_TODAY_SUMMARY,null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject JsonResponse) {
                try {
                    if(JsonResponse.getInt("status")==1)
                    {
                        JSONObject data = JsonResponse.getJSONObject("data");
                        Log.d("TAG", "getSale: "+ new Gson().toJson(data));
                        tvTodaysTC.setText(data.getString("tc"));
                        tvTodaysPC.setText(data.getString("pc"));
                        tvTodaysNC.setText(data.getString("nc"));

                        JSONArray sale = data.getJSONArray("salelst");
                        for (int i = 0; i < sale.length(); i++)
                        {
                            JSONObject saleObj = sale.getJSONObject(i);
                            String strbrand_name = saleObj.getString("brandName");
                            String saleVal = saleObj.getString("sale");
                            Log.d(TAG, "strbrand_name: "+strbrand_name +": "+saleVal);
                            if(strbrand_name.equals("Dhamaka"))
                            {
                                tvDhamaka.setText(saleVal);
                                layout_Dhamaka.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Raid"))
                            {
                                tvRaid.setText(saleVal);
                                layout_Raid.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Gold"))
                            {
                                tvReal_Gold.setText(saleVal);
                                layout_Real_Gold.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Gold Classic"))
                            {
                                tvReal_Gold_Classic.setText(saleVal);
                                layout_Real_Gold_Classic.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Gold Dust"))
                            {
                                tvReal_Gold_Dust.setText(saleVal);
                                layout_Real_Gold_Dust.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Gold Perfact"))
                            {
                                tvReal_Gold_Perfact.setText(saleVal);
                                layout_Real_Gold_Perfact.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Gold Select"))
                            {
                                tvReal_Gold_Select.setText(saleVal);
                                layout_Real_Gold_Select.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Lite"))
                            {
                                tvReal_Lite.setText(saleVal);
                                layout_Real_Lite.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Taste"))
                            {
                                tvReal_Taste.setText(saleVal);
                                layout_Real_Taste.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("ROZ"))
                            {
                                tvROZ.setText(saleVal);
                                layout_ROZ.setVisibility(View.VISIBLE);
                            }else if(strbrand_name.equals("Real Taste ELF")){
                                tvElaichi.setText(saleVal);
                                layout_Elaichi.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    progressDialog.hide();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Sentry.captureMessage(error.getMessage()); //@Umesh
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", myPref.getString("token",""));
                return headers;
            }
        };

        townListRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(townListRequest);
        todaySummaryDialog.show();
    }
    @SuppressLint("SetTextI18n")
    private void showTodaysSummaryOld() {

        Dialog todaySummaryDialog = new Dialog(requireContext(), R.style.DialogActivityTheme);
        todaySummaryDialog.setContentView(R.layout.todays_summary);

        if (todaySummaryDialog.getWindow() != null) {

            todaySummaryDialog.getWindow().setGravity(Gravity.BOTTOM);
        }


        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(todaySummaryDialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.99f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.75f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        todaySummaryDialog.getWindow().setAttributes(layoutParams);

        todaySummaryDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        todaySummaryDialog.setCancelable(true);
        todaySummaryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        TextView tvtodaySummaryCaption = todaySummaryDialog.findViewById(R.id.todaySummaryCaption);
        tvtodaySummaryCaption.setText("Today's Summary As On..."+ DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));

        TextView tvTodaysTC = todaySummaryDialog.findViewById(R.id.tvTodaysTC);
        TextView tvTodaysPC = todaySummaryDialog.findViewById(R.id.tvTodaysPC);
        TextView tvTodaysNC = todaySummaryDialog.findViewById(R.id.tvTodaysNC);


        //@Umesh 20221004
        TextView tvDhamaka = todaySummaryDialog.findViewById(R.id.tvDhamaka);
        TextView tvRaid = todaySummaryDialog.findViewById(R.id.tvRaid);
        TextView tvReal_Gold = todaySummaryDialog.findViewById(R.id.tvReal_Gold);
        TextView tvReal_Gold_Classic = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Classic);
        TextView tvReal_Gold_Dust = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Dust);
        TextView tvReal_Gold_Perfact = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Perfact);
        TextView tvReal_Gold_Select = todaySummaryDialog.findViewById(R.id.tvReal_Gold_Select);
        TextView tvReal_Lite = todaySummaryDialog.findViewById(R.id.tvReal_Lite);
        TextView tvReal_Taste = todaySummaryDialog.findViewById(R.id.tvReal_Taste);
        TextView tvROZ = todaySummaryDialog.findViewById(R.id.tvROZ);

        LinearLayout layout_Dhamaka = todaySummaryDialog.findViewById(R.id.layout_Dhamaka);
        LinearLayout layout_Raid = todaySummaryDialog.findViewById(R.id.layout_Raid);
        LinearLayout layout_Real_Gold = todaySummaryDialog.findViewById(R.id.layout_Real_Gold);
        LinearLayout layout_Real_Gold_Classic = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Classic);
        LinearLayout layout_Real_Gold_Dust = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Dust);
        LinearLayout layout_Real_Gold_Perfact = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Perfact);
        LinearLayout layout_Real_Gold_Select = todaySummaryDialog.findViewById(R.id.layout_Real_Gold_Select);
        LinearLayout layout_Real_Lite = todaySummaryDialog.findViewById(R.id.layout_Real_Lite);
        LinearLayout layout_Real_Taste = todaySummaryDialog.findViewById(R.id.layout_Real_Taste);
        LinearLayout layout_ROZ = todaySummaryDialog.findViewById(R.id.layout_ROZ);

        layout_Dhamaka.setVisibility(View.GONE);
        layout_Raid.setVisibility(View.GONE);
        layout_Real_Gold.setVisibility(View.GONE);
        layout_Real_Gold_Classic.setVisibility(View.GONE);
        layout_Real_Gold_Dust.setVisibility(View.GONE);
        layout_Real_Gold_Perfact.setVisibility(View.GONE);
        layout_Real_Gold_Select.setVisibility(View.GONE);
        layout_Real_Lite.setVisibility(View.GONE);
        layout_Real_Taste.setVisibility(View.GONE);
        layout_ROZ.setVisibility(View.GONE);




        Cursor orederPlacedBy = null, orderPlacedByNew = null;
        int tc = 0, pc = 0, nc = 0;
//        double saleInKg = 0;
//        int saleInPcs = 0, saleInBdl = 0, saleInOthers = 0;

        int Sale_Dhamaka=0,Sale_Raid=0,Sale_Real_Gold=0,Sale_Real_Gold_Classic=0,
                Sale_Real_Gold_Dust=0,Sale_Real_Gold_Perfact=0,Sale_Real_Gold_Select=0,
                Sale_Real_Lite=0,Sale_Real_Taste=0,Sale_ROZ=0;

        try {

            orederPlacedBy = salesBeatDb.getRetailersFromOderPlacedByRetailersTable();

            if (orederPlacedBy != null && orederPlacedBy.getCount() > 0 && orederPlacedBy.moveToFirst()) {

                do {
                    String rid = orederPlacedBy.getString(orederPlacedBy.getColumnIndex("rid"));
                    String did = orederPlacedBy.getString(orederPlacedBy.getColumnIndex("did"));
                    String orderType = orederPlacedBy.getString(orederPlacedBy.getColumnIndex("order_type"));

                    Log.e("VisitedRetailer", "===> rid: " + rid + " did: " + did + " order type: " + orderType);

                    if (orderType.equalsIgnoreCase("onShop")
                            || orderType.equalsIgnoreCase("telephonic")
                            || orderType.equalsIgnoreCase("revise order")) {
                        pc++;
                    }
                    tc++;

                    Cursor cursor = salesBeatDb.getSpecificDataFromOrderEntryListTable22(rid, did);

                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        do {

//                            String strSale = cursor.getString(cursor.getColumnIndex("brand_qty"));
//                            String cFactor = cursor.getString(cursor.getColumnIndex("conversion_factor"));
//                            String unit = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_ORDER_SKU_BRAND_UNIT_L));

                            String strbrand_name = cursor.getString(cursor.getColumnIndex("brand_name"));
                            int saleVal = cursor.getInt(cursor.getColumnIndex("brand_qty"));

                            if (!orderType.equalsIgnoreCase("cancelled"))
                            {
                                try {
//                                    if (unit.toLowerCase(Locale.ROOT).contains("kg")) {
//                                        double saleVal = Double.parseDouble(strSale);
//                                        saleInKg = saleInKg + saleVal;
//                                    } else if (unit.toLowerCase(Locale.ROOT).contains("pc")){
//                                        int saleVal = Integer.parseInt(strSale);
//                                        saleInPcs = saleInPcs + saleVal;
//                                    } else if (unit.toLowerCase(Locale.ROOT).contains("bdl")){
//                                        int saleVal = Integer.parseInt(strSale);
//                                        saleInBdl = saleInBdl + saleVal;
//                                    } else
//                                    {
//                                        int saleVal = Integer.parseInt(strSale);
//                                        saleInOthers = saleInOthers + saleVal;
//                                    }

                                    if(strbrand_name.equals("Dhamaka"))
                                    {
                                        Sale_Dhamaka = Sale_Dhamaka +saleVal;
                                        layout_Dhamaka.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Raid"))
                                    {
                                        Sale_Raid = Sale_Raid +saleVal;
                                        layout_Raid.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold"))
                                    {
                                        Sale_Real_Gold = Sale_Real_Gold +saleVal;
                                        layout_Real_Gold.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Classic"))
                                    {
                                        Sale_Real_Gold_Classic = Sale_Real_Gold_Classic +saleVal;
                                        layout_Real_Gold_Classic.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Dust"))
                                    {
                                        Sale_Real_Gold_Dust = Sale_Real_Gold_Dust +saleVal;
                                        layout_Real_Gold_Dust.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Perfact"))
                                    {
                                        Sale_Real_Gold_Perfact = Sale_Real_Gold_Perfact +saleVal;
                                        layout_Real_Gold_Perfact.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Select"))
                                    {
                                        Sale_Real_Gold_Select = Sale_Real_Gold_Select +saleVal;
                                        layout_Real_Gold_Select.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Lite"))
                                    {
                                        Sale_Real_Lite = Sale_Real_Lite +saleVal;
                                        layout_Real_Lite.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Taste"))
                                    {
                                        Sale_Real_Taste = Sale_Real_Taste +saleVal;
                                        layout_Real_Taste.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("ROZ"))
                                    {
                                        Sale_ROZ = Sale_ROZ +saleVal;
                                        layout_ROZ.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } while (cursor.moveToNext());
                    }

                } while (orederPlacedBy.moveToNext());
            }


            orderPlacedByNew = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable222();

            Log.e("VisitedRetailer", "===> Counter: " + orderPlacedByNew.getCount());

            if (orderPlacedByNew != null && orderPlacedByNew.getCount() > 0 && orderPlacedByNew.moveToFirst()) {

                do {

                    String nrid = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("nrid"));
                    String tempRid = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex(SalesBeatDb.KEY_NEW_RETAILER_TEMP_IDD));
                    String new_did = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex(SalesBeatDb.KEY_NEW_ORDER_PLACED_BY_DID));
                    String orderType = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("new_order_comment"));

                    if (orderType.equalsIgnoreCase("new productive")) {
                        pc++;
                    }
                    nc++;
                    Cursor cursor = salesBeatDb.getSpecificDataFromNewOrderEntryListTable22(nrid, new_did);
                    Log.e("RRRR", "---->" + cursor.getCount() + " " + nrid + "  " + tempRid);

                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        do {

//                            String strSale = cursor.getString(cursor.getColumnIndex("new_brand_qty"));
//                            String cFactor = cursor.getString(cursor.getColumnIndex("conversion_factor"));
//                            String unit = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_ORDER_SKU_BRAND_UNIT_L));

                            //@Umesh
                            String strbrand_name = cursor.getString(cursor.getColumnIndex("new_brand_name"));
                            int saleVal = cursor.getInt(cursor.getColumnIndex("new_brand_qty"));


                            if (!orderType.equalsIgnoreCase("cancelled")) {

                                //saleVal = 0, conversionFactor = 0;
                                try {
//                                    if (unit.toLowerCase(Locale.ROOT).contains("kg")) {
//                                        double saleVal = Double.parseDouble(strSale);
//                                        saleInKg = saleInKg + saleVal;
//                                    } else if (unit.toLowerCase(Locale.ROOT).contains("pc")){
//                                        int saleVal = Integer.parseInt(strSale);
//                                        saleInPcs = saleInPcs + saleVal;
//                                    } else if (unit.toLowerCase(Locale.ROOT).contains("bdl")){
//                                        int saleVal = Integer.parseInt(strSale);
//                                        saleInBdl = saleInBdl + saleVal;
//                                    } else
//                                    {
//                                        int saleVal = Integer.parseInt(strSale);
//                                        saleInOthers = saleInOthers + saleVal;
//                                    }

                                    if(strbrand_name.equals("Dhamaka"))
                                    {
                                        Sale_Dhamaka = Sale_Dhamaka +saleVal;
                                        layout_Dhamaka.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Raid"))
                                    {
                                        Sale_Raid = Sale_Raid +saleVal;
                                        layout_Raid.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold"))
                                    {
                                        Sale_Real_Gold = Sale_Real_Gold +saleVal;
                                        layout_Real_Gold.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Classic"))
                                    {
                                        Sale_Real_Gold_Classic = Sale_Real_Gold_Classic +saleVal;
                                        layout_Real_Gold_Classic.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Dust"))
                                    {
                                        Sale_Real_Gold_Dust = Sale_Real_Gold_Dust +saleVal;
                                        layout_Real_Gold_Dust.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Perfact"))
                                    {
                                        Sale_Real_Gold_Perfact = Sale_Real_Gold_Perfact +saleVal;
                                        layout_Real_Gold_Perfact.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Gold Select"))
                                    {
                                        Sale_Real_Gold_Select = Sale_Real_Gold_Select +saleVal;
                                        layout_Real_Gold_Select.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Lite"))
                                    {
                                        Sale_Real_Lite = Sale_Real_Lite +saleVal;
                                        layout_Real_Lite.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("Real Taste"))
                                    {
                                        Sale_Real_Taste = Sale_Real_Taste +saleVal;
                                        layout_Real_Taste.setVisibility(View.VISIBLE);
                                    }else if(strbrand_name.equals("ROZ"))
                                    {
                                        Sale_ROZ = Sale_ROZ +saleVal;
                                        layout_ROZ.setVisibility(View.VISIBLE);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } while (cursor.moveToNext());

                    }

                } while (orderPlacedByNew.moveToNext());

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            if (orederPlacedBy != null)
                orederPlacedBy.close();
            if (orderPlacedByNew != null)
                orderPlacedByNew.close();
        }


        int finalTc = tc + nc;

        tvTodaysTC.setText("" + finalTc);
        tvTodaysPC.setText("" + pc);
        tvTodaysNC.setText("" + nc);
//        tvTodaysSaleInKg.setText("" + saleInKg + " Kg");
//        tvTodaysSaleInPcs.setText("" + saleInPcs + " Pcs");
//        tvTodaysSaleInBdl.setText("" + saleInBdl + " Bdl");
//        tvTodaysSaleOthers.setText("" + saleInOthers + "");

        tvDhamaka.setText("" + Sale_Dhamaka +"");
        tvRaid.setText("" + Sale_Raid +"");
        tvReal_Gold.setText("" + Sale_Real_Gold +"");
        tvReal_Gold_Classic.setText("" + Sale_Real_Gold_Classic +"");
        tvReal_Gold_Dust.setText("" + Sale_Real_Gold_Dust +"");
        tvReal_Gold_Perfact.setText("" + Sale_Real_Gold_Perfact +"");
        tvReal_Gold_Select.setText("" + Sale_Real_Gold_Select +"");
        tvReal_Lite.setText("" + Sale_Real_Lite +"");
        tvReal_Taste.setText("" + Sale_Real_Taste +"");
        tvROZ.setText("" + Sale_ROZ +"");

        todaySummaryDialog.show();

    }

    @SuppressLint("SetTextI18n")
    public void showEmployeeList(LinearLayout llNewStartWorkList, LinearLayout llNext, TextView tvLoadingMsg, TextView tvNotListed,
                                 TextView tvTitle, AutoCompleteTextView actvEmpList, boolean b) {

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
        tvLoadingMsg.setVisibility(View.VISIBLE);
        //llNewStartWorkList.setVisibility(View.GONE);
        rlEmpList1.setVisibility(View.VISIBLE);

        tvLoadingMsg.setBackgroundColor(Color.TRANSPARENT);

        if (!b) {
            tvTitle.setText("Select employee working with.");
            tvLoadingMsg.setText("Loading employee list...");

            rvEmployeeList.setBackgroundColor(Color.WHITE);
            //llNewStartWorkList.setBackgroundColor(Color.WHITE);

        } else {

            actvEmpList.setHint("Loading....");
        }

        //@Umesh 23-03-2022
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_LIST+"?zoneid=" + myPref.getString(getString(R.string.zone_id_key), ""),
                null, response -> {

            downloadEmployeeList = false;
            Log.e(TAG, "onResponse JOINT WORKING EMP LIST===" + response);

            try {

                JSONObject data = response.getJSONObject("data");
                JSONArray emp = data.getJSONArray("employees");
                ArrayList<String> empId = new ArrayList<>();
                ArrayList<String> empNameL = new ArrayList<>();
                ArrayList<String> empPhone = new ArrayList<>();

                //@Umesh
                ArrayList<Employee> empLst = new ArrayList<>();


                for (int index = 0; index < emp.length(); index++) {
                    JSONObject list = (JSONObject) emp.get(index);

                    String eid = list.getString("eid");
                    if (!myPref.getString(getString(R.string.emp_id_key), "").equalsIgnoreCase(eid)) {

                        empId.add(eid);
                        empNameL.add(list.getString("name"));
                        empPhone.add(list.getString("phone1"));

                        Employee obj = new Employee();
                        obj.setEid(eid);
                        obj.setName(list.getString("name"));
                        empLst.add(obj);
                    }
                }

                //String status = response.getString("status");

                //@Umesh 02-Feb-2022
                if(response.getInt("status")==1)
                {

                    //llNewStartWorkingL.setVisibility(View.VISIBLE);
                    actvEmpList.setHint("Select employee.");

                    //EmloyeeListAdapter emloyeeListAdapter = new EmloyeeListAdapter(getContext(), empId, empName, empPhone);

                    if (b) {

                        showSimpleListDialog(actvEmpList, empNameL, "Select employee working with.");


                    } else {

                        ActivityListAdapter activityListAdapter = new ActivityListAdapter(getContext(), empNameL,
                                new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {

                                        empName = empNameL.get(position);
                                        //@Umesh 20220914
                                        Employee emp = empLst.get(position);
                                        jointwrkempid = emp.getEid();
                                        jointwrkempname = emp.getName();
                                        llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                        llNext.setClickable(true);
                                        tvNotListed.setClickable(false);
                                        tvNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));
                                    }

                                    @Override
                                    public void onItemClick2(int position) {
                                        //AnimCheckBox animCheckBox =
                                        empName = "";
                                        //@Umesh 20220914
                                        jointwrkempid="";
                                        jointwrkempname="";

                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        llNext.setClickable(false);
                                        tvNotListed.setClickable(true);
                                        tvNotListed.setBackgroundColor(Color.parseColor("#F0544D"));

                                    }
                                }, false);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        rvEmployeeList.setLayoutManager(layoutManager);
                        //rvTownList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                        rvEmployeeList.setAdapter(activityListAdapter);

                        rvEmployeeList.setVisibility(View.VISIBLE);
                        tvLoadingMsg.setVisibility(View.GONE);
                        //llNewStartWorkList.setVisibility(View.VISIBLE);

                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
                tvLoadingMsg.setText(e.getMessage());
            }


        }, error -> {

            try {
                tvLoadingMsg.setText("Response code: " + error.networkResponse.statusCode);
            } catch (Exception e) {
                tvLoadingMsg.setText("Response code: null");
            }


            //tvLoadingMsg.setText("helllo testign" );
            tvLoadingMsg.setVisibility(View.VISIBLE);
            rvEmployeeList.setVisibility(View.GONE);

//            try {
//
//                tvLoadingMsg.setText(error.getMessage());
//
//                if (error.networkResponse.statusCode == 422) {
//                    String responseBody = null;
//                    try {
//
//                        responseBody = new String(error.networkResponse.data, "utf-8");
//                        JSONObject object = new JSONObject(responseBody);
//                        String message = object.getString("message");
//                        JSONObject errorr = object.getJSONObject("errors");
//
//                        Log.e(TAG, "Error===" + message + "===" + errorr);
//
//
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
////                Log.e(TAG, "GET Employee : " + e.getMessage());
////                if (!utilityClass.isInternetConnected()) {
////                    Log.e(TAG, "Distributor list: No Internet Error");
////                    tvLoadingMsg.setText("No Internet");
////
//////                    Toast toast =  Toast.makeText(getContext(), "Internet not Connected", Toast.LENGTH_LONG);
//////                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
//////                    toast.show();
////
////                    downloadEmployeeList = true;
////                    rvEmployeeList.setVisibility(View.VISIBLE);
////                    llNewStartWorkList.setVisibility(View.VISIBLE);
////
////                    empllNewStartWork = llNewStartWorkList;
////                    empllNext = llNext;
////                    distvLoadingMsgm = tvLoadingMsg;
////                    distvNotListed = tvNotListed;
////                    emptvEmpNotListed = actvEmpList;
////                    disFlag = b;
////
////                    rvEmployeeList.setAdapter(null);
////                    rvEmployeeList.setBackgroundColor(Color.TRANSPARENT);
////                    empllNewStartWork.setBackgroundColor(Color.TRANSPARENT);
////
////                } else {
////                    error.printStackTrace();
////                    tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
////                    tvLoadingMsg.setVisibility(View.VISIBLE);
////                    llNewStartWorkList.setVisibility(View.GONE);
////                }
//            }

        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private void showSimpleListDialog(AutoCompleteTextView actvList, ArrayList<String> list, String s) {

        Dialog simpleDialog = new Dialog(requireContext(), R.style.DialogActivityTheme);
        simpleDialog.setContentView(R.layout.list_dialog);

        if (simpleDialog.getWindow() != null)
            simpleDialog.getWindow().setGravity(Gravity.BOTTOM);


        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(simpleDialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.99f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.75f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        simpleDialog.getWindow().setAttributes(layoutParams);

        //activityListDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        simpleDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        simpleDialog.setCancelable(true);

        RecyclerView rvSimpleList = simpleDialog.findViewById(R.id.rvSimpleList);
        TextView tvTitle = simpleDialog.findViewById(R.id.tvSimpleTitle);


        tvTitle.setText(s);


        ActivityListAdapter activityListAdapter = new ActivityListAdapter(getContext(), list,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        actvList.setText(list.get(position));
                        simpleDialog.dismiss();
                    }

                    @Override
                    public void onItemClick2(int position) {
                        //AnimCheckBox animCheckBox =
                        actvList.setText("");
                        simpleDialog.dismiss();

                    }
                }, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvSimpleList.setLayoutManager(layoutManager);
        //rvTownList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvSimpleList.setAdapter(activityListAdapter);

        rvSimpleList.setVisibility(View.VISIBLE);

        simpleDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showPlanDetailsView(LinearLayout llCommentAct, EditText edtComment, LinearLayout llNext,
                                     TextView tvTitle, TextView tvNext, ImageView searchImage, boolean flag) {

        //edtComment.getText().clear();
        llCommentAct.setVisibility(View.VISIBLE);
        searchImage.setVisibility(View.GONE);

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
        if (flag)
            tvTitle.setText("Explain tomorrow's plan in details.");
        else
            tvTitle.setText("Explain today's plan in details.");

        tvNext.setText("Done");

    }

    private void showDistributorList(LinearLayout llNewStartWorking, TextView tvDistNotListed, LinearLayout llNext,
                                     TextView tvLoadingMsg, TextView tvTitle, TextView tvNotListed, boolean flagTownNotListed) {

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
        tvTitle.setText("Select distributor name.");
        rlDistributorList1.setVisibility(View.VISIBLE);

        new DownloadDistributors(/*llNewStartWorking, */llNext, tvLoadingMsg,
                tvNotListed, tvDistNotListed, flagTownNotListed).execute();
    }

    private void hideAllRv() {

        rlActivityList1.setVisibility(View.GONE);
        rlTownList1.setVisibility(View.GONE);
        rlDistributorList1.setVisibility(View.GONE);
        rlMeetingList1.setVisibility(View.GONE);
        rlEmpList1.setVisibility(View.GONE);

        rvActivityList.setVisibility(View.GONE);
        rvTownList.setVisibility(View.GONE);
        rvDistributorList.setVisibility(View.GONE);
        rvEmployeeList.setVisibility(View.GONE);
        rvMeetingList.setVisibility(View.GONE);

        //@Umesh 20220908
        llTownBtn.setVisibility(View.GONE);
    }

    //@Umesh 20220908
    public void getTownList() {

        String LoadMsg="";
        if(IsOther)
        {
            LoadMsg="Load Other town list...";
            tvRegularTown.setLinksClickable(false);
            tvRegularTown.setBackgroundColor(Color.parseColor("#1F000000"));

            tvOtherTown.setLinksClickable(true);
            tvOtherTown.setBackgroundColor(Color.parseColor("#F0544D"));
        }
        else
        {
            LoadMsg="Load Regular town list...";
            tvRegularTown.setLinksClickable(true);
            tvRegularTown.setBackgroundColor(Color.parseColor("#F0544D"));

            tvOtherTown.setLinksClickable(false);
            tvOtherTown.setBackgroundColor(Color.parseColor("#1F000000"));
        }

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(LoadMsg);
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObjectRequest townListRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.GET_TOWN_LIST+"?IsOther="+IsOther, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject JsonResponse) {
                Cursor cursor = salesBeatDb.getAllRecordFromTownListTable();
                if (cursor != null && cursor.getCount() > 0)
                    salesBeatDb.deleteAllFromTownList();

                try {
                    if(JsonResponse.getInt("status")==1)
                    {
                        JSONArray towns = JsonResponse.getJSONArray("data");
                        for (int i = 0; i < towns.length(); i++)
                        {
                            String town = towns.get(i).toString();
                            salesBeatDb.insertTownList(town);
                        }
                        if (towns.length() == 0)
                        {
                            SharedPreferences.Editor editor = tempPref.edit();
                            editor.putString(getString(R.string.townErrorKey), "No data: In Towns");
                            editor.apply();
                        }else
                        {
                            cursor = null;

                            ArrayList<String> townItems = new ArrayList<>();
                            //ArrayList<TownItem> townItems = new ArrayList<>();
                            try {
                                cursor = salesBeatDb.getAllRecordFromTownListTable();
                                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                                    do {
                                        townItems.add(cursor.getString(cursor.getColumnIndex("town_name")));
                                        //@Umesh 20220908
//                                        TownItem item = new TownItem();
//                                        item.setTownName(cursor.getString(cursor.getColumnIndex("town_name")));
//                                        townItems.add(item);
                                    } while (cursor.moveToNext());

                                    //Collections.sort(townItems, String::compareTo);
                                    //@Umesh 20220908
//                                    Collections.sort(townItems, new Comparator<TownItem>() {
//                                        @Override
//                                        public int compare(TownItem o1, TownItem o2) {
//                                            return o1.getTownName().compareTo(o2.getTownName());
//                                        }
//                                    });
                                }

                            } catch (Exception e) {
                                e.getMessage();
                            } finally {
                                if (cursor != null)
                                    cursor.close();
                                if (townItems.size() > 0) {

                                    // TownListAdapter adapter = new TownListAdapter(getContext(), townItems);
                                    ActivityListAdapter adapter = new ActivityListAdapter(getContext(), townItems,
                                            new OnItemClickListener() {
                                                @Override
                                                public void onItemClick(int position) {
                                                    //workingTown=townItems.get(position);
                                                    workingTown=townItems.get(position);
                                                    llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                                    llNext.setClickable(true);
                                                    llNext.setEnabled(true);
                                                    //new DownloadMappingDetails(rvDisList, towns).execute();
                                                    tvNotListed.setClickable(false);
                                                    tvNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));

                                                }

                                                @Override
                                                public void onItemClick2(int position) {
                                                    //AnimCheckBox animCheckBox =
                                                    workingTown = "";
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                    llNext.setClickable(false);
                                                    llNext.setEnabled(false);
                                                    tvNotListed.setClickable(true);
                                                    tvNotListed.setBackgroundColor(Color.parseColor("#F0544D"));

                                                }
                                            }, false);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                    rvTownList.setLayoutManager(layoutManager);
                                    int resId = R.anim.layout_animation_fall_down;
                                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                                    rvTownList.setLayoutAnimation(animation);
                                    rvTownList.setAdapter(adapter);
                                    rvTownList.getAdapter().notifyDataSetChanged();
                                    rvTownList.setVisibility(View.VISIBLE);

//                                    if (flagTownNotListed) {
//
//                                        tvNotListed.setVisibility(View.VISIBLE);
//                                        tvDistNotListed.setVisibility(View.GONE);
//
//                                    } else {
//
//                                        tvNotListed.setVisibility(View.GONE);
//                                        tvDistNotListed.setVisibility(View.GONE);
//
//                                    }


                                } else {

                                    //tvLoadingMsg.setText(tempPref.getString(getString(R.string.townErrorKey), ""));
                                    // tvLoadingMsg.setVisibility(View.VISIBLE);
                                    llNewStartWorkList.setVisibility(View.GONE);
                                    townListEmpty = true;
                                }
                            }
                        }
                        progressDialog.dismiss();
                        llNext.setClickable(false);
                        llNext.setEnabled(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(getString(R.string.townErrorKey),
                        error.networkResponse.statusCode + ": " + error.getMessage());
                editor.apply();

                serverCall.handleError2(error.networkResponse.statusCode,
                        TAG, error.getMessage(), "getTowns");

                Sentry.captureMessage(error.getMessage()); //@Umesh
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", myPref.getString("token",""));
                return headers;
            }
        };

        townListRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(townListRequest);
    }

    @SuppressLint("SetTextI18n")
    private void showTownList(LinearLayout llNext, TextView tvDistNotListed,
                              TextView tvNotListed, TextView tvTitle, boolean flagTownNotListed,
                              TextView tvLoadingMsg, LinearLayout llNewStartWorkList) {

        rlTownList1.setVisibility(View.VISIBLE);

        //@Umesh 20220908
        llTownBtn.setVisibility(View.VISIBLE);
        getTownList();


        tvTitle.setText("Select working town.");

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
        llNext.setClickable(false);
        llNext.setEnabled(false);
    }

    private boolean hideShowTownNotListed(String act) {


        if (act.contains(SbAppConstants.ACTIVITY1) || act.contains(SbAppConstants.ACTIVITY6)) {

            return false;

        } else {

            return true;

        }
    }


    private void submitTomorrowPlan(String cmnt) {

        activityListDialog.dismiss();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Submitting...");
        progressDialog.show();

        //@Umesh 10-March-2022
        JSONObject obj = new JSONObject();
        try {
            String lat = "", longt = "";
            if (locationProvider != null) {
                lat = String.valueOf(locationProvider.getLatitude());
                longt = String.valueOf(locationProvider.getLongitude());
            }
            obj.put("activity_type", activities.toString());
            obj.put("meeting_type", meeting);
            obj.put("working_with", empName);
            obj.put("workingtown", workingTown);
            obj.put("did", Integer.parseInt(did));
            // obj.put("zoneId", myPref.getString(getString(R.string.zone_id_key), ""));
            // obj.put("latitude", lat);
            // obj.put("longitude", longt);
            if (!distributorName.isEmpty())
                obj.put("comment", cmnt + " Distributor:" + distributorName);
            else
                obj.put("comment", cmnt);

            Log.e("Tomorrow's Plan", " #### JSON: " + obj.toString());


            SharedPreferences.Editor editor = tempPref.edit();
            editor.putString(getString(R.string.tomActType), activities.toString());
            editor.putString(getString(R.string.tomWorkinTown), workingTown);
            if (!did.isEmpty())
                editor.putString(getString(R.string.tomWorkingDis), disN);
            else
                editor.putString(getString(R.string.tomWorkingDis), distributorName);

            editor.putString(getString(R.string.tomEmpWith), empName);
            editor.putString(getString(R.string.tomMeetingType), meeting);
            editor.putString(getString(R.string.tomComment), cmnt);
            editor.apply();
        }
        catch (Exception ex)
        {

        }


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_SUBMIT_TOMORROWS_PLAN,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();

                        // response
                        Log.e("Response", "Tomorrow's Plan====" + response);

                        try {
                            //@Umesh 10-March-2022
                            if(response.getInt("status")==1)
                            {
                                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.timeformat), Locale.ENGLISH);
                                String checkOuttime = dateFormat.format(Calendar.getInstance().getTime());
                                tvCheckOutTime.setText(getString(R.string.checkout_e) + " : " + checkOuttime);
                                String attendance = "checkOut";
                                String reason = "present";

                                //markAttendance(attendance, reason, tempPref.getString(getString(R.string.check_in_time_key), ""), checkOuttime);

                                startService(attendance, reason, tempPref.getString(getString(R.string.check_in_time_key), ""),
                                        checkOuttime, tvTimer.getText().toString());

                            } else {

                                Toast.makeText(getContext(), "Error:"+response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                //loader.dismiss();
                progressDialog.dismiss();
                Log.e(TAG, " ##########" + error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        postRequest.setShouldCache(false);

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(requireContext()).add(postRequest);

    }

    @SuppressLint("SetTextI18n")
    private void getCmntEnabled(RecyclerView rvDistributorList, LinearLayout llCommentAct,
                                LinearLayout llNext, TextView tvTitle, TextView tvNext,
                                boolean flag, TextView tvNotListed) {

        rvDistributorList.setVisibility(View.GONE);
        tvNotListed.setVisibility(View.GONE);
        llCommentAct.setVisibility(View.VISIBLE);
        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
        if (flag)
            tvTitle.setText("Explain tomorrow's plan in details.");
        else
            tvTitle.setText("Explain today's plan in details.");

        tvNext.setText("Done");

    }

    private void initMeetingList(RecyclerView rvMeetingList, LinearLayout llNext, TextView tvNotListed) {

        ArrayList<String> activityListArr = new ArrayList<>();
        activityListArr.add("Office Meeting");
        activityListArr.add("Distributor Meeting");
        activityListArr.add("Others");

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));

        ActivityListAdapter activityListAdapter = new ActivityListAdapter(getContext(), activityListArr,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        meeting = activityListArr.get(position);
                        llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                        tvNotListed.setClickable(false);
                        tvNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));
                    }

                    @Override
                    public void onItemClick2(int position) {
                        //AnimCheckBox animCheckBox =
                        meeting = "";
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvNotListed.setClickable(true);
                        tvNotListed.setBackgroundColor(Color.parseColor("#F0544D"));

                    }
                }, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rvMeetingList.setLayoutAnimation(animation);
        rvMeetingList.setLayoutManager(layoutManager);
        rvMeetingList.setAdapter(activityListAdapter);

    }

    private void getEmpList(RecyclerView rvEmployeeList, LinearLayout llNext,
                            LinearLayout llNewStartWorkingL, TextView tvLoadingMsg, TextView tvNotListed) {

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
        tvLoadingMsg.setText("Loading employee list...");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_LIST + myPref.getString(getString(R.string.zone_id_key), "") + "/employees",
                null, response -> {

            Log.e(TAG, "onResponse JOINT WORKING EMP LIST===" + response);

            try {

                JSONObject data = response.getJSONObject("data");
                JSONArray emp = data.getJSONArray("employees");
                ArrayList<String> empId = new ArrayList<>();
                ArrayList<String> empNameL = new ArrayList<>();
                ArrayList<String> empPhone = new ArrayList<>();

                for (int index = 0; index < emp.length(); index++) {
                    JSONObject list = (JSONObject) emp.get(index);

                    String eid = list.getString("eid");
                    if (!myPref.getString(getString(R.string.emp_id_key), "").equalsIgnoreCase(eid)) {

                        empId.add(eid);
                        empNameL.add(list.getString("name"));
                        empPhone.add(list.getString("phone1"));
                    }

                }

                String status = response.getString("status");

                if (status.equalsIgnoreCase("success")) {

                    llNewStartWorkingL.setVisibility(View.VISIBLE);

                    //EmloyeeListAdapter emloyeeListAdapter = new EmloyeeListAdapter(getContext(), empId, empName, empPhone);

                    ActivityListAdapter activityListAdapter = new ActivityListAdapter(getContext(), empNameL,
                            new OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                    empName = empNameL.get(position);
                                    llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                    tvNotListed.setClickable(false);
                                    tvNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));
                                }

                                @Override
                                public void onItemClick2(int position) {
                                    //AnimCheckBox animCheckBox =
                                    empName = "";
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvNotListed.setClickable(true);
                                    tvNotListed.setBackgroundColor(Color.parseColor("#F0544D"));

                                }
                            }, false);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    rvEmployeeList.setLayoutManager(layoutManager);
                    //rvTownList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                    rvEmployeeList.setAdapter(activityListAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> {

            try {
                if (error.networkResponse.statusCode == 422) {
                    String responseBody = null;
                    try {

                        responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject object = new JSONObject(responseBody);
                        String message = object.getString("message");
                        JSONObject errorr = object.getJSONObject("errors");

                        Log.e(TAG, "Error===" + message + "===" + errorr);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private void initActivityList(LinearLayout llNext, boolean flagT,
                                  TextView tvNext, TextView tvNotListed) {

        rlActivityList1.setVisibility(View.VISIBLE);
        ArrayList<String> activityListArr = new ArrayList<>();
        if (flagT)
            activityListArr.add("I will be on leave");
        activityListArr.add(SbAppConstants.ACTIVITY1);
        activityListArr.add(SbAppConstants.ACTIVITY2);
        activityListArr.add(SbAppConstants.ACTIVITY3);
        activityListArr.add(SbAppConstants.ACTIVITY4);
        activityListArr.add(SbAppConstants.ACTIVITY5);
        activityListArr.add(SbAppConstants.ACTIVITY6);
        activityListArr.add(SbAppConstants.ACTIVITY7);
        activityListArr.add(SbAppConstants.ACTIVITY8);
        activityListArr.add(SbAppConstants.ACTIVITY9);

        ActivityListAdapter activityListAdapter = new ActivityListAdapter(getContext(), activityListArr,
                new OnItemClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemClick(int position) {


                        if (activities.size() == 1 && activities.get(0).equalsIgnoreCase("I will be on leave")) {

                            ActivityListAdapter.MyViewHolder myViewHolder = (ActivityListAdapter.MyViewHolder) rvActivityList.findViewHolderForAdapterPosition(position);
                            CheckBox checkBox = myViewHolder.itemView.findViewById(R.id.chbActSelect);
                            checkBox.setChecked(false);

                        } else if (activities.size() < 2) {


                            activities.add(activityListArr.get(position));
                            llNext.setBackgroundColor(Color.parseColor("#5aac82"));

                            SharedPreferences.Editor editor = tempPref.edit();
                            if (activities.get(0).equalsIgnoreCase("Retailing")) {

                                editor.putString(getString(R.string.askfororder_key), "yes");
                            } else {


                                editor.putString(getString(R.string.askfororder_key), "No");
                            }

                            if (activities.get(0).equalsIgnoreCase("I will be on leave"))
                                tvNext.setText("Done");

                            editor.apply();


                        } else {

                            ActivityListAdapter.MyViewHolder myViewHolder = (ActivityListAdapter.MyViewHolder) rvActivityList.findViewHolderForAdapterPosition(position);
                            CheckBox checkBox = myViewHolder.itemView.findViewById(R.id.chbActSelect);
                            checkBox.setChecked(false);
                            Toast.makeText(getContext(), "You can select maximum two", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemClick2(int position) {
                        //AnimCheckBox animCheckBox =

                        activities.remove(activityListArr.get(position));
                        if (activities.size() == 0) {
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvNext.setText("Next");
                        }

                    }

                }, true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rvActivityList.setLayoutAnimation(animation);
        rvActivityList.setLayoutManager(layoutManager);
        rvActivityList.setAdapter(activityListAdapter);
        activityListAdapter.notifyDataSetChanged();
        tempAdapter = activityListAdapter;

        rvActivityList.setVisibility(View.VISIBLE);
        tvNotListed.setVisibility(View.GONE);

    }

    private void initTownList(RecyclerView rvTownList, LinearLayout llNext, TextView tvNotListed) {

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));

        Cursor cursor = null;

        ArrayList<String> townItems = new ArrayList<>();

        try {
            cursor = salesBeatDb.getAllRecordFromTownListTable();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {
                    townItems.add(cursor.getString(cursor.getColumnIndex("town_name")));
                } while (cursor.moveToNext());

                Collections.sort(townItems, String::compareTo);

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();

            if (townItems.size() > 0) {

                // TownListAdapter adapter = new TownListAdapter(getContext(), townItems);

                ActivityListAdapter adapter = new ActivityListAdapter(getContext(), townItems,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {

                                workingTown = townItems.get(position);
                                llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                //new DownloadMappingDetails(rvDisList, towns).execute();
                                tvNotListed.setClickable(false);
                                tvNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));

                            }

                            @Override
                            public void onItemClick2(int position) {
                                //AnimCheckBox animCheckBox =
                                workingTown = "";
                                llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                tvNotListed.setClickable(true);
                                tvNotListed.setBackgroundColor(Color.parseColor("#F0544D"));

                            }
                        }, false);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                rvTownList.setLayoutManager(layoutManager);
                int resId = R.anim.layout_animation_fall_down;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                rvTownList.setLayoutAnimation(animation);
                rvTownList.setAdapter(adapter);
                rvTownList.getAdapter().notifyDataSetChanged();

            }
        }
    }

    private void resetImage() {

        emp1Pic.setImageBitmap(null);
        emp2Pic.setImageBitmap(null);
        emp3Pic.setImageBitmap(null);
        emp4Pic.setImageBitmap(null);
        emp5Pic.setImageBitmap(null);
        emp1Pic.setImageResource(R.drawable.men_placeholder);
        emp2Pic.setImageResource(R.drawable.men_placeholder);
        emp3Pic.setImageResource(R.drawable.men_placeholder);
        emp4Pic.setImageResource(R.drawable.men_placeholder);
        emp5Pic.setImageResource(R.drawable.men_placeholder);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.dashboard_layout, container, false);
        tempPref = requireContext().getSharedPreferences(getString(R.string.temp_pref_name), MODE_PRIVATE);
        //tempPref2 = requireContext().getSharedPreferences(getString(R.string.temp_pref_name_2), Context.MODE_PRIVATE);
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
        tvAttendanceStatus = view.findViewById(R.id.tvAttendanceStatus);
        tvActType = view.findViewById(R.id.tvActType);
        tvCheckInTime = view.findViewById(R.id.tvCheckInTime);
        tvCheckOutTime = view.findViewById(R.id.tvCheckOutTime);
        tvWorkingTown = view.findViewById(R.id.tvWorkingTown);
        tvLeaveReason = view.findViewById(R.id.tvLeaveReason);
        tvLeaveReasonTitle = view.findViewById(R.id.tvLeaveReasonTitle);
        tvTimer = view.findViewById(R.id.tvTimer);
        btnCheckOut = view.findViewById(R.id.btnChekOut);
        btnBookOrder = view.findViewById(R.id.btnBookOrder);
        btnStartDay = view.findViewById(R.id.btnStartDay);
        btnShareSummary = view.findViewById(R.id.btnShareSummary);
        btnShareClaim = view.findViewById(R.id.btnShareClaim);
        btnTomorrowsPlan = view.findViewById(R.id.btnTomorrowsPlan);
        btnApplyLeave = view.findViewById(R.id.btnApplyLeave);
        btnShareStartWorkingSummary = view.findViewById(R.id.btnShareStartWorkingSummary);
        llShareStartWorkingSummary = view.findViewById(R.id.llShareStartWorkingSummary);
        llCurrentStatus = view.findViewById(R.id.llCurrentStatus);
        llMarkAttendance = view.findViewById(R.id.llMarkAttendance);
        llShareReport = view.findViewById(R.id.llShareReport);
        promotionViewPager = view.findViewById(R.id.promotionViewPager);
        empPerformanceViewPager = view.findViewById(R.id.empPerformanceViewPager);
        scDashboard = view.findViewById(R.id.scDashboard);
        //imgRefreshTown = view.findViewById(R.id.imgRefreshTown);
        leaderboardChart = view.findViewById(R.id.barChart3);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnTodaysSummary = view.findViewById(R.id.btnTodaysSummary);
        tvNoInterNet = view.findViewById(R.id.tvNoInternet);
        rlFront = view.findViewById(R.id.rlFront);
        rlBack = view.findViewById(R.id.rlBack);
        emp1 = view.findViewById(R.id.emp1);
        emp2 = view.findViewById(R.id.emp2);
        emp3 = view.findViewById(R.id.emp3);
        emp4 = view.findViewById(R.id.emp4);
        emp5 = view.findViewById(R.id.emp5);
        val1 = view.findViewById(R.id.val1);
        val2 = view.findViewById(R.id.val2);
        val3 = view.findViewById(R.id.val3);
        val4 = view.findViewById(R.id.val4);
        val5 = view.findViewById(R.id.val5);
        emp1Pic = view.findViewById(R.id.empPic1);
        emp2Pic = view.findViewById(R.id.empPic2);
        emp3Pic = view.findViewById(R.id.empPic3);
        emp4Pic = view.findViewById(R.id.empPic4);
        emp5Pic = view.findViewById(R.id.empPic5);

        noCampaignPic = view.findViewById(R.id.noCampaignView);
        campaignLoader = view.findViewById(R.id.campaignLoader);
        errorCampaign = view.findViewById(R.id.errorLoading);
        overlayLayout = view.findViewById(R.id.overlayLayout);
        btnOk = view.findViewById(R.id.btnOk);

        tvCheckInTime.setAllCaps(true);
        tvCheckOutTime.setAllCaps(true);
        utilityClass = new UtilityClass(requireContext());
        //salesBeatDb = new SalesBeatDb(requireContext());
        salesBeatDb = SalesBeatDb.getHelper(requireContext());
        serverCall = new ServerCall(requireContext());
        handler1 = new Handler();
        locationProvider = new GPSLocation(requireContext());
//        mFaceCropper = new FaceCropper2(1f);
//        mFaceCropper.setFaceMinSize(0);
//        mFaceCropper.setDebug(true);
        RequestQueue mRequestQueue = Volley.newRequestQueue(requireContext());
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }

        snackbar = Snackbar.make(MainActivity.mainActivityLayout, "Marking your attendance....", Snackbar.LENGTH_INDEFINITE);

        overlayLayout.setVisibility(View.GONE);

        Log.e(TAG, "--->" + tempPref.getString(getString(R.string.act_type_key), ""));

        callAsynctask();

//        NetworkChangeReceiver receiver = new NetworkChangeReceiver();

        return view;
    }

    @Override
    public void connectionChange(boolean status) {

        resetImage();
        callAsynctask();
        try {
            leaderboardChart.reset();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        setValues();

        if (status) {
            if (downloadDistributorList && disllNewStartWork.getVisibility() == View.VISIBLE) {

//                Toast toast = Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
//                toast.show();

                TextView textView = activityListDialog.findViewById(R.id.tvTitleForToady);

                showDistributorList(disllNewStartWork,
                        distvLoadingMsgm, disllNext,
                        distvNotListed, textView, distvDisNotListed, disFlag);

                distvLoadingMsgm.setText("Loading distributor list...");
            } else if (downloadEmployeeList && disllNewStartWork.getVisibility() == View.VISIBLE) {

//                Toast toast = Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
//                toast.show();

                TextView textView = activityListDialog.findViewById(R.id.tvTitleForToady);

                showEmployeeList(disllNewStartWork,
                        empllNext, distvLoadingMsgm,
                        distvNotListed, textView, emptvEmpNotListed, disFlag);


                distvDisNotListed.setText("Loading distributor list...");

            }

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        receiver.InitNetworkListener(this);

        boolean loaded = initializeViews();
        if (loaded) {
            //Check attendance status
            boolean loaded2 = checkAttendance();
            if (loaded2) {
                //initialize EMP PerformanceViewPager
                boolean loaded3 = setupEmpPerformanceViewPager();
                if (loaded3) {
                    //initialize campaign
                    showCampaign();
                }

            }

//            if (!tempPref.getBoolean(getString(R.string.new_feature_key), false))
//                newFeaturePopUp();
        }
        getSettingDetails();
        //showActivityList();

    }

//    private void newFeaturePopUp() {
//
//        Dialog newFeatureDialog = new Dialog(requireContext());
//        newFeatureDialog.setContentView(R.layout.alert_dialog);
//        Button btnNo = newFeatureDialog.findViewById(R.id.btnNo);
//        Button btnYes = newFeatureDialog.findViewById(R.id.btnYes);
//
//
//        btnNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SharedPreferences.Editor editor = tempPref.edit();
//                editor.putBoolean(getString(R.string.new_feature_key), true);
//                editor.apply();
//
//                newFeatureDialog.dismiss();
//            }
//        });
//
//
//        btnYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SharedPreferences.Editor editor = tempPref.edit();
//                editor.putBoolean(getString(R.string.new_feature_key), true);
//                editor.apply();
//
//                empPerformanceViewPager.setCurrentItem(2);
//
//                overlayLayout.setVisibility(View.VISIBLE);
//
//                if (noCampaignPic.isShown())
//                    noCampaignPic.getParent().requestChildFocus(noCampaignPic, noCampaignPic);
//                else if (campaignLoader.isShown())
//                    campaignLoader.getParent().requestChildFocus(campaignLoader, campaignLoader);
//                else if (promotionViewPager.isShown())
//                    promotionViewPager.getParent().requestChildFocus(promotionViewPager, promotionViewPager);
//                else if (errorCampaign.isShown())
//                    errorCampaign.getParent().requestChildFocus(errorCampaign, errorCampaign);
//            }
//        });
//
//
//        newFeatureDialog.show();
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                overlayLayout.setVisibility(View.GONE);
//            }
//        });
//    }


    public void  getSettingDetails() {
        Log.d(TAG, "getSettingDetails");
        JsonObjectRequest getBeatDetailsReq = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_SETTING,null,
                response -> {
                    try {
                        if(response.getInt("status") == 1)
                        {
                            JSONArray settingArr = response.getJSONArray("data");
                            Log.d(TAG, "Get Setting API Response: "+new Gson().toJson(settingArr));
                            if(settingArr!=null)
                            {
                                for (int i = 0; i < settingArr.length(); i++) {

                                    JSONObject disObj = (JSONObject) settingArr.get(i);
                                    String radiusValue = disObj.getString("value");
                                    Log.d(TAG, "get Redius From API: "+radiusValue);
                                    SharedPreferences pref = getActivity().getSharedPreferences("pincode", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("sp_radius", radiusValue);
                                    editor.apply();

                                }
                            }
                        }else {
                            Log.d(TAG, "check NEXT");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("authorization", myPref.getString("token", ""));
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        getBeatDetailsReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity()).add(getBeatDetailsReq);
    }

    public void onResume() {
        super.onResume();
        //view listeners.....
        setViewListeners();

        scDashboard.fullScroll(View.FOCUS_UP);
        scDashboard.pageScroll(View.FOCUS_UP);

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.newsalesbeat_leaderboard"); //@Umesh 18-08-2022
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            requireContext().registerReceiver(receiver, new IntentFilter(intentFilter));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onPause() {

        try {
            requireContext().unregisterReceiver(receiver);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        super.onPause();
    }

    public void onStop() {
        try {
            if (loadProfileImage2 != null)
                loadProfileImage2.cancel(true);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        super.onStop();
    }

    private void callAsynctask() {

        profilePic.add(0, "");
        profilePic.add(1, "");
        profilePic.add(2, "");
        profilePic.add(3, "");
        profilePic.add(4, "");

        Cursor cursor = null;
        try {

            tEid.clear();
            tEmpName.clear();
            profilePic.clear();
            tcData.clear();
            pcData.clear();
            salesData.clear();

            cursor = salesBeatDb.getLeaderboardDetails();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    tEid.add(cursor.getString(cursor.getColumnIndex("emp_id")));
                    tEmpName.add(cursor.getString(cursor.getColumnIndex("emp_name")));
                    profilePic.add(cursor.getString(cursor.getColumnIndex("emp_photo")));
                    tcData.add(cursor.getString(cursor.getColumnIndex("tc")));
                    pcData.add(cursor.getString(cursor.getColumnIndex("pc")));
                    salesData.add(cursor.getString(cursor.getColumnIndex("sales")));

                } while (cursor.moveToNext());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null)
                cursor.close();
        }

    }

    private boolean initializeViews() {


        if (tempPref.getBoolean(getString(R.string.shareSummaryKey), false)) {
            llShareStartWorkingSummary.setVisibility(View.GONE);
        } else {
            llShareStartWorkingSummary.setVisibility(View.VISIBLE);
        }


        mLabels3[0] = "";
        mLabels3[1] = "";
        mLabels3[2] = "";
        mLabels3[3] = "";
        mLabels3[4] = "";

        mSalesValues[0] = (float) 0.0;
        mSalesValues[1] = (float) 0.0;
        mSalesValues[2] = (float) 0.0;
        mSalesValues[3] = (float) 0.0;
        mSalesValues[4] = (float) 0.0;


        if (tEid.size() > 0) {

            setValues();

        } else {

            tvNoInterNet.setVisibility(View.VISIBLE);
            if (utilityClass.isInternetConnected())
                tvNoInterNet.setText("No sale till now");
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    public void setValues() {

        for (int position = 0; position < tEmpName.size(); position++) {

            try {

                mSalesValues[position] = Float.parseFloat(salesData.get(position));

                if (profilePic.get(position) != null && !profilePic.get(position).isEmpty()
                        && !profilePic.get(position).contains("null")) {

                    final int finalPosition = position;
                    mImageLoader.get(SbAppConstants.IMAGE_PREFIX + profilePic.get(position),
                            new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                                    try {
                                        if (response.getBitmap() != null) {
                                            Log.d("TAG", "get Bitmap Response: "+response.getBitmap());

                                           /* Bitmap profileImage = mFaceCropper.getCroppedImage(response.getBitmap());

                                            if (finalPosition == 0) {
                                                emp1Pic.setImageBitmap(profileImage);
                                            } else if (finalPosition == 1) {
                                                emp2Pic.setImageBitmap(profileImage);
                                            } else if (finalPosition == 2) {
                                                emp3Pic.setImageBitmap(profileImage);
                                            } else if (finalPosition == 3) {
                                                emp4Pic.setImageBitmap(profileImage);
                                            } else if (finalPosition == 4) {
                                                emp5Pic.setImageBitmap(profileImage);
                                            }*/

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    error.printStackTrace();
                                }
                            });

                }


                if (tEmpName.get(position) != null) {

                    if (position == 0)
                        emp1.setText(getSortName(tEmpName.get(position)));
                    else if (position == 1)
                        emp2.setText(getSortName(tEmpName.get(position)));
                    else if (position == 2)
                        emp3.setText(getSortName(tEmpName.get(position)));
                    else if (position == 3)
                        emp4.setText(getSortName(tEmpName.get(position)));
                    else if (position == 4)
                        emp5.setText(getSortName(tEmpName.get(position)));
                }


                if (salesData.get(position) != null) {

                    if (position == 0)
                        valueInKg1 = (Double.valueOf(salesData.get(position)));
                    else if (position == 1)
                        valueInKg2 = (Double.valueOf(salesData.get(position)));
                    else if (position == 2)
                        valueInKg3 = (Double.valueOf(salesData.get(position)));
                    else if (position == 3)
                        valueInKg4 = (Double.valueOf(salesData.get(position)));
                    else if (position == 4)
                        valueInKg5 = (Double.valueOf(salesData.get(position)));

                }


            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        val1.setText(new DecimalFormat("##.##").format(valueInKg1) + getString(R.string.unitt));
        val2.setText(new DecimalFormat("##.##").format(valueInKg2) + getString(R.string.unitt));
        val3.setText(new DecimalFormat("##.##").format(valueInKg3) + getString(R.string.unitt));
        val4.setText(new DecimalFormat("##.##").format(valueInKg4) + getString(R.string.unitt));
        val5.setText(new DecimalFormat("##.##").format(valueInKg5) + getString(R.string.unitt));

        BarSet barSet3 = new BarSet(mLabels3, mSalesValues);

        int[] colors = {getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.whitish)};

        float[] index = {0, 1};
        barSet3.setGradientColor(colors, index);


        leaderboardChart.addData(barSet3);

        tvNoInterNet.setVisibility(View.GONE);
        leaderboardChart.setVisibility(View.VISIBLE);
        int maxVal = (int) (valueInKg1 * 3);
        if (maxVal > 0)
            leaderboardChart.setAxisBorderValues(0, maxVal, 10);

        Tooltip tip3 = new Tooltip(getContext());
        tip3.setBackgroundColor(Color.parseColor("#CC7B1F"));

        leaderboardChart.setTooltips(tip3)
                .show(new Animation(1000)
                        .setInterpolator(new AccelerateDecelerateInterpolator()));

    }

    public String getSortName(String name) {

        String sortName = "";

        String[] arr = name.split(" ");

        for (int i = 0; i < arr.length; i++) {

            if (i < (arr.length - 1)) {

                if (sortName.isEmpty())
                    sortName = Character.toString(arr[i].charAt(0));
                else
                    sortName = sortName.concat(Character.toString(arr[i].charAt(0)));

            } else {

                sortName = sortName.concat(" ");
                sortName = sortName.concat(arr[i]);
            }

        }

        return sortName;
    }

    private void showCampaign() {

        loaded = false;

        final Handler handler = new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                //Log.e("Camp","===>"+loaded);
                if (!loaded)
                    loadCampaign();
                else
                    handler.removeCallbacks(runnable);

                handler.postDelayed(runnable, 500);

            }
        };

        handler.post(runnable);

    }

    private void shareStartWorkingSummary() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Alert!");
        builder.setMessage("Please share today start working summary.");

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shareSummary();
            }
        });

        builder.show();
    }

    @SuppressLint("Range")
    private void shareSummary() {

        Cursor cursor = null;
        String saleAchievement = "";
        String saleTarget = "";

        try {

            cursor = salesBeatDb.getEmpPrimarySale();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    saleAchievement = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SALE_ACH));
                    saleTarget = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SALE_TARGET));

                } while (cursor.moveToNext());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null)
                cursor.close();
        }


        Cursor cursorSec = null;
        String saleAchievementSec = "";
        String saleTargetSec = "";

        try {

            cursorSec = salesBeatDb.getEmpSecondarySale();
            if (cursorSec != null && cursorSec.getCount() > 0 && cursorSec.moveToFirst()) {

                do {

//                    saleAchievementWeek1 = cursor.getString(cursor.getColumnIndex("sale_week1"));
//                    saleAchievementWeek2 = cursor.getString(cursor.getColumnIndex("sale_week2"));
//                    saleAchievementWeek3 = cursor.getString(cursor.getColumnIndex("sale_week3"));
//                    saleAchievementWeek4 = cursor.getString(cursor.getColumnIndex("sale_week4"));
                    saleAchievementSec = cursorSec.getString(cursorSec.getColumnIndex(SalesBeatDb.KEY_SALE_ACH));
                    saleTargetSec = cursorSec.getString(cursorSec.getColumnIndex(SalesBeatDb.KEY_SALE_TARGET));

                } while (cursorSec.moveToNext());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursorSec != null)
                cursorSec.close();
        }

        String workingWith = "";
        if (empName.isEmpty())
            workingWith = "Self";
        else
            workingWith = empName;


        String subLocality = locationProvider.getSubLocality();
        String locality = locationProvider.getLocality();
        String state = locationProvider.getState();
        String pincode = locationProvider.getPostalCode();

        String liveLoc = subLocality + "," + locality + "(" + state + ") Pin code:" + pincode;

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String date = dateFormat.format(Calendar.getInstance().getTime());
        String textToShare =
                "Date:-" + date + "\n" +
                        "Name:-" + myPref.getString(getString(R.string.emp_name_key), "") + "\n" +
                        "HQ.    :-" + myPref.getString(getString(R.string.emp_headq_key), "") + "\n" +
                        "TGT. of month (In kg):-" + saleTarget + "\n" +
                        "Ach till Date (In kg):-" + saleAchievement + "\n\n" +
                        "**Today plan**\n" +
                        "Activity Type:-" + activities + "\n" +
                        "Self/work with:-" + workingWith + "\n" +
                        "Town:-" + workingTown + "\n" +
                        "Live location:-" + liveLoc + "\n" +
                        "Secondary Tea Sales Plan:-" + saleTargetSec + "\n" +
                        "Secondary  Match Box Sale Plan:-N/A\n" +
                        "Payment Collection Plan:- Nill\n" +
                        "Primary Tea Sales Plan:-" + saleTarget + "\n" +
                        "Primary Match Box Order Plan:-N/A\n";


        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Today start working summary");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivityForResult(Intent.createChooser(sharingIntent, "Share start working summary"), REQUEST_CODE_MY_PICK);

    }

    private void setViewListeners() {

//        imgHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showHistoryDialog();
//            }
//        });

//        imgRefreshTown.setVisibility(View.INVISIBLE);
//
//        imgRefreshTown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //showTownList();
//            }
//        });

        btnTodaysSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick summary");
                showTodaysSummary();
            }
        });

        btnShareStartWorkingSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareSummary();
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoryDialog();
            }
        });

        emp1Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (utilityClass.isInternetConnected()) {

                    try {

                        String name = tEmpName.get(0);
                        String image = profilePic.get(0);
                        String sale = salesData.get(0);
                        String tc = tcData.get(0);
                        String pc = pcData.get(0);
                        showEmpInfoDialog(name, image, sale, tc, pc);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        emp2Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    try {
                        String name = tEmpName.get(1);
                        String image = profilePic.get(1);
                        String sale = salesData.get(1);
                        String tc = tcData.get(1);
                        String pc = pcData.get(1);
                        showEmpInfoDialog(name, image, sale, tc, pc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        emp3Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    try {
                        String name = tEmpName.get(2);
                        String image = profilePic.get(2);
                        String sale = salesData.get(2);
                        String tc = tcData.get(2);
                        String pc = pcData.get(2);
                        showEmpInfoDialog(name, image, sale, tc, pc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        emp4Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    try {
                        String name = tEmpName.get(3);
                        String image = profilePic.get(3);
                        String sale = salesData.get(3);
                        String tc = tcData.get(3);
                        String pc = pcData.get(3);
                        showEmpInfoDialog(name, image, sale, tc, pc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });


        emp5Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    try {
                        String name = tEmpName.get(4);
                        String image = profilePic.get(4);
                        String sale = salesData.get(4);
                        String tc = tcData.get(4);
                        String pc = pcData.get(4);
                        showEmpInfoDialog(name, image, sale, tc, pc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        promotionViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 1) {

                    promotionViewPager.setClipToPadding(false);
                    promotionViewPager.setPadding(100, 0, 0, 0);

                } else {

                    promotionViewPager.setClipToPadding(false);
                    promotionViewPager.setPadding(0, 0, 100, 0);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnBookOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(myPref.getString(getString(R.string.emp_id_key), ""),
                        checkInTime, "Button BookOrder From Dashboard", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("present"))
                {


                    String activity = tempPref.getString(getString(R.string.act_type_key), "");

                    activity = activity.replaceAll("\\[", "");
                    activity = activity.replaceAll("\\]", "");

                    String[] act = activity.split(",");

                    String activity1 = "", activity2 = "";

                    if (act != null && act.length == 1) {
                        activity1 = act[0];
                    } else if (act != null && act.length == 2) {
                        activity1 = act[0];
                        activity2 = act[1];
                    }

                    Log.e(TAG, "====>" + activity1);

                    if (activity1.contains("Retailing") ||
                            activity2.contains("Retailing")) {

                        Intent intent = new Intent(requireContext(), OrderBookingRetailing.class);
                        startActivity(intent);

                    } else {


                        switch (activity1) {

                            case "Joint working":
                                Toast.makeText(requireContext(), "Are you on joint working", Toast.LENGTH_SHORT).show();
                                break;
                            case "Meeting":
                                Toast.makeText(requireContext(), "You are in meeting", Toast.LENGTH_SHORT).show();
                                break;
                            case "New Distributor Search":
                                Toast.makeText(requireContext(), "You are on new distributor appointment", Toast.LENGTH_SHORT).show();
                                break;
                            case "Travelling":
                                Toast.makeText(requireContext(), "You are Travelling", Toast.LENGTH_SHORT).show();
                                break;
                            case "Payment Collection":
                                Toast.makeText(requireContext(), "You are in beat for payment collection", Toast.LENGTH_SHORT).show();
                                break;
                            case "Marketing/Promotion":
                                Toast.makeText(requireContext(), "You are in beat for marketing/promotion", Toast.LENGTH_SHORT).show();
                                break;
                            case "Van Sales":
                                Toast.makeText(requireContext(), "You are in beat for van sales", Toast.LENGTH_SHORT).show();
                                break;
                            case "Others":
                                Toast.makeText(requireContext(), "Order booking not allowed in Others", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Intent intent = new Intent(requireContext(), OrderBookingRetailing.class);
                                startActivity(intent);
                                break;

                        }
                    }

                }
            }
        });

        btnStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MockLocationChecker mockLocationChecker = new MockLocationChecker(getContext());

                if (mockLocationChecker.isMockLocationEnabled()) {
                    Log.d("TAG", "Check Mock-1");
                    toastError(view,"Mock location is turned ON, you must turn it OFF to continue using this app.");
                    return;
                }

                if (mockLocationChecker.isAnyLocationMock()) {
                    Log.d("TAG", "Check Mock-2");
                    toastError(view,"Mock location is turned ON, you must turn it OFF to continue using this app.");
                    return;
                }

                tokenRegenerate();
                showActivityList(false);
            }
        });

        btnApplyLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(myPref.getString(getString(R.string.emp_id_key), ""),
                        checkInTime, "Apply Leave clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(getContext(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {

                        attendanceDialog();
                    }

                });
            }
        });


        btnShareClaim.setOnClickListener(view -> {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

            String[] temp = checkInTime.split(" ");

            utilityClass.setEvent(myPref.getString(getString(R.string.emp_id_key), ""),
                    checkInTime, "ShareClaim clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                    String.valueOf(locationProvider.getLongitude()));

            Intent intent = new Intent(requireContext(), MyClaimExpense.class);
            intent.putExtra("from", "main");
            startActivity(intent);
        });

        btnTomorrowsPlan.setOnClickListener(view -> {

            Intent intent = new Intent(requireContext(), TomorrowPlanDetails.class);
            startActivity(intent);
        });


        btnShareSummary.setOnClickListener(view -> {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

            String[] temp = checkInTime.split(" ");

            utilityClass.setEvent(myPref.getString(getString(R.string.emp_id_key), ""),
                    checkInTime, "ShareSummary clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                    String.valueOf(locationProvider.getLongitude()));

            new PingServer(internet -> {
                /* do something with boolean response */
                if (!internet) {
                    Toast.makeText(getContext(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                } else {

                    getEmployeeDailySummary();
                }

            });
        });


        btnCheckOut.setOnClickListener(view -> {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

            String[] temp = checkInTime.split(" ");

            utilityClass.setEvent(myPref.getString(getString(R.string.emp_id_key), ""),
                    checkInTime, "Day check out clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                    String.valueOf(locationProvider.getLongitude()));

            new PingServer(internet -> {
                /* do something with boolean response */
                if (!internet) {
                    Toast.makeText(getContext(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                } else {

                    // if (tempPref.getBoolean(getString(R.string.shareSummaryKey), false)) {
                    showAlertDialog();
//                    } else {
//                        Toast.makeText(requireContext(), "Share start working summary first", Toast.LENGTH_SHORT).show();
//                    }

                }

            });
        });
    }


    @SuppressLint({"MissingPermission", "HardwareIds"})
    private void tokenRegenerate() {
        SharedPreferences prefSFA = getActivity().getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
        String uName = prefSFA.getString("username", "");
        String pass = prefSFA.getString("password", "");
        Log.d(TAG, "tokenRegenerate user Name: "+uName);
        Log.d(TAG, "tokenRegenerate password: "+pass);
        if (uName != "" && pass != "") {

            JSONObject orderrrr = new JSONObject();
            try {
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);

                Log.e("FIREBASE", "Firebase reg id: " + regId);

                TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);

                orderrrr.put("auth", getString(R.string.apikey));
                orderrrr.put("cid", Integer.parseInt(prefSFA.getString("cmny_id",""))); //@Umesh
                orderrrr.put("username", uName);
                orderrrr.put("password", pass);
                orderrrr.put("ismobileuser", true);
                orderrrr.put("app_version",  BuildConfig.VERSION_NAME);
                //orderrrr.put("os_version", String.valueOf(Build.VERSION.SDK_INT));
                orderrrr.put("os_version", Build.VERSION.RELEASE);
                orderrrr.put("model", Build.BRAND + " " + Build.MODEL);
                try {

                    if (manager != null)
                        orderrrr.put("imei", manager.getDeviceId());

                } catch (SecurityException e) {
                    orderrrr.put("imei", "restricted in Q");
                }
                orderrrr.put("token", regId);

                Log.e("AUTH", "====" + orderrrr.toString());
                pref.edit().putString(getString(R.string.login_json), orderrrr.toString()).apply();

                Log.d("Login json", pref.getString(getString(R.string.login_json), ""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //@Umesh
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_USER_LOG_IN,
                    orderrrr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.e(TAG, "User Token Login===" + response);

                    try {
                        if(response.getInt("status")==1)
                        {
                            JSONObject data = response.getJSONObject("data");

                            JSONObject employee = data.getJSONObject("emp");

                            String emp_photo_url = employee.getString("profilePic");
                            if (emp_photo_url == null || emp_photo_url.isEmpty() || emp_photo_url.equalsIgnoreCase("null"))
                                emp_photo_url = SbAppConstants.PLACEHOLDER_URL;

                            //String token = response.getString("token");
//                            String token = employee.getString("fcmToken");//@Umesh
//                            Log.d(TAG, "onResponse Token: "+token);
                            JSONObject authtoken = data.getJSONObject("authtoken");
                            String TokenValidTo=authtoken.getString("expiration");
                            String token =authtoken.getString("token");
                            Log.d(TAG, "onResponse valid To JSON: "+new Gson().toJson(authtoken));
                            Log.d(TAG, "onResponse valid To Token: "+TokenValidTo);
                            SharedPreferences.Editor Teditor = prefSFA.edit();
                            Teditor.putString("token", token);
                            Teditor.putString("TokenValidTo", TokenValidTo);
                            Teditor.apply();

                        }

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        SbLog.printException("LoginScreen", "employeeLogin", ex.getMessage(), "0");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //loader.dismiss();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };
        }
    }

    private void toastError(View view,String msg) {
        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    private void attendanceDialog() {

        try {

            final Dialog dialog = new Dialog(requireContext(), R.style.DialogActivityTheme);
            dialog.setContentView(R.layout.attendance_dialog);
            if (dialog.getWindow() != null)
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            final EditText edtReason = dialog.findViewById(R.id.edtReason);
            final EditText edtReason2 = dialog.findViewById(R.id.edtReason2);
            final RadioButton chbMarkPresent = dialog.findViewById(R.id.rdBtn1);
            final RadioButton chbMarkAbsent = dialog.findViewById(R.id.rdBtn2);
            final RadioButton chbMarkWeeklyOff = dialog.findViewById(R.id.rdBtn3);
            //final RadioButton chbMarkLeave = (RadioButton) dialog.findViewById(R.id.rdBtn4);
            final RadioButton chbMarkHoliday = dialog.findViewById(R.id.rdBtn5);
            final RadioGroup rdGroupAttendance = dialog.findViewById(R.id.rdGroupAttendance);
            final LinearLayout llReason = dialog.findViewById(R.id.llReason);
            final LinearLayout llReason2 = dialog.findViewById(R.id.llReason2);
            LinearLayout llDone = dialog.findViewById(R.id.layoutDone);
            final Button btnFrom = dialog.findViewById(R.id.btnFrom);
            final Button btnTo = dialog.findViewById(R.id.btnTo);
            TextView tvDateView = dialog.findViewById(R.id.tvDateViewL);

            SimpleDateFormat sdff = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String date = sdff.format(Calendar.getInstance().getTime());
            tvDateView.setText(date);
            chbMarkPresent.setText("Leave Today");
            chbMarkAbsent.setText("Long Leave");
            chbMarkWeeklyOff.setText(R.string.weeklyoff_e);
            chbMarkHoliday.setText(R.string.holiday_e);


            btnFrom.setOnClickListener(view -> {

                try {
                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    final int mYear = c.get(Calendar.YEAR);
                    final int mMonth = c.get(Calendar.MONTH);
                    final int mDay = c.get(Calendar.DAY_OF_MONTH);

                    @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                            (view12, year, monthOfYear, dayOfMonth) -> {


                                if (dayOfMonth >= mDay && (monthOfYear + 1) >= mMonth && year == mYear)
                                    btnFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                else if (year > mYear)
                                    btnFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                else
                                    Toast.makeText(requireContext(), "Can not apply leave for back date", Toast.LENGTH_SHORT).show();

                            }, mYear, mMonth, mDay);

                    datePickerDialog.show();

                } catch (Exception e) {
                    e.getMessage();
                }


            });


            btnTo.setOnClickListener(view -> {

                try {

                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    final int mYear = c.get(Calendar.YEAR);
                    final int mMonth = c.get(Calendar.MONTH);
                    final int mDay = c.get(Calendar.DAY_OF_MONTH);

                    @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                            (view1, year, monthOfYear, dayOfMonth) -> {

                                if (dayOfMonth >= mDay && (monthOfYear + 1) >= mMonth && year == mYear)
                                    btnTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                else if (year > mYear)
                                    btnTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                else
                                    Toast.makeText(requireContext(), "Can not apply leave for back date", Toast.LENGTH_SHORT).show();

                            }, mYear, mMonth, mDay);

                    datePickerDialog.show();

                } catch (Exception e) {
                    e.getMessage();
                }

            });


            chbMarkPresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    rdGroupAttendance.animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    rdGroupAttendance.setVisibility(View.GONE);
                                    llReason.setVisibility(View.VISIBLE);
                                }
                            });
                }
            });

            chbMarkAbsent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    rdGroupAttendance.animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    rdGroupAttendance.setVisibility(View.GONE);
                                    llReason2.setVisibility(View.VISIBLE);
                                }
                            });

                }
            });

            chbMarkWeeklyOff.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            });

            chbMarkHoliday.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            });

            llDone.setOnClickListener(view -> {

                String attendance = "", reason = "";

                if (chbMarkPresent.isChecked()) {

                    attendance = "leave";
                    reason = edtReason.getText().toString();

                } else if (chbMarkAbsent.isChecked()) {

                    attendance = "long leave";
                    reason = "from: " + btnFrom.getText().toString() + " to  " + btnTo.getText().toString() + " reason - "
                            + edtReason2.getText().toString();

                } else if (chbMarkHoliday.isChecked()) {

                    attendance = "holiday";
                    reason = "holiday";

                } else if (chbMarkWeeklyOff.isChecked()) {

                    attendance = "week off";
                    reason = "week off";

                } else {

                    Toast.makeText(requireContext(), "Select atleast one", Toast.LENGTH_SHORT).show();
                    return;

                }

                DateFormat timeFormat = new SimpleDateFormat(getString(R.string.timeformat));
                String checkInTime = timeFormat.format(Calendar.getInstance().getTime());

                if (!attendance.isEmpty() && !reason.isEmpty() && !checkInTime.isEmpty()) {

                    //markAttendance(attendance, reason, checkInTime, checkInTime);
                    startService(attendance, reason, checkInTime, checkInTime, "");
                    dialog.dismiss();


                } else if (reason.isEmpty()) {
                    edtReason.setError("Reason required");
                    edtReason.requestFocus();
                } else {

                    Toast.makeText(requireContext(), "All field required", Toast.LENGTH_SHORT).show();
                }


            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Dashboard", "===" + e.getMessage());
        }
    }

    private void sendDb() {


        final VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST,
                SbAppConstants.API_SUBMIT_DB,
                response -> {
                    try {

                        //@Umesh 26-06-2022
                        JSONObject obj = new JSONObject(new String(response.data));
                        Log.e(TAG, "Data Response: " + obj.toString());
                        if(obj.getInt("status")==1)
                        {
                            Toast.makeText(requireContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }, error -> {

        }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart("db", getFileDataFromDrawable()));

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private byte[] getFileDataFromDrawable() {

        try {
            String PATH = Environment.getExternalStorageDirectory() + "/CMNY/sb.db";
            File file = new File(PATH);
            //init array with file length
            byte[] bytesArray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

            return bytesArray;

        } catch (Exception e) {

            e.getMessage();
        }

        return null;
    }

    private void startService(String attendance, String reason, String checkInTime,
                              String checkOutTime, String workingHour) {

        //@Umesh 13-08-2022
        Log.d(TAG, "startService checkInTime: "+checkInTime);
        if (comment.isEmpty())comment=reason;

        if (!comment.isEmpty()) {
            Log.e(TAG, " Start service.....");
            if (activityListDialog != null)
                activityListDialog.dismiss();
            if (attendance.equalsIgnoreCase("checkOut")) {
                snackbar.setText("Checking out...");
                //syncing local db  on check out
                sendDb();
            }

            snackbar.show();


            enableDisableView(false);

            MarkAttendance markAttendance = new MarkAttendance(new Handler());
            Intent intent = new Intent(getContext(), MarkAttendanceService.class);
            intent.putExtra("attendance", "present");
//            intent.putExtra("attendance", attendance);
            intent.putExtra("reason", reason);
            intent.putExtra("checkInTime", checkInTime);
            intent.putExtra("checkOutTime", checkOutTime);
            intent.putExtra("workingHour", workingHour);
            intent.putExtra("receiver", markAttendance);
            intent.putExtra("activityType", activities);
            intent.putExtra("workingTown", workingTown);
            intent.putExtra("did", did);
            intent.putExtra("disName", distributorName);
            intent.putExtra("cmnt", comment);
            //@Umesh 20220914
            intent.putExtra("jointwrkemp_id", jointwrkempid);
            intent.putExtra("jointwrkemp_name", jointwrkempname);
            requireActivity().startService(intent);

        } else {
            Toast.makeText(getContext(), "Comment is mandatory", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableDisableView(boolean flag) {

        btnStartDay.setClickable(flag);
        btnApplyLeave.setClickable(flag);
        btnCheckOut.setClickable(flag);
        btnBookOrder.setClickable(flag);
    }

//    private void showNoInternetToast() {
//
//        Toast.makeText(requireContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
//    }

    private void showHistoryDialog() {

        Intent intent = new Intent(getContext(), SaleHistory.class);
        startActivity(intent);

    }

    private void showEmpInfoDialog(String name, String image, String sale, String tc, String pc) {

        Dialog empInfoDialog = new Dialog(getContext());
        empInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        empInfoDialog.setContentView(R.layout.leaderbord_info_dialog);
        final ImageView imgEmp = empInfoDialog.findViewById(R.id.imgEmp);
        TextView tvEmpName = empInfoDialog.findViewById(R.id.tvEmpNameL);
        TextView tvTCL = empInfoDialog.findViewById(R.id.tvTCL);
        TextView tvPCL = empInfoDialog.findViewById(R.id.tvPCL);
        TextView tvEmpSaleInKg = empInfoDialog.findViewById(R.id.tvEmpSaleInKgL);

        tvEmpName.setText(name);
        tvEmpSaleInKg.setText(sale + getString(R.string.unitt));
        tvPCL.setText(pc);
        tvTCL.setText(tc);

//        GlideFaceDetector.initialize(getContext());

        try {

            if (image != null && !image.isEmpty() && !image.contains("null")) {

                Glide.with(getContext())
                        .load(SbAppConstants.IMAGE_PREFIX2 + image)
                        .into(imgEmp);

                loadProfileImage2 = new LoadProfileImage2(imgEmp);
                loadProfileImage2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SbAppConstants.IMAGE_PREFIX2 + image);
            }

        } catch (Exception ignored) {

        }

//        empInfoDialog.setOnDismissListener(dialogInterface -> GlideFaceDetector.releaseDetector());
//
//        empInfoDialog.setOnCancelListener(dialogInterface -> GlideFaceDetector.releaseDetector());

        empInfoDialog.show();
    }

    private void showAlertDialog() {

        String y = "", n = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.alert));
        builder.setMessage("You are about to check out.Once you check " +
                "out you will be unable to check in again today.");
        y = getString(R.string.yes);
        n = getString(R.string.no);

        builder.setPositiveButton(y, (dialogInterface, i) -> {

            dialogInterface.dismiss();
            showAgain();
        });

        builder.setNegativeButton(n, (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAgain() {

        String y = "", n = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.douwanttocheckout));
        y = getString(R.string.yes);
        n = getString(R.string.no);

        builder.setPositiveButton(y, (dialogInterface, i) -> {
            //salesBeatDb.getMyDB();
            checkOut();
        });

        builder.setNegativeButton(n, (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean checkAttendance() {

        String townName = tempPref.getString(getString(R.string.town_name_key), "");

        Log.e(TAG, "------->" + tempPref.getString(getString(R.string.attendance_key), "") +
                " Check in: " + tempPref.getString(getString(R.string.check_in_time_key), "")
                + " actyType: " + tempPref.getString(getString(R.string.act_type_key), ""));

        if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("present")) {
            Log.d(TAG, "checkAttendance: "+tempPref.getString(getString(R.string.check_in_time_key), ""));
            tvAttendanceStatus.setText(tempPref.getString(getString(R.string.attendance_key), ""));
            tvCheckInTime.setText(tempPref.getString(getString(R.string.check_in_time_key_new), ""));
//            tvCheckInTime.setText(utilityClass.formateIn12(tempPref.getString(getString(R.string.check_in_time_key), "")));
            tvCheckOutTime.setText(utilityClass.formateIn12(tempPref.getString(getString(R.string.check_out_time_key), "")));

            String actType = tempPref.getString(getString(R.string.act_type_key), "");
            actType = actType.replaceAll("\\[", "");
            actType = actType.replaceAll("\\]", "");
            tvActType.setText(actType);

            if (!townName.isEmpty())
                tvWorkingTown.setText(townName);

            //to calculate working hour after check in
            calculateWorkingHour(tempPref.getString(getString(R.string.check_in_time_key), ""));

            llCurrentStatus.setVisibility(View.VISIBLE);
            llMarkAttendance.setVisibility(View.GONE);
            llShareReport.setVisibility(View.GONE);

            // if (getString(R.string.url_mode).equalsIgnoreCase("L")){

            /* calculating time for auto check out......*/
            Calendar rightNow = Calendar.getInstance();
            // return the hour in 24 hrs format (ranging from 0-23)
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            int currentMinute = rightNow.get(Calendar.MINUTE);
            if (currentHour >= 23 && currentMinute > 55)
            {
                //@Umesh 19-08-2022
                Sentry.captureMessage("AutoCheckOut Or ClearData Time:"+rightNow.getTime()+" From Dashboard");
                checkOut();
            }
            // }

            //showActivityList();

        } else if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("checkOut")) {


            handler1.removeCallbacks(runnable1);

            tvAttendanceStatus.setText(tempPref.getString(getString(R.string.attendance_key), ""));
            tvCheckInTime.setText(utilityClass.formateIn12(tempPref.getString(getString(R.string.check_in_time_key), "")));
            tvCheckOutTime.setText(utilityClass.formateIn12(tempPref.getString(getString(R.string.check_out_time_key), "")));
            tvTimer.setText(tempPref.getString(getString(R.string.working_time_key), ""));
            if (!townName.isEmpty()) tvWorkingTown.setText(townName);
            tvCheckOutTime.setVisibility(View.VISIBLE);
            btnCheckOut.setVisibility(View.GONE);
            btnBookOrder.setVisibility(View.GONE);
            llCurrentStatus.setVisibility(View.VISIBLE);
            llMarkAttendance.setVisibility(View.GONE);
            llShareReport.setVisibility(View.VISIBLE);

        } else if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("leave")
                || tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("holiday")
                || tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("week off")
                || tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("long leave")
        ) {

            tvLeaveReason.setText(tempPref.getString(getString(R.string.reason_key), ""));

            llMarkAttendance.setVisibility(View.GONE);
            llCurrentStatus.setVisibility(View.GONE);
            tvLeaveReason.setVisibility(View.VISIBLE);
            tvLeaveReasonTitle.setVisibility(View.VISIBLE);
            if (tvLeaveReason.getText().toString().trim().isEmpty())
                tvLeaveReason.setVisibility(View.GONE);


        }

        /*else if (utilityClass.isInternetConnected()) {

            SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
            final String date = sdff.format(Calendar.getInstance().getTime());

            getEmployeeRecordByDate(date);

        } else {

            showNoInternetToast();
        }*/

        refreshPage();

        return true;
    }

    private boolean setupEmpPerformanceViewPager() {

        FragmentManager fm = getChildFragmentManager();
        adapter = new ViewPagerAdapter1(fm);
        adapter.addFragment(new PrimarySale());
        adapter.addFragment(new SecondarySale());
//        adapter.addFragment(new ExpenseRatio());
        adapter.addFragment(new KraFragment());

        if (adapter.getCount() > 1) {

            empPerformanceViewPager.setClipToPadding(false);
            empPerformanceViewPager.setPadding(0, 0, 100, 0);
        }

        empPerformanceViewPager.setAdapter(adapter);

        final ViewPager finalEmpPerformanceViewPager = empPerformanceViewPager;
        empPerformanceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if ((position) == (finalEmpPerformanceViewPager.getAdapter().getCount() - 1)) {

                    finalEmpPerformanceViewPager.setClipToPadding(false);
                    finalEmpPerformanceViewPager.setPadding(100, 0, 0, 0);

                } else {

                    finalEmpPerformanceViewPager.setClipToPadding(false);
                    finalEmpPerformanceViewPager.setPadding(0, 0, 100, 0);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        empPerformanceViewPager.setOffscreenPageLimit(adapter.getCount());

        return true;
    }

    private void calculateWorkingHour(final String checkInTimeParam) {

        final SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);
        final Date Date1;

        try {

            Date1 = format.parse(checkInTimeParam);
            runnable1 = new Runnable() {
                @Override
                public void run() {


                    try {

                        String temp = format.format(Calendar.getInstance().getTime());
                        Date Date2 = format.parse(temp);
                        long mills = Date2.getTime() - Date1.getTime();

                        tvTimer.setText(getFormattedTime(mills));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    handler1.postDelayed(this, 1000);
                }
            };


            handler1.postDelayed(runnable1, 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("DefaultLocale")
    private String getFormattedTime(long mills) {
        final String FORMAT = "%02d:%02d:%02d";
        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(mills),
                TimeUnit.MILLISECONDS.toMinutes(mills) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(mills)),
                TimeUnit.MILLISECONDS.toSeconds(mills) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(mills)));

    }

    private void checkOut() {

        if (utilityClass.isInternetConnected()) {

            showActivityList(true);

        } else {

            Toast.makeText(requireContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotificationDialog(String title, String body) {

        final Dialog mDialog = new Dialog(requireContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.notification_dialog);
        if (mDialog.getWindow() != null)
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(false);
        TextView tvNotTitle = mDialog.findViewById(R.id.tvNotTitle);
        TextView tvNotificationTitle = mDialog.findViewById(R.id.tvNotificationTitle);
        TextView tvNotificationBody = mDialog.findViewById(R.id.tvNotificationBody);
        Button btnOk = mDialog.findViewById(R.id.btnNotiOk);

        tvNotTitle.setText("Alert!!");
        tvNotificationTitle.setText(title);
        tvNotificationBody.setText(body);

        btnOk.setOnClickListener(view -> {
            mDialog.dismiss();
            requireActivity().finishAffinity();
        });

        mDialog.show();
    }

    private void refreshPage() {

        try {

            mainActivity.initializeViewPager();

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void getEmployeeDailySummary() {

        final Dialog loader = new Dialog(requireContext(), R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = sdff.format(Calendar.getInstance().getTime());

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, SbAppConstants.API_GET_DAILY_SUMMARY + "?date=" + date,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        loader.dismiss();

                        Log.e("Response", "Daily summary====" + response);
                        try {
                            //@Umesh 10-March-2022
                            if(response.getInt("status")==1)
                            {
                                stocksList2.clear();
                                marketName.clear();
                                town.clear();
                                beatName.clear();
                                contact.clear();
                                totalOpening.clear();
                                totalSecondary.clear();
                                totalClosing.clear();

                                JSONObject data = response.getJSONObject("data");
                                String date = data.getString("date");
                                String name = data.getString("name");
                                String tc = data.getString("tc");
                                String pc = data.getString("pc");
                                String sale = data.getString("sale");
                                String mtc = data.getString("mtc");
                                String mpc = data.getString("mpc");
                                String nc = data.getString("ncCount");
                                String mSale = data.getString("msale");
                                String lc = data.getString("lc");

                                JSONArray distributors = data.getJSONArray("distributors");
                                for (int index = 0; index < distributors.length(); index++) {

                                    JSONObject object = (JSONObject) distributors.get(index);
                                    marketName.add(object.getString("name"));
                                    town.add(object.getString("town"));
                                    beatName.add(object.getString("beat"));
                                    contact.add(object.getString("contact"));
                                    totalOpening.add(object.getString("opening"));
                                    totalSecondary.add(object.getString("secondary"));
                                    totalClosing.add(object.getString("closing"));

                                    JSONArray stocks = object.getJSONArray("stocks");
                                    ArrayList<SkuItem> stocksList = new ArrayList<>();
                                    for (int i = 0; i < stocks.length(); i++) {

                                        SkuItem skuItem = new SkuItem();
                                        JSONObject objSt = (JSONObject) stocks.get(i);
                                        skuItem.setSku(objSt.getString("sku"));
                                        skuItem.setOpening(objSt.getString("opening"));
                                        skuItem.setSecondary(objSt.getString("secondary"));
                                        skuItem.setClosing(objSt.getString("closing"));

                                        stocksList.add(skuItem);
                                    }

                                    stocksList2.add(stocksList);
                                }

                                final List<SkuItem> skuItemList = new ArrayList<>();
                                if (data.has("linedSoldDetail") && !data.isNull("linedSoldDetail")) {
                                    JSONArray lineSoldArr = data.getJSONArray("linedSoldDetail");

                                    for (int l = 0; l < lineSoldArr.length(); l++) {
                                        SkuItem skuItem = new SkuItem();
                                        JSONArray skuArr = (JSONArray) lineSoldArr.get(l);
                                        String item = skuArr.getString(0);
                                        String unit = skuArr.getString(1);
                                        String qty = skuArr.getString(2);

                                        skuItem.setSku(item);
                                        skuItem.setOpening(qty);
                                        skuItem.setClosing(unit);

                                        skuItemList.add(skuItem);
                                    }
                                }

                                //current date string
                                Calendar cc = java.util.Calendar.getInstance();
                                SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
                                String endDate = sdff.format(cc.getTime());

                                getNewDitributorHistory(date, name, tc, pc, sale, mtc, mpc, mSale, nc, lc, skuItemList, endDate);

//                                showDailySummaryDailog(date, name, tc, pc, sale, mtc, mpc, mSale,
//                                        marketName, town, contact, totalOpening, totalSecondary, totalClosing, stocksList2, nc);
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
                // error
                loader.dismiss();
                if (error != null && error.networkResponse != null)
                    serverCall.handleError(error, TAG, "employeeSummary");
                else
                    Toast.makeText(getContext(), "Null error code", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        postRequest.setShouldCache(false);

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(requireContext()).add(postRequest);

    }

    private void getNewDitributorHistory(final String date, final String name, final String tc,
                                         final String pc, final String sale, final String mtc,
                                         final String mpc, final String mSale, final String nc,
                                         final String lc, final List<SkuItem> skuList, final String endDate) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_NEW_DISTRIBUTOR_HISTORY + "?fromDate=" + endDate + "&toDate=" + endDate,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("onResponse", "New Distributor history===" + response);
                //loader.dismiss();
                try {

                    //@Umesh 10-March-2022
                    if(response.getInt("status")==1)
                    {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray distributors = data.getJSONArray("distributors");
                        ArrayList<ClaimHistoryItem> newDistributorDetails = new ArrayList<>();

                        for (int i = 0; i < distributors.length(); i++)
                        {

                            JSONObject object = (JSONObject) distributors.get(i);
                            ClaimHistoryItem disH = new ClaimHistoryItem();

                            disH.setFirmName(object.getString("firmName"));
                            disH.setFirmAddress(object.getString("firmAddress"));
                            disH.setPin(object.getString("pin"));
                            disH.setCity(object.getString("city"));
                            disH.setState(object.getString("state"));
                            disH.setOwnerName(object.getString("ownerName"));
                            disH.setMobile1(object.getString("ownerMobile1"));
                            disH.setMobile2(object.getString("ownerMobile2"));
                            disH.setOwnerEmail(object.getString("email"));
                            disH.setGstin(object.getString("gstin"));
                            disH.setFssai(object.getString("fsi"));
                            disH.setPan(object.getString("pan"));
                            disH.setMonthlyTurnOver(object.getString("monthlyTurnover"));
                            disH.setOpDis(object.getString("opinion"));
                            disH.setRemarks(object.getString("comment"));
                            List<String> listProductDivision = new ArrayList<>();
                            listProductDivision.add(object.getString("productDivision"));
                            disH.setProduct(listProductDivision.toString());

                            newDistributorDetails.add(disH);

                        }
                        showDailySummaryDailog(date, name, tc, pc, sale, mtc, mpc, mSale,
                                marketName, town, contact, totalOpening, totalSecondary,
                                totalClosing, stocksList2, nc, lc, skuList, newDistributorDetails);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (error != null && error.networkResponse != null) {

                    showDailySummaryDailog(date, name, tc, pc, sale, mtc, mpc, mSale,
                            marketName, town, contact, totalOpening, totalSecondary,
                            totalClosing, stocksList2, nc, lc, skuList, null);

                } else {
                    Toast.makeText(getContext(), "Null error code", Toast.LENGTH_SHORT).show();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setShouldCache(false);

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);

    }

    @SuppressLint("SetTextI18n")
    private void showDailySummaryDailog(String date, String name, String tc, String pc, String sale, String mtc,
                                        String mpc, String mSale, ArrayList<String> marketName, ArrayList<String> town,
                                        ArrayList<String> contact, ArrayList<String> totalOpening,
                                        ArrayList<String> totalSecondary, ArrayList<String> totalClosing,
                                        ArrayList<ArrayList<SkuItem>> stocksList2, String nc,
                                        String lc, List<SkuItem> skuList, ArrayList<ClaimHistoryItem> newDistributorDetails) {

        final Dialog dialog = new Dialog(requireContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.employee_summary_layout);
//        if (dialog.getWindow() != null)
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView tvDate = dialog.findViewById(R.id.tvDate);
        final TextView tvAsmTsiName = dialog.findViewById(R.id.tvAsmTsiName);
        final TextView tvTodayTc = dialog.findViewById(R.id.tvTodayTc);
        final TextView tvTodayPc = dialog.findViewById(R.id.tvTodayPc);
        final TextView tvTodayNc = dialog.findViewById(R.id.tvTodayNc);
        final TextView tvTodayLc = dialog.findViewById(R.id.tvTodayLc);
        final TextView tvTodayTotalSale = dialog.findViewById(R.id.tvTodayTotalSale);
        final TextView tvMTD = dialog.findViewById(R.id.tvMTD);
        final TextView tvTC = dialog.findViewById(R.id.tvTC);
        final TextView tvPC = dialog.findViewById(R.id.tvPC);
        Button btnShare = dialog.findViewById(R.id.btnShare);
        LinearLayout container = dialog.findViewById(R.id.container);
        LinearLayout container2 = dialog.findViewById(R.id.container2);

        String[] months = {"January", "February", "March",
                "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};

        String arr[] = date.split("-");
        String yr = arr[0];
        String mn = arr[1];
        String dy = arr[2];
        int tempCount = Integer.parseInt(mn) - 1;
        String monthStr = months[tempCount];
        String strDate = dy + ", " + monthStr + ", " + yr;

        tvDate.setText(strDate);
        tvAsmTsiName.setText(name);
        tvTC.setText(mtc);
        tvPC.setText(mpc);
        tvMTD.setText(mSale + getString(R.string.unitt));
        tvTodayTc.setText(tc);
        tvTodayPc.setText(pc);
        tvTodayNc.setText(nc);
        tvTodayLc.setText(lc);
        tvTodayTotalSale.setText(sale + getString(R.string.unitt));


        LinearLayout.LayoutParams paramsH = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout llVerticalH = new LinearLayout(requireContext());
        llVerticalH.setLayoutParams(paramsH);
        llVerticalH.setOrientation(LinearLayout.VERTICAL);

        String text = "";

        text = text.concat("Date-03 : " + tvDate.getText().toString());
        text = text.concat("\n");
        text = text.concat("ASM/SO/TSI Name : " + tvAsmTsiName.getText().toString());

        text = text.concat("\n");
        text = text.concat("Today :-  Tc : " + tvTodayTc.getText().toString()
                + "  Pc : " + tvTodayPc.getText().toString()
                + "  Nc : " + tvTodayNc.getText().toString()
                + "  Lc : " + tvTodayLc.getText().toString()
                + "  Total Sale : " + tvTodayTotalSale.getText().toString());
        text = text.concat("\n");
        text = text.concat("\n");
        text = text.concat("\n");


        for (int i = 0; i < marketName.size(); i++) {

            LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView tvMarkNameLabel = new TextView(requireContext());
            tvMarkNameLabel.setText("Party Name :");
            tvMarkNameLabel.setPadding(15, 15, 15, 15);
            tvMarkNameLabel.setTextColor(Color.GRAY);
            tvMarkNameLabel.setLayoutParams(paramsV);

            TextView tvMarkNameValue = new TextView(requireContext());
            tvMarkNameValue.setText(marketName.get(i));
            tvMarkNameValue.setPadding(15, 15, 15, 15);
            tvMarkNameValue.setTextColor(Color.parseColor("#424242"));
            tvMarkNameValue.setLayoutParams(paramsV);

            LinearLayout llPartyName = new LinearLayout(requireContext());
            llPartyName.setLayoutParams(paramsH);
            llPartyName.setOrientation(LinearLayout.HORIZONTAL);
            llPartyName.addView(tvMarkNameLabel);
            llPartyName.addView(tvMarkNameValue);

            llVerticalH.addView(llPartyName);


            TextView tvStationNameLabel = new TextView(requireContext());
            tvStationNameLabel.setText("Station Name :");
            tvStationNameLabel.setPadding(15, 15, 15, 15);
            tvStationNameLabel.setTextColor(Color.GRAY);
            tvStationNameLabel.setLayoutParams(paramsV);

            TextView tvStationNameValue = new TextView(requireContext());
            tvStationNameValue.setText(town.get(i));
            tvStationNameValue.setPadding(15, 15, 15, 15);
            tvStationNameValue.setTextColor(Color.parseColor("#424242"));
            tvStationNameValue.setLayoutParams(paramsV);

            LinearLayout llStationName = new LinearLayout(requireContext());
            llStationName.setLayoutParams(paramsH);
            llStationName.setOrientation(LinearLayout.HORIZONTAL);
            llStationName.addView(tvStationNameLabel);
            llStationName.addView(tvStationNameValue);

            llVerticalH.addView(llStationName);


            TextView tvBeatNameLabel = new TextView(requireContext());
            tvBeatNameLabel.setText("Beat Name :");
            tvBeatNameLabel.setPadding(15, 15, 15, 15);
            tvBeatNameLabel.setTextColor(Color.GRAY);
            tvBeatNameLabel.setLayoutParams(paramsV);

            TextView tvBeatNameValue = new TextView(requireContext());
            tvBeatNameValue.setText(beatName.get(i));
            tvBeatNameValue.setPadding(15, 15, 15, 15);
            tvBeatNameValue.setTextColor(Color.parseColor("#424242"));
            tvBeatNameValue.setLayoutParams(paramsV);

            LinearLayout llBeatName = new LinearLayout(requireContext());
            llBeatName.setLayoutParams(paramsH);
            llBeatName.setOrientation(LinearLayout.HORIZONTAL);
            llBeatName.addView(tvBeatNameLabel);
            llBeatName.addView(tvBeatNameValue);

            llVerticalH.addView(llBeatName);


            TextView tvPartyContactPersonLabel = new TextView(requireContext());
            tvPartyContactPersonLabel.setText("Party Contact Person :");
            tvPartyContactPersonLabel.setPadding(15, 15, 15, 15);
            tvPartyContactPersonLabel.setTextColor(Color.GRAY);
            tvPartyContactPersonLabel.setLayoutParams(paramsV);

            TextView tvPartyContactPersonValue = new TextView(requireContext());
            tvPartyContactPersonValue.setText("");
            tvPartyContactPersonValue.setPadding(15, 15, 15, 15);
            tvPartyContactPersonValue.setTextColor(Color.parseColor("#424242"));
            tvPartyContactPersonValue.setLayoutParams(paramsV);

            LinearLayout llPartyContactPerson = new LinearLayout(requireContext());
            llPartyContactPerson.setLayoutParams(paramsH);
            llPartyContactPerson.setOrientation(LinearLayout.HORIZONTAL);
            llPartyContactPerson.addView(tvPartyContactPersonLabel);
            llPartyContactPerson.addView(tvPartyContactPersonValue);

            llVerticalH.addView(llPartyContactPerson);


            TextView tvMobileLabel = new TextView(requireContext());
            tvMobileLabel.setText("Mobile :");
            tvMobileLabel.setPadding(15, 15, 15, 15);
            tvMobileLabel.setTextColor(Color.GRAY);
            tvMarkNameLabel.setLayoutParams(paramsV);

            TextView tvMobileValue = new TextView(requireContext());
            tvMobileValue.setText(contact.get(i));
            tvMobileValue.setPadding(15, 15, 15, 15);
            tvMobileValue.setTextColor(Color.parseColor("#424242"));
            tvMarkNameValue.setLayoutParams(paramsV);

            LinearLayout llMobile = new LinearLayout(requireContext());
            llMobile.setLayoutParams(paramsH);
            llMobile.setOrientation(LinearLayout.HORIZONTAL);
            llMobile.addView(tvMobileLabel);
            llMobile.addView(tvMobileValue);

            llVerticalH.addView(llMobile);


            text = text.concat("\n");
            text = text.concat("Party Name : " + tvMarkNameValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Station Name : " + tvStationNameValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Beat Name : " + tvBeatNameValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Party Contact Person : " + tvPartyContactPersonValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Mobile  : " + tvMobileValue.getText().toString());

            text = text.concat("\n");
            text = text.concat("\n");
            text = text.concat("-------------------------------------------------------------");
            text = text.concat("\n");
            text = text.concat("SKU" + "      " + "Opening" + "    " + "Secondary" + "    " + "Closing");
            text = text.concat("\n");
            text = text.concat("-------------------------------------------------------------");
            text = text.concat("\n");


            /*------------------------------------*/

            LinearLayout.LayoutParams paramsDivided = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            paramsDivided.weight = (float) 0.15;
            paramsDivided.gravity = Gravity.CENTER;

            LinearLayout.LayoutParams paramsDivided2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            paramsDivided2.weight = (float) 0.55;
            paramsDivided2.gravity = Gravity.CENTER;
            TextView tvSKuLabel = new TextView(requireContext());
            tvSKuLabel.setText("SKU");
            tvSKuLabel.setTextColor(Color.GRAY);
            tvSKuLabel.setPadding(15, 15, 15, 15);
            tvSKuLabel.setLayoutParams(paramsDivided2);

            TextView tvOpeningLabel = new TextView(requireContext());
            tvOpeningLabel.setText("OS");
            tvOpeningLabel.setPadding(15, 15, 15, 15);
            tvOpeningLabel.setTextColor(Color.GRAY);
            tvOpeningLabel.setLayoutParams(paramsDivided);

            TextView tvSecondaryLabel = new TextView(requireContext());
            tvSecondaryLabel.setText("SEC");
            tvSecondaryLabel.setPadding(15, 15, 15, 15);
            tvSecondaryLabel.setTextColor(Color.GRAY);
            tvSecondaryLabel.setLayoutParams(paramsDivided);

            TextView tvPrimaryLabel = new TextView(requireContext());
            tvPrimaryLabel.setText("CS");
            tvPrimaryLabel.setPadding(15, 15, 15, 15);
            tvPrimaryLabel.setTextColor(Color.GRAY);
            tvPrimaryLabel.setLayoutParams(paramsDivided);


            LinearLayout llHeader = new LinearLayout(requireContext());
            llHeader.setLayoutParams(paramsH);
            llHeader.setOrientation(LinearLayout.HORIZONTAL);
            llHeader.addView(tvSKuLabel);
            llHeader.addView(tvOpeningLabel);
            llHeader.addView(tvSecondaryLabel);
            llHeader.addView(tvPrimaryLabel);

            LinearLayout.LayoutParams paramsL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

            LinearLayout line1 = new LinearLayout(requireContext());
            line1.setBackgroundColor(Color.GRAY);
            line1.setLayoutParams(paramsL);

            LinearLayout line2 = new LinearLayout(requireContext());
            line2.setBackgroundColor(Color.GRAY);
            line2.setLayoutParams(paramsL);


            LinearLayout line3 = new LinearLayout(requireContext());
            line3.setBackgroundColor(Color.GRAY);
            line3.setLayoutParams(paramsL);


            LinearLayout line4 = new LinearLayout(requireContext());
            line4.setBackgroundColor(Color.GRAY);
            line4.setLayoutParams(paramsL);


            llVerticalH.addView(line1);
            llVerticalH.addView(llHeader);
            llVerticalH.addView(line2);

            ArrayList<SkuItem> stocks = stocksList2.get(i);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llVertical = new LinearLayout(requireContext());

            for (int index = 0; index < stocks.size(); index++) {

                SkuItem skuItem = stocks.get(index);

                if (!skuItem.getOpening().isEmpty() && !skuItem.getOpening().equalsIgnoreCase("0")
                        || !skuItem.getClosing().isEmpty() && !skuItem.getClosing().equalsIgnoreCase("0")
                        || !skuItem.getSecondary().isEmpty() && !skuItem.getSecondary().equalsIgnoreCase("0")) {

                    TextView tvSKuValue = new TextView(requireContext());
                    tvSKuValue.setText(skuItem.getSku());
                    tvSKuValue.setPadding(15, 15, 15, 15);
                    tvSKuValue.setTextColor(Color.parseColor("#424242"));
                    tvSKuValue.setLayoutParams(paramsDivided2);

                    TextView tvOpeningValue = new TextView(requireContext());
                    tvOpeningValue.setText(skuItem.getOpening());
                    tvOpeningValue.setPadding(15, 15, 15, 15);
                    tvOpeningValue.setTextColor(Color.parseColor("#424242"));
                    tvOpeningValue.setLayoutParams(paramsDivided);

                    TextView tvSecondaryValue = new TextView(requireContext());
                    tvSecondaryValue.setText(skuItem.getSecondary());
                    tvSecondaryValue.setPadding(15, 15, 15, 15);
                    tvSecondaryValue.setTextColor(Color.parseColor("#424242"));
                    tvSecondaryValue.setLayoutParams(paramsDivided);

                    TextView tvPrimaryValue = new TextView(requireContext());
                    tvPrimaryValue.setText(skuItem.getClosing());
                    tvPrimaryValue.setPadding(15, 15, 15, 15);
                    tvPrimaryValue.setTextColor(Color.parseColor("#424242"));
                    tvPrimaryValue.setLayoutParams(paramsDivided);


                    LinearLayout llRow = new LinearLayout(requireContext());
                    llRow.setLayoutParams(paramsH);
                    llRow.setOrientation(LinearLayout.HORIZONTAL);
                    llRow.addView(tvSKuValue);
                    llRow.addView(tvOpeningValue);
                    llRow.addView(tvSecondaryValue);
                    llRow.addView(tvPrimaryValue);


                    llVertical.addView(llRow);
                    llVertical.setLayoutParams(params);
                    llVertical.setOrientation(LinearLayout.VERTICAL);


                    text = text.concat(tvSKuValue.getText().toString() + "          " + tvOpeningValue.getText().toString() + "        "
                            + tvSecondaryValue.getText().toString() + "        " + tvPrimaryValue.getText().toString());

                    text = text.concat("\n");


                }
            }

            llVerticalH.addView(llVertical);

            TextView tvSKuTotal = new TextView(requireContext());
            tvSKuTotal.setText("Total = ");
            tvSKuTotal.setPadding(15, 15, 15, 15);
            tvSKuTotal.setTextColor(Color.parseColor("#424242"));
            tvSKuTotal.setLayoutParams(paramsDivided2);

            TextView tvOpeningTotal = new TextView(requireContext());
            tvOpeningTotal.setText(totalOpening.get(i));
            tvOpeningTotal.setPadding(15, 15, 15, 15);
            tvOpeningTotal.setTextColor(Color.parseColor("#424242"));
            tvOpeningTotal.setLayoutParams(paramsDivided);

            TextView tvSecondaryTotal = new TextView(requireContext());
            tvSecondaryTotal.setText(totalSecondary.get(i));
            tvSecondaryTotal.setPadding(15, 15, 15, 15);
            tvSecondaryTotal.setTextColor(Color.parseColor("#424242"));
            tvSecondaryTotal.setLayoutParams(paramsDivided);

            TextView tvPrimaryTotal = new TextView(requireContext());
            tvPrimaryTotal.setText(totalClosing.get(i));
            tvPrimaryTotal.setPadding(15, 15, 15, 15);
            tvPrimaryTotal.setTextColor(Color.parseColor("#424242"));
            tvPrimaryTotal.setLayoutParams(paramsDivided);


            LinearLayout llFooter = new LinearLayout(requireContext());
            llFooter.setLayoutParams(paramsH);
            llFooter.setOrientation(LinearLayout.HORIZONTAL);
            llFooter.addView(tvSKuTotal);
            llFooter.addView(tvOpeningTotal);
            llFooter.addView(tvSecondaryTotal);
            llFooter.addView(tvPrimaryTotal);

            llVerticalH.addView(line3);
            llVerticalH.addView(llFooter);
            llVerticalH.addView(line4);


            text = text.concat("\n");
            text = text.concat("---------------------------------------------------------");
            text = text.concat("\n");
            text = text.concat("               " + "      " + tvOpeningTotal.getText().toString() + "      " + tvSecondaryTotal.getText().toString() + "      " + tvPrimaryTotal.getText().toString());
            text = text.concat("\n");
            text = text.concat("---------------------------------------------------------");
            text = text.concat("\n");

            if (i > 0) {
                text = text.concat("*****************************************************");
                text = text.concat("\n");
            }
        }


        container.addView(llVerticalH);


        text = text.concat("\n");
        text = text.concat("\n");
        text = text.concat("MTD = " + tvMTD.getText().toString());
        text = text.concat("\n");
        text = text.concat("Tc : " + tvTC.getText().toString());
        text = text.concat("\n");
        text = text.concat("Pc : " + tvPC.getText().toString());
        text = text.concat("\n");
        text = text.concat("\n");
        text = text.concat("--------------------------------------------------------");
        text = text.concat("\n");
        text = text.concat("\n");

        /*----------------------------Add New Distributor----------------------------*/
        if (newDistributorDetails != null && newDistributorDetails.size() > 0) {

            LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llVertical = new LinearLayout(requireContext());
            llVertical.setLayoutParams(params);
            llVertical.setOrientation(LinearLayout.VERTICAL);

            TextView tvSKuValue = new TextView(requireContext());
            tvSKuValue.setText("**** NEW DISTRIBUTOR ADDED ****");
            tvSKuValue.setPadding(15, 15, 15, 15);
            tvSKuValue.setTextColor(Color.parseColor("#424242"));
            tvSKuValue.setLayoutParams(paramsV);

            llVertical.addView(tvSKuValue);

            text = text.concat("\n");
            text = text.concat("*New Distributor Added*");
            text = text.concat("\n");
            text = text.concat("\n");


            for (int i = 0; i < newDistributorDetails.size(); i++) {

                TextView tvDistributorName = new TextView(requireContext());
                tvDistributorName.setText("Distributor Name: " + newDistributorDetails.get(i).getFirmName());
                tvDistributorName.setPadding(15, 15, 15, 15);
                tvDistributorName.setTextColor(Color.parseColor("#424242"));
                tvDistributorName.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorName);

                TextView tvDistributorAddress = new TextView(requireContext());
                tvDistributorAddress.setText("Distributor Address: " + newDistributorDetails.get(i).getFirmAddress());
                tvDistributorAddress.setPadding(15, 15, 15, 15);
                tvDistributorAddress.setTextColor(Color.parseColor("#424242"));
                tvDistributorAddress.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorAddress);


                TextView tvDistributorCity = new TextView(requireContext());
                tvDistributorCity.setText("City: " + newDistributorDetails.get(i).getCity());
                tvDistributorCity.setPadding(15, 15, 15, 15);
                tvDistributorCity.setTextColor(Color.parseColor("#424242"));
                tvDistributorCity.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorCity);


                TextView tvDistributorState = new TextView(requireContext());
                tvDistributorState.setText("State: " + newDistributorDetails.get(i).getState());
                tvDistributorState.setPadding(15, 15, 15, 15);
                tvDistributorState.setTextColor(Color.parseColor("#424242"));
                tvDistributorState.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorState);


                TextView tvOwnerName = new TextView(requireContext());
                tvOwnerName.setText("Owner Name: " + newDistributorDetails.get(i).getOwnerName());
                tvOwnerName.setPadding(15, 15, 15, 15);
                tvOwnerName.setTextColor(Color.parseColor("#424242"));
                tvOwnerName.setLayoutParams(paramsV);

                llVertical.addView(tvOwnerName);


                TextView tvOwnerMobile = new TextView(requireContext());
                tvOwnerMobile.setText("Owner Mobile No.: " + newDistributorDetails.get(i).getMobile1());
                tvOwnerMobile.setPadding(15, 15, 15, 15);
                tvOwnerMobile.setTextColor(Color.parseColor("#424242"));
                tvOwnerMobile.setLayoutParams(paramsV);

                llVertical.addView(tvOwnerMobile);


                TextView tvOpnionDis = new TextView(requireContext());
                tvOpnionDis.setText("Opinion About Distributor: " + newDistributorDetails.get(i).getOpDis());
                tvOpnionDis.setPadding(15, 15, 15, 15);
                tvOpnionDis.setTextColor(Color.parseColor("#424242"));
                tvOpnionDis.setLayoutParams(paramsV);

                llVertical.addView(tvOpnionDis);


                TextView tvRemarks = new TextView(requireContext());
                tvRemarks.setText("Remarks: " + newDistributorDetails.get(i).getRemarks());
                tvRemarks.setPadding(15, 15, 15, 15);
                tvRemarks.setTextColor(Color.parseColor("#424242"));
                tvRemarks.setLayoutParams(paramsV);

                llVertical.addView(tvRemarks);


                TextView tvLine = new TextView(requireContext());
                tvLine.setText("--------------------------------------");
                tvLine.setPadding(15, 15, 15, 15);
                tvLine.setTextColor(Color.parseColor("#424242"));
                tvLine.setLayoutParams(paramsV);

                llVertical.addView(tvLine);


                text = text.concat("Distributor Name : " + newDistributorDetails.get(i).getFirmName());
                text = text.concat("\n");
                text = text.concat("Distributor Address : " + newDistributorDetails.get(i).getFirmAddress());
                text = text.concat("\n");
                text = text.concat("City : " + newDistributorDetails.get(i).getCity());
                text = text.concat("\n");
                text = text.concat("State : " + newDistributorDetails.get(i).getState());
                text = text.concat("\n");
                text = text.concat("Owner Name : " + newDistributorDetails.get(i).getOwnerName());
                text = text.concat("\n");
                text = text.concat("Owner Mobile No.: " + newDistributorDetails.get(i).getMobile1());
                text = text.concat("\n");
                text = text.concat("Product Division : " + newDistributorDetails.get(i).getProduct());
                text = text.concat("\n");
                text = text.concat("Opinion About Distributor : " + newDistributorDetails.get(i).getOpDis());
                text = text.concat("\n");
                text = text.concat("Comments : " + newDistributorDetails.get(i).getRemarks());
                text = text.concat("\n");
                text = text.concat("**********************************************************");
                text = text.concat("\n");
            }

            container2.addView(llVertical);
        }


        final String finalText = text;
        btnShare.setOnClickListener(view -> {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Today Summary");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, finalText);
            startActivity(Intent.createChooser(sharingIntent, "Share summary"));

            dialog.dismiss();

        });

        dialog.show();

    }

    public boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return (manager.getMode() == AudioManager.MODE_IN_CALL || manager.getMode() == AudioManager.MODE_IN_COMMUNICATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getContext(), "onActivityResult Called", Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_CODE_MY_PICK) {

            if (data != null) {

                SharedPreferences.Editor editor = tempPref.edit();
                editor.putBoolean(getString(R.string.shareSummaryKey), true);
                editor.apply();

                if (tempPref.getBoolean(getString(R.string.shareSummaryKey), false)) {
                    llShareStartWorkingSummary.setVisibility(View.GONE);
                } else {
                    llShareStartWorkingSummary.setVisibility(View.VISIBLE);
                }

                startActivity(data);
            }


//            if (data != null && data.getComponent() != null && !TextUtils.isEmpty(data.getComponent().flattenToShortString())) {
//                String appName = data.getComponent().flattenToShortString();
//                // Now you know the app being picked.
//                // data is a copy of your launchIntent with this important extra info added.
//                // Start the selected activity
//
//            }
        }
    }

    private static class EmoticonExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }

    private class MarkAttendance extends ResultReceiver {

        private MarkAttendance(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case MarkAttendanceService.DOWNLOAD_ERROR1:

                    snackbar.dismiss();
                    enableDisableView(true);
                    showNotificationDialog("API deprecated.", "Kindly update your app or contact admin!");

                    activities.clear();
                    workingTown = "";
                    did = "";
                    distributorName = "";
                    comment = "";
                    empName = "";
                    meeting = "";
                    actCount = 0;

                    break;

                case MarkAttendanceService.DOWNLOAD_ERROR2:

                    snackbar.dismiss();
                    enableDisableView(true);
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();

                    activities.clear();
                    workingTown = "";
                    did = "";
                    distributorName = "";
                    comment = "";
                    empName = "";
                    meeting = "";
                    actCount = 0;

                    break;

                case MarkAttendanceService.DOWNLOAD_SUCCESS_PRESENT:

                    snackbar.dismiss();
                    enableDisableView(true);
                    Log.e(TAG, "--->" + tempPref.getString(getString(R.string.act_type_key), ""));
                    tvAttendanceStatus.setText(tempPref.getString(getString(R.string.attendance_key), ""));
                    tvActType.setText(tempPref.getString(getString(R.string.act_type_key), ""));
                    tvCheckInTime.setText(tempPref.getString(getString(R.string.check_in_time_key), ""));
                    tvCheckOutTime.setText(tempPref.getString(getString(R.string.check_out_time_key), ""));
                    tvWorkingTown.setText(tempPref.getString(getString(R.string.town_name_key), ""));


                    llCurrentStatus.setVisibility(View.VISIBLE);
                    llMarkAttendance.setVisibility(View.GONE);

                    activities.clear();
                    workingTown = "";
                    did = "";
                    distributorName = "";
                    comment = "";
                    empName = "";
                    meeting = "";
                    actCount = 0;

                    //showTownList();
                    shareStartWorkingSummary();
                    refreshPage();

                    //showActivityList();

                    break;

                case MarkAttendanceService.DOWNLOAD_SUCCESS_CHECK_OUT:

                    snackbar.dismiss();

                    Snackbar snackbar2 = Snackbar
                            .make(MainActivity.mainActivityLayout, "Thank you...Good night", Snackbar.LENGTH_LONG);

                    snackbar2.addCallback(new Snackbar.Callback() {

                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {

                            Snackbar snackbar3 = Snackbar
                                    .make(MainActivity.mainActivityLayout, "Have a great day ahead.", Snackbar.LENGTH_LONG);

                            snackbar3.addCallback(new Snackbar.Callback() {

                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {


                                }

                                @Override
                                public void onShown(Snackbar snackbar) {

                                }
                            });

                            snackbar3.show();

                        }

                        @Override
                        public void onShown(Snackbar snackbar) {

                        }
                    });

                    snackbar2.show();


                    enableDisableView(true);
                    tvCheckOutTime.setVisibility(View.VISIBLE);
                    btnCheckOut.setVisibility(View.GONE);
                    btnBookOrder.setVisibility(View.GONE);
                    llCurrentStatus.setVisibility(View.VISIBLE);
                    llMarkAttendance.setVisibility(View.GONE);
                    llShareReport.setVisibility(View.VISIBLE);

                    handler1.removeCallbacks(runnable1);

                    activities.clear();
                    workingTown = "";
                    did = "";
                    distributorName = "";
                    comment = "";
                    empName = "";
                    meeting = "";
                    actCount = 0;

                    refreshPage();

                    break;

                case MarkAttendanceService.DOWNLOAD_SUCCESS_LEAVE:
                    snackbar.dismiss();
                    Snackbar snackbar3 = Snackbar
                            .make(MainActivity.mainActivityLayout, "Have a great day ahead.", Snackbar.LENGTH_LONG);

                    snackbar3.show();
                    //enableDisableView(true);

                    tvLeaveReason.setText(tempPref.getString(getString(R.string.reason_key), ""));
                    llMarkAttendance.setVisibility(View.GONE);
                    tvLeaveReason.setVisibility(View.VISIBLE);
                    tvLeaveReasonTitle.setVisibility(View.VISIBLE);
                    if (tvLeaveReason.getText().toString().trim().isEmpty())
                        tvLeaveReason.setVisibility(View.GONE);


                    activities.clear();
                    workingTown = "";
                    did = "";
                    distributorName = "";
                    comment = "";
                    empName = "";
                    meeting = "";
                    actCount = 0;

                    refreshPage();

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class LoadProfileImage2 extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        private LoadProfileImage2(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {

                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(Bitmap result) {
            try {

                Bitmap blurredBitmap = BlurBuilder.blur(getContext(), result);
                Drawable d = new BitmapDrawable(getResources(), blurredBitmap);
                bmImage.setBackground(d);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadDistributors extends AsyncTask<Void, String, String> {

        JSONArray updatedAtArr = new JSONArray();
        ArrayList<String> disList = new ArrayList<>();
        ArrayList<String> didList = new ArrayList<>();
        LinearLayout llNext;
        //LinearLayout llNewStartWorkList;
        TextView tvLoadingMsg, tvNotListed, tvDistNotListed;
        private ArrayList<String> dids = new ArrayList<>();
        private ArrayList<String> didUpdatedAt = new ArrayList<>();
        boolean flag;
        String townName = "";

        public DownloadDistributors(/*LinearLayout llNewStartWorkList,*/
                LinearLayout llNext, TextView tvLoadingMsg,
                TextView tvNotListed, TextView tvDistNotListed, boolean flag) {

            //this.llNewStartWorkList = llNewStartWorkList;
            this.llNext = llNext;
            this.tvLoadingMsg = tvLoadingMsg;
            this.tvNotListed = tvNotListed;
            this.tvDistNotListed = tvDistNotListed;
            this.flag = flag;
        }

        @SuppressLint("SetTextI18n")
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //llNewStartWorkList.setVisibility(View.GONE);
            //llNewStartWorkList.setBackgroundColor(Color.WHITE);
            //rvDistributorList.setBackgroundColor(Color.WHITE);
            tvLoadingMsg.setText("Loading distributor list...");
            tvLoadingMsg.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            // if (!isCancelled()) {

            for (int i = 0; i < dids.size(); i++) {

                String did = dids.get(i);
                String updatedAt = didUpdatedAt.get(i);

                Cursor cursor = salesBeatDb.getDistributor(did);

                try {

                    //JSONObject obj = new JSONObject();
                    String updatedAtStored = "";

                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        updatedAtStored = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_LAST_UPDATED_AT));
                        if (!updatedAt.contains(updatedAtStored)) {

                            updatedAtArr.put(Integer.parseInt(did));

                            salesBeatDb.deleteDistributor(did);
                        }

                    } else {

                        updatedAtArr.put(Integer.parseInt(did));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                } finally {
                    if (cursor != null)
                        cursor.close();

                }

            }

            Log.e("DistributorList", "Distributor Json-->" + updatedAtArr.toString());

            try {
                townName = URLEncoder.encode(workingTown, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }

            return "";
        }


        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            JsonObjectRequest getDistListReq = new JsonObjectRequest(Request.Method.GET,
                    SbAppConstants.API_GET_DISTRIBUTORS_2 + "?town=" + townName+"&IsOther="+IsOther,null,
                    res -> {

                        downloadDistributorList = false;

                        try {

                            Log.e(TAG, " Distributor list: " + res);

                            //@Umesh 02-Feb-2022
                            if (res.getInt("status") == 1)
                            {
                                JSONArray distributors = res.getJSONArray("data");
                                for (int i = 0; i < distributors.length(); i++) {

                                    JSONObject obj = (JSONObject) distributors.get(i);

                                    // JSONObject zoneObj = obj.getJSONObject("zone");
                                    didList.add(obj.getString("did"));
                                    disList.add(obj.getString("name"));

                                }

                                if (res.getInt("status") == 1)
                                {


                                    if (distributors.length() == 0) {

                                        SharedPreferences.Editor editor = tempPref.edit();
                                        editor.putString(getString(R.string.distErrorKey), "No data: In Distributors!!");
                                        editor.apply();

                                    }

                                    //llNewStartWorkList.setVisibility(View.VISIBLE);
                                    tvLoadingMsg.setVisibility(View.GONE);
                                    rvDistributorList.setVisibility(View.VISIBLE);

                                    if (flag) {

                                        tvDistNotListed.setVisibility(View.VISIBLE);
                                        tvNotListed.setVisibility(View.GONE);

                                    } else {

                                        tvDistNotListed.setVisibility(View.GONE);
                                        tvNotListed.setVisibility(View.GONE);
                                    }

                                    if (disList.size() > 0) {

                                        ActivityListAdapter adapter = new ActivityListAdapter(getContext(), disList,
                                                new OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(int position) {

                                                        did = didList.get(position);
                                                        disN = disList.get(position);
                                                        llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                                        llNext.setClickable(true);
                                                        tvDistNotListed.setClickable(false);
                                                        tvDistNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));
                                                    }

                                                    @Override
                                                    public void onItemClick2(int position) {
                                                        //AnimCheckBox animCheckBox =
                                                        did = "";
                                                        disN = "";
                                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                        llNext.setClickable(false);
                                                        tvDistNotListed.setClickable(true);
                                                        tvDistNotListed.setBackgroundColor(Color.parseColor("#F0544D"));

                                                    }
                                                }, false);

                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                        rvDistributorList.setLayoutManager(layoutManager);
                                        int resId = R.anim.layout_animation_fall_down;
                                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                                        rvDistributorList.setLayoutAnimation(animation);
                                        rvDistributorList.setAdapter(adapter);
                                        rvDistributorList.getAdapter().notifyDataSetChanged();

                                        rvDistributorList.setVisibility(View.VISIBLE);

                                    } else {

                                        tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
                                        tvLoadingMsg.setVisibility(View.VISIBLE);
                                        //llNewStartWorkList.setVisibility(View.GONE);

                                    }


                                } else {

                                    tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
                                    tvLoadingMsg.setVisibility(View.VISIBLE);
                                    //llNewStartWorkList.setVisibility(View.GONE);

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvLoadingMsg.setText("" + e.getMessage());
                            tvLoadingMsg.setVisibility(View.VISIBLE);
                            //llNewStartWorkList.setVisibility(View.GONE);
                        }

                    }, error -> {

                //Toast.makeText(getContext(), "called: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                //                    if (error instanceof NetworkError) {
                //                        tvLoadingMsg.setText("" + error.getMessage());
                //                    } else if (error instanceof ServerError) {
                //                        tvLoadingMsg.setText("" + error.getMessage());
                //                    } else if (error instanceof AuthFailureError) {
                //                        tvLoadingMsg.setText("" + error.getMessage());
                //                    } else if (error instanceof ParseError) {
                //                        tvLoadingMsg.setText("" + error.getMessage());
                //                    } else if (error instanceof NoConnectionError) {
                //                        tvLoadingMsg.setText("" + error.getMessage());
                //                    } else if (error instanceof TimeoutError) {
                //                        Toast.makeText(getContext(),
                //                                "Oops. Timeout error!",
                //                                Toast.LENGTH_LONG).show();
                //                        tvLoadingMsg.setText("" + error.getMessage());
                //                    }
                //
                error.printStackTrace();
                try {
                    tvLoadingMsg.setText("Response code: " + error.networkResponse.statusCode);
                } catch (Exception e) {
                    tvLoadingMsg.setText("Response code: null");
                }


                //tvLoadingMsg.setText("helllo testign" );
                tvLoadingMsg.setVisibility(View.VISIBLE);
                rvDistributorList.setVisibility(View.GONE);

                //                    if (!utilityClass.isInternetConnected()) {
                //                        Log.e(TAG, "Distributor list: No Internet Error");
                //                        tvLoadingMsg.setText("No Internet");
                //
                //                        downloadDistributorList = true;
                //                        rvDistributorList.setVisibility(View.VISIBLE);
                //                        llNewStartWorkList.setVisibility(View.VISIBLE);
                //
                //                        disllNewStartWork = llNewStartWorkList;
                //                        disllNext = llNext;
                //                        distvLoadingMsgm = tvLoadingMsg;
                //                        distvNotListed = tvNotListed;
                //                        distvDisNotListed = tvDistNotListed;
                //                        disFlag = flag;
                //                        llNewStartWorkList.setBackgroundColor(Color.TRANSPARENT);
                //                        rvDistributorList.setBackgroundColor(Color.TRANSPARENT);
                //                        distvLoadingMsgm.setVisibility(View.VISIBLE);
                //
                //                        rvDistributorList.setAdapter(null);
                //
                //                    } else {
                //                        error.printStackTrace();
                //                        tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
                //                        tvLoadingMsg.setVisibility(View.VISIBLE);
                //                        llNewStartWorkList.setVisibility(View.GONE);
                //                    }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", myPref.getString("token", ""));
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            getDistListReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(requireContext()).add(getDistListReq);

//            StringRequest getDistListReq = new StringRequest(Request.Method.GET,
//                    SbAppConstants.API_GET_DISTRIBUTORS_3+"zone_id="+myPref.getString(getString(R.string.zone_id_key), ""),
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String res) {
//
//                            downloadDistributorList = false;
//
//                            try {
//
//                                Log.e(TAG, " Distributor list: " + res);
//                                JSONObject object = new JSONObject(res);
//
//                                String status = object.getString("status");
//                                String msg = object.getString("statusMessage");
//
//                                JSONArray distributors = object.getJSONArray("distributors");
//                                for (int i = 0; i < distributors.length(); i++) {
//
//
//                                    JSONObject obj = (JSONObject) distributors.get(i);
//
//                                    // JSONObject zoneObj = obj.getJSONObject("zone");
//                                    didList.add(obj.getString("did"));
//                                    disList.add(obj.getString("name"));
//
//                                }
//
//                                if (status.equalsIgnoreCase("success")) {
//
//
//                                    if (distributors.length() == 0) {
//
//                                        SharedPreferences.Editor editor = tempPref.edit();
//                                        editor.putString(getString(R.string.distErrorKey), "No data: " + msg);
//                                        editor.apply();
//
//                                    }
//
//                                    llNewStartWorkList.setVisibility(View.VISIBLE);
//                                    rvDistributorList.setVisibility(View.VISIBLE);
//
//                                    if (flag) {
//
//                                        tvDistNotListed.setVisibility(View.VISIBLE);
//                                        tvNotListed.setVisibility(View.GONE);
//
//                                    } else {
//
//                                        tvDistNotListed.setVisibility(View.GONE);
//                                        tvNotListed.setVisibility(View.GONE);
//                                    }
//
//                                    if (disList.size() > 0) {
//
//                                        ActivityListAdapter adapter = new ActivityListAdapter(getContext(), disList,
//                                                new OnItemClickListener() {
//                                                    @Override
//                                                    public void onItemClick(int position) {
//
//                                                        did = didList.get(position);
//                                                        disN = disList.get(position);
//                                                        llNext.setBackgroundColor(Color.parseColor("#5aac82"));
//                                                        tvDistNotListed.setClickable(false);
//                                                        tvDistNotListed.setBackgroundColor(Color.parseColor("#c0c0c0"));
//                                                    }
//
//                                                    @Override
//                                                    public void onItemClick2(int position) {
//                                                        //AnimCheckBox animCheckBox =
//                                                        did = "";
//                                                        disN = "";
//                                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
//                                                        tvDistNotListed.setClickable(true);
//                                                        tvDistNotListed.setBackgroundColor(Color.parseColor("#F0544D"));
//
//                                                    }
//                                                }, false);
//
//                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//                                        rvDistributorList.setLayoutManager(layoutManager);
//                                        int resId = R.anim.layout_animation_fall_down;
//                                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
//                                        rvDistributorList.setLayoutAnimation(animation);
//                                        rvDistributorList.setAdapter(adapter);
//                                        rvDistributorList.getAdapter().notifyDataSetChanged();
//
//                                        rvDistributorList.setVisibility(View.VISIBLE);
//
//                                    } else {
//
//                                        tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
//                                        tvLoadingMsg.setVisibility(View.VISIBLE);
//                                        llNewStartWorkList.setVisibility(View.GONE);
//
//                                    }
//
//
//                                } else {
//
//                                    tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
//                                    tvLoadingMsg.setVisibility(View.VISIBLE);
//                                    llNewStartWorkList.setVisibility(View.GONE);
//
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    Log.e(TAG, error.toString());
//                    tvLoadingMsg.setText(error.getMessage());
//                    tvLoadingMsg.setVisibility(View.VISIBLE);
//                    llNewStartWorkList.setVisibility(View.GONE);
//                    if (!utilityClass.isInternetConnected()) {
//                        Log.e(TAG, "Distributor list: No Internet Error");
//                        tvLoadingMsg.setText("No Internet");
//
////                        Toast toast =  Toast.makeText(getContext(), "Internet not Connected", Toast.LENGTH_LONG);
////                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
////                        toast.show();
//
//                        downloadDistributorList = true;
//                        rvDistributorList.setVisibility(View.VISIBLE);
//                        llNewStartWorkList.setVisibility(View.VISIBLE);
//
//                        disllNewStartWork = llNewStartWorkList;
//                        disllNext = llNext;
//                        distvLoadingMsgm = tvLoadingMsg;
//                        distvNotListed = tvNotListed;
//                        distvDisNotListed = tvDistNotListed;
//                        disFlag = flag;
//                        llNewStartWorkList.setBackgroundColor(Color.TRANSPARENT);
//                        rvDistributorList.setBackgroundColor(Color.TRANSPARENT);
//                        distvLoadingMsgm.setVisibility(View.VISIBLE);
//
//                        rvDistributorList.setAdapter(null);
//
//                    } else {
//                        error.printStackTrace();
//                        tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
//                        tvLoadingMsg.setVisibility(View.VISIBLE);
//                        llNewStartWorkList.setVisibility(View.GONE);
//                    }
//                }
//            }) {
//
//                @Override
//                public byte[] getBody() {
//                    HashMap<String, String> params2 = new HashMap<String, String>();
//                    params2.put("zone_id", myPref.getString(getString(R.string.zone_id_key), ""));
//                    return new JSONObject(params2).toString().getBytes();
//                }
//
//                @Override
//                public Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("Content-Type", "application/json; charset=utf-8");
//                    headers.put("authorization", myPref.getString("token", ""));
//                    return headers;
//                }
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/json";
//                }
//            };
//
//            Volley.newRequestQueue(requireContext()).add(getDistListReq);

        }

    }

}