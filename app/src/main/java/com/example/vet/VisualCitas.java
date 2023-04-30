package com.example.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.vet.clases.Cit;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VisualCitas extends AppCompatActivity implements View.OnClickListener{
    private List<Cit> listPerson = new ArrayList<Cit>();
    ArrayAdapter<Cit> arrayAdapterPersona;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText nombre, fecha, hora;
    Button btnactuali;
    ListView listV_personas;
    Cit personaSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_citas);
        nombre = findViewById(R.id.nomCita);
        fecha = findViewById(R.id.fechCita);
        hora = findViewById(R.id.horCita);
        btnactuali = findViewById(R.id.btnactuali);
        listV_personas = findViewById(R.id.lv_datosPersonas);
        inicializarFirebase();
        listarDatos();
        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Cit) parent.getItemAtPosition(position);
                nombre.setText(personaSelected.getName());
                fecha.setText(personaSelected.getDate());
                hora.setText(personaSelected.getTime());
            }
        });
        btnactuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCajas();
            }
        });

    }

    private void listarDatos() {
        databaseReference.child("Citas").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Cit p = objSnaptshot.getValue(Cit.class);
                    listPerson.add(p);
                    arrayAdapterPersona = new ArrayAdapter<Cit>(VisualCitas.this, android.R.layout.simple_list_item_1, listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public void onClick(View v) {

        }


    private void limpiarCajas() {
        nombre.setText("");
        fecha.setText("");
        hora.setText("");
    }

}