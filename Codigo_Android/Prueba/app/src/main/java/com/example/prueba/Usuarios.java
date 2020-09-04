package com.example.prueba;

public class Usuarios {
    private String pass;
    private String house;
    private String id;


    public Usuarios(){

    }

    public Usuarios(String id, String pass, String house) {
        this.id  = id;
        this.pass = pass;
        this.house = house;
    }

    public String getId() {
        return id ;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }
}
