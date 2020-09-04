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

//-------------------VARIABLES GLOBALES --------------------------
// VARIABLE PARA ALMACENAR LA TEMPERATURA
int TEMPERATURA;
//VARIABLE PARA ALMACENAR LA HUMEDAD
int HUMEDAD; 
// VARIABLE FIREBASE
FirebaseData firebaseData;

//------------------- CONSTANTES --------------------------;
//MODO CONFIGURACION
int ACTUADOR_MODO_CONFIGURACION = 16;
//SENSOR DE HUMO/GAS
int RL_VALUE = 5;
float RO_CLEAN_AIR_FACTOR = 9.83;
int CALIBARAION_SAMPLE_TIMES = 50;    
int CALIBRATION_SAMPLE_INTERVAL = 50;   
int READ_SAMPLE_INTERVAL = 50;    
int READ_SAMPLE_TIMES = 5; 
int GAS_CO = 1;
int GAS_SMOKE = 2;
float COCurve[3] = {2.3,0.72,-0.34};
float SmokeCurve[3] = {2.3,0.53,-0.44}; 
float Ro = 10;
// METODO DEL SENSOR DE TEMPERATURA DHT
int SENSOR_TEMP_HUMEDAD = 2; //D4
DHT dht(SENSOR_TEMP_HUMEDAD, DHT11);
// METODO PARA EL LCD
LiquidCrystal_I2C lcd(0x27,16,2); 
// WIFI
WiFiClient espClient;
// HABILITAR PUERTO PARA EL ESP8266
ESP8266WebServer server(80);

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

//----------------Función para grabar en la EEPROM-------------------
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

//-----------------Función para leer la EEPROM------------------------
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


//************************************************************************************/ 
float MQResistanceCalculation(int raw_adc){
  return ( ((float)RL_VALUE*(1023-raw_adc)/raw_adc));
}
 
//************************************************************************************/ 
float MQCalibration(){
  int i;
  float val=0;
  for (i=0;i<CALIBARAION_SAMPLE_TIMES;i++) {            //take multiple samples
    val += MQResistanceCalculation(analogRead(A0));
    delay(CALIBRATION_SAMPLE_INTERVAL);
  }
  val = val/CALIBARAION_SAMPLE_TIMES; //calculate the average value
  val = val/RO_CLEAN_AIR_FACTOR; //divided by RO_CLEAN_AIR_FACTOR yields the Ro according to the chart in the datasheet
  return val; 
}

//************************************************************************************/ 
float MQRead(){
  int i;
  float rs=0;
  for (i=0;i<READ_SAMPLE_TIMES;i++) {
    rs += MQResistanceCalculation(analogRead(A0));
    delay(READ_SAMPLE_INTERVAL);
  }
  rs = rs/READ_SAMPLE_TIMES;
  return rs;  
}
 
//************************************************************************************/ 
int MQGetGasPercentage(float rs_ro_ratio, int gas_id){
  if ( gas_id == GAS_CO ) {
     return MQGetPercentage(rs_ro_ratio,COCurve);
  } else if ( gas_id == GAS_SMOKE ) {
     return MQGetPercentage(rs_ro_ratio,SmokeCurve);
  }    
 
  return 0;
}
 
//************************************************************************************/ 
int  MQGetPercentage(float rs_ro_ratio, float *pcurve){
  return (pow(10,( ((log(rs_ro_ratio)-pcurve[1])/pcurve[2]) + pcurve[0])));
}

//------------------- PINES PROPIOS DEL ESCENARIO --------------------------
// LUZ PRINCIPAL 
int ACTUADOR_LUZ_ON_OFF_PRINC = 0; //D3
//CALDERA
int ACTUADOR_CALDERA = 14; //D5
// ACTUADOR DE PARED
int ACTUADOR_PARED = 12; //D6
// AUXILIAR FB
int aux_fb = 0;
//SENSOR FUEGO
int SENSOR_FUEGO = 13; //D7
//SENSOR MOVIMIENTO
int SENSOR_MOVIMIENTO = 15; //D8
//TEMP DESEADA
int temp_deseada = 0;
//TEMP ACTUAL
int temp_actual = 0;
// CERRADURA
int aux_cerradura = 0;


