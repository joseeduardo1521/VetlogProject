package com.example.vet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Encuesta extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private FloatingActionButton btnCerrar;

    private String urlToload="https://docs.google.com/forms/d/e/1FAIpQLSd-A34hrcpx_QBHNou6xXYR4eWsNvliAN-o3-cx6-2ccTZdGw/viewform?usp=sf_link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta);

        //assignviewbyid
        mProgressBar = findViewById(R.id.loading_progressbar);
        btnCerrar = findViewById(R.id.cerrar);
        mWebView = findViewById(R.id.webview);

        mWebView.loadUrl(urlToload);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //getSupportActionBar().setTitle(title);
            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ns = new Intent(Encuesta.this, Usuario_Menu.class);
                startActivity(ns);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack())
        {
            mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}