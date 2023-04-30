package com.example.vet.clases;

public class mostrarAdop {
    String idM,nombreM,imagenM,edadM,razaM,generoM,especieM,colorM;

    public mostrarAdop(String idM, String nombreM, String imagenM, String edadM, String razaM, String generoM, String especieM, String colorM) {
        this.idM = idM;
        this.nombreM = nombreM;
        this.imagenM = imagenM;
        this.edadM = edadM;
        this.razaM = razaM;
        this.generoM = generoM;
        this.especieM = especieM;
        this.colorM = colorM;
    }

    public String getColorM() {
        return colorM;
    }

    public void setColorM(String colorM) {
        this.colorM = colorM;
    }

    public String getIdM() {
        return idM;
    }

    public void setIdM(String idM) {
        this.idM = idM;
    }

    public String getNombreM() {
        return nombreM;
    }

    public void setNombreM(String nombreM) {
        this.nombreM = nombreM;
    }

    public String getImagenM() {
        return imagenM;
    }

    public void setImagenM(String imagenM) {
        this.imagenM = imagenM;
    }

    public String getEdadM() {
        return edadM;
    }

    public void setEdadM(String edadM) {
        this.edadM = edadM;
    }

    public String getRazaM() {
        return razaM;
    }

    public void setRazaM(String razaM) {
        this.razaM = razaM;
    }

    public String getGeneroM() {
        return generoM;
    }

    public void setGeneroM(String generoM) {
        this.generoM = generoM;
    }

    public String getEspecieM() {
        return especieM;
    }

    public void setEspecieM(String especieM) {
        this.especieM = especieM;
    }
}
