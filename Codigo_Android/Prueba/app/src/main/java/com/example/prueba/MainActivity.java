package com.example.prueba;

// Librerias de Android
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

// Librerias de Google
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.*;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.example.prueba.R.drawable.luz_abierta;

public class MainActivity extends AppCompatActivity {

    //public String dato_pass ;
    Button boton_login;
    EditText text_id, text_pass;
    DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
    DatabaseReference refUsuario;
    Context context;
    SharedPreferences sharprefs;
    String password = "";
    String union_string = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.prueba.R.layout.activity_main);
        refUsuario = mDatabase.child("user_1");
        boton_login = (Button) findViewById(com.example.prueba.R.id.b_ingresar);
        text_id = (EditText) findViewById(R.id.id_user);
        text_pass = (EditText) findViewById(R.id.id_pass);
        context = this;
        password = getIntent().getStringExtra("password");
        sharprefs =  getSharedPreferences("Archivo_Registro", context.MODE_PRIVATE);
        SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
        String id = sharpref.getString("My_User_Name", "");
        String pass = sharpref.getString("My_User_Pass", "");
        text_id.setText(id);
        text_pass.setText(pass);
        Comprobar_Datos();
    }

    //Metodo el boton Ingresar
    public  void Ingresar(View v) throws  Exception{
        final String dat_1 = text_id.getText().toString();
        final String dat_2  = text_pass.getText().toString();
        if(text_id.getText().toString().isEmpty()){
            Toast.makeText(MainActivity.this, "Por favor ingrese un usuario", Toast.LENGTH_SHORT).show();
        }
        else if(text_pass.getText().toString().isEmpty()){
            Toast.makeText(MainActivity.this, "Por favor ingrese una contraseña", Toast.LENGTH_SHORT).show();
        }
        else{
            refUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String name_user = dataSnapshot.child("id").getValue().toString();
                        String pass_user = dataSnapshot.child("pass").getValue().toString();
                        if(dat_1.equals(name_user)){
                            try{
                                union_string = dat_1 + dat_2;
                                password = cifradoCesar(union_string,union_string.length());
                                password = cifradoCesar(password,password.length());
                                String dato_encriptado = encrypt(dat_2, password);
                                if(dato_encriptado.equals(pass_user)){
                                    SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharpref.edit();
                                    editor.putString("My_User_Name", dat_1);
                                    editor.putString("My_User_Pass",dat_2);
                                    editor.commit();
                                    Intent siguiente = new Intent(getApplicationContext(), Escenarios.class);
                                    startActivity(siguiente);
                                }
                                else{
                                    Log.d("Varificacion","error");
                                    Toast.makeText(MainActivity.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception e){

                            }

                        }
                        else{
                            Log.d("Varificacion","error");
                            Toast.makeText(MainActivity.this, "Usuario Incorrecto", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //Metodo el boton Modificar
    public  void Modificar_Datos(View v){
        Intent sig_modificar = new Intent(v.getContext(), Modificar_Datos.class);
        startActivity(sig_modificar);
    }

    //Metodo para Comprobar Datos de ingreso
    public void Comprobar_Datos(){
        final String dat_1 = text_id.getText().toString();
        final String dat_2 = text_pass.getText().toString();
        if(dat_1.equals("")){
            Toast.makeText(MainActivity.this, "Por favor ingrese su usuario", Toast.LENGTH_SHORT).show();
        }
        else if(dat_2.equals("")){
            Toast.makeText(MainActivity.this, "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
        }
        else{
            refUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String name_user = dataSnapshot.child("id").getValue().toString();
                        String pass_user = dataSnapshot.child("pass").getValue().toString();

                        if(dat_1.equals(name_user)){
                            try{
                                String dato_encriptado = encrypt(dat_2, password);
                                if(dato_encriptado.equals(pass_user)){
                                    Intent siguiente = new Intent(getApplicationContext(), Escenarios.class);
                                    startActivity(siguiente);
                                }
                                else{
                                    Log.d("Varificacion","error");
                                    Toast.makeText(MainActivity.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){

                            }
                        }
                        else{
                            Log.d("Varificacion","error");
                            Toast.makeText(MainActivity.this, "Usuario Incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //Metodo para Encriptar usando AES
    private String encrypt(String inputString, String password) throws  Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(inputString.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    //Metodo para Desencriptar usando AES
    private String decrypt(String outputString, String password) throws  Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    //Metodo para generar una llave para AES
    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    //Metodo para el cifrado Cesar
    private static String cifradoCesar(String texto, int codigo){
        StringBuilder cifrado = new StringBuilder();
        codigo = codigo % 26;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) >= 'a' && texto.charAt(i) <= 'z') {
                if ((texto.charAt(i) + codigo) > 'z') {
                    cifrado.append((char) (texto.charAt(i) + codigo - 26));
                } else {
                    cifrado.append((char) (texto.charAt(i) + codigo));
                }
            } else if (texto.charAt(i) >= 'A' && texto.charAt(i) <= 'Z') {
                if ((texto.charAt(i) + codigo) > 'Z') {
                    cifrado.append((char) (texto.charAt(i) + codigo - 26));
                } else {
                    cifrado.append((char) (texto.charAt(i) + codigo));
                }
            }
        }
        return cifrado.toString();
    }
}
