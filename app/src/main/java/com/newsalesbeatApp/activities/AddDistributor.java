package com.newsalesbeatApp.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.AddNewDistributor;
import com.newsalesbeatApp.fragments.AddNewDistributorForm;
import com.newsalesbeatApp.fragments.DistributorHistory;
import com.newsalesbeatApp.sblocation.GPSLocation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/*
 * Created by MTC on 23-08-2017.
 */

public class AddDistributor extends AppCompatActivity {

    public static TabLayout distributorTab;
    ImageView imgBack;
    ViewPager distributorViewpager;
    GPSLocation locationProvider;
    private TextView tvTimer;
    private CountDownTimer countDownTimer;
//    private VoiceRipple voiceRipple;
    private static final long MAX_TIME_MS = 180000;
    private static final int REQUEST_PERMISSION_CODE = 1001;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.other_activity2);
        distributorTab = findViewById(R.id.distributorTab);
        distributorViewpager = findViewById(R.id.distributorViewpager);

        Toolbar mToolbar = findViewById(R.id.toolbar3);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        tvTimer = findViewById(R.id.tvTimer);
        setSupportActionBar(mToolbar);

        tvPageTitle.setText(getIntent().getStringExtra("page_title"));

        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        //set up view pager
        setUpViewPager(distributorViewpager);
        distributorTab.setSelectedTabIndicatorColor(Color.WHITE);
        distributorTab.setSelectedTabIndicatorHeight(2);
        distributorTab.setupWithViewPager(distributorViewpager);

        if (!checkPermissions()) {
            requestPermissions();
            return;
        }


        try {

            int tabPos = getIntent().getIntExtra("tabPos", 0);
            distributorTab.getTabAt(tabPos).select();

        } catch (Exception e) {
            e.getMessage();
        }

        imgBack.setOnClickListener(view -> {

            AddDistributor.this.finish();

        });

//        voiceRipple = findViewById(R.id.imgRecordAudio);
//
//        voiceRipple.setRecordingListener(new RecordingListener() {
//            @Override
//            public void onRecordingStopped() {
//                Log.d("TAG", "onRecordingStopped()");
//            }
//
//            @Override
//            public void onRecordingStarted() {
//                Log.d("TAG", "onRecordingStarted()");
//            }
//        });

// Create a consistent file path
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VoiceRecordings");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

// Set the audio file path to a consistent name
        File audioFile = new File(directory, "audio.mp3");

// Log the file path
        Log.d("TAG", "Audio path: " + audioFile.getAbsolutePath());

// Ensure any existing file is deleted before recording
        if (audioFile.exists()) {
            audioFile.delete();
        }

// Set view-related settings for ripple view
//        voiceRipple.setRippleSampleRate(Rate.LOW);
//        voiceRipple.setRippleDecayRate(Rate.HIGH);
//        voiceRipple.setBackgroundRippleRatio(1.4);
//        voiceRipple.setBackgroundColor(Color.TRANSPARENT);
//        voiceRipple.setBackgroundResource(R.drawable.background_circle);

// Set recorder-related settings for ripple view
        MediaRecorder mediaRecorder = new MediaRecorder();
//        voiceRipple.setMediaRecorder(mediaRecorder);
//        voiceRipple.setOutputFile(audioFile.getAbsolutePath());
//        voiceRipple.setAudioSource(MediaRecorder.AudioSource.MIC);
//        voiceRipple.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        voiceRipple.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

// Set inner icon
//        voiceRipple.setRecordDrawable(
//                ContextCompat.getDrawable(AddDistributor.this, R.drawable.mute_microphone),
//                ContextCompat.getDrawable(AddDistributor.this, R.drawable.microphone)
//        );
//        voiceRipple.setIconSize(32);

// Set up a custom renderer
//        Renderer currentRenderer = new TimerCircleRippleRenderer(
//                getDefaultRipplePaint(),
//                getDefaultRippleBackgroundPaint(),
//                getButtonPaint(),
//                getArcPaint(),
//                180000.0, // Set timer value
//                0.0
//        );
//        if (currentRenderer instanceof TimerCircleRippleRenderer) {
//            ((TimerCircleRippleRenderer) currentRenderer).setStrokeWidth(0);
//        }


