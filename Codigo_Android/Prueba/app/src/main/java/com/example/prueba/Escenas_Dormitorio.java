package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class Escenas_Dormitorio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenas__dormitorio);
    }

    //Metodo para ir a Main
    public  void Siguiente_Main(View view){
        Intent siguiente_main = new Intent(this, Modificar_Datos.class);
        startActivity(siguiente_main);
    }

    //Metodo para ir a Escena
    public  void Siguiente_Escena(View view){
        Intent siguiente_escena = new Intent(this, Escenarios.class);
        startActivity(siguiente_escena);
    }

    //Metodo el boton Cerradura
    public  void Siguiente_Cerradura(View view){
        Intent siguiente_cerradura = new Intent(this, Cerraduras.class);
        startActivity(siguiente_cerradura);
    }

    //Metodo el boton Alarma
    public  void Siguiente_Alarma(View view){
        Intent siguiente_alarma = new Intent(this, Alarmas.class);
        startActivity(siguiente_alarma);
    }

    //Metodo para ir a Habitacion Principal
    public  void Siguiente_HP(View view){
        Intent siguiente_main = new Intent(this, Dormitorio_Principal.class);
        startActivity(siguiente_main);
    }

    //Metodo para ir a Habitacion Secundaria
    public  void Siguiente_HS(View view){
        Intent siguiente_escena = new Intent(this, Dormitorio_Secundario.class);
        startActivity(siguiente_escena);
    }

    //Metodo el boton Habitacion Tercera
    public  void Siguiente_HT(View view){
        Intent siguiente_cerradura = new Intent(this, Dormitorio_Tercero.class);
        startActivity(siguiente_cerradura);
    }

    //Metodo el boton Habitacion Cuarta
    public  void Siguiente_HC(View view){
        Intent siguiente_alarma = new Intent(this, Dormitorio_Cuarto.class);
        startActivity(siguiente_alarma);
    }
}
