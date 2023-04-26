package com.example.vet;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class new_receta extends AppCompatActivity {

    private EditText editTextVeterinarian, editTextDate, editTextMedicine, editTextDose, editTextFrequency, editTextDuration, editTextObserv;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button buttonPrescribe,btnUpdateRes;
    private AwesomeValidation awesomeValidation;
    private String keyM="",keyR="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receta);
        String keyM = getIntent().getExtras().getString("idMas");
        this.keyM = keyM;
        String keyR = getIntent().getExtras().getString("llave2");
        this.keyR = keyR;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        obtenerVeterinario();
        editTextVeterinarian = findViewById(R.id.editTextVeterinarian);
        editTextDate = findViewById(R.id.editTextDate);
        editTextMedicine = findViewById(R.id.editTextMedicine);
        editTextDose = findViewById(R.id.editTextDose);
        editTextFrequency = findViewById(R.id.editTextFrequency);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextObserv = findViewById(R.id.editTextObserv);
        buttonPrescribe = findViewById(R.id.buttonPrescribe);
        btnUpdateRes = findViewById(R.id.btnUpdateRes);
        String regex= "^(?!\\s*$).+";
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.editTextMedicine, regex, R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.editTextDose, regex, R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.editTextFrequency, regex, R.string.err_campova);
        awesomeValidation.addValidation(this, R.id.editTextDuration, regex, R.string.err_campova);


        buttonPrescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDatos(keyM);
            }
        });

        btnUpdateRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatosMascota();
            }
        });

        if(this.keyM != null){
            buttonPrescribe.setVisibility(View.VISIBLE);
        }
        if(this.keyR != null) {
            btnUpdateRes.setVisibility(View.VISIBLE);
           obtenerDatosReceta(this.keyM, this.keyR);
        }

    }

    private void obtenerDatosReceta(String claveM, String claveR) {
        mDatabase.child("Mascotas").child(claveM).child("Recetas").child(claveR).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String dosis = snapshot.child("dosis").getValue().toString();
                    String durante = snapshot.child("durante").getValue().toString();
                    String frecuencia =snapshot.child("frecuencia").getValue().toString();
                    String medicamento =snapshot.child("medicamento").getValue().toString();
                    String observaciones =snapshot.child("observaciones").getValue().toString();

                    editTextDose.setText(dosis);
                    editTextDuration.setText(durante);
                    editTextFrequency.setText(frecuencia);
                    editTextMedicine.setText(medicamento);
                    editTextObserv.setText(observaciones);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void actualizarDatosMascota(){
        if(awesomeValidation.validate()) {
            String vet = String.valueOf(editTextVeterinarian.getText());
            String fecha = String.valueOf(editTextDate.getText());
            String med = String.valueOf(editTextMedicine.getText());
            String dosis = String.valueOf(editTextDose.getText());
            String freq = String.valueOf(editTextFrequency.getText());
            String dur = String.valueOf(editTextDuration.getText());
            String obs = String.valueOf(editTextObserv.getText());

            final Map<String, Object> map = new HashMap<>();
            map.put("vet", vet);
            map.put("fecha", fecha);
            map.put("medicamento", med);
            map.put("dosis", dosis);
            map.put("frecuencia", freq);
            map.put("durante", dur);
            map.put("observaciones", obs);


            mDatabase.child("Mascotas").child(keyM).child("Recetas").child(keyR).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if (task2.isSuccessful()) {
                        Toast.makeText(new_receta.this, "Receta Actualizada", Toast.LENGTH_SHORT).show();
                        regresarmenu();
                    } else
                        Toast.makeText(new_receta.this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void registrarDatos(String keyM){
        if(awesomeValidation.validate()){
            String vet = String.valueOf(editTextVeterinarian.getText());
            String fecha = String.valueOf(editTextDate.getText());
            String med = String.valueOf(editTextMedicine.getText());
            String dosis = String.valueOf(editTextDose.getText());
            String freq = String.valueOf(editTextFrequency.getText());
            String dur = String.valueOf(editTextDuration.getText());
            String obs = String.valueOf(editTextObserv.getText());

            final Map<String, Object> map = new HashMap<>();
            map.put("vet", vet);
            map.put("fecha", fecha);
            map.put("medicamento", med);
            map.put("dosis", dosis);
            map.put("frecuencia", freq);
            map.put("durante", dur);
            map.put("observaciones", obs);
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Mascotas").child(keyM);
            String keyR = myRef.push().getKey();

            myRef.child("Recetas").child(keyR).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                        Toast.makeText(new_receta.this, "Registrado", Toast.LENGTH_SHORT).show();
                        regresarmenu();
                    }
                    else Toast.makeText(new_receta.this, "Error al registrar receta", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void regresarmenu() {
        Intent intent = new Intent(this,Menu.class);
        startActivity(intent);
        finish();
    }


    private void obtenerVeterinario(){
        String idVet;
        idVet= mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(idVet).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String nombre;
                    nombre = snapshot.child("name").getValue().toString();
                    editTextVeterinarian.setText(nombre);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String fechaActual = dateFormat.format(calendar.getTime());
                    editTextDate.setText(fechaActual);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}