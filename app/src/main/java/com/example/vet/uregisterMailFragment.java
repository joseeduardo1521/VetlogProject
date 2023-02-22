package com.example.vet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


public class uregisterMailFragment extends Fragment {
    private EditText TxtUsuario;
    private TextInputEditText TxtPass, TxtPass2;
    AwesomeValidation awesomeValidation;

   private Button btnregisDatos;
    public uregisterMailFragment() {
        // Required empty public constructor
    }


    public static uregisterMailFragment newInstance(String param1, String param2) {
        uregisterMailFragment fragment = new uregisterMailFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uregister_mail, container, false);
        btnregisDatos = view.findViewById(R.id.btnPersonal);
        TxtUsuario = (EditText) view.findViewById(R.id.txtUsuario);
        TxtPass = (TextInputEditText) view.findViewById(R.id.txtPass);
        TxtPass2 = (TextInputEditText) view.findViewById(R.id.txtPass2);


        btnregisDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment bottomFragment = manager.findFragmentById(R.id.fragmentDato);
                ft.show(bottomFragment);
                Fragment bottomFragment1 = manager.findFragmentById(R.id.fragmentMP);
                ft.hide(bottomFragment1);
                ft.commit();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}