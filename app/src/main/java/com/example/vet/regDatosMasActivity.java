package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class regDatosMasActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_datos_mas);
        String key = getIntent().getExtras().getString("llave");
        Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
    }
}