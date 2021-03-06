//------------------- LIBRERIAS --------------------------
#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <EEPROM.h>
#include <DHT.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Servo.h>
#include <ArduinoJson.h>
#include <DHT_U.h>

//------------------- CONSTANTES  --------------------------
//MODO CONFIGURACION
int ACTUADOR_MODO_CONFIGURACION = 16; //D0
// METODO PARA EL LCD
LiquidCrystal_I2C lcd(0x27,16,2); //D1 - D2
// WIFI
WiFiClient espClient;
// HABILITAR PUERTO PARA EL ESP8266
ESP8266WebServer server(80);
FirebaseData firebaseData;

                                 
//-------------------VARIABLES GLOBALES CONFIGURACION --------------------------
int contconexion = 0;
unsigned long previousMillis = 0;
float Sensibilidad = 0.100;
char ssid[50];      
char pass[50];
const char *ssidConf = "HOME_MANAGMENT";
const char *passConf = "12345678";
String mensaje = "";
#define FIREBASE_HOST "tfg-seguridad.firebaseio.com"
#define FIREBASE_AUTH "itL4LUUL0iE3kQTQy9MWunmGfwNUDi1JhiLXeuuR"

//-----------CODIGO HTML PAGINA DE CONFIGURACION---------------
String pagina = "<!DOCTYPE html>"
"<html>"
"<head>"
"<title>Configuracion</title>"
"<meta charset='UTF-8'>"
"<meta name='MobileOptimized' content='width' />"
"<meta name='HandheldFriendly' content='true' />"
"<meta name='viewport' content='width=device-width, initial-scale=1'>"
"</head>"
"<center>"
"<body bgcolor = 'black' >"
"<div style='background-color:#FF5722'>"
"<h1>"
"<center>"
"<font color = '#ffffff' SIZE=7>"
"<p style = 'font-family:arial,helvética;'>HOME CENTER</p>"          
"</font>"
"</center>"
"</h1>"
"</div>"  
"<div>"
"<h2>"
"<center>"
"<font color = '#FF5722' SIZE=6>¡BIENVENIDO!</font>"
"</center>"
"</h2>"
"</div>"
"<div>"
"<form action='guardar_conf' method='get'>"
"<br><br>"      
"<font color = '#ffffff' SIZE=5>"
"WiFi:<br><br>"
"</font>"
"<input class='input1' name='ssid' type='text' placeholder='Name'><br>"  
"<br><br><br><br>"        
"<font color = '#ffffff' SIZE=5>"
"PASSWORD:<br><br>"
"</font>"
"<input class='input1' name='pass' type='password' placeholder='Pass'><br><br><br><br>"
"<input class='boton' type='submit' value='GUARDAR'/><br><br>"
"</form>"
"</div>"
"</body>"
"</center>";

String paginafin = "</body>"
"</html>";

String mensajeinicio = "<div>"
  "<h3>"
  "<center>"
  "<font color = '#FFFFFF' SIZE=5>";

String mensajefinal = "</font>"
  "</center>"
  "</h2>"
  "</div>";

//------------------------SETUP WIFI-----------------------------
void setup_wifi() {
// Conexión WIFI
  WiFi.mode(WIFI_STA); //para que no inicie el SoftAP en el modo normal
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED and contconexion <50) { //Cuenta hasta 50 si no se puede conectar lo cancela
    ++contconexion;
    delay(250);
    lcd.clear();
    lcd.print("Esperando...");
  }
  if (contconexion <50) {   
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print("WiFi ");
      lcd.setCursor(0,1);
      lcd.print("conectado");
      Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
      delay(1000);
  }
  else { 
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print("Error de ");
      lcd.setCursor(0,1);
      lcd.print("conexion");
      delay(1000);
  }
}

//-------------------PAGINA DE CONFIGURACION--------------------
void paginaconf() { 
  mensaje = "";
  server.send(200, "text/html", pagina + mensaje + paginafin); 
}
void paginaconf_save() { 
  server.send(200, "text/html", pagina + mensajeinicio + mensaje + mensajefinal + paginafin); 
}

