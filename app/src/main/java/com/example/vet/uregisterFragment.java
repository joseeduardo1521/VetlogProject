package com.example.vet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class uregisterFragment extends Fragment {


    public uregisterFragment() {
        // Required empty public constructor
    }



    public static uregisterFragment newInstance(String param1, String param2) {
        uregisterFragment fragment = new uregisterFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_uregister, container, false);
    }
}