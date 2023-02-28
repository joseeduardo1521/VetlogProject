package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {

    private View cardPersonal;
    private TextView mnombre;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private View cardCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mnombre = (TextView) findViewById(R.id.usu);
        cardPersonal = (View) findViewById(R.id.D4);
        cardCitas = (View) findViewById(R.id.D3);
        getUserInfo();

        cardPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrarU = new Intent(Menu.this, RegisUser.class);
                startActivity(registrarU);
            }
        });

        cardCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Citas = new Intent(Menu.this, RegCitas.class);
                startActivity(Citas);
            }
        });
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}