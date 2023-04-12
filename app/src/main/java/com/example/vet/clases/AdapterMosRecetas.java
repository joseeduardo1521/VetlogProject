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
import com.example.vet.new_receta;
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
    private String llaveMas;


    @Override
    public int getItemCount() {
        return recetaList.size();
    }

    public AdapterMosRecetas(Context mCtx, List<mostrarRecetaList> recetaList,String llaveMas) {
        this.mCtx=mCtx;
        this.recetaList = recetaList;
        this.mInflater = LayoutInflater.from(mCtx);
        this.llaveMas = llaveMas;

    }


    public class MosMacotasViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreMed;
        TextView txtveterinario;
        TextView txtDosis;
        TextView txtFrecuencia;
        TextView txtDuracion;
        TextView txtObservaciones;
        TextView txtFechaReceta;
        CardView cv;
        Button btnEdReceta, btnBorrarReceta;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        int position;
        LinearLayout layout_btn;
        String idRec;

        public MosMacotasViewHolder(View view) {
            super(view);

            txtNombreMed = view.findViewById(R.id.txtMedicamento);
            txtveterinario = view.findViewById(R.id.txtveterinario);
            txtFechaReceta = view.findViewById(R.id.txtFechaReceta);
            txtDosis = view.findViewById(R.id.txtDosis);
            txtFrecuencia = view.findViewById(R.id.txtFrecuencia);
            txtDuracion = view.findViewById(R.id.txtDuracion);
            txtObservaciones = view.findViewById(R.id.txtObservaciones);
            btnEdReceta = view.findViewById(R.id.btnEdReceta);
            btnBorrarReceta = view.findViewById(R.id.btnBorrarReceta);

            cv = view.findViewById(R.id.cardViewM);
            layout_btn = view.findViewById(R.id.layBotones);
            // Define click listener for the ViewHolder's View

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int vw =(layout_btn.getVisibility() ==  v.GONE)? v.VISIBLE: v.GONE;
                    TransitionManager.beginDelayedTransition(layout_btn, new AutoTransition());
                    layout_btn.setVisibility(vw);

                }
            });

            view.findViewById(R.id.btnEdReceta).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(mCtx, new_receta.class);
                        intent.putExtra("idMas", llaveMas);
                        intent.putExtra("llave2", idRec);
                        mCtx.startActivity(intent);

                }
            });

            view.findViewById(R.id.btnBorrarReceta).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference registroRef = databaseRef.child("Mascotas").child(llaveMas).child("Recetas").child(idRec);
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
        mostrarRecetaList receta = recetaList.get(position);
        viewHolder.idRec = receta.getIdM();
        viewHolder.txtNombreMed.setText(receta.getMed());
        viewHolder.txtDosis.setText(receta.getDose());
        viewHolder.txtDuracion.setText(receta.getDurante());
        viewHolder.txtveterinario.setText(receta.getIdVet());
        viewHolder.txtFechaReceta.setText(receta.getDate());
        viewHolder.txtFrecuencia.setText(receta.getFreq());
        viewHolder.txtObservaciones.setText(receta.getObser());
        viewHolder.cv.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_trans));
    }

    // Return the size of your dataset (invoked by the layout manager)

}