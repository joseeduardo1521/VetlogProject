package com.example.vet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vet.clases.AdapterMosMascotas;
import com.example.vet.clases.AdapterMosRecetas;
import com.example.vet.clases.mostrarMascota;
import com.example.vet.clases.mostrarRecetaList;
import com.example.vet.frag.gestionarMasFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class mostrarReceta extends AppCompatActivity {

    private String key="";
    private Button btnnvRec;
    private List<mostrarRecetaList> recetaList;
    private TextView tv_pet_name,tv_pet_species, tv_pet_sex, tv_pet_birth, tv_pet_color, tv_pet_raze, tv_pet_weight;
    private CircleImageView img_pet_photo;
    private RecyclerView recyclerView;
    private TextView tv_empty;
    private LinearLayout btnInternar;
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
        btnInternar = findViewById(R.id.btnInternar);
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

        btnInternar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(mostrarReceta.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Si no se tiene permiso, solicitarlo
                    ActivityCompat.requestPermissions(mostrarReceta.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    // Si se tiene permiso, abrir el escáner de QR
                    IntentIntegrator integrator = new IntentIntegrator(mostrarReceta.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    integrator.setPrompt("Escanea el código QR del habitáculo");
                    integrator.setOrientationLocked(false);
                    integrator.initiateScan();
                }
            }
        });

    }

    private void verificarInicioDueno() {
        String userkey = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lvl;
                    lvl = snapshot.child("lvl").getValue().toString();
                    if (lvl.equals("3")) {
                        btnnvRec.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    // Método que se ejecuta después de que se solicitan los permisos
    @Override
    public void onRequestPermissionsResult ( int requestCode, String[] permissions,
                                             int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si se concedió el permiso, abrir el escáner de QR
                IntentIntegrator integrator = new IntentIntegrator(mostrarReceta.this);
                integrator.setPrompt("Escanea el código QR del habitáculo");
                integrator.setBeepEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            } else {
                // Si se negó el permiso, mostrar un mensaje de error
                Toast.makeText(mostrarReceta.this, "Se necesita permiso para acceder a la cámara", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelando internado", Toast.LENGTH_LONG).show();
            } else {
                // Aquí puedes obtener el resultado del escaneo del código QR y modificar los campos necesarios
                String habitaculoCodigo = result.getContents();
                mDatabase.child("habitaculo").orderByChild("lugar").equalTo(habitaculoCodigo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                String keyh = dataSnapshot.getKey();
                                registrarInter(keyh);
                            }
                        }else
                            Toast.makeText(mostrarReceta.this, "El codigo no correspone a ningun habitaculo", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mostrarReceta.this, "Error al registrar el internado", Toast.LENGTH_SHORT).show();

                    }
                });

                // Modifica los campos según sea necesario
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void registrarInter (String codigo){
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        Map<String, Object> updMas = new HashMap<>();
        updMas.put("idhabit", codigo);
        updMas.put("start_date",currentDate.toString());
        updMas.put("status","Internado");
        updMas.put("end_date","");


        mDatabase.child("Mascotas").child(key).updateChildren(updMas).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // Actualizar los campos del habitáculo
                Map<String, Object> updates = new HashMap<>();
                updates.put("idMas", key);
                updates.put("fecha_ingreso",currentDate);

                mDatabase.child("habitaculo").child(codigo).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mostrarReceta.this, "Ingreso Completado", Toast.LENGTH_SHORT).show();
                    }
                });
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
                    if(ds.child("status").getValue().toString().equals("Internado")){

                    }


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