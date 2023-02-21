package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

public class RegisUser extends AppCompatActivity {


    Button regisDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_user);
        regisDatos = findViewById(R.id.btnPersonal);


       FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment bottomFragment = manager.findFragmentById(R.id.fragmentDato);
        ft.hide(bottomFragment);
        ft.commit();


    }


}