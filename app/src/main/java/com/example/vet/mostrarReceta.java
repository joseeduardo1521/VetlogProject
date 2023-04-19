package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vet.clases.AdapterMosMascotas;
import com.example.vet.clases.AdapterMosRecetas;
import com.example.vet.clases.mostrarMascota;
import com.example.vet.clases.mostrarRecetaList;
import com.example.vet.frag.gestionarMasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class mostrarReceta extends AppCompatActivity {

    private String key="";
    private Button btnnvRec;
    private List<mostrarRecetaList> recetaList;
    private TextView tv_pet_name,tv_pet_species, tv_pet_sex, tv_pet_birth, tv_pet_color, tv_pet_raze, tv_pet_weight;
    private CircleImageView img_pet_photo;
    private RecyclerView recyclerView;
    private TextView tv_empty;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_receta);
        String key = getIntent().getExtras().getString("llave2");
        this.key = key;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        img_pet_photo = findViewById(R.id.img_pet_photo);
        tv_empty = findViewById(R.id.tv_empty);
        tv_pet_name = findViewById(R.id.tv_pet_name);
        tv_pet_species = findViewById(R.id.tv_pet_species);
        tv_pet_sex = findViewById(R.id.tv_pet_sex);
        tv_pet_birth = findViewById(R.id.tv_pet_birth);
        tv_pet_birth = findViewById(R.id.tv_pet_color);
        tv_pet_color = findViewById(R.id.tv_pet_color);
        tv_pet_raze = findViewById(R.id.tv_pet_raze);
        tv_pet_weight = findViewById(R.id.tv_pet_weight);
        recyclerView = findViewById(R.id.recycler_view_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Añade SnapHelper para centrar el elemento
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        btnnvRec = findViewById(R.id.btn_nueva_receta);
        recetaList = new ArrayList<>();
        verificarInicioDueno();
        obtenerInfoRecetas();
        buscarMascota(key);
        btnnvRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mostrarReceta.this, new_receta.class);
                intent.putExtra("idMas", key);
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
                        btnnvRec.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void buscarMascota(String clave) {
        mDatabase.child("Mascotas").child(clave).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if(ds.exists()){
                    String name = ds.child("name").getValue().toString();
                    String img;
                    if (ds.child("photo").exists()){
                        if(ds.child("photo").getValue().toString().equals(null))
                            img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                        else
                            img = ds.child("photo").getValue().toString();
                    }else {
                        img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                    }
                    String edad =ds.child("birth").getValue().toString();
                    String genero =ds.child("sex").getValue().toString();
                    String raza =ds.child("raze").getValue().toString();
                    String esp =ds.child("species").getValue().toString();
                    String color =ds.child("color").getValue().toString();
                    String weight =ds.child("weight").getValue().toString();


                    Glide.with(mostrarReceta.this)
                            .load(img)
                            .into(img_pet_photo);
                    tv_pet_name.setText(name);
                    tv_pet_species.setText(esp);
                    tv_pet_sex.setText(genero);
                    tv_pet_birth.setText(edad);
                    tv_pet_color.setText(color);
                    tv_pet_raze.setText(raza);
                    tv_pet_weight.setText(weight);



                } else {
                    Toast.makeText(mostrarReceta.this, "No se encontró ninguna mascota con el código QR proporcionado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                verificarRecetas();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void verificarRecetas(){
        if (recetaList.isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tv_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}