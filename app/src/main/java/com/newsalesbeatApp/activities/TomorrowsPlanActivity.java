package com.newsalesbeatApp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.ActivityListAdapter;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.interfaces.OnItemClickListener;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

public class TomorrowsPlanActivity extends AppCompatActivity {

    String TAG = "TomorrowsPlanActivity";
    GPSLocation locationProvider;
    ArrayList<String> activities = new ArrayList<>();
    //ArrayList<String> activitiesId = new ArrayList<>();
    String towns = "";
    String distributors = "";
    String comment = "";
    String empName = "";
    String meeting = "";
    int actCount = 0;
    SalesBeatDb salesBeatDb;
    ActivityListAdapter adapter;
    private SharedPreferences tempPref, myPref;
    private ClientInterface apiIntentface;
    private MenuItem mSearchItem;
    private Toolbar mToolbar;

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        apiIntentface = RetrofitClient.getClient().create(ClientInterface.class);
        //tempPref2 = requireContext().getSharedPreferences(getString(R.string.temp_pref_name_2), Context.MODE_PRIVATE);
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        setContentView(R.layout.tomorrows_plan);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);


        mToolbar = findViewById(R.id.toolbar2);

        tvPageTitle.setText("Tomorrow's Plan");

        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();
        salesBeatDb = SalesBeatDb.getHelper(this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                TomorrowsPlanActivity.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });


        RecyclerView rvActivityList = findViewById(R.id.rvActivityList);
        RecyclerView rvTownList = findViewById(R.id.rvTownList);
        RecyclerView rvDistributorList = findViewById(R.id.rvDistributorList);
        RecyclerView rvEmployeeList = findViewById(R.id.rvEmployeeList);
        RecyclerView rvMeetingList = findViewById(R.id.rvMeetingList);
        LinearLayout llCommentAct = findViewById(R.id.llCommentAct);
        LinearLayout llTravelling = findViewById(R.id.llTravelling);
        LinearLayout llVanSales = findViewById(R.id.llVanSales);
        LinearLayout llNext = findViewById(R.id.llNext);
        LinearLayout llNewStartWorkList = findViewById(R.id.llNewStartWorkList);
        LinearLayout llDone = findViewById(R.id.llDone);
        LinearLayout llTownDist = findViewById(R.id.llTownDist);
        EditText edtComment = findViewById(R.id.edtComment);
        EditText edtFromNS = findViewById(R.id.edtFromNS);
        EditText edtToNS = findViewById(R.id.edtToNS);
        EditText edtVanNumber = findViewById(R.id.edtVanNumber);
        EditText edtDriverName = findViewById(R.id.edtDriverName);
        EditText edtTownName = findViewById(R.id.edtTownName);
        EditText edtDistributorName = findViewById(R.id.edtDistributorName);
        EditText edtEmpName = findViewById(R.id.edtEmpName);
        TextView tvTitle = findViewById(R.id.tvTitleForToady);
        TextView tvNext = findViewById(R.id.tvNext);
        TextView tvNotListed = findViewById(R.id.tvNotListed);
        TextView tvDone = findViewById(R.id.tvDone);
