package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vet.adapters.AdapterMosCamp;
import com.example.vet.adapters.AdapterMosQr;
import com.example.vet.clases.Habitat;
import com.example.vet.clases.mostrarCamList;
import com.example.vet.clases.mostrarqrList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VisualHabit extends AppCompatActivity {

    private List<mostrarqrList> qrList;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private TextView title;
    private DatabaseReference mDatabase;
    private FloatingActionButton btnAdd;
    private String idMas ="", lugar="",fecha="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_habit);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.hab_recycler_view);
        recyclerView.setHasFixedSize(true);
        title = findViewById(R.id.txttitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnAdd = findViewById(R.id.add_hab_button);
        qrList = new ArrayList<>();
        verificarInicioDueno();
        obtenerInfoQr();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VisualHabit.this, Genqr.class);
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

    private void obtenerInfoQr() {
        qrList.clear();
        mDatabase.child("habitaculo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    qrList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idHab = ds.getKey();
                        idMas = ds.child("idMas").getValue().toString();
                        lugar = ds.child("lugar").getValue().toString();
                        fecha = ds.child("fecha_ingreso").getValue().toString();
                        qrList.add(new mostrarqrList(
                                idHab, idMas, lugar, fecha
                        ));
                    }
                    AdapterMosQr adapter = new AdapterMosQr(VisualHabit.this, qrList);
                    recyclerView.setAdapter(adapter);
                }
                verificarQr();
            }


            private void verificarQr(){
                if (qrList.isEmpty()) {
                    title.setVisibility(View.VISIBLE);
                    title.setText("No hay habitacullos registrados");
                    recyclerView.setVisibility(View.GONE);
                } else {
                    title.setVisibility(View.VISIBLE);
                    title.setText("Habitacullos registrados");
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}