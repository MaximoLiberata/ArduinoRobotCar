#include <Servo.h>

Servo servoMotor;
const int serveMotorPin = 11;
const int LEFT = 3;
const int LEFT_IN3 = 4;
const int LEFT_IN4 = 2;
const int RIGHT = 6;
const int RIGHT_IN1 = 7;
const int RIGHT_IN2 = 5;

void setup() {
  servoMotor.attach(serveMotorPin);
  pinMode(LEFT_IN3, OUTPUT);
  pinMode(LEFT_IN4, OUTPUT);
  pinMode(LEFT, OUTPUT);
  pinMode(RIGHT_IN1, OUTPUT);
  pinMode(RIGHT_IN2, OUTPUT);
  pinMode(RIGHT, OUTPUT);
}

void loop() {
  digitalWrite(LEFT, HIGH);
  digitalWrite(LEFT_IN3, HIGH);
  digitalWrite(LEFT_IN4, LOW);
  digitalWrite(RIGHT, HIGH);
  digitalWrite(RIGHT_IN1, HIGH);
  digitalWrite(RIGHT_IN2, LOW);
  delay(30);
  digitalWrite(LEFT, LOW);
  digitalWrite(RIGHT, LOW);
  servoMotor.write(90);
  delay(30);
  servoMotor.write(120);
  delay(300);
  servoMotor.write(90);
  delay(300);
  servoMotor.write(60);
  delay(300);
  servoMotor.write(90);
  delay(300);
  delay(86400000);
}
