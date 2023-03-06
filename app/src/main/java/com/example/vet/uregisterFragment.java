package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class uregisterFragment extends Fragment {

    private String mail, pass, lvl;
    private Button btnregistrar;
    private EditText edtTel, edtDir, edtNom;
   private FirebaseAuth mAuth,mAuthM;
    private DatabaseReference mDatabase;
    String  arr;
    AwesomeValidation awesomeValidation;

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
        View view = inflater.inflate(R.layout.fragment_uregister, container, false);

        btnregistrar = view.findViewById(R.id.btnregistrarusuario);
        edtNom = view.findViewById(R.id.edtNom1);
        edtTel = view.findViewById(R.id.edtTel1);
        edtDir = view.findViewById(R.id.edtDir1);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    arr = mAuthM.getInstance().getCurrentUser().getIdToken(true).toString();

        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edtDir1, "[a-zA-Z0-9\\s]+", R.string.err_dir);
        awesomeValidation.addValidation(getActivity(), R.id.edtNom1, "[a-zA-Z\\s]+", R.string.err_nom);
        awesomeValidation.addValidation(getActivity(), R.id.edtTel1, RegexTemplate.TELEPHONE, R.string.err_tel);


        getParentFragmentManager().setFragmentResultListener("key", getActivity(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                mail=result.getString("mail");
                pass=result.getString("pass");
                lvl=result.getString("lvl");
            }
        });


        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                   String nivels ,email, password,address,phone,name;
                    email = mail;
                    password = pass;
                    address = String.valueOf(edtDir.getText());
                    name = String.valueOf(edtNom.getText());
                    phone = String.valueOf(edtTel.getText());
                    nivels = lvl;
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        registro(name,email,address,phone,nivels);
                                        Toast.makeText(getActivity(), "Registrado con exito",Toast.LENGTH_SHORT).show();
                                        volverMenu();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getActivity(), "Ya existe una cuenta con ese correo",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
            }
        });

        return view;


    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setuponBack();
        super.onViewCreated(view, savedInstanceState);
    }
    private void setuponBack(){
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment bottomFragment = manager.findFragmentById(R.id.fragmentMP);

                ft.detach(bottomFragment);
                ft.attach(bottomFragment);
                ft.show(bottomFragment);
                Fragment bottomFragment1 = manager.findFragmentById(R.id.fragmentDato);
                ft.hide(bottomFragment1);
                ft.commit();
            }
        });
    }

    private void registro(String name,String email, String address, String phone, String nivel){
        final Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email",email);
        map.put("address",address);
        map.put("phone",phone);
        map.put("lvl",nivel);

        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(getActivity(), "Registrado", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getActivity(), "Error al registrar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void volverMenu (){
        mAuthM.signInWithCustomToken(arr);
        Intent iniciar = new Intent(getActivity(),Menu.class);
        iniciar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(iniciar);
        getActivity().finish();
    }

}