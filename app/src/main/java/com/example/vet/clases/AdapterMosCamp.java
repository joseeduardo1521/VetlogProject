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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vet.CrearVacuna;
import com.example.vet.R;
import com.example.vet.new_receta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class AdapterMosCamp extends RecyclerView.Adapter<AdapterMosCamp.MosMacotasViewHolder> {

    private LayoutInflater mInflater;
    private Context mCtx;
    private List<mostrarCamList> camLists;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    public int getItemCount() {
        return camLists.size();
    }

    public AdapterMosCamp(Context mCtx, List<mostrarCamList> camLists) {
        this.mCtx=mCtx;
        this.camLists = camLists;
        this.mInflater = LayoutInflater.from(mCtx);
    }


    public class MosMacotasViewHolder extends RecyclerView.ViewHolder {
        TextView txtCampaign_name;
        TextView txtCampaign_dates;
        TextView txtCampaign_location;
        TextView txtCampaign_species;
        TextView txtCampaign_additional_notes;
        CardView cv;
        Button btnEdCamp, btnBorrarCamp;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        int position;
        RelativeLayout layout_btn;
        String idCam;

        public MosMacotasViewHolder(View view) {
            super(view);

            txtCampaign_name = view.findViewById(R.id.campaign_name);
            txtCampaign_dates = view.findViewById(R.id.campaign_dates);
            txtCampaign_location = view.findViewById(R.id.campaign_location);
            txtCampaign_species = view.findViewById(R.id.campaign_species);
            txtCampaign_additional_notes = view.findViewById(R.id.campaign_additional_notes);
            btnEdCamp = view.findViewById(R.id.btnEdCamp);
            btnBorrarCamp = view.findViewById(R.id.btnBorrarCamp);


            cv = view.findViewById(R.id.cardCamp);
            layout_btn = view.findViewById(R.id.layBotones);
            // Define click listener for the ViewHolder's View

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int vw =(layout_btn.getVisibility() ==  v.GONE)? v.VISIBLE: v.GONE;
                    TransitionManager.beginDelayedTransition(layout_btn, new AutoTransition());
                    layout_btn.setVisibility(vw);

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
                                    btnBorrarCamp.setVisibility(View.VISIBLE);
                                    btnEdCamp.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
            });

            view.findViewById(R.id.btnEdCamp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(mCtx, CrearVacuna.class);
                        intent.putExtra("llaveCam", idCam);
                        mCtx.startActivity(intent);
                }
            });

            view.findViewById(R.id.btnBorrarCamp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference registroRef = databaseRef.child("Campains").child(idCam);
                    registroRef.removeValue();
                    camLists.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MosMacotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view  = mInflater.from(viewGroup.getContext()).inflate(R.layout.cardcamp, viewGroup,false   );
        return new MosMacotasViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MosMacotasViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        mostrarCamList campa = camLists.get(position);
        viewHolder.idCam = campa.getIdC();
        viewHolder.txtCampaign_name.setText(campa.getNomca());
        viewHolder.txtCampaign_dates.setText(campa.getInidate()+" --- "+campa.getFindate());
        viewHolder.txtCampaign_location.setText(campa.getLoc());
        viewHolder.txtCampaign_species.setText(campa.getEsp());
        viewHolder.txtCampaign_additional_notes.setText(campa.getNota());
        viewHolder.cv.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_trans));
    }

    // Return the size of your dataset (invoked by the layout manager)

}