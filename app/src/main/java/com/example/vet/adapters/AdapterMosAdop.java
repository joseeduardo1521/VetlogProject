package com.example.vet.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vet.R;
import com.example.vet.clases.mostrarAdop;
import com.example.vet.mostrarReceta;
import com.example.vet.regDatosMasActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class AdapterMosAdop extends RecyclerView.Adapter<AdapterMosAdop.MosAdopViewHolder> {
    private LayoutInflater mInflater;
    private Context mCtx;
    private List<mostrarAdop> adopLists;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    public AdapterMosAdop(Context mCtx, List<mostrarAdop> adopLists) {
        this.mCtx=mCtx;
        this.adopLists = adopLists;
        this.mInflater = LayoutInflater.from(mCtx);
    }

    public class MosAdopViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtRaza,txtEdad,txtEspecie, txtDueño,txtGenero;
        ImageView imgMasc;
        CardView cv;
        RelativeLayout layImg;
        Button btnreceta;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        int position;
        Button btnEdCamp, btnBorrarCamp;
        LinearLayout layout_btn;
        String llave,imgMascota;

        public MosAdopViewHolder(View view) {
            super(view);

            txtNombre = view.findViewById(R.id.nombreTextView);
            txtRaza = view.findViewById(R.id.razaTextView);
            txtEdad = view.findViewById(R.id.edadTextView);
            txtEspecie = view.findViewById(R.id.especieTextView);
            txtDueño = view.findViewById(R.id.correoDueño);
            txtGenero = view.findViewById(R.id.generoTextView);
            imgMasc = view.findViewById(R.id.imageViewM);
            btnreceta = view.findViewById(R.id.btnRecetar);

            cv = view.findViewById(R.id.cardViewAdop);
            layout_btn = view.findViewById(R.id.layBotones);
            // Define click listener for the ViewHolder's View

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth = FirebaseAuth.getInstance();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    String id= mAuth.getCurrentUser().getUid();
                    mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String lvl;
                                lvl = snapshot.child("lvl").getValue().toString();
                                if (lvl.equals("1")|| lvl.equals("2")){
                                    int vw =(layout_btn.getVisibility() ==  v.GONE)? v.VISIBLE: v.GONE;
                                    TransitionManager.beginDelayedTransition(layout_btn, new AutoTransition());
                                    layout_btn.setVisibility(vw);
                                }
                                if (lvl.equals("1")){
                                    btnreceta.setVisibility(View.INVISIBLE);
                                }
                                if (lvl.equals("3")){
                                    Intent intent = new Intent(mCtx, mostrarReceta.class);
                                    intent.putExtra("llave2", llave);
                                    //mCtx.startActivity(intent);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });

            view.findViewById(R.id.btnEdtMas).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, regDatosMasActivity.class);
                    intent.putExtra("llave2", llave);
                    mCtx.startActivity(intent);

                }
            });

            view.findViewById(R.id.btnRecetar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, mostrarReceta.class);
                    intent.putExtra("llave2", llave);
                    mCtx.startActivity(intent);

                }
            });

            view.findViewById(R.id.btnBorrarM).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference registroRef = databaseRef.child("Adopcion").child(llave);
                    if (!(imgMascota.equals("https://firebasestorage.googleapis.com/v0/b/vetlog-fec63.appspot.com/o/user%2Fvetg.png?alt=media&token=75ea52f3-4fe1-4c8e-821f-575edbced693"))) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgMascota);
                        // Borra el archivo de Firebase Storage
                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Borra la referencia al archivo en la base de datos
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Maneja el error al borrar el archivo de Firebase Storage
                            }
                        });
                    }
                    registroRef.removeValue();
                    //mascotaList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }




    @NonNull
    @Override
    public AdapterMosAdop.MosAdopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = mInflater.from(parent.getContext()).inflate(R.layout.cardadop, parent,false);
        return new AdapterMosAdop.MosAdopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMosAdop.MosAdopViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mostrarAdop campa = adopLists.get(position);
        holder.imgMascota = campa.getImagenM();
        holder.llave = campa.getIdM();
        Glide.with(mCtx)
                .load(campa.getImagenM())
                .into(holder.imgMasc);

        holder.txtNombre.setText(campa.getNombreM());
        holder.txtEspecie.setText(campa.getEspecieM());
        //holder.txtDueño.setText(campa.getCorreoDueno());
        holder.txtRaza.setText(campa.getColorM());
        holder.txtEdad.setText(campa.getEdadM());
        holder.txtGenero.setText(campa.getGeneroM());
        holder.position = position;
        holder.cv.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_trans));


    }

    @Override
    public int getItemCount() {
       return adopLists.size();
    }
}
