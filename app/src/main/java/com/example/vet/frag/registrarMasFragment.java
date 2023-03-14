package com.example.vet.frag;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.vet.MainActivity;
import com.example.vet.R;
import com.example.vet.regDatosMasActivity;
import com.google.android.gms.common.internal.Constants;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class registrarMasFragment extends Fragment {
    private Button btnBuscar;
    private EditText edtBusMail;

    private String key = "";
    private DatabaseReference mDatabase;
    AwesomeValidation awesomeValidation;



    public registrarMasFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_mas, container, false);
        // Inflate the layout for this fragment
        btnBuscar = view.findViewById(R.id.btnBuscarMail);
        edtBusMail = view.findViewById(R.id.edtCorreoDueño);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edtCorreoDueño, Patterns.EMAIL_ADDRESS, R.string.err_email);



        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()){

                   String mail = edtBusMail.getText().toString();

                   // Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT).show();
                   mDatabase.child("Usuario").orderByChild("email").equalTo(mail).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    key = dataSnapshot.getKey();
                                    cambiarFragment(key);
                            }
                            verificar(key);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                    key = "";
                }
            }
        });


        return view;
    }

    public void verificar(String llave){
        if (llave.equals(""))
            edtBusMail.setError("No existe una cuenta con ese correo");
        }

    public void cambiarFragment(String key){
        Intent intent = new Intent(getActivity(), regDatosMasActivity.class);
        intent.putExtra("llave", key);
        startActivity(intent);
    }




}