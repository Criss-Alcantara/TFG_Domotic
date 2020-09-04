package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import static com.example.prueba.R.drawable.boton_encendido;
import static com.example.prueba.R.drawable.luz_abierta;
import static com.example.prueba.R.drawable.persiana_abierta;
import static com.example.prueba.R.drawable.persiana_baja;
import static com.example.prueba.R.drawable.ventana_abierta;

public class Escenas_Salon extends AppCompatActivity {

    ToggleButton b_s_lp, b_s_ec;
    Button b_s_p_a, b_s_p_c, b_s_p_p;
    TextView temp_salon, hum_salon, estado_caldera;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");
    DatabaseReference refSalon, refLuces_Salon, refPersiana_Salon, refVentana_Salon, refCaldera, refEncender_Caldera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenas__salon);

        //Salon
        refSalon = refHome.child("Salon");
        refEncender_Caldera = refSalon.child("Encender_Caldera");
        refLuces_Salon = refSalon.child("Luz_Princ");
        refPersiana_Salon = refSalon.child("Persiana");
        refVentana_Salon = refSalon.child("Ventana");
        refCaldera = refSalon.child("Caldera");
        temp_salon = (TextView) findViewById(R.id.tv_grados_s);
        hum_salon = (TextView) findViewById(R.id.tv_cantidad_s);
        b_s_lp = (ToggleButton) findViewById(R.id.b_s_lp);
        b_s_ec = (ToggleButton) findViewById(R.id.b_s_ec);
        estado_caldera = (TextView) findViewById(R.id.tv_estado_caldera);

        actualizar();
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
    public void Cambio_LP_S(View view){
        Map<String, Object> luz_map = new HashMap<>();
        b_s_lp = (ToggleButton) findViewById(R.id.b_s_lp);
        if(b_s_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_s_lp.setBackgroundResource(R.drawable.luz_abierta);
            luz_map.put("Luz_Princ", true);
        }
        else if(b_s_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_abierta).getConstantState()){
            b_s_lp.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Princ", false);
        }
        refSalon.updateChildren(luz_map);
    }

    //Metodo para Aumentar la temperatura
    public void Aumento_Temperatura (View view){
        int mynum = Integer.parseInt(estado_caldera.getText().toString());
        if(mynum < 40){
            mynum = mynum + 1;
            Integer intInstance = new Integer(mynum);
            estado_caldera.setText(intInstance.toString());
            Map<String, Object> caldera_map = new HashMap<>();
            caldera_map.put("Caldera", mynum);
            refSalon.updateChildren(caldera_map);
        }
    }

    //Metodo para Disminuir la temperatura
    public void Disminuncion_Temperatura (View view){
        int mynum = Integer.parseInt(estado_caldera.getText().toString());
        if(mynum > 0){
            mynum = mynum - 1;
            Integer intInstance = new Integer(mynum);
            estado_caldera.setText(intInstance.toString());
            Map<String, Object> caldera_map = new HashMap<>();
            caldera_map.put("Caldera", mynum);
            refSalon.updateChildren(caldera_map);
        }
    }

    //Metodo para Encender o Apagar la caldera
    public void Cambio_EC_S(View view){
        Map<String, Object> caldera_map = new HashMap<>();
        b_s_ec = (ToggleButton) findViewById(R.id.b_s_ec);
        if(b_s_ec.getBackground().getConstantState() == getResources().getDrawable(R.drawable.boton_apagado).getConstantState()){
            b_s_ec.setBackgroundResource(R.drawable.boton_encendido);
            caldera_map.put("Encender_Caldera", true);
        }
        else if(b_s_ec.getBackground().getConstantState() == getResources().getDrawable(R.drawable.boton_encendido).getConstantState()){
            b_s_ec.setBackgroundResource(R.drawable.boton_apagado);
            caldera_map.put("Encender_Caldera", false);
        }
        refSalon.updateChildren(caldera_map);
    }
    //Metodo para actualizar
    public void actualizar() {
        // Comedor
        b_s_lp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Salon.setValue(isChecked);
            }
        });
        b_s_ec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refEncender_Caldera.setValue(isChecked);
            }
        });

        refSalon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_luz_principal = (Boolean) dataSnapshot.child("Luz_Princ").getValue();
                    Boolean estado_encender_caldera = (Boolean) dataSnapshot.child("Encender_Caldera").getValue();
                    int caldera = Integer.parseInt(dataSnapshot.child("Caldera").getValue().toString());
                    int temperatura = Integer.parseInt(dataSnapshot.child("Temperatura").getValue().toString());
                    int humedad = Integer.parseInt(dataSnapshot.child("Humedad").getValue().toString());
                    b_s_lp.setChecked(estado_luz_principal);
                    b_s_ec.setChecked(estado_encender_caldera);
                    if(estado_luz_principal){
                        b_s_lp.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_s_lp.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                    if(estado_encender_caldera){
                        b_s_ec.setBackgroundResource(R.drawable.boton_encendido);
                    }
                    else{
                        b_s_ec.setBackgroundResource(R.drawable.boton_apagado);
                    }
                    estado_caldera.setText(""+caldera);
                    temp_salon.setText(""+temperatura);
                    hum_salon.setText(""+humedad);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
