package com.example.vet.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;

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


    String corr = "";

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

    public interface OnCorreoDuenoListener {
        void onCorreoDuenoObtenido(String correoDueno);
    }

    private void correoDueno(String keydueno, OnCorreoDuenoListener listener){
        String[] correoDu = new String[1];
        mDatabase.child("Usuario").child(keydueno).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                correoDu[0] = snapshot.child("email").getValue().toString();
                listener.onCorreoDuenoObtenido(correoDu[0]);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void obtenerInfo(){
        mDatabase.child("Mascotas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        String key = ds.getKey();
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
                        String especie =ds.child("species").getValue().toString();
                        String correo = ds.child("key").getValue().toString();
                        correoDueno(correo, new OnCorreoDuenoListener() {
                            @Override
                            public void onCorreoDuenoObtenido(String correoDueno) {
                                mascotaList.add(new mostrarMascota(
                                        key, name, img, edad, raza, genero, especie, correoDueno
                                ));

                                AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}