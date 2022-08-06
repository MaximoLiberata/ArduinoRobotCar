#include <ServoEasing.hpp>


// Servo Motor
ServoEasing servoMotor;

// Ultra Sonic
byte TRIG = 13;
byte ECHO = 12;

void setup() {
  Serial.begin(9600);
  servoMotor.attach(A5, 90);
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
}

int calculateDistance () {
  digitalWrite(TRIG, HIGH);
  delay(1);
  digitalWrite(TRIG, LOW);
  int duration = pulseIn(ECHO, HIGH);
  return (duration / 58.2);
}

void loop() {

  for(int i = 0; i <= 180; i++){  
    servoMotor.easeTo(i, 100);
    Serial.println(calculateDistance());
  }

  for(int i = 180; i > 0; i--){  
    servoMotor.easeTo(i, 100);
    Serial.println(calculateDistance());
  }

}