//------------------------SETUP-----------------------------
void setup() {  
  // Inicia Serial
  Serial.begin(9600);
  Serial.println("");
  dht.begin();
  lcd.init();
  lcd.backlight();
  lcd.clear();
  EEPROM.begin(512);
  //MODO CONFIGURACION
  pinMode (ACTUADOR_MODO_CONFIGURACION, INPUT); // D0
  //LUZ ON - OFF PRINCIPAL
  pinMode (ACTUADOR_LUZ_ON_OFF_PRINC, OUTPUT); // D3
  //ACTUADOR
  pinMode(ACTUADOR_PARED, INPUT); //D6
  //CALDERA
  pinMode(ACTUADOR_CALDERA, OUTPUT);  //D5
  //FUEGO
  pinMode(SENSOR_FUEGO, INPUT); //D7
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
  Ro = MQCalibration();//Calibrating the sensor. Please make sure the sensor is in clean air when you perform the calibration 
  setup_wifi();
  //Auxiliares  
  digitalWrite(ACTUADOR_LUZ_ON_OFF_PRINC, LOW);
  digitalWrite(ACTUADOR_CALDERA, LOW);
  aux_fb = -1;
}

//--------------------------LOOP--------------------------------
void loop() {
//--------------------------- TEMPERATURA - HUMEDAD -----------------------------------
    unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= 5000) { //envia la temperatura cada 5 segundos
    TEMPERATURA = dht.readTemperature();
    HUMEDAD = dht.readHumidity();
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("Temperatura: "); // Mensaje a desplegar
    lcd.print(TEMPERATURA);
    lcd.print("C");
    lcd.setCursor(0,1);
    lcd.print("Humedad: "); // Mensaje a desplegar
    lcd.print(HUMEDAD);
    lcd.print("%"); 
    Firebase.setInt(firebaseData,"house_1/Cocina/Temperatura",TEMPERATURA);
    Firebase.setInt(firebaseData,"house_1/Cocina/Humedad",HUMEDAD);
    previousMillis = currentMillis;
  }
  
//--------------------------------------------------------------
  //ESTADO LUZ 
  if(Firebase.getBool(firebaseData,"house_1/Cocina/Luz_Princ")){
    if(firebaseData.dataType() == "boolean"){
      if(firebaseData.boolData () == 1 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED) == digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
       Firebase.setBool(firebaseData,"house_1/Cocina/Luz_Princ", false);
       aux_fb = 0; 
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () == aux_fb && digitalRead(ACTUADOR_PARED) != digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
       Firebase.setBool(firebaseData,"house_1/Cocina/Luz_Princ", true);
       aux_fb = 1; 
      }
      else if(firebaseData.boolData () == 1 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED) == digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_PRINC, !digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC));
        aux_fb = 1;
      }
      else if(firebaseData.boolData () == 0 && firebaseData.boolData () != aux_fb && digitalRead(ACTUADOR_PARED) != digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC)){
        digitalWrite(ACTUADOR_LUZ_ON_OFF_PRINC, !digitalRead(ACTUADOR_LUZ_ON_OFF_PRINC));
        aux_fb = 0;
      }
    }
  }
    else {
    Serial.println("Sensor Luz: " + firebaseData.errorReason());
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }
   
//--------------------------------------------------------------
  //SENSOR LLAMA
  if(Firebase.getBool(firebaseData,"house_1/Alarma/Sensores/S_Fuego")){
   if(firebaseData.dataType() == "boolean"){
     if(digitalRead(SENSOR_FUEGO) == LOW){
       Firebase.setBool(firebaseData,"house_1/Alarma/Sensores/S_Fuego", true);
     }
     else{
       Firebase.setBool(firebaseData,"house_1/Alarma/Sensores/S_Fuego", false);
     }
   }
  }
  else {
    Serial.println("Sensor Llama: " + firebaseData.errorReason());
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }
  
