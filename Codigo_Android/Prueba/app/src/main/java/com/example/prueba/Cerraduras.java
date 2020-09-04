package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Cerraduras extends AppCompatActivity {

    TextView estado_pp,estado_pt,estado_vc;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");
    DatabaseReference refCerraduras, ref_PP, ref_PT, ref_VC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerraduras);

        //Dormitorio Principal
        refCerraduras = refHome.child("Cerraduras");
        ref_PP = refCerraduras.child("Puerta_Principal");
        ref_PT = refCerraduras.child("Puerta_Terraza");
        ref_VC = refCerraduras.child("Ventana_Comedor");
        estado_pp = (TextView) findViewById(R.id.tv_estado_cerradura_pp);
        estado_pt = (TextView) findViewById(R.id.tv_estado_cerradura_pt);
        estado_vc = (TextView) findViewById(R.id.tv_estado_cerradura_vc);

        actualizacion();
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Modificar(View view){
        Intent siguiente_main = new Intent(this, Modificar_Datos.class);
        startActivity(siguiente_main);
    }

    public  void Siguiente_Escena(View view){
        Intent siguiente_escena = new Intent(this, Escenarios.class);
        startActivity(siguiente_escena);
    }

    //Metodo el boton Siguiente
    public  void Siguiente_Alarma(View view){
        Intent siguiente_alarma = new Intent(this, Alarmas.class);
        startActivity(siguiente_alarma);
    }

    //Puerta Principal
    public void Cambio_Estado_Cerrado_PP(View view){
        Map<String, Object> estado_PP = new HashMap<>();
        estado_pp.setText("Cerrado");
        estado_pp.setTextColor(Color.GREEN);
        estado_PP.put("Puerta_Principal", true);
        refCerraduras.updateChildren(estado_PP);
    }

    public void Cambio_Estado_Abierto_PP(View view){
        Map<String, Object> estado_PP = new HashMap<>();
        estado_pp.setText("Abierto");
        estado_pp.setTextColor(Color.RED);
        estado_PP.put("Puerta_Principal", false);
        refCerraduras.updateChildren(estado_PP);
    }

    //Puerta Terraza
    public void Cambio_Estado_Cerrado_PT(View view){
        Map<String, Object> estado_PT = new HashMap<>();
        estado_pt.setText("Cerrado");
        estado_pt.setTextColor(Color.GREEN);
        estado_PT.put("Puerta_Terraza", true);
        refCerraduras.updateChildren(estado_PT);
    }

    public void Cambio_Estado_Abierto_PT(View view){
        Map<String, Object> estado_PT = new HashMap<>();
        estado_pt.setText("Abierto");
        estado_pt.setTextColor(Color.RED);
        estado_PT.put("Puerta_Terraza", false);
        refCerraduras.updateChildren(estado_PT);
    }

    //Ventana Comedor
    public void Cambio_Estado_Cerrado_VC(View view){
        Map<String, Object> estado_VC = new HashMap<>();
        estado_vc.setText("Cerrado");
        estado_vc.setTextColor(Color.GREEN);
        estado_VC.put("Ventana_Comedor", true);
        refCerraduras.updateChildren(estado_VC);
    }

    public void Cambio_Estado_Abierto_VC(View view){
        Map<String, Object> estado_VC = new HashMap<>();
        estado_vc.setText("Abierto");
        estado_vc.setTextColor(Color.RED);
        estado_VC.put("Ventana_Comedor", false);
        refCerraduras.updateChildren(estado_VC);
    }

    //Actualizacion
    public void actualizacion() {
        // Cerraduras
        refCerraduras.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_puerta_principal = (Boolean) dataSnapshot.child("Puerta_Principal").getValue();
                    Boolean estado_puerta_terraza = (Boolean) dataSnapshot.child("Puerta_Terraza").getValue();
                    Boolean estado_ventana_comedor = (Boolean) dataSnapshot.child("Ventana_Comedor").getValue();
                    if(estado_puerta_principal){
                        estado_pp.setText("Cerrado");
                        estado_pp.setTextColor(Color.GREEN);
                    }
                    else{
                        estado_pp.setText("Abierto");
                        estado_pp.setTextColor(Color.RED);
                    }
                    if(estado_puerta_terraza){
                        estado_pt.setText("Cerrado");
                        estado_pt.setTextColor(Color.GREEN);
                    }
                    else{
                        estado_pt.setText("Abierto");
                        estado_pt.setTextColor(Color.RED);
                    }
                    if(estado_ventana_comedor){
                        estado_vc.setText("Cerrado");
                        estado_vc.setTextColor(Color.GREEN);
                    }
                    else{
                        estado_vc.setText("Abierto");
                        estado_vc.setTextColor(Color.RED);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
