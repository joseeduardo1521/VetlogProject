package com.example.vet.clases;

public class mostrarMascota {
    String idM,nombreM,imagenM,edadM,razaM,generoM,especieM, correoDueno;

    public mostrarMascota(String idM, String nombreM, String imagenM, String edadM, String razaM, String generoM, String especieM, String correoDueno) {
        this.idM = idM;
        this.nombreM = nombreM;
        this.imagenM = imagenM;
        this.edadM = edadM;
        this.razaM = razaM;
        this.generoM = generoM;
        this.especieM = especieM;
        this.correoDueno = correoDueno;
    }

    public String getIdM() {
        return idM;
    }

    public String getNombreM() {
        return nombreM;
    }

    public String getImagenM() {
        return imagenM;
    }

    public String getEdadM() {
        return edadM;
    }

    public String getRazaM() {
        return razaM;
    }

    public String getGeneroM() {
        return generoM;
    }

    public String getEspecieM() {
        return especieM;
    }

    public String getCorreoDueno() {
        return correoDueno;
    }
}