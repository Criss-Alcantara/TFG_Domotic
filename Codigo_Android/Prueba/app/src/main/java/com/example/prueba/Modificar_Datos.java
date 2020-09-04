package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Modificar_Datos extends AppCompatActivity {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
    DatabaseReference refUsuario;
    TextView tv_name_user,tv_code_access,tv_pass_new;
    Button b_guardar;
    Context context;
    SharedPreferences sharprefs;
    String password = "";
    String union_string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_modificar__datos);
        refUsuario = mDatabase.child("user_1");
        b_guardar = (Button) findViewById(com.example.prueba.R.id.b_salvar);
        tv_name_user = (EditText) findViewById(R.id.id_user_modificar);
        tv_code_access = (EditText) findViewById(R.id.id_pass_modificar);
        tv_pass_new = (EditText) findViewById(R.id.id_code_access_modificar);
        sharprefs =  getSharedPreferences("Archivo_Registro", context.MODE_PRIVATE);
    }

    public  void Guardar( View v) throws Exception{
        final String dat_1 = tv_name_user.getText().toString();
        final String dat_2 = tv_code_access.getText().toString();
        final String dat_3 = tv_pass_new.getText().toString();

        if(tv_name_user.getText().toString().isEmpty()){
            Toast.makeText(Modificar_Datos.this, "Por favor ingrese su usuario", Toast.LENGTH_SHORT).show();
        }
        else if(tv_code_access.getText().toString().isEmpty()){
            Toast.makeText(Modificar_Datos.this, "Por favor ingrese su codigo de eguridad\"", Toast.LENGTH_SHORT).show();
        }
        else if(tv_pass_new.getText().toString().isEmpty()){
            Toast.makeText(Modificar_Datos.this, "Por favor ingrese su nueva contraseña de usuario", Toast.LENGTH_SHORT).show();
        }
        else{
            refUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String user_id = dataSnapshot.child("id").getValue().toString();
                        String code_access = dataSnapshot.child("code_access").getValue().toString();
                        if(dat_1.equals(user_id) && dat_2.equals(code_access)){
                            try{
                                Map<String, Object> guardar_registro = new HashMap<>();
                                union_string = dat_1 + dat_3;
                                password = cifradoCesar(union_string,union_string.length());
                                password = cifradoCesar(password,password.length());
                                String dato_encriptado = encrypt(dat_3, password);
                                guardar_registro.put("id", dat_1);
                                guardar_registro.put("pass",dato_encriptado );
                                refUsuario.updateChildren(guardar_registro);
                                SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharpref.edit();
                                editor.putString("My_User_Name", dat_1);
                                editor.putString("My_User_Pass",dato_encriptado);
                                editor.commit();
                                Intent guardar = new Intent(context, MainActivity.class);
                                guardar.putExtra("password",password);
                                startActivity(guardar);
                            }catch(Exception e){

                            }
                        }
                        else{
                            Log.d("Varificacion","error");
                            Toast.makeText(Modificar_Datos.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private String encrypt(String inputString, String password) throws  Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(inputString.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

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
