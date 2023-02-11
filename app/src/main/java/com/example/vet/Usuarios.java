package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Usuarios extends AppCompatActivity {
    private TextView mcorreo;
    private TextView mnombre;
    private TextView mphono;
    private TextView mdir;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mnombre= (TextView) findViewById(R.id.nombre);
        mcorreo = (TextView) findViewById(R.id.correo);
        mphono = (TextView) findViewById(R.id.cel);
        mdir = (TextView) findViewById(R.id.direc);

        getUserInfo();

    }

    private void getUserInfo(){
        String id= mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre,correo,phone,direc;
                    nombre = snapshot.child("name").getValue().toString();
                    correo = snapshot.child("email").getValue().toString();
                    phone = snapshot.child("phone").getValue().toString();
                    direc = snapshot.child("address").getValue().toString();
                    mnombre.setText(nombre);
                    mcorreo.setText(correo);
                    mphono.setText(phone);
                    mdir.setText(direc);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}