//--------------------------------------------------------------
  //SENSOR MQ-02

  if(Firebase.getBool(firebaseData,"house_1/Alarma/Sensores/S_Gas")){
   if(firebaseData.dataType() == "boolean"){
     if(MQGetGasPercentage(MQRead()/Ro,GAS_CO) > 300){
      Firebase.setBool(firebaseData,"house_1/Alarma/Sensores/S_Gas", true); 
     }
     else{
      Firebase.setBool(firebaseData,"house_1/Alarma/Sensores/S_Gas", false); 
      if(Firebase.getBool(firebaseData,"house_1/Alarma/Sensores/S_Humo")){
        if(firebaseData.dataType() == "boolean"){
           if(MQGetGasPercentage(MQRead()/Ro,GAS_SMOKE) > 0){
            Firebase.setBool(firebaseData,"house_1/Alarma/Sensores/S_Humo", true); 
           }
           else{
            Firebase.setBool(firebaseData,"house_1/Alarma/Sensores/S_Humo", false); 
           }
         }
       }
      else{
       Serial.println("Sensor Humo: " + firebaseData.errorReason()); 
      }
     }
    }
   }
  else {
   Serial.println("Sensor Gas: " + firebaseData.errorReason());  
  }
 
//--------------------------------------------------------------
  //SALON - CALDERA
  if(Firebase.getInt(firebaseData, "house_1/Salon/Caldera")){
    if(firebaseData.dataType() == "int"){
      temp_deseada = firebaseData.intData();
    }
  }
  else {
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }
  if(Firebase.getInt(firebaseData, "house_1/Salon/Temperatura")){
    if(firebaseData.dataType() == "int"){
      temp_actual = firebaseData.intData();
    }
  }
  else {
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }

//--------------------------------------------------------------
  //ENCENDER CALDERA
  if(Firebase.getBool(firebaseData,"house_1/Salon/Encender_Caldera")){
   if(firebaseData.dataType() == "boolean"){
    if(firebaseData.boolData() == 1 && temp_deseada != temp_actual){
       digitalWrite(ACTUADOR_CALDERA,HIGH);
    }
    else{
      digitalWrite(ACTUADOR_CALDERA,LOW);
    }
   }
  }
  else {
    Serial.println("Caldera: " + firebaseData.errorReason());
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }
//--------------------------------------------------------------
   //SENSOR MOVIMIENTO
  if(Firebase.getBool(firebaseData,"house_1/Cerraduras/Puerta_Terraza")){
    if(firebaseData.dataType() == "boolean"){
      if(firebaseData.boolData() == 1){
        aux_cerradura = 1;
      }
      else{
        aux_cerradura = 0;
      }
    }
  }
  else {
    Serial.println("Cerradura: " + firebaseData.errorReason());  
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }
  
   
   if(Firebase.getBool(firebaseData,"house_1/Alarma/Cerraduras/P_Terraza")){
    if(firebaseData.dataType() == "boolean"){
      if(aux_cerradura == 1){
        if(digitalRead(SENSOR_MOVIMIENTO) == 1){
          Firebase.setBool(firebaseData,"house_1/Alarma/Cerraduras/P_Terraza", true);
        }
        else{
          Firebase.setBool(firebaseData,"house_1/Alarma/Cerraduras/P_Terraza", false);
        }
      }
      else{
        Firebase.setBool(firebaseData,"house_1/Alarma/Cerraduras/P_Terraza", false);
      }
    }
  }
  else {
    Serial.println("Movimiento: " + firebaseData.errorReason());  
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("    Error al    ");
    lcd.setCursor(0,1);
    lcd.print("   Conectar  ");  
  }
  delay(300);
}
