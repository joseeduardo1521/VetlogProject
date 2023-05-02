package com.example.vet.frag;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vet.R;
import com.example.vet.adapters.AdapterMosMascotas;
import com.example.vet.clases.mostrarMascota;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;


import java.util.ArrayList;
import java.util.List;


public class gestionarMasFragment extends Fragment {

    private List<mostrarMascota> mascotaList;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private View btnQR;
    private TextView txtMasReg,tv_empty;
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
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        edtTextoCorreo = view.findViewById(R.id.edtBusqueda);
        recyclerView = view.findViewById(R.id.recyvlerMostrarMas);
        btnQR = view.findViewById(R.id.btnBuscarQR);
        tv_empty = view.findViewById(R.id.tv_empty);
        txtMasReg = view.findViewById(R.id.textViewMasReg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mascotaList = new ArrayList<>();
        verificarInicioDueno();
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
                            if(snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    llaveCodu = dataSnapshot.getKey();
                                    buscarMascota(llaveCodu, "key");
                                }
                            }else
                                Toast.makeText(getActivity(), "No no encuentra ninguna mascota con ese correo", Toast.LENGTH_SHORT).show();
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


        return view;
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
                String habitaculoCodigo = result.getContents(); // obtiene los datos del código QR
                mDatabase.child("habitaculo").orderByChild("lugar").equalTo(habitaculoCodigo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                if(dataSnapshot.child("idMas").exists()) {
                                    String llave = dataSnapshot.child("idMas").getValue().toString();
                                    if(!(llave.equals(""))) {
                                        buscarMascota(llave, "qr");
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "No hay mascota en este habitaculo", Toast.LENGTH_SHORT).show();
                                    }
                                    }else{
                                    Toast.makeText(getActivity(), "No hay mascota en este habitaculo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else
                            Toast.makeText(getActivity(), "El codigo no correspone a ningun habitaculo", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error al registrar el internado", Toast.LENGTH_SHORT).show();

                    }
                });

               } else {
                // Código que se ejecutará si se cancela el escaneo del código QR
                Toast.makeText(getActivity(), "Se canceló el escaneo del código QR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Query busquedas;
    private void buscarMascota(String busqueda, String clave) {
        mascotaList.clear();
        if (clave.equals("key")) {
            busquedas = mDatabase.child("Mascotas").orderByChild(clave).equalTo(busqueda);
            busquedas.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mascotaList.clear(); // Limpiar la lista de mascotas antes de actualizarla
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            tv_empty.setVisibility(View.GONE);
                            String key = ds.getKey();
                            String name = ds.child("name").getValue().toString();
                            String img;
                            if (ds.child("photo").exists()) {
                                if (ds.child("photo").getValue().toString().equals(null))
                                    img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                                else
                                    img = ds.child("photo").getValue().toString();
                            } else {
                                img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                            }
                            String edad = ds.child("birth").getValue().toString();
                            String genero = ds.child("sex").getValue().toString();
                            String raza = ds.child("raze").getValue().toString();
                            String esp = ds.child("species").getValue().toString();
                            String estado = ds.child("status").getValue().toString();
                            String corred = ds.child("key").getValue().toString();
                            correoDueno(corred, new OnCorreoDuenoListener() {
                                @Override
                                public void onCorreoDuenoObtenido(String correoDueno) {

                                    mascotaList.add(new mostrarMascota(
                                            key, name, img, edad, raza, genero, esp, estado, correoDueno
                                    ));

                                    AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } else {
                        tv_empty.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if  (clave.equals("qr")){
            busquedas = mDatabase.child("Mascotas").child(busqueda);
            busquedas.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                            String key = snapshot.getKey();
                            String name = snapshot.child("name").getValue().toString();
                            String img;
                            if (snapshot.child("photo").exists()) {
                                if (snapshot.child("photo").getValue().toString().equals(null))
                                    img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                                else
                                    img = snapshot.child("photo").getValue().toString();
                            } else {
                                img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                            }
                            String edad = snapshot.child("birth").getValue().toString();
                            String genero = snapshot.child("sex").getValue().toString();
                            String raza = snapshot.child("raze").getValue().toString();
                            String esp = snapshot.child("species").getValue().toString();
                            String estado = snapshot.child("status").getValue().toString();
                            String corred = snapshot.child("key").getValue().toString();
                            correoDueno(corred, new OnCorreoDuenoListener() {
                                @Override
                                public void onCorreoDuenoObtenido(String correoDueno) {

                                    mascotaList.add(new mostrarMascota(
                                            key, name, img, edad, raza, genero, esp, estado, correoDueno
                                    ));
                                    AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                }
                            });

                    } else {

                        Toast.makeText(getActivity(), "No se encontró ninguna mascota con la busqueda realizada", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


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

    private void verificarInicioDueno() {
        String userkey = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuario").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lvl;
                    lvl = snapshot.child("lvl").getValue().toString();
                    if (lvl.equals("3")) {
                        edtTextoCorreo.setVisibility(View.GONE);
                        btnQR.setVisibility(View.GONE);
                        txtMasReg.setText("Sus Mascotas registradas");
                        buscarMascota(userkey, "key");
                    } else {
                        obtenerInfo();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void obtenerInfo() {
        mascotaList.clear();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String name = dataSnapshot.child("name").getValue().toString();
                String img;
                if (dataSnapshot.child("photo").exists()) {
                    img = dataSnapshot.child("photo").getValue().toString();
                } else {
                    img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                }
                String edad = dataSnapshot.child("birth").getValue().toString();
                String genero = dataSnapshot.child("sex").getValue().toString();
                String raza = dataSnapshot.child("raze").getValue().toString();
                String estado = dataSnapshot.child("status").getValue().toString();
                String especie = dataSnapshot.child("species").getValue().toString();
                String correo = dataSnapshot.child("key").getValue().toString();
                correoDueno(correo, new OnCorreoDuenoListener() {
                    @Override
                    public void onCorreoDuenoObtenido(String correoDueno) {
                        mostrarMascota mascota = new mostrarMascota(
                                key, name, img, edad, raza, genero, especie, estado, correoDueno
                        );
                        mascotaList.add(mascota);
                        if (getActivity() != null) {
                            AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                            recyclerView.setAdapter(adapter);
                        }
                        AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String name = dataSnapshot.child("name").getValue().toString();
                String img;
                if (dataSnapshot.child("photo").exists()) {
                    img = dataSnapshot.child("photo").getValue().toString();
                } else {
                    img = "https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693";
                }
                String edad = dataSnapshot.child("birth").getValue().toString();
                String genero = dataSnapshot.child("sex").getValue().toString();
                String raza = dataSnapshot.child("raze").getValue().toString();
                String estado = dataSnapshot.child("status").getValue().toString();
                String especie = dataSnapshot.child("species").getValue().toString();
                String correo = dataSnapshot.child("key").getValue().toString();

                correoDueno(correo, new OnCorreoDuenoListener() {
                    @Override
                    public void onCorreoDuenoObtenido(String correoDueno) {
                        mostrarMascota mascota = new mostrarMascota(
                                key, name, img, edad, raza, genero, especie, estado, correoDueno);


                        int index = -1;
                        for (int i = 0; i < mascotaList.size(); i++) {
                            if (mascotaList.get(i).getIdM().equals(key)) {
                                index = i;
                                break;
                            }
                        }
                        if (getActivity() != null) {
                            AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                            recyclerView.setAdapter(adapter);
                            if (index != -1) {
                                mascotaList.set(index, mascota);
                                AdapterMosMascotas adaapter = new AdapterMosMascotas(getActivity(), mascotaList);
                                recyclerView.setAdapter(adaapter);
                            }
                        }

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = -1;
                for (int i = 0; i < mascotaList.size(); i++) {
                    if (mascotaList.get(i).getIdM().equals(key)) {
                        index = i;
                        break;
                    }
                }
                if (getActivity() != null) {
                    AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                    recyclerView.setAdapter(adapter);
                }
                if (index != -1) {
                    mascotaList.remove(index);
                    AdapterMosMascotas adapter = new AdapterMosMascotas(getActivity(), mascotaList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // No es necesario implementar este método para esta situación
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores en la consulta
            }
        };
        mDatabase.child("Mascotas").addChildEventListener(childEventListener);
    }


}