//        voiceRipple.setRenderer(currentRenderer);
//        voiceRipple.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!voiceRipple.isRecording()) {
//                    startPulseAnimation(voiceRipple);
//                    tvTimer.setVisibility(View.VISIBLE);
//                    voiceRipple.startRecording();
//                    startCountdownTimer();
//                }else {
//                    stopPulseAnimation(voiceRipple);
//                    stopRecording(voiceRipple);
//                }
//            }
//        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissions() {
        int recordAudio = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return recordAudio == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || storage == PackageManager.PERMISSION_GRANTED);
    }

    private void startPulseAnimation(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.2f, 1.0f);

        scaleX.setDuration(1000); // Pulse duration
        scaleY.setDuration(1000);

        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet pulseAnimator = new AnimatorSet();
        pulseAnimator.playTogether(scaleX, scaleY);
        pulseAnimator.start();

        view.setTag(pulseAnimator); // Store the animator to stop later
    }

    private void stopPulseAnimation(View view) {
        AnimatorSet pulseAnimator = (AnimatorSet) view.getTag();
        if (pulseAnimator != null) {
            pulseAnimator.cancel(); // Stop the animation
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
//                stopRecording(voiceRipple);
            }
        }.start();
    }

    private void fadeInView(View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(300); // Animation duration
        fadeIn.start();
    }

    private void fadeOutView(View view) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(300); // Animation duration
        fadeOut.start();
    }

//    private void stopRecording(VoiceRippleView voiceRipple) {
//        if (voiceRipple.isRecording()) {
//            voiceRipple.stopRecording();
//            fadeOutView(tvTimer); // Fade out the timer
//
//            // Cancel the countdown timer
//            if (countDownTimer != null) {
//                countDownTimer.cancel();
//            }
//        }
//    }

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

    private Paint getArcPaint() {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(AddDistributor.this, R.color.colorAccent));
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint getDefaultRipplePaint() {
        Paint ripplePaint = new Paint();
        ripplePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setColor(ContextCompat.getColor(AddDistributor.this, R.color.colorPrimary));
        ripplePaint.setAntiAlias(true);

        return ripplePaint;
    }

    private Paint getDefaultRippleBackgroundPaint() {
        Paint rippleBackgroundPaint = new Paint();
        rippleBackgroundPaint.setStyle(Paint.Style.FILL);
        rippleBackgroundPaint.setColor((ContextCompat.getColor(AddDistributor.this, R.color.colorPrimary) & 0x00FFFFFF) | 0x40000000);
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

    private void setUpViewPager(ViewPager distributorViewpager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddNewDistributorForm(), "Add Distributor");
//        adapter.addFragment(new AddNewDistributor(), "Add Distributor");
        adapter.addFragment(new DistributorHistory(), "Distributor History");
        distributorViewpager.setAdapter(adapter);

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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (distributorViewpager.getCurrentItem() == 0){
//            showAlertDialog();
        } else {

            AddDistributor.this.finish();
            //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }

    }

    private void showAlertDialog() {

        //MainActivity.mainActivity.syncData();
        String y = "", n = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDistributor.this);
        builder.setTitle(getString(R.string.alert));
        builder.setMessage("Are you sure want to go back");
        y = getString(R.string.yes);
        n = getString(R.string.no);

        builder.setPositiveButton(y, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                AddDistributor.this.finish();
            }
        });

        builder.setNegativeButton(n, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Set margins programmatically (modify LayoutParams)
        LinearLayout.LayoutParams positiveParams = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveParams.setMargins(0, 0, 5, 0);  // Add 16dp margin to the left of the "Yes" button
        positiveButton.setLayoutParams(positiveParams);

        LinearLayout.LayoutParams negativeParams = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        negativeParams.setMargins(5, 0, 16, 0);  // Add 16dp margin to the right of the "No" button
        negativeButton.setLayoutParams(negativeParams);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
