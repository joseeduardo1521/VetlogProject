package com.example.vet.clases;

public class mostrarCamList {
    String idC, nomca, inidate, findate, loc, esp, nota;

    public mostrarCamList(String idC, String nomca , String inidate, String findate, String loc, String esp, String nota) {
        this.idC = idC;
        this.nomca = nomca;
        this.inidate = inidate;
        this.findate = findate;
        this.loc = loc;
        this.esp = esp;
        this.nota = nota;
    }

    public String getIdC() {
        return idC;
    }

    public String getNomca() {
        return nomca;
    }

    public String getInidate() {
        return inidate;
    }

    public String getFindate() {
        return findate;
    }

    public String getLoc() {
        return loc;
    }

    public String getEsp() {
        return esp;
    }

    public String getNota() {
        return nota;
    }
}