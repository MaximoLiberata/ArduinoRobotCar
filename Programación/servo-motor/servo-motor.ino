#include <Servo.h>

Servo servoMotor;

void setup() {
  servoMotor.attach(A0);
}

void loop() {
  for(int i=15;i<=165;i++){  
    servoMotor.write(i);
    delay(30);
  }

  for(int i=165;i>15;i--){  
    servoMotor.write(i);
    delay(30);
  }
}
