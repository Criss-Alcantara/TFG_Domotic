package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class Escenarios extends AppCompatActivity {

    private Context thisContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenarios);

        //startService(new Intent(thisContext, ServiceAlerta.class));
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Main(View view){
        Intent siguiente_main = new Intent(this, Modificar_Datos.class);
        startActivity(siguiente_main);
    }

    public  void Siguiente_Cerradura(View view){
        Intent siguiente_cerradura = new Intent(this, Cerraduras.class);
        startActivity(siguiente_cerradura);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Alarma(View view){
        Intent siguiente_alarma = new Intent(this, Alarmas.class);
        startActivity(siguiente_alarma);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Dormitorio(View view){
        Intent siguiente_dormitorio = new Intent(this, Escenas_Dormitorio.class);
        startActivity(siguiente_dormitorio);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Cocina(View view){
        Intent siguiente_cocina = new Intent(this, Escenas_Cocina.class);
        startActivity(siguiente_cocina);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Baños(View view){
        Intent siguiente_baños = new Intent(this, Escenas_Bath.class);
        startActivity(siguiente_baños);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Comedor(View view){
        Intent siguiente_comedor = new Intent(this, Escenas_Comedor.class);
        startActivity(siguiente_comedor);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Salon(View view){
        Intent siguiente_salon = new Intent(this, Escenas_Salon.class);
        startActivity(siguiente_salon);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Pasillo(View view){
        Intent siguiente_pasillo = new Intent(this, Escenas_Pasillo.class);
        startActivity(siguiente_pasillo);
    }
}
