#include <SoftwareSerial.h>
SoftwareSerial BTSerial(4,5); //TX, RX 
int coolingFan = 6;
int led[4]= {7,8,9,10};
int ledOn[4]={0,0,0,0};
int ledTimerTime[5] = {0,10,20,30,60};
String byteTmp = "";
int ledNum=0, ledTimer=0, panState=0;
unsigned long prevTime, currTime;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  for(int i=0; i<2; i++){
    pinMode(led[i],OUTPUT);
  }
  pinMode(coolingFan, OUTPUT);
}

void loop() {
  while(BTSerial.available()){//앱에서 아두이노로 데이터 전송하는 부분 
    byte data = BTSerial.read(); //안드로이드에서 보낼 때 길이수 맞춰서 보내도록 하기
    byteTmp+=data;
  }
  
  if(byteTmp.length()>=6){
    ledNum = (byteTmp.substring(0,2)).toInt()-48;
    ledTimer = (byteTmp.substring(2,4)).toInt()-48;
    panState = (byteTmp.substring(4,6)).toInt()-48;
    if(ledTimer > 0){
      prevTime = millis();
    }
    byteTmp="";
  }
  
  //LED Control
  if(ledNum==0){ // 다끔 
    for(int i=0; i<4; i++){
      ledOn[i]=0;
    }
  }else if(ledNum==1){ //하나만 켠다 
    ledOn[0]=1;
    for(int i=1; i<4; i++){
      ledOn[i]=0;
    }
  }else if(ledNum==2){ //두개 켠다
    for(int i=0; i<4; i++){
      if(i<2){ledOn[i]=1;}
      else{ledOn[i]=0;}
    }
  }else if(ledNum==3){ //세개 켠다 
    for(int i=0; i<3; i++){
      ledOn[i]=1;
    }
    ledOn[3]=0;
  }else if(ledNum==4){
    for(int i=0; i<4; i++){
      ledOn[i]=1;
    }
  }
  
  for(int i=0; i<4; i++){
    if(ledOn[i]==0){
      digitalWrite(led[i],LOW);
    }else{
      digitalWrite(led[i],HIGH);
    }
  }

  //LED set Timer
  if(ledTimer != 0){
    currTime = millis();
    if(currTime - prevTime >= ledTimerTime[ledTimer] ){
      allLedTurnOff();
      prevTime = currTime;
      BTSerial.write("Timer is done");
    }
  }

  if(panState == 0){//팬 끔
    digitalWrite(coolingFan, LOW);
  }else{//팬 켬
    digitalWrite(coolingFan, HIGH);
  }

  
  while(Serial.available()){ //아두이노에서 앱으로 데이터 전송하는 부분, 근데 당장은 이 부분 기능은 구현하지 않았음 
    byte data = Serial.read();
    BTSerial.write(data);//앱의 터미널에서 확인 가능 
  }

}

void allLedTurnOff(){
  for(int i=0; i<4; i++){
    ledOn[i]=0;
    digitalWrite(led[i],LOW);
  }
}