//--------------------MODO_CONFIGURACION------------------------
void modoconf() {  
  WiFi.softAP(ssidConf, passConf);
  IPAddress myIP = WiFi.softAPIP();
  lcd.setCursor(0,0); 
  lcd.print("WebServer on ....");
  lcd.setCursor(0,1); 
  lcd.print("IP: ");
  lcd.print(myIP);
  server.on("/", paginaconf); //esta es la pagina de configuracion
  server.on("/guardar_conf", guardar_conf); //Graba en la eeprom la configuracion
  server.begin();
  while (true) {
      server.handleClient();
  }
}

//---------------------GUARDAR CONFIGURACION-------------------------
void guardar_conf() {
  Serial.println(server.arg("ssid"));//Recibimos los valores que envia por GET el formulario web
  grabar(0,server.arg("ssid"));
  Serial.println(server.arg("pass"));
  grabar(50,server.arg("pass"));
  mensaje = "Configuracion Guardada...";
  paginaconf_save();
}

//----------------FUNCION PARA GRABAR EN LA EEPROM-------------------
void grabar(int addr, String a) {
  int tamano = a.length(); 
  char inchar[50]; 
  a.toCharArray(inchar, tamano+1);
  for (int i = 0; i < tamano; i++) {
    EEPROM.write(addr+i, inchar[i]);
  }
  for (int i = tamano; i < 50; i++) {
    EEPROM.write(addr+i, 255);
  }
  EEPROM.commit();
}

//-----------------FUNCION PARA LEER LA EEPROM------------------------
String leer(int addr) {
   byte lectura;
   String strlectura;
   for (int i = addr; i < addr+50; i++) {
      lectura = EEPROM.read(i);
      if (lectura != 255) {
        strlectura += (char)lectura;
      }
   }
   return strlectura;
}

//------------------- PINES PROPIOS DEL ESCENARIO --------------------------
// LUZ PRINCIPAL 
int ACTUADOR_LUZ_ON_OFF_PRINC = 0; //D3
// LUZ SECUNDARIA 
int ACTUADOR_LUZ_ON_OFF_SECUND = 2; //D4
// LUZ TERCIARIA 
int ACTUADOR_LUZ_ON_OFF_TERC = 14; //D5
// ACTUADORES DE PARED
int ACTUADOR_PARED_PRINC = 12; //D6
int ACTUADOR_PARED_SECUND = 13; //D7
int ACTUADOR_PARED_TERC = 15; //D8
//AUXILIAR ESTADO PARED
int aux_fb_p = 0;
int aux_fb_s = 0;
int aux_fb_t = 0;
// SENSOR MOVIMIENTO
int SENSOR_MOVIMIENTO = 3; //D9 - RX

//------------------------SETUP-----------------------------
void setup() { 
  // EEPROM
  EEPROM.begin(512); 
  // Inicia Serial
  Serial.begin(9600);
  Serial.println(""); 
  //MODO CONFIGURACION
  pinMode (ACTUADOR_MODO_CONFIGURACION, INPUT); // D0
  //LUZ ON - OFF PRINCIPAL
  pinMode (ACTUADOR_LUZ_ON_OFF_PRINC, OUTPUT); // D3
  pinMode (ACTUADOR_PARED_PRINC, INPUT); // D6
  //LUZ ON - OFF SECUNDARIA
  pinMode (ACTUADOR_LUZ_ON_OFF_SECUND, OUTPUT); // D4
  pinMode (ACTUADOR_PARED_PRINC, INPUT); // D7
  //LUZ ON - OFF TERCIARIA
  pinMode (ACTUADOR_LUZ_ON_OFF_TERC, OUTPUT); // D5
  pinMode (ACTUADOR_PARED_PRINC, INPUT); // D8
  //SENSOR MOVIMIENTO
  pinMode (SENSOR_MOVIMIENTO, INPUT); //D10
  // LCD: D1 - D2
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0,0);
  lcd.print("  Bienvenidos  ");
  lcd.setCursor(0,1);
  lcd.print("Configurando.....");
  delay(3000);
  if (digitalRead(ACTUADOR_MODO_CONFIGURACION) == 0) {
    modoconf();
  }
  leer(0).toCharArray(ssid, 50);
  leer(50).toCharArray(pass, 50);
  setup_wifi();
  //Auxiliares  
  digitalWrite(ACTUADOR_LUZ_ON_OFF_PRINC, LOW);
  aux_fb_p = -1;
  //Auxiliares  
  digitalWrite(ACTUADOR_LUZ_ON_OFF_SECUND, LOW);
  aux_fb_s = -1;
  //Auxiliares  
  digitalWrite(ACTUADOR_LUZ_ON_OFF_TERC, LOW);
  aux_fb_t = -1;
}

