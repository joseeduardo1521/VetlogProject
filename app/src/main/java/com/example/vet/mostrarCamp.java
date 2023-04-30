package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.vet.adapters.AdapterMosCamp;
import com.example.vet.clases.mostrarCamList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class mostrarCamp extends AppCompatActivity {

    private List<mostrarCamList> campList;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseAuth mAuth;
    private TextView title;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_camp);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.campaigns_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnAdd = findViewById(R.id.add_campaign_button);
        title = findViewById(R.id.txttitle);
        campList = new ArrayList<>();
        verificarInicioDueno();
        obtenerInfoCampañas();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mostrarCamp.this, CrearVacuna.class);
                startActivity(intent);
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
        campList.clear();
        mDatabase.child("Campains").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    campList.clear();
                    deleteExpiredCampaigns();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idCam = ds.getKey();
                        String nomCam = ds.child("namecam").getValue().toString();
                        String iniD = ds.child("iniciodate").getValue().toString();
                        String finD = ds.child("findate").getValue().toString();
                        String esp = ds.child("speciescam").getValue().toString();
                        String ubi = ds.child("ubicam").getValue().toString();
                        String nota = ds.child("notecam").getValue().toString();

                        campList.add(new mostrarCamList(
                                idCam, nomCam, iniD, finD, ubi, esp, nota
                        ));
                    }
                    AdapterMosCamp adapter = new AdapterMosCamp(mostrarCamp.this, campList);
                    recyclerView.setAdapter(adapter);
                }
                verificarCamp();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void deleteExpiredCampaigns() {
        mDatabase.child("Campains").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date currentDate = new Date();
                List<String> campaignsToDelete = new ArrayList<>();

                for (DataSnapshot campaignSnapshot : dataSnapshot.getChildren()) {
                    String endDateString = campaignSnapshot.child("findate").getValue().toString();
                    try {
                        Date endDate = dateFormat.parse(endDateString);
                        // Elimina la hora del día de ambas fechas
                        Date currentDateWithoutTime = dateFormat.parse(dateFormat.format(currentDate));
                        Date endDateWithoutTime = dateFormat.parse(dateFormat.format(endDate));

                        // Comprueba si la fecha de finalización es estrictamente anterior a la fecha actual sin la hora del día
                        if (endDateWithoutTime.before(currentDateWithoutTime)) {
                            campaignsToDelete.add(campaignSnapshot.getKey());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (!campaignsToDelete.isEmpty()) {
                    for (String campaignKey : campaignsToDelete) {
                        mDatabase.child("Campains").child(campaignKey).removeValue();
                    }
                    updateRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void updateRecyclerView() {
        campList.clear();
        obtenerInfoCampañas();
    }


    private void verificarCamp(){
        if (campList.isEmpty()) {
            title.setVisibility(View.VISIBLE);
            title.setText("No hay campañas activas");
            recyclerView.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText("Campañas activas");
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}