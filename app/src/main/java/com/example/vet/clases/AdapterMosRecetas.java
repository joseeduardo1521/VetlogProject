package com.example.vet.clases;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vet.R;
import com.example.vet.mostrarReceta;
import com.example.vet.regDatosMasActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class AdapterMosRecetas extends RecyclerView.Adapter<AdapterMosRecetas.MosMacotasViewHolder> {

    private LayoutInflater mInflater;
    private Context mCtx;
    private List<mostrarRecetaList> recetaList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    public int getItemCount() {
        return recetaList.size();
    }

    public AdapterMosRecetas(Context mCtx, List<mostrarRecetaList> recetaList) {

        this.mCtx=mCtx;
        this.recetaList = recetaList;
        this.mInflater = LayoutInflater.from(mCtx);

    }


    public class MosMacotasViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtRaza,txtEdad,txtEspecie, txtDueño,txtGenero;
        ImageView imgMasc;
        CardView cv;
        Button btnreceta;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        int position;
        LinearLayout layout_btn;
        String llave,idRec;

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
                                if (lvl.equals("1")){
                                    btnreceta.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                    int vw =(layout_btn.getVisibility() ==  v.GONE)? v.VISIBLE: v.GONE;
                    TransitionManager.beginDelayedTransition(layout_btn, new AutoTransition());
                    layout_btn.setVisibility(vw);

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
                    DatabaseReference registroRef = databaseRef.child("Mascotas").child(llave).child("Recetas").child(idRec);
                    registroRef.removeValue();
                    recetaList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }





    // Create new views (invoked by the layout manager)
    @Override
    public MosMacotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view  = mInflater.from(viewGroup.getContext()).inflate(R.layout.cardrecetas, viewGroup,false   );
        return new MosMacotasViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MosMacotasViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        mostrarRecetaList mascota = recetaList.get(position);



        viewHolder.cv.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_trans));


    }

    // Return the size of your dataset (invoked by the layout manager)

}