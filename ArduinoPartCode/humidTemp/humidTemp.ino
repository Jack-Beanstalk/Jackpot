#include <DHT11.h>
#include "Timer.h"
int pin=A2; //출력 핀 아날로그2
DHT11 dht11(pin);
Timer t;
void setup(){
  Serial.begin(9600);
  t.every(2000, getValue); //매 2초마다 getValue()함수 호출
}

float humi=0, temp=0;

void loop(){
  t.update(); //millis로 인터럽트하는 것과 같음
}

void getValue(){ //습도, 온도 값 출력
  dht11.read(humi, temp);
  Serial.print("temperature: ");
  Serial.println(temp);
  Serial.print("humidity: ");
  Serial.println(humi);
}
