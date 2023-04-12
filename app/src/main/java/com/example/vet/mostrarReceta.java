package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vet.clases.AdapterMosMascotas;
import com.example.vet.clases.AdapterMosRecetas;
import com.example.vet.clases.mostrarMascota;
import com.example.vet.clases.mostrarRecetaList;
import com.example.vet.frag.gestionarMasFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class mostrarReceta extends AppCompatActivity {

    private String key="";
    private Button btnnvRec;
    private List<mostrarRecetaList> recetaList;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_receta);
        String key = getIntent().getExtras().getString("llave2");
        this.key = key;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler_view_rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnnvRec = findViewById(R.id.btn_nueva_receta);
        recetaList = new ArrayList<>();
        obtenerInfoRecetas();
        btnnvRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mostrarReceta.this, new_receta.class);
                intent.putExtra("idMas", key);
                startActivity(intent);
            }
        });

    }


    private void obtenerInfoRecetas(){
        recetaList.clear();
        mDatabase.child("Mascotas").child(key).child("Recetas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        String idMed = ds.getKey();
                        String med = ds.child("medicamento").getValue().toString();
                        String dosis =ds.child("dosis").getValue().toString();
                        String durante =ds.child("durante").getValue().toString();
                        String fecha =ds.child("fecha").getValue().toString();
                        String freq =ds.child("frecuencia").getValue().toString();
                        String obs = ds.child("observaciones").getValue().toString();
                        String vet = ds.child("vet").getValue().toString();

                        recetaList.add(new mostrarRecetaList(
                                idMed, vet, fecha, med, dosis, freq, durante, obs
                        ));

                        AdapterMosRecetas adapter = new AdapterMosRecetas(mostrarReceta.this, recetaList,key);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}