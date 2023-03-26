package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Prueba extends AppCompatActivity {
    TextView nofi;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        nofi = findViewById(R.id.nofifi);

        if(!isConnected()){
            nofi.setVisibility(View.VISIBLE);
        }

    }
    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}