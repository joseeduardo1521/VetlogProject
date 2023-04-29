package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

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
        handleDynamicLink(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Maneja el enlace dinámico cuando la actividad se reanuda
        handleDynamicLink(getIntent());
    }

    private void handleDynamicLink(Intent intent) {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Obtiene el enlace dinámico
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            // Maneja el enlace aquí, por ejemplo, navegando a la pantalla principal
                        }
                    }
                });
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