package com.newsalesbeatApp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.newsalesbeatApp.R;

public class ReportDetails extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_details);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);

            final View rootView = findViewById(R.id.main_layout);
            rootView.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                // Optionally use insets.top or bottom padding if needed
                v.setPadding(0, systemBars.top, 0, systemBars.bottom); // or (0,0,0,0) if full overlay
                return insets;
            });
        } else {
            // For Android below R
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
                v.setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            });

            // Also request layout flags to allow drawing under system bars
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String mName = intent.hasExtra("mName") ? intent.getStringExtra("mName") : "";
        String apUrl = intent.hasExtra("apUrl") ? intent.getStringExtra("apUrl") : "";

        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        tvPageTitle.setText(mName);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                if (webView.canGoBack()) {
                    webView.goBack(); // Go back in WebView history
                }
                ReportDetails.this.finish();
            }
        });

        bindWebView(apUrl);
    }

    private void bindWebView(String apUrl) {
        Log.d("TAG", "bindWebView: "+apUrl);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // If the page requires JS
        webSettings.setDomStorageEnabled(true); // For modern web apps

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        if (!apUrl.isEmpty()) {
            webView.loadUrl(apUrl);
        } else {
            webView.loadData("<h2>No URL provided</h2>", "text/html", "UTF-8");
        }
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Go back in WebView history
        } else {
            super.onBackPressed(); // Exit activity
        }
    }
}