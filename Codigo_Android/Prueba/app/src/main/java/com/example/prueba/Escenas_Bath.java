package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import static com.example.prueba.R.drawable.luz_abierta;

public class Escenas_Bath extends AppCompatActivity {

    ToggleButton b_bp_lp,b_bs_lp;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");
    DatabaseReference refBath, refLuces_Baño_Principal, refLuces_Baño_Secundario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenas__bath);
        refBath = refHome.child("Bath");
        refLuces_Baño_Principal = refBath.child("Luz_Princ");
        refLuces_Baño_Secundario = refBath.child("Luz_Secund");

        //Botones

        b_bp_lp = (ToggleButton) findViewById(R.id.b_bp_lp);
        b_bs_lp = (ToggleButton) findViewById(R.id.b_bs_lp);

        control_multi_luz();
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

    //Metodo para modificar la Luz Principal - Baño Principal
    public void Cambio_LP_BP(View view){
        Map<String, Object> luz_map = new HashMap<>();
        if(b_bp_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_bp_lp.setBackgroundResource(luz_abierta);
            luz_map.put("Luz_Princ", true);
        }
        else if(b_bp_lp.getBackground().getConstantState() == getResources().getDrawable(luz_abierta).getConstantState()){
            b_bp_lp.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Princ", false);
        }
        refBath.updateChildren(luz_map);
    }

    //Metodo para modificar la Luz Principal - Baño Secundario
    public void Cambio_LP_BS(View view){
        Map<String, Object> luz_map = new HashMap<>();
        if(b_bs_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_bs_lp.setBackgroundResource(luz_abierta);
            luz_map.put("Luz_Secund", true);
        }
        else if(b_bs_lp.getBackground().getConstantState() == getResources().getDrawable(luz_abierta).getConstantState()){
            b_bs_lp.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Secund", false);
        }
        refBath.updateChildren(luz_map);
    }

    //Metodo para actualizar
    public void control_multi_luz(){

        //Baño Principal Luz principal
        b_bp_lp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Baño_Principal.setValue(isChecked);
            }
        });
        refBath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_luz_principal = (Boolean) dataSnapshot.child("Luz_Princ").getValue();
                    b_bp_lp.setChecked(estado_luz_principal);
                    if(estado_luz_principal){
                        b_bp_lp.setBackgroundResource(luz_abierta);
                    }
                    else{
                       b_bp_lp.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Baño Secundario Luz Principal
        b_bs_lp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_Baño_Secundario.setValue(isChecked);
            }
        });
        refBath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_luz_principal = (Boolean) dataSnapshot.child("Luz_Secund").getValue();
                    b_bs_lp.setChecked(estado_luz_principal);
                    if(estado_luz_principal){
                        b_bs_lp.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_bs_lp.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
