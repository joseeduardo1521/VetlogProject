package com.example.vet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisUser extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_user);

       FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment bottomFragment = manager.findFragmentById(R.id.fragmentDato);
        ft.hide(bottomFragment);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment bottomFragment = manager.findFragmentById(R.id.fragmentMP);

        if (bottomFragment.isHidden()) {
            ft.detach(bottomFragment);
            ft.attach(bottomFragment);
            ft.show(bottomFragment);
            Fragment bottomFragment1 = manager.findFragmentById(R.id.fragmentDato);
            ft.hide(bottomFragment1);
            ft.commit();
        } else {
            super.onBackPressed();
        }
    }


}