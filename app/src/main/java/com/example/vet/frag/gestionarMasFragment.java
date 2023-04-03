package com.example.vet.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vet.R;
import com.example.vet.clases.AdapterMosMascotas;
import com.example.vet.clases.mostrarMascota;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class gestionarMasFragment extends Fragment {

    private List<mostrarMascota> mascotaList;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;

    public gestionarMasFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestionar_mas, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recyvlerMostrarMas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mascotaList = new ArrayList<>();
        obtenerInfo();

        // Inflate the layout for this fragment
        return view;
    }

    private void obtenerInfo(){

        mDatabase.child("Mascotas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        String name = ds.child("name").getValue().toString();
                        String img = ds.child("photo").getValue().toString();
                        String edad =ds.child("birth").getValue().toString();
                        String genero =ds.child("sex").getValue().toString();
                        String raza =ds.child("raze").getValue().toString();
                        String especie =ds.child("species").getValue().toString();
                        String dueno =ds.child("key").getValue().toString();
                        mascotaList.add(new mostrarMascota(
                                name, img, edad, raza, genero, especie, dueno
                        ));
                    }

                    AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}