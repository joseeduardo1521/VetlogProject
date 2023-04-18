package com.example.vet.frag;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.vet.Menu;
import com.example.vet.R;
import com.google.android.material.textfield.TextInputEditText;


public class uregisterMailFragment extends Fragment {
    private EditText TxtUsuario;
    private TextInputEditText TxtPass, TxtPass2;
    private Spinner comboRoles;
    private String item;
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
        TxtUsuario = (EditText) view.findViewById(R.id.txtUsuarioR);
        comboRoles = (Spinner) view.findViewById(R.id.sRoles);
        TxtPass = (TextInputEditText) view.findViewById(R.id.txtPassR);
        TxtPass2 = (TextInputEditText) view.findViewById(R.id.txtPass2R);
        TxtPass2 = (TextInputEditText) view.findViewById(R.id.txtPass2R);
        awesomeValidation = new AwesomeValidation(BASIC);
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}";
        awesomeValidation.addValidation(TxtUsuario, Patterns.EMAIL_ADDRESS, "Introduzca un correo valido");
        awesomeValidation.addValidation(TxtPass, regexPassword, "La contraseña no es segura min 8 Caracteres una letra mayuscula y un numero");
        awesomeValidation.addValidation(TxtPass2, regexPassword, "La contraseña no es segura min 8 Caracteres una letra mayuscula y un numero");

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getActivity(),R.array.combo_lvl,
                android.R.layout.simple_spinner_item);
        comboRoles.setAdapter(adapter);

        comboRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item= parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnregisDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()){
                    String email, password, password2,nivels;
                    email = String.valueOf(TxtUsuario.getText());
                    password = String.valueOf(TxtPass.getText());
                    password2 = String.valueOf(TxtPass2.getText());
                    nivels = item;

                    switch (nivels){
                        case "Veterinario":
                            nivels = "1";
                            break;
                        case "Secretario":
                            nivels = "2";
                            break;
                    }
                    if(password.equals(password2)){
                     cambiarFragment(email,password,nivels);
                    }else {
                        TxtPass2.setError("Las contraseñas no coiniden");
                    }
                 }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void cambiarFragment(String email,String password,String nivels){
        Bundle bundle = new Bundle();
        bundle.putString("mail", email.trim());
        bundle.putString("pass", password.trim());
        bundle.putString("lvl", nivels.trim());

        getParentFragmentManager().setFragmentResult("key",bundle);


        FragmentManager manager = getParentFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment bottomFragment = manager.findFragmentById(R.id.fragmentDato);

        ft.detach(bottomFragment);
        ft.attach(bottomFragment);
        ft.show(bottomFragment);
        Fragment bottomFragment1 = manager.findFragmentById(R.id.fragmentMP);
        ft.hide(bottomFragment1);
        ft.commit();
    }

}