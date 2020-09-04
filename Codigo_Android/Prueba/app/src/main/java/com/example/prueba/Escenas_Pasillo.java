package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.prueba.R.drawable.luz_abierta;
import static com.example.prueba.R.drawable.persiana_abierta;
import static com.example.prueba.R.drawable.persiana_baja;
import static com.example.prueba.R.drawable.ventana_abierta;

public class Escenas_Pasillo extends AppCompatActivity {

    ToggleButton b_p_lp, b_p_ls, b_p_lh;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");
    DatabaseReference refPasillo, refLuces_Puerta, refLuces_Baño, refLuces_Habitaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenas__pasillo);

        //Comedor
        refPasillo = refHome.child("Pasillo");
        refLuces_Puerta = refPasillo.child("Luz_Princ");
        refLuces_Baño = refPasillo.child("Luz_Secund");
        refLuces_Habitaciones = refPasillo.child("Luz_Tercera");
        b_p_lp = (ToggleButton) findViewById(R.id.b_p_lp);
        b_p_ls = (ToggleButton) findViewById(R.id.b_p_ls);
        b_p_lh = (ToggleButton) findViewById(R.id.b_p_lh);

        actualizacion();
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

    //Metodo para modificar la Luz Principal
    public void Cambio_LP_P(View view){
        Map<String, Object> luz_map = new HashMap<>();
        b_p_lp = (ToggleButton) findViewById(R.id.b_p_lp);
        if(b_p_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_p_lp.setBackgroundResource(R.drawable.luz_abierta);
            luz_map.put("Luz_Princ", true);
        }
        else if(b_p_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_abierta).getConstantState()){
            b_p_lp.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Princ", false);
        }
        refPasillo.updateChildren(luz_map);
    }

    //Metodo para modificar la Luz Secundaria
    public void Cambio_LS_P(View view){
        Map<String, Object> luz_map = new HashMap<>();
        b_p_ls = (ToggleButton) findViewById(R.id.b_p_ls);
        if(b_p_ls.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_p_ls.setBackgroundResource(R.drawable.luz_abierta);
            luz_map.put("Luz_Secund", true);
        }
        else if(b_p_ls.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_abierta).getConstantState()){
            b_p_ls.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Secund", false);
        }
        refPasillo.updateChildren(luz_map);
    }

    //Metodo para modificar la Luz Habitaciones
    public void Cambio_LH_P(View view){
        Map<String, Object> luz_map = new HashMap<>();
        b_p_lh = (ToggleButton) findViewById(R.id.b_p_lh);
        if(b_p_lh.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_p_lh.setBackgroundResource(R.drawable.luz_abierta);
            luz_map.put("Luz_Tercera", true);
        }
        else if(b_p_lh.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_abierta).getConstantState()){
            b_p_lh.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Tercera", false);
        }
        refPasillo.updateChildren(luz_map);
    }

    //Metodo para actualizar
    public void actualizacion(){
        // Pasillo
        b_p_lp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Puerta.setValue(isChecked);
            }
        });
        b_p_ls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Baño.setValue(isChecked);
            }
        });
        b_p_lh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Habitaciones.setValue(isChecked);
            }
        });

        refPasillo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_luz_principal = (Boolean) dataSnapshot.child("Luz_Princ").getValue();
                    Boolean estado_luz_secundaria = (Boolean) dataSnapshot.child("Luz_Secund").getValue();
                    Boolean estado_luz_habitaciones = (Boolean) dataSnapshot.child("Luz_Tercera").getValue();

                    b_p_lp.setChecked(estado_luz_principal);
                    b_p_ls.setChecked(estado_luz_secundaria);
                    b_p_lh.setChecked(estado_luz_habitaciones);
                    if(estado_luz_principal){
                        b_p_lp.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_p_lp.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                    if(estado_luz_secundaria){
                        b_p_ls.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_p_ls.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                    if(estado_luz_habitaciones){
                        b_p_lh.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_p_lh.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

