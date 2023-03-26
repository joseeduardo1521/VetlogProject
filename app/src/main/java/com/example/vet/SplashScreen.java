package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver;
    TextView nofi;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        nofi = findViewById(R.id.nofifi);
        //broadcastReceiver = new ConnectionReceiver();
        //registoreNetworkBroadcast();


        if(!isConnected()){
            nofi.setVisibility(View.VISIBLE);
            Intent m = new Intent(SplashScreen.this,Prueba.class);
            startActivity(m);
            finish();

        }else {
            nofi.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },7000);
        }

    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }
/*
    protected void registoreNetworkBroadcast(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregistorNetwork(){
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistorNetwork();
    }
    */

}