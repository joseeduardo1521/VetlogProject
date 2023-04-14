package com.example.vet.clases;

public class Habitat {
    private String uid;
    private String lugar;

    public Habitat() {
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }


    @Override
    public String toString() {
        return lugar;
    }

}
