package com.example.vet.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vet.CrearVacuna;
import com.example.vet.R;
import com.example.vet.clases.mostrarCamList;
import com.example.vet.clases.mostrarqrList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

public class AdapterMosQr extends RecyclerView.Adapter<AdapterMosQr.MosMacotasViewHolder> {

    private LayoutInflater mInflater;
    private Context mCtx;
    private List<mostrarqrList> qrlist;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public int getItemCount() {
        return qrlist.size();
    }

    public AdapterMosQr(Context mCtx, List<mostrarqrList> qrlist) {
        this.mCtx=mCtx;
        this.qrlist = qrlist;
        this.mInflater = LayoutInflater.from(mCtx);
    }


    public class MosMacotasViewHolder extends RecyclerView.ViewHolder {
        TextView txthabitaculo, txtMascota, txtFechaIntern;
        CardView cv;
        Button  btnBorrarCamp;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        int position;
        LinearLayout layout_btn;
        LinearLayout layb;
        String idHab;
        ImageView imgQr;

        public MosMacotasViewHolder(View view) {
            super(view);

            txthabitaculo = view.findViewById(R.id.txthabitaculo);
            layb = view.findViewById(R.id.layBtn);
            txtMascota = view.findViewById(R.id.txtMascota);
            txtFechaIntern = view.findViewById(R.id.txtFechaIntern);
            btnBorrarCamp = view.findViewById(R.id.btnBorrarQr);
            imgQr =  view.findViewById(R.id.qrCode);
            cv = view.findViewById(R.id.carQr);
            layout_btn = view.findViewById(R.id.layBotones);
            // Define click listener for the ViewHolder's View

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth = FirebaseAuth.getInstance();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    int vw =(layout_btn.getVisibility() ==  v.GONE)? v.VISIBLE: v.GONE;
                    TransitionManager.beginDelayedTransition(layout_btn, new AutoTransition());
                    layout_btn.setVisibility(vw);
                    String id= mAuth.getCurrentUser().getUid();
                    mDatabase.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String lvl;
                                lvl = snapshot.child("lvl").getValue().toString();
                                if (lvl.equals("1")){
                                    btnBorrarCamp.setVisibility(View.VISIBLE);
                                    layb.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
            });

            view.findViewById(R.id.btnBorrarQr).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference registroRef = databaseRef.child("habitaculo").child(idHab);
                    registroRef.removeValue();
                    qrlist.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MosMacotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view  = mInflater.from(viewGroup.getContext()).inflate(R.layout.cardqr, viewGroup,false);
        return new MosMacotasViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MosMacotasViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        mostrarqrList qr = qrlist.get(position);
        viewHolder.idHab = qr.getIdH();
        viewHolder.txthabitaculo.setText(qr.getLugar());
        viewHolder.txtFechaIntern.setText("Fecha de ingreso: "+qr.getFech_in());
        String mas = qr.getIdMas();
        Toast.makeText(mCtx, mas, Toast.LENGTH_SHORT).show();
        if(mas.equals("")) {
            viewHolder.txtMascota.setText("Sin mascota en habitaculo");
        }
        else {
            getPetName(qr.getIdMas(), viewHolder);
        }
        try {
            // Oculta el teclado
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qr.getLugar(), BarcodeFormat.QR_CODE, 750,750);
            viewHolder.imgQr.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }

        viewHolder.cv.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_trans));
    }

    private void getPetName(String idPet, MosMacotasViewHolder viewHolder){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Mascotas").child(idPet).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nomMas = snapshot.child("name").getValue().toString();
                    viewHolder.txtMascota.setText(nomMas);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // Return the size of your dataset (invoked by the layout manager)

}