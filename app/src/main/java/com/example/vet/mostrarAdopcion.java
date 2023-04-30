package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.vet.clases.AdapterMosAdop;
import com.example.vet.clases.AdapterMosCamp;
import com.example.vet.clases.mostrarAdop;
import com.example.vet.clases.mostrarCamList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class mostrarAdopcion extends AppCompatActivity {

    private List<mostrarAdop> adopList;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseAuth mAuth;
    private TextView title;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_adopcion);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.adop_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnAdd = findViewById(R.id.add_campaign_button);
        title = findViewById(R.id.txttitle);
        adopList = new ArrayList<>();
        verificarInicioDueno();
        obtenerInfoCampañas();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clip = new Intent(mostrarAdopcion.this,CrearAdopcion.class);
                startActivity(clip);
            }
        });


    }
    private void verificarInicioDueno(){
        String userkey =  mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String lvl;
                    lvl = snapshot.child("lvl").getValue().toString();
                    if (lvl.equals("3")){
                        btnAdd.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void obtenerInfoCampañas() {
        adopList.clear();
        mDatabase.child("Adopcion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    adopList.clear();
                    //deleteExpiredCampaigns();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idM = ds.getKey();
                        String nombreM = ds.child("name").getValue().toString();
                        String imagenM = "";
                        DataSnapshot photoNode = ds.child("photo");
                        if (photoNode.exists()) {
                            imagenM = photoNode.getValue().toString();
                        }
                        String edadM = ds.child("birth").getValue().toString();
                        String razaM = ds.child("raze").getValue().toString();
                        String generoM = ds.child("sex").getValue().toString();
                        String especieM = ds.child("species").getValue().toString();
                        String colorM   = ds.child("color").getValue().toString();

                        adopList.add(new mostrarAdop(idM, nombreM, imagenM, edadM, razaM, generoM, especieM,colorM));
                    }
                    AdapterMosAdop adapter = new AdapterMosAdop(mostrarAdopcion.this, adopList);
                    recyclerView.setAdapter(adapter);
                }
                verificarCamp();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void verificarCamp(){
        if (adopList.isEmpty()) {
            title.setVisibility(View.VISIBLE);
            title.setText("No hay campañas activas");
            recyclerView.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText("Adopciones Activas");
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}