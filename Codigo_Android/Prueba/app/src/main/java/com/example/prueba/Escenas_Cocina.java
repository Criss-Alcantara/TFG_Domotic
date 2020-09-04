package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.prueba.R.drawable.luz_abierta;

public class Escenas_Cocina extends AppCompatActivity {

    ToggleButton b_c_lp,b_t_lp;
    TextView temp_cocina, hum_cocina, temp_terraza, hum_terraza;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");
    DatabaseReference refCocina, refLuces_Cocina_Principal, refTerraza, refLuces_Terraza_Principal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenas__cocina);

        //Cocina
        refCocina = refHome.child("Cocina");
        refLuces_Cocina_Principal = refCocina.child("Luz_Princ");
        temp_cocina = (TextView) findViewById(R.id.tv_grados_c);
        hum_cocina = (TextView) findViewById(R.id.tv_cantidad_c);
        b_c_lp = (ToggleButton) findViewById(R.id.b_c_lp);


        //Terraza
        refTerraza = refHome.child("Terraza");
        refLuces_Terraza_Principal = refTerraza.child("Luz_Princ");
        temp_terraza = (TextView) findViewById(R.id.tv_grados_t);
        hum_terraza = (TextView) findViewById(R.id.tv_cantidad_t);
        b_t_lp = (ToggleButton) findViewById(R.id.b_t_lp);

       actualizacion();

    }

    //Metodo para Modificar Datos
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

    //Metodo para modificar la Luz Principal Cocina
    public void Cambio_LP_C(View view){
        Map<String, Object> luz_map = new HashMap<>();
        if(b_c_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_c_lp.setBackgroundResource(R.drawable.luz_abierta);
            luz_map.put("Luz_Princ", true);
        }
        else if(b_c_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_abierta).getConstantState()){
            b_c_lp.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Princ", false);
        }
        refCocina.updateChildren(luz_map);
    }

    //Metodo para modificar la Luz Principal Terraza
    public void Cambio_LP_T(View view){
        Map<String, Object> luz_map = new HashMap<>();
        if(b_t_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_t_lp.setBackgroundResource(R.drawable.luz_abierta);
            luz_map.put("Luz_Princ", true);
        }
        else if(b_t_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_abierta).getConstantState()){
            b_t_lp.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Princ", false);
        }
        refTerraza.updateChildren(luz_map);

    }

    //Metodo para actualizar
    public void actualizacion() {
        // Cocina
        b_c_lp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Cocina_Principal.setValue(isChecked);
            }
        });
        refCocina.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_luz_principal = (Boolean) dataSnapshot.child("Luz_Princ").getValue();
                    int temperatura = Integer.parseInt(dataSnapshot.child("Temperatura").getValue().toString());
                    int humedad = Integer.parseInt(dataSnapshot.child("Humedad").getValue().toString());
                    b_c_lp.setChecked(estado_luz_principal);
                    if(estado_luz_principal){
                        b_c_lp.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_c_lp.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                    hum_cocina.setText(""+humedad);
                    temp_cocina.setText(""+temperatura);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Terraza
        b_t_lp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Terraza_Principal.setValue(isChecked);
            }
        });
        refTerraza.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_luz_principal = (Boolean) dataSnapshot.child("Luz_Princ").getValue();
                    int temperatura = Integer.parseInt(dataSnapshot.child("Temperatura").getValue().toString());
                    int humedad = Integer.parseInt(dataSnapshot.child("Humedad").getValue().toString());
                    b_t_lp.setChecked(estado_luz_principal);
                    if(estado_luz_principal){
                        b_t_lp.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_t_lp.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                    temp_terraza.setText(""+temperatura);
                    hum_terraza.setText(""+humedad);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
