package com.example.vet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class uregisterFragment extends Fragment {

    String mail, pass, lvl;
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


        getParentFragmentManager().setFragmentResultListener("key", getActivity(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                mail=result.getString("mail");
                pass=result.getString("pass");
                lvl=result.getString("lvl");
            }
        });

        Toast.makeText(getActivity(), mail+" "+pass, Toast.LENGTH_SHORT).show();

        return inflater.inflate(R.layout.fragment_uregister, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}