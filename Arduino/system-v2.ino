#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <SoftwareSerial.h>
#include <DHT11.h>
#include "Timer.h"

#define SCREEN_WIDTH 128 
#define SCREEN_HEIGHT 64 
#define SECOND 1000
#define MINUTE 60000
#define HOUR 3600000
#define DAY 86400000

//pin
int pin_waterpump = 3;
int pin_coolingfan = 4;
int pin_led[3] = {5,6,7};
int pin_soil = A0;
int pin_dht = A2;

//variables
String byteToString = "";
String outputString = "";
String oledMessage = "";
float humidity = 0;
float temperature = 0;
float soilmoisture = 0;
int androidInputValue[4] = {0,0,0,0}; //led, timer, coolingfan, waterpump

//control variables
int androidInputCount = 0;
bool commandActive = false;
bool isCoolingfanOn = false;
bool isWaterpumpOn = false;
bool isLedOn = false;

//Millis timer
long long waterpumpStartTime = 0;
long long waterpumpEndTime = 0;
long long coolingfanStartTime = 0;
long long coolingfanEndTime = 0;
long long waterpumpPeriod = 0;
long long coolingfanPeriod = 3*SECOND;
long long ledOnTime = 0;
long long ledOffTime = 0;
long long ledPeriod = 0;

//set utils
Timer timer;
DHT11 dht11(pin_dht);
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);

void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);
  
  pinMode(pin_coolingfan,OUTPUT);
  pinMode(pin_waterpump,OUTPUT);
  for(int i=0; i<3; i++){
    pinMode(pin_led[i],OUTPUT);
  }
  
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;);
  }
  
  drawDisplay("Set up...");
  timer.every(5000,getSensorValue); //5초마다 센서값 읽음
}

void loop() {
  timer.update();
  oledMessage = "T:"+(String)temperature+"C\nH:"+(String)humidity+"%\nS:"+(String)soilmoisture+"%";
  drawDisplay(oledMessage);
  coolingfanEndTime = millis();
  waterpumpEndTime = millis();
  ledOffTime = millis();
  
  while(Serial1.available()) {//안드로이드 앱에서 아두이노로 데이터 전송
    byte data = Serial1.read();//바이트 단위로 읽힘
    
    //JACKPOT의 J가 들어오면 byteToString 값 초기화
    if(data == 'J'){ 
      byteToString = "";
    }
    
    byteToString += char(data); //char로 형변환 안하면 숫자만 저장됨

    //커맨드 들어옴
    if(byteToString == "JACKPOT"){
      Serial.println(byteToString);
      commandActive = true;
      byteToString = "";
      continue;
    }

    //JACKPOT 커맨드 받고 뒤 값 4개 받는 과정
    if(commandActive){
      androidInputValue[androidInputCount] = data - 48;//어차피 숫자라 형변환 필요 없음, 아스키코드 형태라서 -48이나 -'0'해야함
      androidInputCount += 1;
    }

    //값 4개 다 받았으면 출력
    if(androidInputCount == 4){
      androidInputCount = 0;
      commandActive = false;
      if(androidInputValue[2] ==  1) {
        startCoolingfan();
      }else {
        stopCoolingfan();
      }
      
      setWaterpumpPeriod(androidInputValue[3]);
      
      if(androidInputValue[3] != 0) {
        startWaterpump();
      }
      
      if(androidInputValue[1] != 0) {
        setLedTimer(androidInputValue[1]);
      }
      
      ledOn();
    }
  }
  if(isCoolingfanOn && coolingfanEndTime > coolingfanStartTime && coolingfanEndTime - coolingfanStartTime >= coolingfanPeriod) {
    stopCoolingfan();
  }

  if(androidInputValue[3]!=0 && waterpumpEndTime > waterpumpStartTime && waterpumpEndTime - waterpumpStartTime >= waterpumpPeriod){
    startWaterpump();
  }

  if(isWaterpumpOn && waterpumpEndTime > waterpumpStartTime && waterpumpEndTime - waterpumpStartTime >= 3 * SECOND){
    stopWaterpump();
  }
  if(isLedOn && androidInputValue[1] !=0 && ledOffTime - ledOnTime >= ledPeriod){
    ledOff();
  }
  sensorDependency(); 
}

void getSensorValue() {
  delay(2000);
  soilmoisture = analogRead(pin_soil);
  soilmoisture = ((1023-soilmoisture)/1023)*100;
  dht11.read(humidity, temperature);
}

void drawDisplay(String message) {
  display.clearDisplay();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0, 10);
  display.print(message);
  display.display(); 
}

void sensorDependency() {
  if(!isCoolingfanOn && temperature >= 30.0 || humidity >= 70.0) {
    startCoolingfan(); //!!need!! set 5min
  }
  if(!isWaterpumpOn && soilmoisture <= 5.0 && soilmoisture > 0.5) { //오차 방지를 위한 0.5
    setWaterpumpPeriod(1);
    startWaterpump(); //!!need!! set period
  }  
}

void startCoolingfan() {
  isCoolingfanOn = true;
  coolingfanStartTime = millis();
  digitalWrite(pin_coolingfan, HIGH);
}

void stopCoolingfan(){
  isCoolingfanOn = false;
  digitalWrite(pin_coolingfan,LOW);
}

void startWaterpump(){
  isWaterpumpOn = true;
  waterpumpStartTime = millis();
  digitalWrite(pin_waterpump, HIGH);
}

void stopWaterpump(){
  isWaterpumpOn = false;
  digitalWrite(pin_waterpump, LOW);
}

void setWaterpumpPeriod(int key){
  if(key == 0){
    stopWaterpump(); //그냥 끄기 
  }else if(key == 1){
    waterpumpPeriod = 10*SECOND; //10초마다 물주기
  }else if(key == 2){
    waterpumpPeriod = 20*SECOND; //20초마다 물주기
  }else if(key == 3){
    waterpumpPeriod = 30*SECOND; //30초마다 물주기
  }
  waterpumpStartTime = millis();
}

void ledOn(){
  isLedOn = true;
  for(int i=0; i<3; i++){
    if(i<androidInputValue[0]){
      digitalWrite(pin_led[i],HIGH);  
    }else{
      digitalWrite(pin_led[i],LOW);
    }
  }
  if(androidInputValue[0]==0){
    isLedOn = false;
  }
}

void ledOff(){
  isLedOn = false;
  for(int i=0; i<3; i++){
    digitalWrite(pin_led[i],LOW);
  }
}

void setLedTimer(int key) {
  isLedOn = true;
  if(key == 1){
    ledPeriod = 10*SECOND; //10초
  }else if(key == 2){
    ledPeriod = 20*SECOND; //20초
  }else if(key == 3){
    ledPeriod = 30*SECOND; //30초
  }else if(key == 4){
    ledPeriod = 40*SECOND; //40초
  }
  ledOnTime = millis();
  ledOn();
}
