package com.example.vet.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.example.vet.Menu;
import com.example.vet.R;
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


public class AdapterMosMascotas extends RecyclerView.Adapter<AdapterMosMascotas.MosMacotasViewHolder> {

    private LayoutInflater mInflater;
    private Context mCtx;
    private List<mostrarMascota> mascotaList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;



    @Override
    public int getItemCount() {
        return mascotaList.size();
    }

    public AdapterMosMascotas(Context mCtx, List<mostrarMascota> mascotaList) {
        this.mCtx=mCtx;
        this.mascotaList = mascotaList;
        this.mInflater = LayoutInflater.from(mCtx);

    }


    public class MosMacotasViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtRaza,txtEdad,txtEspecie, txtDueño,txtGenero;
        ImageView imgMasc;
        CardView cv;
        RelativeLayout layImg;
        Button btnreceta;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        int position;
        LinearLayout layout_btn;
        String llave,imgMascota;

        public MosMacotasViewHolder(View view) {
            super(view);
            txtNombre = view.findViewById(R.id.nombreTextView);
            txtRaza = view.findViewById(R.id.razaTextView);
            txtEdad = view.findViewById(R.id.edadTextView);
            txtEspecie = view.findViewById(R.id.especieTextView);
            txtDueño = view.findViewById(R.id.correoDueño);
            txtGenero = view.findViewById(R.id.generoTextView);
            imgMasc = view.findViewById(R.id.imageViewM);
            btnreceta = view.findViewById(R.id.btnRecetar);

            cv = view.findViewById(R.id.cardViewM);
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
                                    btnreceta.setVisibility(View.VISIBLE);
                                }
                                if (lvl.equals("3")){
                                    Intent intent = new Intent(mCtx, mostrarReceta.class);
                                    intent.putExtra("llave2", llave);
                                    mCtx.startActivity(intent);
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
                    DatabaseReference registroRef = databaseRef.child("Mascotas").child(llave);
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
                    mascotaList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }





    // Create new views (invoked by the layout manager)
    @Override
    public MosMacotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view  = mInflater.from(viewGroup.getContext()).inflate(R.layout.cardmascotas, viewGroup,false   );
        return new MosMacotasViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MosMacotasViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        mostrarMascota mascota = mascotaList.get(position);
        viewHolder.imgMascota = mascota.getImagenM();
        viewHolder.llave = mascota.getIdM();
        Glide.with(mCtx)
                .load(mascota.getImagenM())
                .into(viewHolder.imgMasc);

        changeShapeColor(viewHolder,mascota.getEstadoM() );

        viewHolder.txtNombre.setText(mascota.getNombreM());
        viewHolder.txtEspecie.setText(mascota.getEspecieM());
        viewHolder.txtDueño.setText(mascota.getCorreoDueno());
        viewHolder.txtRaza.setText(mascota.getRazaM());
        viewHolder.txtEdad.setText(mascota.getEdadM());
        viewHolder.txtGenero.setText(mascota.getGeneroM());
        viewHolder.position = position;
        viewHolder.cv.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_trans));


    }

    private void changeShapeColor(RecyclerView.ViewHolder holder, String estado) {
         RelativeLayout layImg = holder.itemView.findViewById(R.id.layImg);
        GradientDrawable drawable = (GradientDrawable) layImg.getBackground();
        switch (estado){
            case "Casa":
                drawable.setStroke(4, Color.parseColor("#004AF0")); // Nuevo color si se cumple la condición
                break;
            case "Revision":
                drawable.setStroke(4, Color.parseColor("#FFFF00")); // Nuevo color si se cumple la condición
                break;
            case "Operando":
                drawable.setStroke(4, Color.parseColor("#FF0000")); // Nuevo color si se cumple la condición
                break;
            case "Internado":
                drawable.setStroke(4, Color.parseColor("#FFA500")); // Nuevo color si se cumple la condición
                break;
            case "Estable":
                drawable.setStroke(4, Color.parseColor("#00FF00")); // Nuevo color si se cumple la condición
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)

}