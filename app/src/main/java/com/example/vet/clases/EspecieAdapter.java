package com.example.vet.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.vet.R;

import java.util.List;

public class EspecieAdapter extends BaseAdapter {
    private Context context;
    private List<Especies> especiesList;

    public EspecieAdapter(Context context, List<Especies> especiesList) {
        this.context = context;
        this.especiesList = especiesList;
    }

    @Override
    public int getCount() {
        return especiesList != null ? especiesList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_especie, viewGroup, false);

        TextView txtName = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        txtName.setText(especiesList.get(i).getEspecie());
        image.setImageResource(especiesList.get(i).getImage());

        return rootView;
    }
}
