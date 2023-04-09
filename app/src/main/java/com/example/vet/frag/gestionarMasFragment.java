package com.example.vet.frag;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vet.Menu;
import com.example.vet.R;
import com.example.vet.clases.AdapterMosMascotas;
import com.example.vet.clases.mostrarMascota;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;


import java.util.ArrayList;
import java.util.List;


public class gestionarMasFragment extends Fragment {

    private List<mostrarMascota> mascotaList;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private EditText edtTextoCorreo;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private String qrData = "";
    private String llaveCodu = "";

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
        edtTextoCorreo = view.findViewById(R.id.edtBusqueda);
        recyclerView = view.findViewById(R.id.recyvlerMostrarMas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mascotaList = new ArrayList<>();

        edtTextoCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Aquí es donde actualizas la consulta en Firebase Realtime Database y muestras los resultados
                String searchText = charSequence.toString();
                if(searchText.equals("")){
                    obtenerInfo();
                }else {
                    mDatabase.child("Usuario").orderByChild("email").equalTo(searchText).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                llaveCodu = dataSnapshot.getKey();
                                buscarMascota(llaveCodu, "key");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        view.findViewById(R.id.btnBuscarQR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarEscanerQR();
            }
        });

        // Inflate the layout for this fragment
        obtenerInfo();
        return view;
    }

    public void verificar(String llave){
        if (llave.equals(""))
            edtTextoCorreo.setError("No existe una cuenta con ese correo");
    }

    private void iniciarEscanerQR() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setOrientationLocked(false); // desbloquea la orientación del escáner
        integrator.setCaptureActivity(CaptureActivity.class); // especifica la clase CaptureActivity
        integrator.setPrompt("Escanee el código QR"); // establece un mensaje de ayuda para el usuario
        integrator.initiateScan(); // inicia el escaneo del código QR
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Código que se ejecutará cuando se escanee el código QR
                 qrData = result.getContents(); // obtiene los datos del código QR
                 buscarMascota(qrData,"key");
               } else {
                // Código que se ejecutará si se cancela el escaneo del código QR
                Toast.makeText(getActivity(), "Se canceló el escaneo del código QR", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void buscarMascota(String busqueda,String clave) {
        mascotaList.clear();
        mDatabase.child("Mascotas").orderByChild(clave).equalTo(busqueda).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        String esp =ds.child("species").getValue().toString();
                        String corred =ds.child("key").getValue().toString();
                         correoDueno(corred, new OnCorreoDuenoListener() {
                            @Override
                            public void onCorreoDuenoObtenido(String correoDueno) {

                                mascotaList.add(new mostrarMascota(
                                        key, name, img, edad, raza, genero, esp, correoDueno
                                ));
                                AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "No se encontró ninguna mascota con el código QR proporcionado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        mascotaList.clear();
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