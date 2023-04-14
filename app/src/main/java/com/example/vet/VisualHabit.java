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

import com.example.vet.clases.Habitat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VisualHabit extends AppCompatActivity  implements View.OnClickListener {
    private List<Habitat> listmc = new ArrayList<Habitat>();
    ArrayAdapter<Habitat> arrayAdapterlugar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText lug,fech;
    Button btnactuali;
    ListView listV_hab;
    Habitat habiSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_habit);
        lug = findViewById(R.id.nomHabi);
        fech = findViewById(R.id.fechCita);
        btnactuali = findViewById(R.id.btnactualizar);
        listV_hab = findViewById(R.id.lv_datoshabit);
        inicializarFirebase();
        listarDatos();
        listV_hab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                habiSelected = (Habitat) parent.getItemAtPosition(position);
                fech.setText(habiSelected.getUid());
                lug.setText(habiSelected.getLugar());
            }
        });
    }


    private void listarDatos() {
        databaseReference.child("habitaculo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listmc.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Habitat p = objSnaptshot.getValue(Habitat.class);
                    listmc.add(p);
                    arrayAdapterlugar = new ArrayAdapter<Habitat>(VisualHabit.this, android.R.layout.simple_list_item_1, listmc);
                    listV_hab.setAdapter(arrayAdapterlugar);
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
        lug.setText("");

    }
}