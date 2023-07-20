#include <WiFi.h>
#include <WiFiClient.h>
#include <HTTPClient.h>
#include <WebServer.h>
#include <time.h> 
#include <ArduinoJson.h>
#include <stdio.h>

//WIFI setting
const char* ssid = "KEB_INHA"; 
const char* password = "inha123*"; 
String TempUrl = "http://165.246.80.63:8080/session-login/temp";
//sensor setting
int sensor = A2;    
int Vo;
float R1 = 10000;
float logR2, R2, T, Tc, Tf;
float c1 = 1.009249522e-03, c2 = 2.378405444e-04, c3 = 2.019202697e-07;

//count setting
int count = 0;

//Time setting
int timezone = 3; 
int dst = 0; 
unsigned long previousMillis = 0;
const long interval = 1000; 

//JSON
char buffer[96];

//WebServer Setting
WebServer server(80); // Create WebServer Object, port 
void printConnectMsg(){
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: "); 
  Serial.println(WiFi.localIP()); //print assigned ip address 

}
 //온도 센서의 값을 읽고 온도로 변환
float getTemp(){
  Vo = analogRead(sensor);
  R2 = R1 * (4095.0 / (float)Vo - 1.0);
  logR2 = log(R2);
  T = (1.0 / (c1 + c2*logR2 + c3*logR2*logR2*logR2));
  Tc = T - 273.15;
  return Tc;
}

//온도 센서의 값을 화씨 온도로 변환 
float tcToTf(float Tc){
  return (Tc * 9.0/5.0) +32.0;
}

// 현재 카운트 값 반환
int getCount(){
  return count;
} 

// IP 주소 XXX 처리 
String ipAddressConverter(String IpAddress){
  int a1,a2,a3,a4;
  sscanf(IpAddress.c_str(),"%d.%d.%d.%d", &a1, &a2, &a3, &a4);
  return String(a1)+".XXX.XXX."+String(a4);
} 


/// Web 핸들러 
// 루트로 접속했을때 핸들러 
void handleRootEvent() {
  String clientIp = server.client().remoteIP().toString();
  String privateIP = ipAddressConverter(clientIp);
  String message = "Hello World!!";
  
  Serial.print("Main Page from :"); // Page Enter 
  Serial.println(clientIp);

  server.send(200 , "text/plain", message);   
}
// getTemp 로 접속했을때 핸들러 
void getTempJson(){
  float temp = getTemp();
  StaticJsonDocument<200> doc;
  JsonObject root = doc.to<JsonObject>();
  root["Sensor"] = "temperature";
  root["Value"] = temp;

  serializeJson(root,buffer);
  server.send(200 , "text/Json",buffer); 
}

void getCountJson(){
  int count = getCount();
  StaticJsonDocument<200> doc;
  JsonObject root = doc.to<JsonObject>();
  root["Sensor"] = "ultrasonicWave";
  root["Value"] = count;

  serializeJson(root,buffer);
  server.send(200 , "text/Json",buffer); 
}

void setup() {

  Serial.begin(115200); // ESP32 baud rate

  //WIFI Setting  
  WiFi.mode(WIFI_STA); 
  WiFi.begin(ssid, password); 
  Serial.println("");

  while(WiFi.status() != WL_CONNECTED) {
  delay(500);
  Serial.print(".");
  }
  printConnectMsg(); //시리얼에 IP 주소 정보 출력 

  server.on("/", handleRootEvent); // root(/) Enter Function 
  server.on("/getTemp",getTempJson); // (/getTemp) 접속시 Json 포맷으로 온도 데이터 전송 
  server.on("/getCount",getCountJson); // (/getCount) 접속시 Json 포맷으로 카운트 값 전송 
  server.begin(); // Server Start 
  Serial.println("HTTP server started");

  //time setting
  configTime(3 * 3600, 0, "pool.ntp.org", "time.nist.gov"); 
  Serial.println("\nWaiting for time"); 
  while (!time(nullptr)) { 
    Serial.print("."); 
    delay(1000); 
  }
  Serial.println("");         
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient(); // Client session Receive
  if((WiFi.status()) == WL_CONNECTED){
    WiFiClient WiFiClient;
    HTTPClient httpClient; 

    httpClient.begin(WiFiClient,TempUrl);
    httpClient.addHeader("Content-Type", "application/json");

    StaticJsonDocument<200> json;

    float temp = getTemp();
    int count = getCount();
    json["temp"] = temp;
    json["count"] = count;

    String paredJsonToString;

    serializeJson(json,paredJsonToString);
    String tempString = "22222.222";
    int httpResponseCode = httpClient.POST(paredJsonToString);

    if(httpResponseCode>0){
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);

      String response = httpClient.getString();
      Serial.println(response);
    }else{
      Serial.print("Error Code: ");
      Serial.println(httpResponseCode);
    }
    httpClient.end();
    delay(1000);
  }
}
