package com.example.vet.clases;

public class mostrarqrList {
    String idH, idMas, lugar, fech_in;

    public mostrarqrList(String idH, String idMas, String lugar, String fech_in) {
        this.idH = idH;
        this.idMas = idMas;
        this.lugar = lugar;
        this.fech_in = fech_in;
    }


    public String getIdH() {
        return idH;
    }

    public String getIdMas() {
        return idMas;
    }

    public String getLugar() {
        return lugar;
    }

    public String getFech_in() {
        return fech_in;
    }
}