//--------------------------LOOP--------------------------------
void loop() {   
//-----------------------LUZ PRINCIPAL---------------------------------------
   //ESTADO LUZ PRINCIPAL
   if(Firebase.getBool(firebaseData,"house_1/Pasillo/Luz_Princ")){
    if(firebaseData.dataType() == "boolean"){
      if(firebaseData.boolData () == 1 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED_PRINC) == digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
       Firebase.setBool(firebaseData,"house_1/Pasillo/Luz_Princ", false);
       aux_fb_p = 0; 
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED_PRINC) != digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
       Firebase.setBool(firebaseData,"house_1/Pasillo/Luz_Princ", true);
       aux_fb_p = 1; 
      }
      else if(firebaseData.boolData () == 1 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED_PRINC) == digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_PRINC, !digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC));
        aux_fb_p = 1;
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED_PRINC) != digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_PRINC, !digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC));
        aux_fb_p = 0;
      }
    }
  }
  else {
    Serial.println(firebaseData.errorReason());
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }
  
   //ESTADO LUZ SECUNDARIA
   if(Firebase.getBool(firebaseData,"house_1/Pasillo/Luz_Secund")){
    if(firebaseData.dataType() == "boolean"){
      if(firebaseData.boolData () == 1 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED_SECUND) == digitalRead(ACTUADOR_LUZ_ON_OFF_SECUND)){
       Firebase.setBool(firebaseData,"house_1/Pasillo/Luz_Secund", false);
       aux_fb_s = 0; 
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED_SECUND) != digitalRead(ACTUADOR_LUZ_ON_OFF_SECUND)){
       Firebase.setBool(firebaseData,"house_1/Pasillo/Luz_Secund", true);
       aux_fb_s = 1; 
      }
      else if(firebaseData.boolData () == 1 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED_SECUND) == digitalRead(ACTUADOR_LUZ_ON_OFF_SECUND)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_SECUND, !digitalRead(ACTUADOR_LUZ_ON_OFF_SECUND));
        aux_fb_s = 1;
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED_SECUND) != digitalRead(ACTUADOR_LUZ_ON_OFF_SECUND)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_SECUND, !digitalRead(ACTUADOR_LUZ_ON_OFF_SECUND));
        aux_fb_s = 0;
      }
    }
  }
  else {
    Serial.println(firebaseData.errorReason());
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }

     //ESTADO LUZ TERCIARIA
   if(Firebase.getBool(firebaseData,"house_1/Pasillo/Luz_Terc")){
    if(firebaseData.dataType() == "boolean"){
      if(firebaseData.boolData () == 1 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED_TERC) == digitalRead(ACTUADOR_LUZ_ON_OFF_TERC)){
       Firebase.setBool(firebaseData,"house_1/Pasillo/Luz_Terc", false);
       aux_fb_t = 0; 
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED_TERC) != digitalRead(ACTUADOR_LUZ_ON_OFF_TERC)){
       Firebase.setBool(firebaseData,"house_1/Pasillo/Luz_Terc", true);
       aux_fb_t = 1; 
      }
      else if(firebaseData.boolData () == 1 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED_TERC) == digitalRead(ACTUADOR_LUZ_ON_OFF_TERC)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_TERC, !digitalRead(ACTUADOR_LUZ_ON_OFF_TERC));
        aux_fb_t = 1;
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED_TERC) != digitalRead(ACTUADOR_LUZ_ON_OFF_TERC)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_TERC, !digitalRead(ACTUADOR_LUZ_ON_OFF_TERC));
        aux_fb_t = 0;
      }
    }
  }
  else {
    Serial.println(firebaseData.errorReason());
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }


  delay(300);
}
