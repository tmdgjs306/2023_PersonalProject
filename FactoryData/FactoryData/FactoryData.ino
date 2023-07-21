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

//URL Setting 
String addDataUrl = "http://165.246.80.104:8080/session-login/addData";

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

//JSON
char buffer[96];

//led
const int led = D2;

//photoresistor sensor setting
int photoresistor = A1;

// 지연 효과 변수 
unsigned long previousMillis = 0;
const long interval = 3000; 

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

// 조도 센서 값 측정 
int getPhotoresistor(){
  return analogRead(photoresistor);
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

//Count 값을 JSON 형태로 파싱하여 반환 
void getCountJson(){
  int count = getCount();
  StaticJsonDocument<200> doc;
  JsonObject root = doc.to<JsonObject>();
  root["Sensor"] = "ultrasonicWave";
  root["Value"] = count;

  serializeJson(root,buffer);
  server.send(200 , "text/Json",buffer); 
}

// RED LED 제어 함수   
void handleRedledOn(){
  Serial.println("D2 ON Call!!");
  digitalWrite(led,HIGH);
  server.send(200,"text/plain","LED ON!!");
}

// RED LED 제어 함수 
void handleRedledOff(){
  Serial.println("D2 OFF Call!!");
  digitalWrite(led, LOW);
  server.send(200,"text/plain","LED OFF!!");
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
  server.on("/red_led_on",handleRedledOn);
  server.on("/red_led_off",handleRedledOff);
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

  //led Setting
  pinMode(led,OUTPUT);
  digitalWrite(led, LOW);

  Serial.println("");         
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient(); // Client session Receive
  unsigned long currentMillis = millis();
  if(currentMillis - previousMillis >= interval){
    previousMillis = currentMillis;
  if((WiFi.status()) == WL_CONNECTED){

    // 연결 설정 
    WiFiClient WiFiClient;
    HTTPClient httpClient; 
    httpClient.begin(WiFiClient,addDataUrl);
    httpClient.addHeader("Content-Type", "application/json");

    //Json 객체 생성 
    StaticJsonDocument<200> json;

    // 센서 측정값 저장 
    float temp = getTemp();
    int photoresistor_result = getPhotoresistor();
    int count = getCount();

    json["temp"] = temp;
    json["photo"] = photoresistor_result;
    json["count"] = count;

    // Json 형태로 데이터 저장  
    String parsedJsonToString;
    serializeJson(json,parsedJsonToString);

    // 데이터 서버로 전송 
    int httpResponseCode = httpClient.POST(parsedJsonToString);

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
  }
  }
}
