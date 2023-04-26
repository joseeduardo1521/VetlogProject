package com.example.vet.frag;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.app.ProgressDialog;
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
import com.example.vet.Menu;
import com.example.vet.R;
import com.example.vet.RegisUser;
import com.example.vet.RegisterAct;
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
   private ProgressDialog progressDialog;
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Cargando datos...");
        progressDialog.setMessage("Por favor, espere.");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        arr = mAuthM.getInstance().getCurrentUser().getIdToken(true).toString();
        String regex= "^(?!\\s*$).+";
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation( edtDir, regex, "Ingrese una direccion");
        awesomeValidation.addValidation( edtNom, regex, "Ingrese su nombre completo");
        awesomeValidation.addValidation( edtTel, RegexTemplate.TELEPHONE, "Ingrese un numero de telefono");


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
                    progressDialog.show();
                   String nivels ,email, password,address,phone,name,photo;
                    email = mail;
                    password = pass;
                    address = String.valueOf(edtDir.getText());
                    name = String.valueOf(edtNom.getText());
                    phone = String.valueOf(edtTel.getText());
                    nivels = lvl;
                    photo = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.setTitle("Cuenta casi completa verifique su correo electronico");

                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    registro(name,email,address,phone,nivels,photo);
                                                    progressDialog.dismiss();
                                                    volverMenu();
                                                } else {
                                                    Toast.makeText(getActivity(), "Error al verificar el correo",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
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

    private void registro(String name,String email, String address, String phone, String nivel,String photo){
        final Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email",email);
        map.put("address",address);
        map.put("phone",phone);
        map.put("lvl",nivel);
        map.put("photo",photo);

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
        Intent iniciar = new Intent(getActivity(), Menu.class);
        iniciar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(iniciar);
        getActivity().finish();
    }

}