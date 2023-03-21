package com.example.vet.clases;

import com.example.vet.R;

import java.util.ArrayList;
import java.util.List;

public class DataEspecies {

    public static List<Especies> getEspecieList() {
            List<Especies> especiesList = new ArrayList<>();

            Especies Canino = new Especies();
            Canino.setEspecie("Perro");
            Canino.setImage(R.drawable.perro);
            especiesList.add(Canino);

            Especies Felino = new Especies();
            Felino.setEspecie("Gato");
            Felino.setImage(R.drawable.gato);
            especiesList.add(Felino);

            Especies Ave = new Especies();
            Ave.setEspecie("Ave");
            Ave.setImage(R.drawable.ave);
            especiesList.add(Ave);

            Especies Roedor = new Especies();
            Roedor.setEspecie("Roedor");
            Roedor.setImage(R.drawable.roedor);
            especiesList.add(Roedor);

            Especies Reptil  = new Especies();
            Reptil.setEspecie("Reptil");
            Reptil.setImage(R.drawable.reptil);
            especiesList.add(Reptil);

            return especiesList;
    }
}
