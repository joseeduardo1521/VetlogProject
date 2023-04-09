package com.example.vet.clases;

public class mostrarRecetaList {
    String idR, idVet, date, Med, Dose, Freq, Durante, Obser;

    public mostrarRecetaList(String idR, String idVet, String date, String Med, String Dose, String Freq, String Durante, String Obser) {
        this.idR = idR;
        this.idVet = idVet;
        this.date = date;
        this.Med = Med;
        this.Dose = Dose;
        this.Freq = Freq;
        this.Durante = Durante;
        this.Obser = Obser;

    }

    public String getIdM() {
        return idR;
    }

    public String getIdVet() {
        return idVet;
    }

    public String getDate() {
        return date;
    }

    public String getMed() {
        return Med;
    }

    public String getDose() {
        return Dose;
    }

    public String getFreq() {
        return Freq;
    }

    public String getDurante() {
        return Durante;
    }

    public String getObser() {
        return Obser;
    }
}