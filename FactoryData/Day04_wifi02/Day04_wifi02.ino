#include <WiFi.h>
#include <WiFiClient.h>
#include <WebServer.h>
#include "oled_u8g2.h"
#include <stdio.h>
#include <string.h>

//WIFI Name
const char* ssid = "SmartFactory"; 

//WIFI Pwd
const char* password = "inha4885"; 

WebServer server(80); // Create WebServer Object, port 

int sensor = A2;    
int Vo;
float R1 = 10000;
float logR2, R2, T, Tc, Tf;
float c1 = 1.009249522e-03, c2 = 2.378405444e-04, c3 = 2.019202697e-07;

//OLED
OLED_U8G2 oled;


float getTemp(){
  Vo = analogRead(sensor);                               // 센서의 값을 읽고 온도로 변환
  R2 = R1 * (4095.0 / (float)Vo - 1.0);
  logR2 = log(R2);
  T = (1.0 / (c1 + c2*logR2 + c3*logR2*logR2*logR2));
  Tc = T - 273.15;
  return Tc;
}

void handleRootEvent() { // root(/)
  String clientIp = server.client().remoteIP().toString();

  Tc = getTemp();
  Tf =(Tc * 9.0/5.0) +32.0;
  int a1,a2,a3,a4;
  String str = server.client().remoteIP().toString();
  sscanf(str.c_str(),"%d.%d.%d.%d", &a1, &a2,&a3,&a4);
  //char *token = strtok(str,"."); // 첫 번째 token을 반환한다
  //int count =0;
  String privateIP ="";
  privateIP = String(a1)+".XXX.XXX."+String(a2);
  // while (token != NULL) // delimiter 중 하나가 str[]에 존재할 경우 계속해서 token을 출력한다.
  // {
  //   if(count==1 || count ==2){
  //     privateIP+=*+".";
  //   }
  //   else{
  //     privateIP+=token+"."
  //   }
  //   token = strtok(NULL, ".");
  //   count++;
  // }
  String message = "Welcome Inha SmartFactory WebServer!\n\n";
  message += "Temperature: "+String(Tc)+"C "+"( "+String(Tf) + "F)\n";
  message += "Your IP: "+privateIP+"\n";

  Serial.print("Main Page from :"); // Page Enter 
  Serial.println(clientIp);
  server.send(200 , "text/plain", message);
  Serial.println(message); 
  Serial.print(Tc);
  Serial.print("C (");
  Serial.print(Tf);
  Serial.print("F)");            
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200); // ESP32 baud rate 
  WiFi.mode(WIFI_STA); 
  WiFi.begin(ssid, password); 
  Serial.println("");
  
  //  wait for connection
  while(WiFi.status() != WL_CONNECTED) {
  delay(500);
  Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: "); 
  Serial.println(WiFi.localIP()); //print assigned ip address 

  // root(/) Enter Function 
  server.on("/", handleRootEvent); //event handlong function
  server.begin(); // Server Start 
  Serial.println("HTTP server started");

  //OLED Setup
  oled.setup();        
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient(); // Client session Receive
  oled.setLine(1, "*Welcom KGU");               // OLED 첫 번째 줄 : 시스템 이름
  oled.setLine(2, "smart Factory");              // OLED 두 번째 줄 : count 값
  oled.setLine(3, "---------------------");   // OLED 세 번째 줄 
  oled.display();
  delay(500);
}
