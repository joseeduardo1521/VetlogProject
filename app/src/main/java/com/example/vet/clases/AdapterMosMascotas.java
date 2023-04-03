package com.example.vet.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vet.R;

import java.util.List;

public class AdapterMosMascotas extends RecyclerView.Adapter<AdapterMosMascotas.MosMacotasViewHolder> {

    private String[] localDataSet;
    private Context mCtx;
    private List<mostrarMascota> mascotaList;


    public AdapterMosMascotas(Context mCtx, List<mostrarMascota> mascotaList) {
        this.mCtx=mCtx;
        this.mascotaList = mascotaList;

    }


    public static class MosMacotasViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtRaza,txtEdad,txtEspecie, txtDueño,txtGenero;
        ImageView imgMasc;

        public MosMacotasViewHolder(View view) {
            super(view);
            txtNombre = view.findViewById(R.id.nombreTextView);
            txtRaza = view.findViewById(R.id.razaTextView);
            txtEdad = view.findViewById(R.id.edadTextView);
            txtEspecie = view.findViewById(R.id.especieTextView);
            txtDueño = view.findViewById(R.id.correoDueño);
            txtGenero = view.findViewById(R.id.generoTextView);
            imgMasc = view.findViewById(R.id.imageViewM);
            // Define click listener for the ViewHolder's View
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MosMacotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
       LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view  = inflater.inflate(R.layout.cardmascotas, null   );
        return new MosMacotasViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MosMacotasViewHolder viewHolder, final int position) {
        mostrarMascota mascota = mascotaList.get(position);
        Glide.with(mCtx)
                .load(mascota.getImagenM())
                .into(viewHolder.imgMasc);
        viewHolder.txtNombre.setText(mascota.getNombreM());
        viewHolder.txtEspecie.setText(mascota.getEspecieM());
        viewHolder.txtDueño.setText(mascota.getCorreoDueno());
        viewHolder.txtRaza.setText(mascota.getRazaM());
        viewHolder.txtEdad.setText(mascota.getEdadM());
        viewHolder.txtGenero.setText(mascota.getGeneroM());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mascotaList.size();
    }
}