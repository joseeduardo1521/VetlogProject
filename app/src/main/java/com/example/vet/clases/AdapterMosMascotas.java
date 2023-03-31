package com.example.vet.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
            // Define click listener for the ViewHolder's View


        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MosMacotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardmascotas, viewGroup, false);
        return new MosMacotasViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MosMacotasViewHolder viewHolder, final int position) {
        mostrarMascota mascota = mascotaList.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mascotaList.size();
    }
}