//        VoiceRippleView voiceRipple = findViewById(R.id.imgRecordAudio);


        rvActivityList.setVisibility(View.VISIBLE);
        initActivityList(rvActivityList, llNext);
        final int[] count = {0};
        activities.clear();

        tvNotListed.setVisibility(View.GONE);


        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                edtComment.setHint("Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                edtComment.setHint("Working on result...");
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    edtComment.setText(matches.get(0));
                else
                    edtComment.setHint("Nothing heard...try again");

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


//        voiceRipple.setRecordingListener(new RecordingListener() {
//            @Override
//            public void onRecordingStopped() {
//                Log.d(TAG, "onRecordingStopped()");
//            }
//
//            @Override
//            public void onRecordingStarted() {
//                Log.d(TAG, "onRecordingStarted()");
//            }
//        });

        String DIRECTORY_NAME = "AudioCache";

        // directory = new File(Environment.getExternalStorageDirectory(), DIRECTORY_NAME);
        File directory = new File(getExternalCacheDir().getAbsolutePath(), DIRECTORY_NAME);

        if (directory.exists()) {
            deleteFilesInDir(directory);
        } else {
            directory.mkdirs();
        }

        File audioFile = new File(directory + "/audio.mp3");

        // set view related settings for ripple view
//        voiceRipple.setRippleSampleRate(Rate.LOW);
//        voiceRipple.setRippleDecayRate(Rate.HIGH);
//        voiceRipple.setBackgroundRippleRatio(1.4);

        // set recorder related settings for ripple view
//        voiceRipple.setMediaRecorder(new MediaRecorder());
//        voiceRipple.setOutputFile(audioFile.getAbsolutePath());
//        voiceRipple.setAudioSource(MediaRecorder.AudioSource.MIC);
//        voiceRipple.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        voiceRipple.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // set inner icon
//        voiceRipple.setRecordDrawable(ContextCompat.getDrawable(TomorrowsPlanActivity.this, R.drawable.record),
//                ContextCompat.getDrawable(TomorrowsPlanActivity.this, R.drawable.recording));
//        voiceRipple.setIconSize(20);
//
//        Renderer currentRenderer = new TimerCircleRippleRenderer(getDefaultRipplePaint(),
//                getDefaultRippleBackgroundPaint(), getButtonPaint(), getArcPaint(), 10000.0, 0.0);
//        if (currentRenderer instanceof TimerCircleRippleRenderer) {
//            ((TimerCircleRippleRenderer) currentRenderer).setStrokeWidth(0);
//        }
//
//        voiceRipple.setRenderer(currentRenderer);

//        voiceRipple.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (voiceRipple.isRecording()) {
//                    voiceRipple.stopRecording();
//                    mSpeechRecognizer.stopListening();
//                    edtComment.setHint("You will see input here");
//                } else {
//                    voiceRipple.startRecording();
//                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//                    edtComment.setText("");
//                    edtComment.setHint("Listening...");
//                }
//            }
//        });


//        voiceRipple.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                        if (voiceRipple.isRecording()) {
//                            try {
//                                voiceRipple.stopRecording();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            mSpeechRecognizer.stopListening();
//                            edtComment.setHint("You will see input here");
//                        }
//                        break;
//
//                    case MotionEvent.ACTION_DOWN:
//                        if (!voiceRipple.isRecording()) {
//                            voiceRipple.startRecording();
//                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//                            edtComment.setText("");
//                            edtComment.setHint("Please wait...");
//                        }
//                        break;
//                }
//                return false;
//            }
//        });


        tvNotListed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activities.size() != 0) {
                    if (towns.isEmpty()) {

                        rvTownList.setVisibility(View.GONE);
                        llTownDist.setVisibility(View.VISIBLE);
                        llDone.setVisibility(View.VISIBLE);
                        llCommentAct.setVisibility(View.VISIBLE);
                        llNext.setVisibility(View.GONE);

                        if (activities.size() == 1 && activities.get(0).equalsIgnoreCase("Retailing"))
                            edtEmpName.setVisibility(View.GONE);
                        else if (activities.size() == 1 && activities.get(0).equalsIgnoreCase("Joint Working"))
                            edtEmpName.setVisibility(View.VISIBLE);
                        else if ((activities.size() == 2)
                                && ((activities.get(0).equalsIgnoreCase("Retailing") && activities.get(1).equalsIgnoreCase("Joint Working"))
                                || (activities.get(0).equalsIgnoreCase("Joint Working") && activities.get(1).equalsIgnoreCase("Retailing")))) {

                            edtEmpName.setVisibility(View.VISIBLE);

                        } else {

                            edtEmpName.setVisibility(View.GONE);
                        }


                    } else {

                        rvDistributorList.setVisibility(View.GONE);
                        llTownDist.setVisibility(View.VISIBLE);
                        llDone.setVisibility(View.VISIBLE);
                        llCommentAct.setVisibility(View.VISIBLE);
                        llNext.setVisibility(View.GONE);

                        edtTownName.setVisibility(View.GONE);

                        if (activities.size() == 1 && activities.get(0).equalsIgnoreCase("Retailing"))
                            edtEmpName.setVisibility(View.GONE);
                        else if (activities.size() == 1 && activities.get(0).equalsIgnoreCase("Joint Working"))
                            edtEmpName.setVisibility(View.VISIBLE);
                        else if ((activities.size() == 2)
                                && ((activities.get(0).equalsIgnoreCase("Retailing") && activities.get(1).equalsIgnoreCase("Joint Working"))
                                || (activities.get(0).equalsIgnoreCase("Joint Working") && activities.get(1).equalsIgnoreCase("Retailing")))) {

                            edtEmpName.setVisibility(View.VISIBLE);

                        } else {

                            edtEmpName.setVisibility(View.GONE);
                        }

                    }
                }
            }
        });


        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String town = edtTownName.getText().toString();
                String distributor = edtDistributorName.getText().toString();
                String comment = edtComment.getText().toString();

                if (!town.isEmpty()) {

                    if (!distributor.isEmpty()) {

                        if (!comment.isEmpty()) {

                            submitTomorrowPlan(comment, "", "",
                                    activities, town, distributor);

                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Comment is empty", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(TomorrowsPlanActivity.this, "Distributor name is empty", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(TomorrowsPlanActivity.this, "Town is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String temp = edtComment.getText().toString();
                if (!temp.isEmpty())
                    llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                else
                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Retailing
                if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("Retailing")) {

                    tvNotListed.setVisibility(View.VISIBLE);

                    if (count[0] == 0) {

                        rvActivityList.setVisibility(View.GONE);
                        rvTownList.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Select town.");

                        initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                        count[0] = 1;

                    } else if (count[0] == 1) {

                        if (!towns.isEmpty()) {

                            //imgBackPage.setVisibility(View.VISIBLE);
                            rvTownList.setVisibility(View.GONE);
                            //rvDistributorList.setVisibility(View.VISIBLE);
                            new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Select distributor.");


                            //initDistList(rvDistributorList);

                            count[0] = 2;
                            //tvNext.setText("Done");


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 2) {

                        if (!distributors.isEmpty()) {

                            rvDistributorList.setVisibility(View.GONE);

                            if (activities.size() > 1) {


                                if (activities.get(1).equalsIgnoreCase("Joint Working")) {

                                    getEmpList(rvEmployeeList, llNext, llNewStartWorkList);

                                    rvEmployeeList.setVisibility(View.VISIBLE);
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Joint working with?");

                                } else if (activities.get(1).equalsIgnoreCase("Meeting")) {

                                    rvMeetingList.setVisibility(View.VISIBLE);
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Type of meeting!");


                                    initMeetingList(rvMeetingList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Travelling")) {

                                    String from = edtFromNS.getText().toString();
                                    String to = edtToNS.getText().toString();

                                    if (from.isEmpty() && to.isEmpty()) {

                                        rvActivityList.setVisibility(View.GONE);
                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llTravelling.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide travelling details?");

                                    } else {

                                        if (!from.isEmpty()) {

                                            if (!to.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, "", "",
                                                                    activities, towns, distributors);
                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Van Sales")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String vanNum = edtVanNumber.getText().toString();
                                    String driverName = edtDriverName.getText().toString();

                                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llVanSales.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide Van details?");
                                        tvNext.setText("Done");

                                    } else {

                                        if (!vanNum.isEmpty()) {

                                            if (!driverName.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van number:" + vanNum, "", "",
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                } else {

                                    getCmntEnabled(rvDistributorList, llCommentAct, llNext, tvTitle, tvNext);
                                }

                                count[0] = 3;

                            } else {

                                getCmntEnabled(rvDistributorList, llCommentAct, llNext, tvTitle, tvNext);

                                count[0] = 4;
                            }

                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 3) {

                        if (activities.get(1).equalsIgnoreCase("Joint Working")) {

                            getCmntEnabled(rvEmployeeList, llCommentAct, llNext, tvTitle, tvNext);

                        } else if (activities.get(1).equalsIgnoreCase("Meeting")) {

                            getCmntEnabled(rvMeetingList, llCommentAct, llNext, tvTitle, tvNext);

                        } else {


                            comment = edtComment.getText().toString();
                            if (!comment.isEmpty()) {

                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {


                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, "", "",
                                                activities, towns, distributors);

                                    }

                                });

                            } else {
                                Toast.makeText(TomorrowsPlanActivity.this, "Comment should not be empty", Toast.LENGTH_SHORT).show();
                            }


                        }

                        count[0] = 4;

                    } else if (count[0] == 4) {

                        comment = edtComment.getText().toString();
                        if (!comment.isEmpty()) {

                            if (activities.size() > 1) {

                                if (activities.get(1) != null && activities.get(1).equalsIgnoreCase("Joint Working")) {

                                    new PingServer(internet -> {
                                        /* do something with boolean response */
                                        if (!internet) {
                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                        } else {


                                            //markAttendance("present", "present", checkInTime, "missing");
                                            submitTomorrowPlan(comment, empName, "",
                                                    activities, towns, distributors);

                                        }

                                    });


                                } else if (activities.get(1) != null && activities.get(1).equalsIgnoreCase("Meeting")) {

                                    new PingServer(internet -> {
                                        /* do something with boolean response */
                                        if (!internet) {
                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                        } else {


                                            //markAttendance("present", "present", checkInTime, "missing");
                                            submitTomorrowPlan(comment, "", meeting,
                                                    activities, towns, distributors);
                                        }

                                    });


                                } else {


                                    new PingServer(internet -> {
                                        /* do something with boolean response */
                                        if (!internet) {
                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                        } else {


                                            //markAttendance("present", "present", checkInTime, "missing");
                                            submitTomorrowPlan(comment, "", "",
                                                    activities, towns, distributors);

                                        }

                                    });
                                }

                            } else {

                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {


                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, "", "",
                                                activities, towns, distributors);
                                    }

                                });

                            }


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Comment should not be empty", Toast.LENGTH_SHORT).show();
                        }


                    }

                }
                //Joint Working
                else if (!activities.isEmpty() && activities.get(0).equalsIgnoreCase("Joint Working")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    if (count[0] == 0) {

                        rvActivityList.setVisibility(View.GONE);
                        rvTownList.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Select town");

                        initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                        count[0] = 1;

                    } else if (count[0] == 1) {

                        if (!towns.isEmpty()) {

                            //imgBackPage.setVisibility(View.VISIBLE);
                            rvTownList.setVisibility(View.GONE);
                            //rvDistributorList.setVisibility(View.VISIBLE);
                            new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Select distributor");


                            //initDistList(rvDistributorList);


                            count[0] = 2;
                            //tvNext.setText("Done");

                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 2) {

                        if (!distributors.isEmpty()) {

                            rvDistributorList.setVisibility(View.GONE);
                            rvEmployeeList.setVisibility(View.VISIBLE);
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Joint working with?");
                            llNewStartWorkList.setVisibility(View.GONE);
                            getEmpList(rvEmployeeList, llNext, llNewStartWorkList);

                            count[0] = 3;

                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 3) {


                        if (!empName.isEmpty()) {

//                            rvEmployeeList.setVisibility(View.GONE);
//                            llCommentAct.setVisibility(View.VISIBLE);
//                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
//                            tvTitle.setText("Explain today's plan in details.");
//                            tvNext.setText("Done");

                            if (activities.size() > 1) {

                                rvEmployeeList.setVisibility(View.GONE);

                                if (activities.get(1).equalsIgnoreCase("Meeting")) {

                                    rvMeetingList.setVisibility(View.VISIBLE);
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Type of meeting!");

                                    initMeetingList(rvMeetingList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String from = edtFromNS.getText().toString();
                                    String to = edtToNS.getText().toString();

                                    if (from.isEmpty() && to.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llTravelling.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide travelling details?");


                                    } else {

                                        if (!from.isEmpty()) {

                                            if (!to.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, "",
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                        }


                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Van Sales")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String vanNum = edtVanNumber.getText().toString();
                                    String driverName = edtDriverName.getText().toString();

                                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llVanSales.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide Van details?");
                                        tvNext.setText("Done");


                                    } else {

                                        if (!vanNum.isEmpty()) {

                                            if (!driverName.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, "",
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                } else {

                                    getCmntEnabled(rvEmployeeList, llCommentAct, llNext, tvTitle, tvNext);
                                }

                                count[0] = 4;

                            } else {

                                getCmntEnabled(rvEmployeeList, llCommentAct, llNext, tvTitle, tvNext);

                                count[0] = 5;
                            }


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 4) {

                        if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Meeting")) {

                            rvMeetingList.setVisibility(View.GONE);

                        } else {

                            comment = edtComment.getText().toString();
                            if (!comment.isEmpty()) {


                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {

                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, empName, "",
                                                activities, towns, distributors);

                                    }

                                });


                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                            }


                        }

                        getCmntEnabled(rvEmployeeList, llCommentAct, llNext, tvTitle, tvNext);

                        count[0] = 5;

                    } else if (count[0] == 5) {

                        comment = edtComment.getText().toString();
                        if (!comment.isEmpty()) {


                            new PingServer(internet -> {
                                /* do something with boolean response */
                                if (!internet) {
                                    Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                } else {

                                    //markAttendance("present", "present", checkInTime, "missing");
                                    submitTomorrowPlan(comment, empName, "",
                                            activities, towns, distributors);

                                }

                            });


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                        }


                    }


                }
                //Meeting
                else if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("Meeting")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    if (count[0] == 0) {

                        rvActivityList.setVisibility(View.GONE);
                        rvMeetingList.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Type of meeting!");


                        initMeetingList(rvMeetingList, llNext);

                        count[0] = 1;

                    } else if (count[0] == 1) {

                        if (!meeting.isEmpty()) {

                            //imgBackPage.setVisibility(View.VISIBLE);
                            rvMeetingList.setVisibility(View.GONE);
                            rvTownList.setVisibility(View.VISIBLE);
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Select town");


                            initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);


                            count[0] = 2;
                            //tvNext.setText("Done");

                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 2) {

                        if (!towns.isEmpty()) {


                            if (activities.size() > 1) {

                                rvTownList.setVisibility(View.GONE);

                                if (activities.get(1).equalsIgnoreCase("Joint Working")) {


                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                    //initDistList(rvDistributorList);
                                    //getEmpList(rvEmployeeList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Retailing")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String from = edtFromNS.getText().toString();
                                    String to = edtToNS.getText().toString();

                                    if (from.isEmpty() && to.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llTravelling.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide travelling details?");


                                    } else {

                                        if (!from.isEmpty()) {

                                            if (!to.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, "", meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                        }


                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Van Sales")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String vanNum = edtVanNumber.getText().toString();
                                    String driverName = edtDriverName.getText().toString();

                                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llVanSales.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide Van details?");
                                        tvNext.setText("Done");

                                    } else {

                                        if (!vanNum.isEmpty()) {

                                            if (!driverName.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, "", meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } else {

                                    getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);
                                }

                                count[0] = 3;

                            } else {

                                getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);

                                count[0] = 4;
                            }


//                            rvTownList.setVisibility(View.GONE);
//                            llCommentAct.setVisibility(View.VISIBLE);
//                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
//                            tvTitle.setText("Explain today's plan in details.");
//
//                            count[0] = 3;
//                            tvNext.setText("Done");

                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 3) {

                        if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Joint Working")) {

                            rvDistributorList.setVisibility(View.GONE);
                            rvEmployeeList.setVisibility(View.VISIBLE);
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Joint working with?");
                            getEmpList(rvEmployeeList, llNext, llNewStartWorkList);

                        } else if (activities.get(1) != null && activities.get(1).equalsIgnoreCase("Retailing")) {

                            getCmntEnabled(rvDistributorList, llCommentAct, llNext, tvTitle, tvNext);

                        } else {


                            comment = edtComment.getText().toString();
                            if (!comment.isEmpty()) {

                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {

                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, "", meeting,
                                                activities, towns, distributors);

                                    }

                                });


                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                            }

                        }

                        count[0] = 4;

                    } else if (count[0] == 4) {

                        comment = edtComment.getText().toString();
                        if (!comment.isEmpty()) {

                            if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Joint Working")) {

                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {


                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, empName, meeting,
                                                activities, towns, distributors);

                                    }

                                });

                            } else {

                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {


                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, "", meeting,
                                                activities, towns, distributors);

                                    }

                                });

                            }


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                        }


                    }


                }
                //New Distributor Appointment
                else if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("New Distributor Appointment")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    if (count[0] == 0) {

                        rvActivityList.setVisibility(View.GONE);
                        rvTownList.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Select town.");

                        initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                        count[0] = 1;

                    } else if (count[0] == 1) {

                        if (!towns.isEmpty()) {


                            if (activities.size() > 1) {

                                rvTownList.setVisibility(View.GONE);
                                if (activities.get(1).equalsIgnoreCase("Joint Working")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                    //initDistList(rvDistributorList);
                                    //getEmpList(rvEmployeeList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Meeting")) {

                                    rvMeetingList.setVisibility(View.VISIBLE);
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Type of meeting!");


                                    initMeetingList(rvMeetingList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String from = edtFromNS.getText().toString();
                                    String to = edtToNS.getText().toString();

                                    if (from.isEmpty() && to.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llTravelling.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide travelling details?");


                                    } else {

                                        if (!from.isEmpty()) {

                                            if (!to.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, "", "",
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                } else if (activities.get(1).equalsIgnoreCase("Van Sales")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String vanNum = edtVanNumber.getText().toString();
                                    String driverName = edtDriverName.getText().toString();

                                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llVanSales.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide Van details?");
                                        tvNext.setText("Done");

                                    } else {

                                        if (!vanNum.isEmpty()) {

                                            if (!driverName.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, "", "",
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                        }


                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Retailing")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                } else {

                                    getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);
                                }

                                count[0] = 2;

                            } else {


                                getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);

                                count[0] = 3;
                            }

                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 2) {


                        if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Joint Working")) {

                            rvDistributorList.setVisibility(View.GONE);
                            rvEmployeeList.setVisibility(View.VISIBLE);
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Joint working with?");
                            getEmpList(rvEmployeeList, llNext, llNewStartWorkList);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Retailing")) {

                            getCmntEnabled(rvDistributorList, llCommentAct, llNext, tvTitle, tvNext);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Meeting")) {

                            getCmntEnabled(rvMeetingList, llCommentAct, llNext, tvTitle, tvNext);

                        } else {


                            // getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);

                            comment = edtComment.getText().toString();
                            if (!comment.isEmpty()) {


                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {

                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, "", "",
                                                activities, towns, distributors);

                                    }

                                });


                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                            }

                        }

                        count[0] = 3;

                    } else if (count[0] == 3) {

                        comment = edtComment.getText().toString();
                        if (!comment.isEmpty()) {


                            new PingServer(internet -> {
                                /* do something with boolean response */
                                if (!internet) {
                                    Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                } else {

                                    //markAttendance("present", "present", checkInTime, "missing");
                                    submitTomorrowPlan(comment, empName, meeting,
                                            activities, towns, distributors);

                                }

                            });


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                        }


                    }

                }
                //Travelling
                else if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("Travelling")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    //initTownList(rvTownList, rvDistributorList);

                    String from = edtFromNS.getText().toString();
                    String to = edtToNS.getText().toString();

                    if (from.isEmpty() && to.isEmpty()) {

                        rvActivityList.setVisibility(View.GONE);
                        llCommentAct.setVisibility(View.VISIBLE);
                        llTravelling.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Please provide travelling details?");

                    } else {


                        if (!from.isEmpty()) {

                            if (!to.isEmpty()) {

                                comment = edtComment.getText().toString();

                                if (!comment.isEmpty()) {

                                    rvActivityList.setVisibility(View.GONE);
                                    llCommentAct.setVisibility(View.GONE);
                                    llVanSales.setVisibility(View.GONE);


                                    if (activities.size() > 1) {

                                        if (activities.get(1).equalsIgnoreCase("Joint Working")) {


                                            if (towns.isEmpty()) {

                                                tvTitle.setText("Select town.");
                                                tvNext.setText("Next");
                                                initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);
                                                rvTownList.setVisibility(View.VISIBLE);

                                            } else {


                                                if (distributors.isEmpty()) {

                                                    rvTownList.setVisibility(View.GONE);
                                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                    tvTitle.setText("Select distributor");
                                                    //getEmpList(rvEmployeeList, llNext);

                                                } else {

                                                    rvDistributorList.setVisibility(View.GONE);
                                                    rvEmployeeList.setVisibility(View.VISIBLE);
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                    tvTitle.setText("Joint working with?");
                                                    getEmpList(rvEmployeeList, llNext, llNewStartWorkList);
                                                    tvNext.setText("Done");

                                                    if (!empName.isEmpty()) {

                                                        new PingServer(internet -> {
                                                            /* do something with boolean response */
                                                            if (!internet) {
                                                                Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                            } else {

                                                                //markAttendance("present", "present", checkInTime, "missing");
                                                                submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                        activities, towns, distributors);

                                                            }

                                                        });
                                                    }
                                                }
                                            }

                                        } else if (activities.get(1).equalsIgnoreCase("Meeting")) {


                                            if (meeting.isEmpty()) {

                                                rvMeetingList.setVisibility(View.VISIBLE);
                                                llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                tvTitle.setText("Type of meeting!");
                                                initMeetingList(rvMeetingList, llNext);

                                            } else {

                                                if (towns.isEmpty()) {

                                                    rvMeetingList.setVisibility(View.GONE);
                                                    rvTownList.setVisibility(View.VISIBLE);
                                                    tvTitle.setText("Select town.");
                                                    tvNext.setText("Done");
                                                    initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                                                } else {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });
                                                }

                                            }

                                        } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                            rvActivityList.setVisibility(View.GONE);
                                            llCommentAct.setVisibility(View.VISIBLE);
                                            llVanSales.setVisibility(View.VISIBLE);
                                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                            tvTitle.setText("Please provide Van details?");

                                            //initTownList(rvTownList, rvDistributorList);

                                            String vanNum = edtVanNumber.getText().toString();
                                            String driverName = edtDriverName.getText().toString();

                                            if (!vanNum.isEmpty()) {

                                                if (!driverName.isEmpty()) {

                                                    comment = edtComment.getText().toString();
                                                    if (!comment.isEmpty()) {


                                                        new PingServer(internet -> {
                                                            /* do something with boolean response */
                                                            if (!internet) {
                                                                Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                            } else {


                                                                //markAttendance("present", "present", checkInTime, "missing");
                                                                submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                        activities, towns, distributors);

                                                            }

                                                        });


                                                    } else {

                                                        Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Driver name is empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                            }


                                        } else if (activities.get(1).equalsIgnoreCase("Retailing")) {

                                            Log.e("NAAAN", "---->" + activities.get(1) + " and town-->" + towns);

                                            if (towns.isEmpty()) {

                                                tvTitle.setText("Select town.");
                                                tvNext.setText("Next");
                                                initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);
                                                rvTownList.setVisibility(View.VISIBLE);

                                            } else {


                                                if (distributors.isEmpty()) {

                                                    rvTownList.setVisibility(View.GONE);
                                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                    tvTitle.setText("Select distributor");
                                                    tvNext.setText("Done");

                                                } else {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });
                                                }
                                            }

                                        } else {

                                            if (towns.isEmpty()) {

                                                tvTitle.setText("Select town.");
                                                tvNext.setText("Done");
                                                initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);
                                                rvTownList.setVisibility(View.VISIBLE);

                                            } else {

                                                new PingServer(internet -> {
                                                    /* do something with boolean response */
                                                    if (!internet) {
                                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                    } else {


                                                        //markAttendance("present", "present", checkInTime, "missing");
                                                        submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                activities, towns, distributors);

                                                    }

                                                });
                                            }
                                        }

                                    } else {

                                        new PingServer(internet -> {
                                            /* do something with boolean response */
                                            if (!internet) {
                                                Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                            } else {


                                                //markAttendance("present", "present", checkInTime, "missing");
                                                submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                        activities, towns, distributors);
                                            }

                                        });

                                    }


                                } else {

                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                }


                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                //Payment Collection
                else if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("Payment Collection")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    if (count[0] == 0) {

                        rvActivityList.setVisibility(View.GONE);
                        rvTownList.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Select town.");

                        initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                        count[0] = 1;

                    } else if (count[0] == 1) {

                        if (!towns.isEmpty()) {


                            if (activities.size() > 1) {

                                rvTownList.setVisibility(View.GONE);
                                if (activities.get(1).equalsIgnoreCase("Joint Working")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                    //initDistList(rvDistributorList);
                                    //getEmpList(rvEmployeeList, llNext,llNewStartWorkList);

                                } else if (activities.get(1).equalsIgnoreCase("Meeting")) {

                                    rvMeetingList.setVisibility(View.VISIBLE);
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Type of meeting!");


                                    initMeetingList(rvMeetingList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String from = edtFromNS.getText().toString();
                                    String to = edtToNS.getText().toString();

                                    if (from.isEmpty() && to.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llTravelling.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide travelling details?");

                                    } else {

                                        if (!from.isEmpty()) {

                                            if (!to.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Van Sales")) {

                                    //initTownList(rvTownList, rvDistributorList);

                                    String vanNum = edtVanNumber.getText().toString();
                                    String driverName = edtDriverName.getText().toString();

                                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llVanSales.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide Van details?");
                                        tvNext.setText("Done");

                                    } else {

                                        if (!vanNum.isEmpty()) {

                                            if (!driverName.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } else if (activities.get(1).equalsIgnoreCase("Retailing")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                } else {

                                    getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);
                                }

                                count[0] = 2;


                            } else {

                                getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);

                                count[0] = 3;
                            }


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 2) {

                        if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Joint Working")) {

                            rvDistributorList.setVisibility(View.GONE);
                            rvEmployeeList.setVisibility(View.VISIBLE);
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Joint working with?");
                            getEmpList(rvEmployeeList, llNext, llNewStartWorkList);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Retailing")) {

                            getCmntEnabled(rvDistributorList, llCommentAct, llNext, tvTitle, tvNext);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Meeting")) {

                            getCmntEnabled(rvMeetingList, llCommentAct, llNext, tvTitle, tvNext);

                        } else {

                            getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);
                            comment = edtComment.getText().toString();
                            if (!comment.isEmpty()) {


                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {

                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, empName, meeting,
                                                activities, towns, distributors);

                                    }

                                });


                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                            }

                        }


                        count[0] = 3;

                    } else if (count[0] == 3) {

                        comment = edtComment.getText().toString();
                        if (!comment.isEmpty()) {


                            new PingServer(internet -> {
                                /* do something with boolean response */
                                if (!internet) {
                                    Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                } else {

                                    //markAttendance("present", "present", checkInTime, "missing");
                                    submitTomorrowPlan(comment, empName, meeting,
                                            activities, towns, distributors);

                                }

                            });


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                        }


                    }

                }

                //Marketing/Promotion
                else if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("Marketing/Promotion")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    Log.e("ActType", "count==>" + count[0]);
                    if (count[0] == 0) {

                        rvActivityList.setVisibility(View.GONE);
                        rvTownList.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Select town.");

                        initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                        count[0] = 1;

                    } else if (count[0] == 1) {

                        if (!towns.isEmpty()) {


                            if (activities.size() > 1) {

                                rvTownList.setVisibility(View.GONE);
                                if (activities.get(1).equalsIgnoreCase("Joint Working")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                    //initDistList(rvDistributorList);
                                    //getEmpList(rvEmployeeList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Meeting")) {

                                    rvMeetingList.setVisibility(View.VISIBLE);
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Type of meeting!");


                                    initMeetingList(rvMeetingList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String from = edtFromNS.getText().toString();
                                    String to = edtToNS.getText().toString();

                                    if (from.isEmpty() && to.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llTravelling.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide travelling details?");

                                    } else {

                                        if (!from.isEmpty()) {

                                            if (!to.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                    activities, towns, distributors);
                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Van Sales")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String vanNum = edtVanNumber.getText().toString();
                                    String driverName = edtDriverName.getText().toString();

                                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llVanSales.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide Van details?");
                                        tvNext.setText("Done");


                                    } else {

                                        if (!vanNum.isEmpty()) {

                                            if (!driverName.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                        }


                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Retailing")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                } else {
                                    getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);
                                }

                                count[0] = 2;

                            } else {

                                getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);

                                count[0] = 3;
                            }


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 2) {

                        if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Joint Working")) {

                            rvDistributorList.setVisibility(View.GONE);
                            rvEmployeeList.setVisibility(View.VISIBLE);
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Joint working with?");
                            getEmpList(rvEmployeeList, llNext, llNewStartWorkList);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Retailing")) {

                            getCmntEnabled(rvDistributorList, llCommentAct, llNext, tvTitle, tvNext);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Meeting")) {

                            getCmntEnabled(rvMeetingList, llCommentAct, llNext, tvTitle, tvNext);

                        } else {


                            comment = edtComment.getText().toString();
                            if (!comment.isEmpty()) {


                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {


                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, empName, meeting,
                                                activities, towns, distributors);
                                    }

                                });


                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                            }


                        }


                        count[0] = 3;

                    } else if (count[0] == 3) {

                        comment = edtComment.getText().toString();
                        if (!comment.isEmpty()) {


                            new PingServer(internet -> {
                                /* do something with boolean response */
                                if (!internet) {
                                    Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                } else {


                                    //markAttendance("present", "present", checkInTime, "missing");
                                    submitTomorrowPlan(comment, empName, meeting,
                                            activities, towns, distributors);
                                }

                            });


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                        }


                    }


                }
                //Others
                else if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("Others")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    if (count[0] == 0) {

                        rvActivityList.setVisibility(View.GONE);
                        rvTownList.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Select town.");

                        initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                        count[0] = 1;

                    } else if (count[0] == 1) {

                        if (!towns.isEmpty()) {


                            if (activities.size() > 1) {
                                rvTownList.setVisibility(View.GONE);
                                if (activities.get(1).equalsIgnoreCase("Joint Working")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                    //initDistList(rvDistributorList);
                                    //getEmpList(rvEmployeeList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Meeting")) {

                                    rvMeetingList.setVisibility(View.VISIBLE);
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Type of meeting!");


                                    initMeetingList(rvMeetingList, llNext);

                                } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                    //initTownList(rvTownList, rvDistributorList);

                                    String from = edtFromNS.getText().toString();
                                    String to = edtToNS.getText().toString();

                                    if (from.isEmpty() && to.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llTravelling.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide travelling details?");

                                    } else {

                                        if (!from.isEmpty()) {

                                            if (!to.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " From:" + from + " To:" + to, empName, meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                } else if (activities.get(1).equalsIgnoreCase("Van Sales")) {

                                    //initTownList(rvTownList, rvDistributorList);

                                    String vanNum = edtVanNumber.getText().toString();
                                    String driverName = edtDriverName.getText().toString();

                                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                                        llCommentAct.setVisibility(View.VISIBLE);
                                        llVanSales.setVisibility(View.VISIBLE);
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                        tvTitle.setText("Please provide Van details?");
                                        tvNext.setText("Done");

                                    } else {

                                        if (!vanNum.isEmpty()) {

                                            if (!driverName.isEmpty()) {

                                                comment = edtComment.getText().toString();
                                                if (!comment.isEmpty()) {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                                    activities, towns, distributors);
                                                        }

                                                    });


                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                } else if (activities.get(1).equalsIgnoreCase("Retailing")) {

                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                    tvTitle.setText("Select distributor");

                                } else {
                                    getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);
                                }

                                count[0] = 2;

                            } else {

                                getCmntEnabled(rvTownList, llCommentAct, llNext, tvTitle, tvNext);

                                count[0] = 3;
                            }


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                        }


                    } else if (count[0] == 2) {

                        if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Joint Working")) {

                            rvDistributorList.setVisibility(View.GONE);
                            rvEmployeeList.setVisibility(View.VISIBLE);
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                            tvTitle.setText("Joint working with?");
                            getEmpList(rvEmployeeList, llNext, llNewStartWorkList);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Retailing")) {

                            getCmntEnabled(rvDistributorList, llCommentAct, llNext, tvTitle, tvNext);

                        } else if (activities.size() > 1 && activities.get(1).equalsIgnoreCase("Meeting")) {

                            getCmntEnabled(rvMeetingList, llCommentAct, llNext, tvTitle, tvNext);

                        } else {

                            comment = edtComment.getText().toString();
                            if (!comment.isEmpty()) {


                                new PingServer(internet -> {
                                    /* do something with boolean response */
                                    if (!internet) {
                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                    } else {


                                        //markAttendance("present", "present", checkInTime, "missing");
                                        submitTomorrowPlan(comment, empName, meeting,
                                                activities, towns, distributors);

                                    }

                                });


                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                            }
                        }


                        count[0] = 3;

                    } else if (count[0] == 3) {

                        comment = edtComment.getText().toString();
                        if (!comment.isEmpty()) {


                            new PingServer(internet -> {
                                /* do something with boolean response */
                                if (!internet) {
                                    Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                } else {

                                    //markAttendance("present", "present", checkInTime, "missing");
                                    submitTomorrowPlan(comment, empName, meeting,
                                            activities, towns, distributors);

                                }

                            });


                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                        }


                    }


                }
                //Van Sales
                else if (activities.size() != 0 && activities.get(0).equalsIgnoreCase("Van Sales")) {

                    tvNotListed.setVisibility(View.VISIBLE);
                    //initTownList(rvTownList, rvDistributorList);

                    String vanNum = edtVanNumber.getText().toString();
                    String driverName = edtDriverName.getText().toString();

                    if (vanNum.isEmpty() && driverName.isEmpty()) {

                        rvActivityList.setVisibility(View.GONE);
                        llCommentAct.setVisibility(View.VISIBLE);
                        llVanSales.setVisibility(View.VISIBLE);
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                        tvTitle.setText("Please provide Van details?");


                    } else {

                        if (!vanNum.isEmpty()) {

                            if (!driverName.isEmpty()) {

                                comment = edtComment.getText().toString();
                                if (!comment.isEmpty()) {

                                    rvActivityList.setVisibility(View.GONE);
                                    llCommentAct.setVisibility(View.GONE);
                                    llVanSales.setVisibility(View.GONE);


                                    if (activities.size() > 1) {

                                        if (activities.get(1).equalsIgnoreCase("Joint Working")) {


                                            if (towns.isEmpty()) {

                                                tvTitle.setText("Select town.");
                                                tvNext.setText("Next");
                                                initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);
                                                rvTownList.setVisibility(View.VISIBLE);

                                            } else {


                                                if (distributors.isEmpty()) {

                                                    rvTownList.setVisibility(View.GONE);
                                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                    tvTitle.setText("Select distributor");


                                                } else {

                                                    rvDistributorList.setVisibility(View.GONE);
                                                    rvEmployeeList.setVisibility(View.VISIBLE);
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                    tvTitle.setText("Joint working with?");
                                                    getEmpList(rvEmployeeList, llNext, llNewStartWorkList);
                                                    tvNext.setText("Done");

                                                    if (!empName.isEmpty()) {

                                                        new PingServer(internet -> {
                                                            /* do something with boolean response */
                                                            if (!internet) {
                                                                Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                            } else {

                                                                //markAttendance("present", "present", checkInTime, "missing");
                                                                submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                                        activities, towns, distributors);

                                                            }

                                                        });
                                                    }
                                                }
                                            }

                                        } else if (activities.get(1).equalsIgnoreCase("Meeting")) {


                                            if (meeting.isEmpty()) {

                                                rvMeetingList.setVisibility(View.VISIBLE);
                                                llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                tvTitle.setText("Type of meeting!");
                                                initMeetingList(rvMeetingList, llNext);

                                            } else {

                                                if (towns.isEmpty()) {

                                                    rvMeetingList.setVisibility(View.GONE);
                                                    rvTownList.setVisibility(View.VISIBLE);
                                                    tvTitle.setText("Select town.");
                                                    tvNext.setText("Done");
                                                    initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);

                                                } else {


                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                                    activities, towns, distributors);

                                                        }

                                                    });
                                                }

                                            }

                                        } else if (activities.get(1).equalsIgnoreCase("Travelling")) {


                                            rvActivityList.setVisibility(View.GONE);
                                            llCommentAct.setVisibility(View.VISIBLE);
                                            llTravelling.setVisibility(View.VISIBLE);
                                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                            tvTitle.setText("Please provide travelling details?");

                                            //initTownList(rvTownList, rvDistributorList);

                                            String from = edtFromNS.getText().toString();
                                            String to = edtToNS.getText().toString();

                                            if (!from.isEmpty()) {

                                                if (!to.isEmpty()) {

                                                    comment = edtComment.getText().toString();
                                                    if (!comment.isEmpty()) {


                                                        new PingServer(internet -> {
                                                            /* do something with boolean response */
                                                            if (!internet) {
                                                                Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                            } else {


                                                                //markAttendance("present", "present", checkInTime, "missing");
                                                                submitTomorrowPlan(comment
                                                                                + " Driver Name:" + driverName + " Van Number:" + vanNum
                                                                                + " & From:" + from + " To:" + to, empName, meeting,
                                                                        activities, towns, distributors);

                                                            }

                                                        });


                                                    } else {

                                                        Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {

                                                    Toast.makeText(TomorrowsPlanActivity.this, "To is empty", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {

                                                Toast.makeText(TomorrowsPlanActivity.this, "From is empty", Toast.LENGTH_SHORT).show();
                                            }


                                        } else if (activities.get(1).equalsIgnoreCase("Retailing")) {

                                            Log.e("NAAAN", "---->" + activities.get(1) + " and town-->" + towns);

                                            if (towns.isEmpty()) {

                                                tvTitle.setText("Select town.");
                                                tvNext.setText("Next");
                                                initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);
                                                rvTownList.setVisibility(View.VISIBLE);

                                            } else {


                                                if (distributors.isEmpty()) {

                                                    rvTownList.setVisibility(View.GONE);
                                                    //rvDistributorList.setVisibility(View.VISIBLE);
                                                    new DownloadDistributors(rvDistributorList, llNewStartWorkList, towns, llNext).execute();
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));
                                                    tvTitle.setText("Select distributor");
                                                    tvNext.setText("Done");

                                                } else {

                                                    new PingServer(internet -> {
                                                        /* do something with boolean response */
                                                        if (!internet) {
                                                            Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            //markAttendance("present", "present", checkInTime, "missing");
                                                            submitTomorrowPlan(comment
                                                                            + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                                    activities, towns, distributors);
                                                        }

                                                    });
                                                }
                                            }

                                        } else {

                                            if (towns.isEmpty()) {

                                                tvTitle.setText("Select town.");
                                                tvNext.setText("Done");
                                                initTownList(rvTownList, llNewStartWorkList, rvDistributorList, llNext);
                                                rvTownList.setVisibility(View.VISIBLE);

                                            } else {

                                                new PingServer(internet -> {
                                                    /* do something with boolean response */
                                                    if (!internet) {
                                                        Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                                    } else {


                                                        //markAttendance("present", "present", checkInTime, "missing");
                                                        submitTomorrowPlan(comment
                                                                        + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                                activities, towns, distributors);
                                                    }

                                                });
                                            }
                                        }

                                    } else {

                                        new PingServer(internet -> {
                                            /* do something with boolean response */
                                            if (!internet) {
                                                Toast.makeText(TomorrowsPlanActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                                            } else {

                                                //markAttendance("present", "present", checkInTime, "missing");
                                                submitTomorrowPlan(comment
                                                                + " Driver Name:" + driverName + " Van Number:" + vanNum, empName, meeting,
                                                        activities, towns, distributors);
                                            }

                                        });

                                    }


                                } else {

                                    Toast.makeText(TomorrowsPlanActivity.this, "Reason should not be empty", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(TomorrowsPlanActivity.this, "Driver Name is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(TomorrowsPlanActivity.this, "Van number is empty", Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {

                    Toast.makeText(TomorrowsPlanActivity.this, "Please select at least one ", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(mSearchItem);

        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RetailerActivity.retailerTab.setVisibility(View.GONE);
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Get the search close button image view
        ImageView closeButton = searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                adapter.getFilter().filter("");

                EditText et = searchViewAndroidActionBar.findViewById(R.id.search_src_text);

                //Clear the text from EditText view
                et.setText("");

                //Clear query
                searchViewAndroidActionBar.setQuery("", false);
                //Collapse the action view
                searchViewAndroidActionBar.onActionViewCollapsed();

            }
        });

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is collapsing
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }

                RetailerActivity.retailerTab.setVisibility(View.VISIBLE);
                RetailerActivity.headerTCPC.setVisibility(View.VISIBLE);
                RetailerActivity.retailerViewPager.setPagingEnabled(true);

                adapter.getFilter().filter("");

                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                RetailerActivity.retailerTab.setVisibility(View.GONE);
                RetailerActivity.headerTCPC.setVisibility(View.GONE);
                RetailerActivity.retailerViewPager.setPagingEnabled(false);
                return true;
            }
        });


        return true;
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        //mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.quantum_grey_600));

        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, 0.0f, (float) width);
                createCircularReveal.setDuration(250);
                createCircularReveal.start();

            } else {

                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (-mToolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(220);
                mToolbar.clearAnimation();
                mToolbar.startAnimation(translateAnimation);

            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, (float) width, 0.0f);
                createCircularReveal.setDuration(250);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mToolbar.setBackgroundColor(getThemeColor(TomorrowsPlanActivity.this, android.R.attr.colorPrimary));
                        //mDrawerLayout.setStatusBarBackgroundColor(getThemeColor(OrderBookingRetailing.this, R.attr.colorPrimaryDark));
                    }
                });
                createCircularReveal.start();

            } else {

                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-mToolbar.getHeight()));
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(220);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mToolbar.setBackgroundColor(getThemeColor(TomorrowsPlanActivity.this, android.R.attr.colorPrimary));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mToolbar.startAnimation(animationSet);
            }
            // mDrawerLayout.setStatusBarBackgroundColor(ThemeUtils.getThemeColor(OrderBookingRetailing.this, R.attr.colorPrimaryDark));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private Paint getArcPaint() {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(TomorrowsPlanActivity.this, R.color.colorAccent));
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint getDefaultRipplePaint() {
        Paint ripplePaint = new Paint();
        ripplePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setColor(ContextCompat.getColor(TomorrowsPlanActivity.this, R.color.colorPrimary));
        ripplePaint.setAntiAlias(true);

        return ripplePaint;
    }

    private Paint getDefaultRippleBackgroundPaint() {
        Paint rippleBackgroundPaint = new Paint();
        rippleBackgroundPaint.setStyle(Paint.Style.FILL);
        rippleBackgroundPaint.setColor((ContextCompat.getColor(TomorrowsPlanActivity.this, R.color.colorPrimary) & 0x00FFFFFF) | 0x40000000);
        rippleBackgroundPaint.setAntiAlias(true);

        return rippleBackgroundPaint;
    }

    private Paint getButtonPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#c0c0c0"));
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private boolean deleteFilesInDir(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    files[i].delete();
                }
            }
        }
        return true;
    }


    private void getCmntEnabled(RecyclerView rvDistributorList, LinearLayout llCommentAct,
                                LinearLayout llNext, TextView tvTitle, TextView tvNext) {

        rvDistributorList.setVisibility(View.GONE);
        llCommentAct.setVisibility(View.VISIBLE);
        llNext.setBackgroundColor(Color.parseColor("#1F000000"));
        tvTitle.setText("Explain today's plan in details.");
        tvNext.setText("Done");

    }

    private void initMeetingList(RecyclerView rvMeetingList, LinearLayout llNext) {

        ArrayList<String> activityListArr = new ArrayList<>();
        activityListArr.add("Office Meeting");
        activityListArr.add("Distributor Meeting");
        activityListArr.add("Others");

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));

        adapter = new ActivityListAdapter(TomorrowsPlanActivity.this, activityListArr,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        meeting = activityListArr.get(position);
                        llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                    }

                    @Override
                    public void onItemClick2(int position) {
                        //AnimCheckBox animCheckBox =
                        meeting = "";
                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));

                    }
                }, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(TomorrowsPlanActivity.this);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(TomorrowsPlanActivity.this, resId);
        rvMeetingList.setLayoutAnimation(animation);
        rvMeetingList.setLayoutManager(layoutManager);
        rvMeetingList.setAdapter(adapter);

    }

    private void getEmpList(RecyclerView rvEmployeeList, LinearLayout llNext,
                            LinearLayout llNewStartWorkingL) {

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_LIST + myPref.getString(getString(R.string.zone_id_key), "") + "/employees",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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

                        //EmloyeeListAdapter emloyeeListAdapter = new EmloyeeListAdapter(TomorrowsPlanActivity.this, empId, empName, empPhone);

                        adapter = new ActivityListAdapter(TomorrowsPlanActivity.this, empNameL,
                                new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {

                                        empName = empNameL.get(position);
                                        llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                    }

                                    @Override
                                    public void onItemClick2(int position) {
                                        //AnimCheckBox animCheckBox =
                                        empName = "";
                                        llNext.setBackgroundColor(Color.parseColor("#1F000000"));

                                    }

                                }, false);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TomorrowsPlanActivity.this);
                        rvEmployeeList.setLayoutManager(layoutManager);
                        //rvTownList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                        rvEmployeeList.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        Volley.newRequestQueue(TomorrowsPlanActivity.this).add(jsonObjectRequest);
    }

    private void initActivityList(RecyclerView rvActivityList, LinearLayout llNext) {

        ArrayList<String> activityListArr = new ArrayList<>();
        activityListArr.add("Retailing");
        activityListArr.add("Joint Working");
        activityListArr.add("Meeting");
        activityListArr.add("New Distributor Appointment");
        activityListArr.add("Travelling");
        activityListArr.add("Payment Collection");
        activityListArr.add("Marketing/Promotion");
        activityListArr.add("Van Sales");
        activityListArr.add("Others");

        actCount = 0;

        adapter = new ActivityListAdapter(TomorrowsPlanActivity.this, activityListArr,
                new OnItemClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemClick(int position) {

                        if (actCount < 2) {

                            llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                            //AnimCheckBox animCheckBox =
                            activities.add(activityListArr.get(position));
                            Log.e("ACtivity--->", "" + activities);
                            SharedPreferences.Editor editor = tempPref.edit();
                            if (activities.get(0).equalsIgnoreCase("Retailing")) {

                                editor.putString(getString(R.string.askfororder_key), "yes");
                            } else {

                                editor.putString(getString(R.string.askfororder_key), "No");
                            }

                            editor.apply();

                            actCount++;

                        } else {

                            ActivityListAdapter.MyViewHolder myViewHolder = (ActivityListAdapter.MyViewHolder) rvActivityList.findViewHolderForAdapterPosition(position);
                            CheckBox checkBox = myViewHolder.itemView.findViewById(R.id.chbActSelect);
                            checkBox.setChecked(false);
                            Toast.makeText(TomorrowsPlanActivity.this, "You can select maximum two", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("ACtivity--->", "ActCount==" + actCount);

                    }

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemClick2(int position) {
                        //AnimCheckBox animCheckBox =

                        activities.remove(activityListArr.get(position));
                        if (actCount != 0)
                            actCount--;
                        if (actCount == 0)
                            llNext.setBackgroundColor(Color.parseColor("#1F000000"));

                        Log.e("ACtivity--->", "ActCount22==" + actCount);

                    }

                }, true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(TomorrowsPlanActivity.this);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(TomorrowsPlanActivity.this, resId);
        rvActivityList.setLayoutAnimation(animation);
        rvActivityList.setLayoutManager(layoutManager);
        rvActivityList.setAdapter(adapter);

    }

    private void initTownList(RecyclerView rvTownList, LinearLayout llNewStartWorkList,
                              RecyclerView rvDisList, LinearLayout llNext) {

        llNext.setBackgroundColor(Color.parseColor("#1F000000"));

        Cursor cursor = null;

        ArrayList<String> townItems = new ArrayList<>();

        try {
            cursor = salesBeatDb.getAllRecordFromTownListTable();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {
                    townItems.add(cursor.getString(cursor.getColumnIndex("town_name")));
                } while (cursor.moveToNext());

                Collections.sort(townItems, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();

            if (townItems.size() > 0) {

                // TownListAdapter adapter = new TownListAdapter(TomorrowsPlanActivity.this, townItems);

                adapter = new ActivityListAdapter(TomorrowsPlanActivity.this, townItems,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {

                                towns = townItems.get(position);
                                llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                //new DownloadMappingDetails(rvDisList, towns).execute();
                            }

                            @Override
                            public void onItemClick2(int position) {
                                //AnimCheckBox animCheckBox =
                                towns = "";
                                llNext.setBackgroundColor(Color.parseColor("#1F000000"));

                            }

                        }, false);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TomorrowsPlanActivity.this);
                rvTownList.setLayoutManager(layoutManager);
                int resId = R.anim.layout_animation_fall_down;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(TomorrowsPlanActivity.this, resId);
                rvTownList.setLayoutAnimation(animation);
                rvTownList.setAdapter(adapter);
                rvTownList.getAdapter().notifyDataSetChanged();

            }
        }
    }

    private void submitTomorrowPlan(String reason, String workingWith, String meetingType,
                                    ArrayList<String> activity_type, String workingtown, String disId) {


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting...");
        progressDialog.show();

        // Volley
        StringRequest postRequest = new StringRequest(Request.Method.POST, SbAppConstants.API_SUBMIT_TOMORROWS_PLAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        // response
                        Log.e("Response", "Tomorrow's Plan====" + response);

                        try {
                            //loader.dismiss();
                            JSONObject object = new JSONObject(response);
                            String status = object.getString("status");
                            String msg = object.getString("statusMessage");


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
            protected Map<String, String> getParams() {

                String lat = "", longt = "";
                if (locationProvider != null) {
                    lat = String.valueOf(locationProvider.getLatitude());
                    longt = String.valueOf(locationProvider.getLongitude());
                }

                Map<String, String> params = new HashMap<>();
                params.put("activity_type", activity_type.toString());
                params.put("meeting_type", meetingType);
                params.put("working_with", workingWith);
                params.put("workingtown", workingtown);
                params.put("did", disId);
                params.put("zoneId", tempPref.getString(getString(R.string.zone_id_key), ""));
                params.put("latitude", lat);
                params.put("longitude", longt);
                params.put("comment", reason);

                Log.e("Tomorrow's Plan", " #### JSON: " + params.toString());

                return params;
            }

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

        Volley.newRequestQueue(this).add(postRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
        //locationProvider.unregisterReceiver();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private class DownloadDistributors extends AsyncTask<Void, String, String> {

        String townName;
        JSONArray updatedAtArr = new JSONArray();
        RecyclerView rvDistributorList;
        ArrayList<String> disList = new ArrayList<>();
        ArrayList<String> didList = new ArrayList<>();
        LinearLayout llNext;
        LinearLayout llNewStartWorkList;
        ArrayList<String> dids = new ArrayList<>();
        ArrayList<String> didUpdatedAt = new ArrayList<>();

        public DownloadDistributors(RecyclerView rvDistributorList, LinearLayout llNewStartWorkList,
                                    String townName, LinearLayout llNext) {

            this.rvDistributorList = rvDistributorList;
            this.llNewStartWorkList = llNewStartWorkList;
            this.townName = townName;
            this.llNext = llNext;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            llNewStartWorkList.setVisibility(View.GONE);
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
                townName = URLEncoder.encode(townName, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }

            /*
            // Retrofit
            Call<HttpEntity> callGetDistributor = apiIntentface.getDistributors2(myPref.getString("token", ""),
                    townName);

            callGetDistributor.enqueue(new Callback<HttpEntity>() {
                @Override
                public void onResponse(Call<HttpEntity> call, retrofit2.Response<HttpEntity> response) {
                    if (response.isSuccessful()) {
                        try {

//                            StatusLine statusLine = response.body();
                            int statusCode = response.code();
                            Log.e("DistributorList", "Distributor Response Status code-->" + statusCode);
                            if (statusCode == 200) {

                                HttpEntity entity = response.body();
                                InputStream content = entity.getContent();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                                String res = reader.readLine();

                                Log.e("Dis Response is", "::" + res);

                                JSONObject object = new JSONObject(res);

                                String status = object.getString("status");

                                if (status.equalsIgnoreCase("success")) {

                                    JSONArray distributors = object.getJSONArray("distributors");
                                    for (int i = 0; i < distributors.length(); i++) {


                                        JSONObject obj = (JSONObject) distributors.get(i);

                                        // JSONObject zoneObj = obj.getJSONObject("zone");
                                        didList.add(obj.getString("did"));
                                        disList.add(obj.getString("name"));

                                    }


                                }

                            } else {
                                Log.e("Error....", "Failed to download file");
                                //      return "error";
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                            return "error";
                        }
                    }
                }

                @Override
                public void onFailure(Call<HttpEntity> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    return "error";
                }
            });
        */
            //TODO Implement Retrofit

            // Http Client
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(SbAppConstants.API_GET_DISTRIBUTORS_2 + "town=" + townName);
//            httpGet.addHeader("authorization", myPref.getString("token", ""));
//
//            try {
//
//                HttpResponse response = httpClient.execute(httpGet);
//
//                StatusLine statusLine = response.getStatusLine();
//                int statusCode = statusLine.getStatusCode();
//                Log.e("DistributorList", "Distributor Response Status code-->" + statusCode);
//                if (statusCode == 200) {
//
//                    HttpEntity entity = response.getEntity();
//                    InputStream content = entity.getContent();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                    String res = reader.readLine();
//
//                    Log.e("Dis Response is", "::" + res);
//
//                    JSONObject object = new JSONObject(res);
//
//                    String status = object.getString("status");
//
//                    if (status.equalsIgnoreCase("success")) {
//
//                        JSONArray distributors = object.getJSONArray("distributors");
//                        for (int i = 0; i < distributors.length(); i++) {
//
//
//                            JSONObject obj = (JSONObject) distributors.get(i);
//
//                            // JSONObject zoneObj = obj.getJSONObject("zone");
//                            didList.add(obj.getString("did"));
//                            disList.add(obj.getString("name"));
//
//                        }
//
//
//                    }
//
//                    return status;
//
//                } else {
//                    //Log.e("Error....", "Failed to download file");
//                    return "error";
//                }
//
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                return "error";
//            }

            return "";
        }


        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            JsonObjectRequest getDistListReq = new JsonObjectRequest(Request.Method.GET, SbAppConstants.API_GET_DISTRIBUTORS_2 + "?town=" + townName,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject res) {

                            try {

                                //@Umesh 02-Feb-2022
                                if(res.getInt("status")==1)
                                {

                                    JSONArray distributors = res.getJSONArray("data");
                                    for (int i = 0; i < distributors.length(); i++) {


                                        JSONObject obj = (JSONObject) distributors.get(i);

                                        // JSONObject zoneObj = obj.getJSONObject("zone");
                                        didList.add(obj.getString("did"));
                                        disList.add(obj.getString("name"));

                                    }


                                }

                                if(res.getInt("status")==1)
                                {

                                    llNewStartWorkList.setVisibility(View.VISIBLE);
                                    rvDistributorList.setVisibility(View.VISIBLE);

                                    adapter = new ActivityListAdapter(TomorrowsPlanActivity.this, disList,
                                            new OnItemClickListener() {
                                                @Override
                                                public void onItemClick(int position) {

                                                    distributors = didList.get(position);
                                                    llNext.setBackgroundColor(Color.parseColor("#5aac82"));
                                                }

                                                @Override
                                                public void onItemClick2(int position) {
                                                    //AnimCheckBox animCheckBox =
                                                    distributors = "";
                                                    llNext.setBackgroundColor(Color.parseColor("#1F000000"));

                                                }
                                            }, false);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TomorrowsPlanActivity.this);
                                    rvDistributorList.setLayoutManager(layoutManager);
                                    int resId = R.anim.layout_animation_fall_down;
                                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(TomorrowsPlanActivity.this, resId);
                                    rvDistributorList.setLayoutAnimation(animation);
                                    rvDistributorList.setAdapter(adapter);
                                    rvDistributorList.getAdapter().notifyDataSetChanged();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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

            Volley.newRequestQueue(TomorrowsPlanActivity.this).add(getDistListReq);

        }
    }
}
