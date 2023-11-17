package com.v.internet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextLinks;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText edtSearch;
    ImageView imgHistory,imgInfo;
    WebView webPage;
    private long pressTime;
    ProgressBar progressBar;
    SwipeRefreshLayout mySwipe;
    LinearLayout demoInfoLayout;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_main);

        edtSearch = findViewById(R.id.edtSearch);
        imgHistory = findViewById(R.id.imgHistory);
        imgInfo = findViewById(R.id.imgInfo);
        webPage = findViewById(R.id.webPage);
        progressBar = findViewById(R.id.progress_bar);
        mySwipe = (SwipeRefreshLayout)this.findViewById(R.id.Swiperef);
        demoInfoLayout = findViewById(R.id.demoInfoLayout);
        mAuth = FirebaseAuth.getInstance();

        //Setting up a Webview
        WebSettings webSetting = webPage.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setDisplayZoomControls(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setAllowFileAccessFromFileURLs(true);





        //Going to History
        imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(MainActivity.this, ActivityHistory.class);
                startActivity(historyIntent);
            }
        });

        //Going to Info
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(MainActivity.this, ActivityInfo.class);
                startActivity(infoIntent);
            }
        });

        //Changing Progress of Progressbar
        webPage.setWebViewClient(new MyWebViewClient());
        webPage.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });


        //After Clicking Go Button
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                    loadurl(edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        //refreash Layout
        mySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webPage.reload();
                mySwipe.setRefreshing(false);
            }
        });




    }

    void loadurl(String url){

        demoInfoLayout.setVisibility(View.GONE);
        webPage.setVisibility(View.VISIBLE);

        if (url != null || url.isEmpty()){
            boolean match = Patterns.WEB_URL.matcher(url).matches();
            if (match){
                webPage.loadUrl(url);

//                putToDatabase(url);
            }
            else{
                webPage.loadUrl("https://www.google.com/search?q="+url);

//                putToDatabase("https://www.google.com/search?q="+url);

            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Enter Something to Search",Toast.LENGTH_SHORT).show();
        }

    }

    void putToDatabase(String url){
        String title = webPage.getTitle();
        FirebaseDatabase.getInstance().getReference("User/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(new History(title, url));
    }

    //Inner Class for Web View Client
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            edtSearch.setText(webPage.getUrl());
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            putToDatabase(url);

            progressBar.setVisibility(View.INVISIBLE);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }
    }

    //back Press
    public void onBackPressed() {


        if (webPage.canGoBack()) {
            webPage.goBack();
        } else {
            if (pressTime + 2000 > System.currentTimeMillis()) {

                super.onBackPressed();

            } else {
                webPage.setVisibility(View.GONE);
                demoInfoLayout.setVisibility(View.VISIBLE);
                edtSearch.setText("");
                Toast.makeText(getApplicationContext(), "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            }


            pressTime = System.currentTimeMillis();

        }

    }


}