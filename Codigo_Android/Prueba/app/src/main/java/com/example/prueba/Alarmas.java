package com.example.prueba;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

public class Alarmas extends AppCompatActivity {


    NotificationCompat.Builder mBuilder;
    int Noti_PP = 912992;
    int Noti_PT = 523340;
    int Noti_VC = 864658;
    int Noti_SF = 280199;
    int Noti_SG = 908328;
    int Noti_SH = 694758;
    int Noti_SL = 654487;


    boolean Activacion_PP = false;
    boolean Activacion_PT = false;
    boolean Activacion_VC = false;

    String channelId = "my_channel_01";

    private PendingIntent pendingIntent;

    //Button b_a_state;
    TextView tv_estado_alarma_pp,tv_estado_alarma_pt,tv_estado_alarma_vc,tv_estado_alarma_sf,tv_estado_alarma_sg,tv_estado_alarma_sh,tv_estado_alarma_sl;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);

        tv_estado_alarma_pp = (TextView) findViewById(R.id.tv_estado_alarma_pp);
        tv_estado_alarma_pt = (TextView) findViewById(R.id.tv_estado_alarma_pt);
        tv_estado_alarma_vc = (TextView) findViewById(R.id.tv_estado_alarma_vc);
        tv_estado_alarma_sf = (TextView) findViewById(R.id.tv_estado_alarma_sf);
        tv_estado_alarma_sg = (TextView) findViewById(R.id.tv_estado_alarma_sg);
        tv_estado_alarma_sh = (TextView) findViewById(R.id.tv_estado_alarma_sh);
        tv_estado_alarma_sl = (TextView) findViewById(R.id.tv_estado_alarma_sl);

        setPendingIntent();
        detectar_emergencia();

    }

    //Metodo el boton Siguiente
    public  void Siguiente_Modificar(View view){
        Intent siguiente_main = new Intent(this, Modificar_Datos.class);
        startActivity(siguiente_main);
    }

    //Metodo para ir a Escenas
    public  void Siguiente_Escena(View view){
        Intent siguiente_escena = new Intent(this, Escenarios.class);
        startActivity(siguiente_escena);
    }

    //Metodo para ir a Cerradura
    public  void Siguiente_Cerradura(View view){
        Intent siguiente_cerradura = new Intent(this, Cerraduras.class);
        startActivity(siguiente_cerradura);
    }

    //Metodo para Crear Notificacion
    public void createNotification(String dato, int id_noti){

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this, null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name =  "Emergencia";
            String description = "Comunicacion de emergencias a usuarios";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,100});
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder = new NotificationCompat.Builder(this, channelId);
        }

        mBuilder.setSmallIcon(R.drawable.ic_sms_failed_black_24dp);
        mBuilder.setTicker("Ticker");
        mBuilder.setContentTitle("Notificacion Emergencia");
        mBuilder.setContentText(dato);
        mBuilder.setAutoCancel(true);
        mBuilder.setColor(Color.RED);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setLights(Color.MAGENTA, 1000, 1000);
        mBuilder.setVibrate(new long[]{1000,1000,1000,1000});
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setContentIntent(pendingIntent);
        //mBuilder.setOnlyAlertOnce(true);
        //mBuilder.setChannelId(channelId);

        mNotificationManager.notify(id_noti, mBuilder.build());
    }

    //Metodo para Detectar Emergencia
    public void detectar_emergencia(){
       refHome.child("Cerraduras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){
                    Boolean p_principal = (Boolean) dataSnapshot.child("Puerta_Principal").getValue();
                    Boolean p_terraza = (Boolean) dataSnapshot.child("Puerta_Terraza").getValue();
                    Boolean v_comedor = (Boolean) dataSnapshot.child("Ventana_Comedor").getValue();
                    if(p_principal){
                        Activacion_PP = true;
                    }
                    else{
                        Activacion_PP = false;
                    }
                    if(p_terraza){
                        Activacion_PT = true;
                    }
                    else{
                        Activacion_PT = false;
                    }
                    if(v_comedor){
                        Activacion_VC = true;
                    }
                    else{
                        Activacion_VC = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        refHome.child("Alarma").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Boolean p_principal = (Boolean) dataSnapshot.child("Cerraduras").child("P_Principal").getValue();
                    Boolean estado_pp = (Boolean) dataSnapshot.child("Cerraduras").child("Estado_PP").getValue();

                    Boolean p_terraza = (Boolean) dataSnapshot.child("Cerraduras").child("P_Terraza").getValue();
                    Boolean estado_pt = (Boolean) dataSnapshot.child("Cerraduras").child("Estado_PT").getValue();

                    Boolean v_comedor = (Boolean) dataSnapshot.child("Cerraduras").child("V_Comedor").getValue();
                    Boolean estado_vc = (Boolean) dataSnapshot.child("Cerraduras").child("Estado_VC").getValue();

                    Boolean s_fuego = (Boolean) dataSnapshot.child("Sensores").child("S_Fuego").getValue();
                    Boolean estado_sf = (Boolean) dataSnapshot.child("Sensores").child("Estado_SF").getValue();

                    Boolean s_gas = (Boolean) dataSnapshot.child("Sensores").child("S_Gas").getValue();
                    Boolean estado_sg = (Boolean) dataSnapshot.child("Sensores").child("Estado_SG").getValue();

                    Boolean s_humo = (Boolean) dataSnapshot.child("Sensores").child("S_Humo").getValue();
                    Boolean estado_sh = (Boolean) dataSnapshot.child("Sensores").child("Estado_SH").getValue();

                    Boolean s_lluvia = (Boolean) dataSnapshot.child("Sensores").child("S_Lluvia").getValue();
                    Boolean estado_sl = (Boolean) dataSnapshot.child("Sensores").child("Estado_SL").getValue();

                    Map<String, Object> cerraduras = new HashMap<>();
                    Map<String, Object> sensores = new HashMap<>();

                    if(Activacion_PP == true){
                        //Puerta Principal
                        if(p_principal == true  && estado_pp == false){
                            tv_estado_alarma_pp.setText("Peligro");
                            tv_estado_alarma_pp.setTextColor(Color.RED);
                            cerraduras.put("Estado_PP", true);
                            refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                            createNotification("Se ha detectado movimiento en la Puerta Principal", Noti_PP);
                        }
                        else if(p_principal == false && estado_pp == true){
                            tv_estado_alarma_pp.setText("Tranquilo");
                            tv_estado_alarma_pp.setTextColor(Color.GREEN);
                            cerraduras.put("Estado_PP", false);
                            refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                        }
                        else if(p_principal == true && estado_pp == true){
                            tv_estado_alarma_pp.setText("Peligro");
                            tv_estado_alarma_pp.setTextColor(Color.RED);
                        }
                        else{
                            tv_estado_alarma_pp.setText("Tranquilo");
                            tv_estado_alarma_pp.setTextColor(Color.GREEN);
                        }
                    }
                    else{
                        cerraduras.put("P_Principal", false);
                        tv_estado_alarma_pp.setText("Desactivado");
                        tv_estado_alarma_pp.setTextColor(Color.RED);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                    }

                    if(Activacion_PT == true){
                        //Puerta Terraza
                        if(p_terraza == true && estado_pt == false){
                            tv_estado_alarma_pt.setText("Peligro");
                            tv_estado_alarma_pt.setTextColor(Color.RED);
                            cerraduras.put("Estado_PT", true);
                            refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                            createNotification("Se ha detectado movimiento en la Puerta de la Terraza", Noti_PT);
                        }
                        else if(p_terraza == false && estado_pt == true){
                            tv_estado_alarma_pt.setText("Tranquilo");
                            tv_estado_alarma_pt.setTextColor(Color.GREEN);
                            cerraduras.put("Estado_PT", false);
                            refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                        }
                        else if(p_terraza == true && estado_pt == true){
                            tv_estado_alarma_pt.setText("Peligro");
                            tv_estado_alarma_pt.setTextColor(Color.RED);
                        }
                        else{
                            tv_estado_alarma_pt.setText("Tranquilo");
                            tv_estado_alarma_pt.setTextColor(Color.GREEN);
                        }
                    }
                    else{
                        cerraduras.put("P_Terraza", false);
                        tv_estado_alarma_pt.setText("Desactivado");
                        tv_estado_alarma_pt.setTextColor(Color.RED);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                    }

                    if(Activacion_VC == true){
                        //Ventana Comedor
                        if(v_comedor == true && estado_vc == false){
                            tv_estado_alarma_vc.setText("Peligro");
                            tv_estado_alarma_vc.setTextColor(Color.RED);
                            cerraduras.put("Estado_VC", true);
                            refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                            createNotification("Se ha detectado movimiento en la Ventana del Comedor", Noti_VC);
                        }
                        else if(v_comedor == false && estado_vc == true){
                            tv_estado_alarma_vc.setText("Tranquilo");
                            tv_estado_alarma_vc.setTextColor(Color.GREEN);
                            cerraduras.put("Estado_VC", false);
                            refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                        }
                        else if(v_comedor == true && estado_vc == true){
                            tv_estado_alarma_vc.setText("Peligro");
                            tv_estado_alarma_vc.setTextColor(Color.RED);
                        }
                        else{
                            tv_estado_alarma_vc.setText("Tranquilo");
                            tv_estado_alarma_vc.setTextColor(Color.GREEN);
                        }
                    }
                    else{
                        cerraduras.put("V_Comedor", false);
                        tv_estado_alarma_vc.setText("Desactivado");
                        tv_estado_alarma_vc.setTextColor(Color.RED);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                    }


                    //Sensor de Fuego
                    if(s_fuego == true && estado_sf == false){
                        tv_estado_alarma_sf.setText("Peligro");
                        tv_estado_alarma_sf.setTextColor(Color.RED);
                        sensores.put("Estado_SF", true);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                        createNotification("Se ha detectado Fuego en la Cocina", Noti_SF);
                    }
                    else if(s_fuego == false && estado_sf == true){
                        tv_estado_alarma_sf.setText("Tranquilo");
                        tv_estado_alarma_sf.setTextColor(Color.GREEN);
                        sensores.put("Estado_SF", false);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                    }
                    else if(s_fuego == true && estado_sf == true){
                        tv_estado_alarma_sf.setText("Peligro");
                        tv_estado_alarma_sf.setTextColor(Color.RED);
                    }
                    else{
                        tv_estado_alarma_sf.setText("Tranquilo");
                        tv_estado_alarma_sf.setTextColor(Color.GREEN);
                    }

                    //Sensor de Gas
                    if(s_gas == true && estado_sg == false){
                        tv_estado_alarma_sg.setText("Peligro");
                        tv_estado_alarma_sg.setTextColor(Color.RED);
                        sensores.put("Estado_SG", true);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                        createNotification("Se ha detectado Gas en la Cocina", Noti_SG);
                    }
                    else if(s_gas == false && estado_sg == true){
                        tv_estado_alarma_sg.setText("Tranquilo");
                        tv_estado_alarma_sg.setTextColor(Color.GREEN);
                        sensores.put("Estado_SG", false);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                    }
                    else if(s_gas == true && estado_sg == true){
                        tv_estado_alarma_sg.setText("Peligro");
                        tv_estado_alarma_sg.setTextColor(Color.RED);
                    }
                    else{
                        tv_estado_alarma_sg.setText("Tranquilo");
                        tv_estado_alarma_sg.setTextColor(Color.GREEN);
                    }

                    //Sensor de Humo
                    if(s_humo == true && estado_sh == false){
                        tv_estado_alarma_sh.setText("Peligro");
                        tv_estado_alarma_sh.setTextColor(Color.RED);
                        sensores.put("Estado_SH", true);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                        createNotification("Se ha detectado Humo en la Cocina", Noti_SH);
                    }
                    else if(s_humo == false && estado_sh == true){
                        tv_estado_alarma_sh.setText("Tranquilo");
                        tv_estado_alarma_sh.setTextColor(Color.GREEN);
                        sensores.put("Estado_SH", false);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                    }
                    else if(s_humo == true && estado_sh == true){
                        tv_estado_alarma_sh.setText("Peligro");
                        tv_estado_alarma_sh.setTextColor(Color.RED);
                    }
                    else{
                        tv_estado_alarma_sh.setText("Tranquilo");
                        tv_estado_alarma_sh.setTextColor(Color.GREEN);
                    }

                    //Sensor de Lluvia
                    if(s_lluvia == true && estado_sl == false){
                        tv_estado_alarma_sl.setText("Peligro");
                        tv_estado_alarma_sl.setTextColor(Color.RED);
                        sensores.put("Estado_SL", true);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                        createNotification("Se ha detectado Lluvia", Noti_SH);
                    }
                    else if(s_lluvia == false && estado_sl == true){
                        tv_estado_alarma_sl.setText("Tranquilo");
                        tv_estado_alarma_sl.setTextColor(Color.GREEN);
                        sensores.put("Estado_SL", false);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                    }
                    else if(s_lluvia == true && estado_sl == true){
                        tv_estado_alarma_sl.setText("Peligro");
                        tv_estado_alarma_sl.setTextColor(Color.RED);
                    }
                    else{
                        tv_estado_alarma_sl.setText("Tranquilo");
                        tv_estado_alarma_sl.setTextColor(Color.GREEN);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setPendingIntent(){
        Intent intent = new Intent(this, Alarmas.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(Alarmas.class);
            stackBuilder.addNextIntent(intent);
            pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        }
    }
}
