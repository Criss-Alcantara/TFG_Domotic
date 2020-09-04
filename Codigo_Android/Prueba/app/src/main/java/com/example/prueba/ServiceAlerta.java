package com.example.prueba;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ServiceAlerta extends Service {

    NotificationCompat.Builder mBuilder;
    int Noti_PP = 912992;
    int Noti_PT = 523340;
    int Noti_VC = 864658;
    int Noti_SF = 280199;
    int Noti_SG = 908328;
    int Noti_SH = 694758;
    String channelId = "my_channel_01";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("house_1");
    Alarmas alarmas = new Alarmas();

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProcess){
        detectar_emergencia();
        return START_STICKY;
    }

    public void onDestroy(){

    }

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
        //mBuilder.setOnlyAlertOnce(true);
        //mBuilder.setChannelId(channelId);

        mNotificationManager.notify(id_noti, mBuilder.build());
    }

    public void detectar_emergencia(){
        refHome.child("Alarma").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String p_principal = dataSnapshot.child("Cerraduras").child("P_Principal").getValue().toString();
                    String estado_pp = dataSnapshot.child("Cerraduras").child("Estado_PP").getValue().toString();

                    String p_terraza = dataSnapshot.child("Cerraduras").child("P_Terraza").getValue().toString();
                    String estado_pt = dataSnapshot.child("Cerraduras").child("Estado_PT").getValue().toString();

                    String v_comedor = dataSnapshot.child("Cerraduras").child("V_Comedor").getValue().toString();
                    String estado_vc = dataSnapshot.child("Cerraduras").child("Estado_VC").getValue().toString();

                    String s_fuego = dataSnapshot.child("Sensores").child("S_Fuego").getValue().toString();
                    String estado_sf = dataSnapshot.child("Sensores").child("Estado_SF").getValue().toString();

                    String s_gas = dataSnapshot.child("Sensores").child("S_Gas").getValue().toString();
                    String estado_sg = dataSnapshot.child("Sensores").child("Estado_SG").getValue().toString();

                    String s_humo = dataSnapshot.child("Sensores").child("S_Humo").getValue().toString();
                    String estado_sh = dataSnapshot.child("Sensores").child("Estado_SH").getValue().toString();

                    Map<String, Object> cerraduras = new HashMap<>();
                    Map<String, Object> sensores = new HashMap<>();

                    //Puerta Principal
                    if(p_principal.equals("true") && estado_pp.equals("false")){
                        cerraduras.put("Estado_PP", true);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                        createNotification("Se ha detectado movimiento en la Puerta Principal", Noti_PP);
                    }
                    else if(p_principal.equals("false") && estado_pp.equals("true")){
                        cerraduras.put("Estado_PP", false);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                    }

                    //Puerta Terraza
                    if(p_terraza.equals("true") && estado_pt.equals("false")){
                        cerraduras.put("Estado_PT", true);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                        createNotification("Se ha detectado movimiento en la Puerta de la Terraza", Noti_PT);
                    }
                    else if(p_terraza.equals("false") && estado_pt.equals("true")){
                        cerraduras.put("Estado_PT", false);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                    }

                    //Ventana Comedor
                    if(v_comedor.equals("true") && estado_vc.equals("false")){
                        cerraduras.put("Estado_VC", true);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                        createNotification("Se ha detectado movimiento en la Ventana del Comedor", Noti_VC);
                    }
                    else if(v_comedor.equals("false") && estado_vc.equals("true")){
                        cerraduras.put("Estado_VC", false);
                        refHome.child("Alarma").child("Cerraduras").updateChildren(cerraduras);
                    }

                    //Sensor de Fuego
                    if(s_fuego.equals("true") && estado_sf.equals("false")){
                        sensores.put("Estado_SF", true);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                        createNotification("Se ha detectado Fuego en la Cocina", Noti_SF);
                    }
                    else if(s_fuego.equals("false") && estado_sf.equals("true")){
                        sensores.put("Estado_SF", false);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                    }

                    //Sensor de Gas
                    if(s_gas.equals("true") && estado_sg.equals("false")){
                        sensores.put("Estado_SG", true);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                        createNotification("Se ha detectado Gas en la Cocina", Noti_SG);
                    }
                    else if(s_gas.equals("false") && estado_sg.equals("true")){
                        sensores.put("Estado_SG", false);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                    }

                    //Sensor de Humo
                    if(s_humo.equals("true") && estado_sh.equals("false")){
                        sensores.put("Estado_SH", true);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                        createNotification("Se ha detectado Humo en la Cocina", Noti_SH);
                    }
                    else if(s_humo.equals("false") && estado_sh.equals("true")){
                        sensores.put("Estado_SH", false);
                        refHome.child("Alarma").child("Sensores").updateChildren(sensores);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
