package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuSec extends AppCompatActivity {

    private View cardCerSesion;
    private FirebaseAuth mAuth;
    private String idMas;
    private TextView mnombre, mRol;
    private DatabaseReference mDatabase;
    public static final String SHARED_PREFS ="sharedPrefs.mail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_sec);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        cardCerSesion = (View) findViewById(R.id.D7);
        mnombre = (TextView) findViewById(R.id.txtUsu);
        mRol = (TextView) findViewById(R.id.txtPuesto);
        getUserInfo();

        cardCerSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("mail","");
                editor.putString("pass","");
                editor.apply();
                cerrarSesion();
            }
        });

    }

    private void getUserInfo(){
        String id= mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre,correo,phone,direc,lvl;
                    nombre = snapshot.child("name").getValue().toString();
                    correo = snapshot.child("email").getValue().toString();
                    phone = snapshot.child("phone").getValue().toString();
                    direc = snapshot.child("address").getValue().toString();
                    lvl = snapshot.child("lvl").getValue().toString();
                    switch (lvl){
                        case "1":
                            mRol.setText("Veterinario");
                            break;
                        case "2":
                            mRol.setText("Secretario");
                            break;
                        case "3":
                            mRol.setText("Dueño");
                            break;
                    }
                    mnombre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cerrarSesion(){
        Intent iniciar = new Intent(this,MainActivity.class);
        iniciar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(iniciar);
        finish();
    }
}