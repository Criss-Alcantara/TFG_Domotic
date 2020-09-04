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

import static com.example.prueba.R.drawable.luz_abierta;
import static com.example.prueba.R.drawable.persiana_abierta;
import static com.example.prueba.R.drawable.persiana_baja;
import static com.example.prueba.R.drawable.ventana_abierta;

public class Dormitorio_Secundario extends AppCompatActivity {

    ToggleButton b_d_s_lp;
    Button b_d_s_p_a, b_d_s_p_c, b_d_s_p_p;
    TextView temp_dor_sec, hum_dor_sec;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");
    DatabaseReference refDormitorioSecundario, refLuces_DS, refPersiana_DS, refVentana_DS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitorio__secundario);

        //Dormitorio Secundario
        refDormitorioSecundario = refHome.child("Dormitorio").child("Hab_Secund");
        refLuces_DS = refDormitorioSecundario.child("Luz_Princ");
        refPersiana_DS = refDormitorioSecundario.child("Persiana");
        refVentana_DS = refDormitorioSecundario.child("Ventana");
        temp_dor_sec = (TextView) findViewById(R.id.tv_grados_d_s);
        hum_dor_sec = (TextView) findViewById(R.id.tv_cantidad_d_s);
        b_d_s_lp = (ToggleButton) findViewById(R.id.b_d_s_lp);
        b_d_s_p_a = (Button) findViewById(R.id.b_d_s_p_a);
        b_d_s_p_c = (Button) findViewById(R.id.b_d_s_p_c);
        b_d_s_p_p = (Button) findViewById(R.id.b_d_s_p_p);

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
    public void Cambio_LP_D_S(View view){
        Map<String, Object> luz_map = new HashMap<>();
        b_d_s_lp = (ToggleButton) findViewById(R.id.b_d_s_lp);
        if(b_d_s_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_cerrada).getConstantState()){
            b_d_s_lp.setBackgroundResource(R.drawable.luz_abierta);
            luz_map.put("Luz_Princ", true);
        }
        else if(b_d_s_lp.getBackground().getConstantState() == getResources().getDrawable(R.drawable.luz_abierta).getConstantState()){
            b_d_s_lp.setBackgroundResource(R.drawable.luz_cerrada);
            luz_map.put("Luz_Princ", false);
        }
        refDormitorioSecundario.updateChildren(luz_map);
    }

    //Metodo para abrir la persiana
    public void Cambio_P_D_P_A(View view){
        Map<String, Object> persiana_map = new HashMap<>();
        persiana_map.put("Persiana_Abrir", true);
        persiana_map.put("Persiana_Cerrar", false);
        persiana_map.put("Persiana_Parar", false);
        refDormitorioSecundario.updateChildren(persiana_map);
    }

    //Metodo para cerrar la persiana
    public void Cambio_P_D_P_C(View view){
        Map<String, Object> persiana_map = new HashMap<>();
        persiana_map.put("Persiana_Abrir", false);
        persiana_map.put("Persiana_Cerrar", true);
        persiana_map.put("Persiana_Parar", false);
        refDormitorioSecundario.updateChildren(persiana_map);
    }

    //Metodo para parar la persiana
    public void Cambio_P_D_P_P(View view){
        Map<String, Object> persiana_map = new HashMap<>();
        persiana_map.put("Persiana_Abrir", false);
        persiana_map.put("Persiana_Cerrar", false);
        persiana_map.put("Persiana_Parar", true);
        refDormitorioSecundario.updateChildren(persiana_map);
    }

    //Metodo para actualizar
    public void actualizacion() {
        // Dormitorio Principal
        b_d_s_lp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLuces_DS.setValue(isChecked);
            }
        });

        refDormitorioSecundario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean estado_luz_principal = (Boolean) dataSnapshot.child("Luz_Princ").getValue();
                    int temperatura = Integer.parseInt(dataSnapshot.child("Temperatura").getValue().toString());
                    int humedad = Integer.parseInt(dataSnapshot.child("Humedad").getValue().toString());
                    b_d_s_lp.setChecked(estado_luz_principal);
                    if(estado_luz_principal){
                        b_d_s_lp.setBackgroundResource(luz_abierta);
                    }
                    else{
                        b_d_s_lp.setBackgroundResource(R.drawable.luz_cerrada);
                    }
                    temp_dor_sec.setText(""+temperatura);
                    hum_dor_sec.setText(""+humedad);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
