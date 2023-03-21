package com.example.vet.clases;

import java.io.Serializable;

public class Especies implements Serializable {

    private String especie;
    private int image;

    public Especies() {

    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
