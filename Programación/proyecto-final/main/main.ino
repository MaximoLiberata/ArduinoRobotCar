#include <SoftwareSerial.h>
#include <ServoEasing.hpp>


// General
long previousTimeMotor = 0;
long previousTimeLine = 0;
long previousTimeDistance = 0;
long lineInterval = 750000;
int motorInterval = 100;
int limitAutoControlDistance = 10;
bool IS_MOTOR_OFF = true;
bool INIT_CALCULATE_DISTANCE = false;
bool IS_LINE_TRACKING = false;
bool LINE_TRACKING_RL = false;
bool IS_AUTO_CONTROL = false;

// Bluetooth
#define pinRX 10
#define pinTX 11
SoftwareSerial bluetooth(pinTX, pinRX);

// Bluetooth Commands
enum Commands {
  CC_UP = 0,
  CC_DOWN = 1,
  CC_LEFT = 2,
  CC_RIGHT = 3,
  LT_START = 4,
  LT_STOP = 5,
  US_START = 6,
  US_STOP = 7
};

// Motors
const byte MOTOR_LEFT = 3;
const byte MOTOR_LEFT_IN3 = 4;
const byte MOTOR_LEFT_IN4 = 2;
const byte MOTOR_RIGHT = 6;
const byte MOTOR_RIGHT_IN1 = 7;
const byte MOTOR_RIGHT_IN2 = 5;


// Line Sensor
const byte PIN_LINE_LEFT = 0;
const byte PIN_LINE_MIDDLE = 9;
const byte PIN_LINE_RIGHT = 8;

// Servo Motor
ServoEasing servoMotor;

// Ultra Sonic
const byte TRIG = 13;
const byte ECHO = 12;

void setup() {
  bluetooth.begin(9600);
  servoMotor.attach(A5, 90);
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(MOTOR_LEFT, OUTPUT);
  pinMode(MOTOR_LEFT_IN3, OUTPUT);
  pinMode(MOTOR_LEFT_IN4, OUTPUT);
  pinMode(MOTOR_RIGHT, OUTPUT);
  pinMode(MOTOR_RIGHT_IN1, OUTPUT);
  pinMode(MOTOR_RIGHT_IN2, OUTPUT);
  pinMode(PIN_LINE_LEFT, INPUT);
  pinMode(PIN_LINE_MIDDLE, INPUT);
  pinMode(PIN_LINE_RIGHT, INPUT);
}

int asciiCodeToInt(int code) {
  return (int)static_cast<char>(code) - 48;
}

int calculateDistance () {
  digitalWrite(TRIG, HIGH);
  delay(1);
  digitalWrite(TRIG, LOW);
  int duration = pulseIn(ECHO, HIGH);
  return (duration / 58.2);
}

void motorMove (int command, int leftSpeed = 0, int rightSpeed = 0) {

  switch(command) {
         case Commands::CC_UP: {
            digitalWrite(MOTOR_LEFT_IN3, HIGH);
            digitalWrite(MOTOR_LEFT_IN4, LOW);
            digitalWrite(MOTOR_RIGHT_IN1, HIGH);
            digitalWrite(MOTOR_RIGHT_IN2, LOW);
            break;
        }
        case Commands::CC_DOWN: {
            digitalWrite(MOTOR_LEFT_IN3, LOW);
            digitalWrite(MOTOR_LEFT_IN4, HIGH);
            digitalWrite(MOTOR_RIGHT_IN1, LOW);
            digitalWrite(MOTOR_RIGHT_IN2, HIGH);
            break;
        }
        case Commands::CC_LEFT: {
            digitalWrite(MOTOR_LEFT_IN3, LOW);
            digitalWrite(MOTOR_LEFT_IN4, HIGH);
            digitalWrite(MOTOR_RIGHT_IN1, HIGH);
            digitalWrite(MOTOR_RIGHT_IN2, LOW);
            break;
        }
        case Commands::CC_RIGHT: {
            digitalWrite(MOTOR_RIGHT_IN1, LOW);
            digitalWrite(MOTOR_RIGHT_IN2, HIGH);
            digitalWrite(MOTOR_LEFT_IN3, HIGH);
            digitalWrite(MOTOR_LEFT_IN4, LOW);
            break;
        }
        default: {
          return;
          break;
        }
    }

    if (leftSpeed > 0 && rightSpeed > 0) {
      analogWrite(MOTOR_LEFT, leftSpeed);
      analogWrite(MOTOR_RIGHT, rightSpeed);
    }
    else if (leftSpeed > 0) {
      analogWrite(MOTOR_LEFT, leftSpeed);
    }
    else if (rightSpeed > 0) {
      analogWrite(MOTOR_RIGHT, rightSpeed);
    }
    else {
      digitalWrite(MOTOR_LEFT, HIGH);
      digitalWrite(MOTOR_RIGHT, HIGH);
    }

}

void lineTracking(bool LEFT, bool MIDDLE, bool RIGHT) {

  long currentMicros = micros();

  if (INIT_CALCULATE_DISTANCE == false) {
    INIT_CALCULATE_DISTANCE = true;
    previousTimeDistance = currentMicros;
    digitalWrite(TRIG, HIGH);
  }
  else if ((currentMicros - previousTimeDistance) >= 500) {

    INIT_CALCULATE_DISTANCE = false;

    digitalWrite(TRIG, LOW);
    int duration = pulseIn(ECHO, HIGH);
    int distance = duration / 58.2;

    if (distance <= 5) {

      if (IS_MOTOR_OFF == false) {
        IS_MOTOR_OFF = true;
        motorMove(Commands::CC_DOWN);
        delay(75);
        digitalWrite(MOTOR_LEFT, LOW);
        digitalWrite(MOTOR_RIGHT, LOW);
      }

      return;

    }
    else {
      IS_MOTOR_OFF = false;
    }

  }

  if (IS_MOTOR_OFF) {
    return;
  }

  long interval = currentMicros - previousTimeLine;

  if (LEFT == true && MIDDLE == true && RIGHT == false && (interval >= lineInterval || LINE_TRACKING_RL == true)) {
    LINE_TRACKING_RL = true;
    motorMove(Commands::CC_LEFT, 140, 140);
    previousTimeLine = micros();
  }
  else if (LEFT == false && MIDDLE == true && RIGHT == true && (interval >= lineInterval || LINE_TRACKING_RL == true)) {
    LINE_TRACKING_RL = true;
    motorMove(Commands::CC_RIGHT, 140, 140);
    previousTimeLine = micros();
  }
  else if (MIDDLE) {
    LINE_TRACKING_RL = false;
    motorMove(Commands::CC_UP, 160, 160);
  }
  else if (!LEFT) {
    LINE_TRACKING_RL = false;
    motorMove(Commands::CC_RIGHT, 120, 120);
  }
  else if (!RIGHT) {
    LINE_TRACKING_RL = false;
    motorMove(Commands::CC_LEFT, 120, 120);
  }

}

void autoControl () {

  int distance = calculateDistance();

  if (distance > limitAutoControlDistance) {
    IS_MOTOR_OFF = false;
    motorMove(Commands::CC_UP, 170, 170);
  }
  else if (distance <= limitAutoControlDistance && IS_MOTOR_OFF == false) {

    IS_MOTOR_OFF = true;
    motorMove(Commands::CC_DOWN, 170, 170);
    delay(200);
    digitalWrite(MOTOR_LEFT, LOW);
    digitalWrite(MOTOR_RIGHT, LOW);
    delay(50);

    int lastDistance = distance;
    int angle = 0;

    servoMotor.easeTo(0, 400);

    for(byte i = 0; i <= 180; i += 10){

      servoMotor.easeTo(i, 400);
      distance = calculateDistance();

      if (distance > calculateDistance()) {
        lastDistance = distance;
        angle = i;
      }

    }

    for(byte i = 180; i > 0; i -= 10){

      servoMotor.easeTo(i, 400);
      distance = calculateDistance();

      if (distance > lastDistance) {
        lastDistance = distance;
        angle = i;
      }

    }

    servoMotor.write(90);
    delay(250);

    if (lastDistance <= limitAutoControlDistance) {
      motorMove(Commands::CC_DOWN, 170, 170);
      delay(300);
      motorMove(Commands::CC_RIGHT, 170, 170);
      delay(500);
    }
    else {

      motorMove(Commands::CC_DOWN, 170, 170);
      delay(200);

      if (angle <= 90) {

        motorMove(Commands::CC_RIGHT);

        if (angle >= 70 && angle < 80) {
          delay(115);
        }
        else if (angle >= 60  && angle < 70) {
          delay(140);
        }
        else if (angle >= 50 && angle < 60) {
          delay(175);
        }
        else if (angle >= 40 && angle < 50) {
          delay(230);
        }
        else if (angle >= 30 && angle < 40) {
          delay(280);
        }
        else if (angle >= 20 && angle < 30) {
          delay(320);
        }
        else if (angle >= 10 && angle < 20) {
          delay(450);
        }

      }
      else {

        motorMove(Commands::CC_LEFT);

        if (angle > 100 && angle <= 110) {
          delay(115);
        }
        else if (angle > 110  && angle <= 120) {
          delay(140);
        }
        else if (angle > 120 && angle <= 130) {
          delay(175);
        }
        else if (angle > 130 && angle <= 140) {
          delay(230);
        }
        else if (angle > 140 && angle <= 150) {
          delay(280);
        }
        else if (angle > 150 && angle <= 160) {
          delay(320);
        }
        else if (angle > 160 && angle <= 170) {
          delay(450);
        }

      }

    }

  }

}


void loop() {

  if (IS_LINE_TRACKING) {
    lineTracking(
      (bool)digitalRead(PIN_LINE_LEFT),
      (bool)digitalRead(PIN_LINE_MIDDLE),
      (bool)digitalRead(PIN_LINE_RIGHT)
    );
  }
  else if (IS_AUTO_CONTROL) {
    autoControl();
  }

  if (bluetooth.available() > 0) {

    const int command = asciiCodeToInt(bluetooth.read());

    if (!IS_AUTO_CONTROL) {
      if (command == Commands::LT_START && IS_LINE_TRACKING == false) {
        IS_LINE_TRACKING = true;
      }
      else if (command == Commands::LT_STOP && IS_LINE_TRACKING == true) {
        IS_LINE_TRACKING = false;
      }
    }

    if (!IS_LINE_TRACKING) {
      if (command == Commands::US_START && IS_AUTO_CONTROL == false) {
        IS_AUTO_CONTROL = true;
      }
      else if (command == Commands::US_STOP && IS_AUTO_CONTROL == true) {
        IS_AUTO_CONTROL = false;
      }
    }

    if (IS_LINE_TRACKING == false && IS_AUTO_CONTROL == false) {

      if (command == Commands::CC_UP) {

        const int distance = calculateDistance();

        if (distance <= 20) {

          if (IS_MOTOR_OFF == false) {
            IS_MOTOR_OFF = true;
            digitalWrite(MOTOR_LEFT, LOW);
            digitalWrite(MOTOR_RIGHT, LOW);
          }

          return;

        }
        else if (distance <= 50) {
          IS_MOTOR_OFF = false;
          motorMove(command, 120, 120);
          previousTimeMotor = millis();
          return;
        }
        else if (distance <= 70) {
          IS_MOTOR_OFF = false;
          motorMove(command, 150, 150);
          previousTimeMotor = millis();
          return;
        }

      }

      IS_MOTOR_OFF = false;
      motorMove(command);
      previousTimeMotor = millis();

    }

  }
  else if (IS_AUTO_CONTROL == false && IS_MOTOR_OFF == false && millis() - previousTimeMotor >= motorInterval) {

    IS_MOTOR_OFF = true;
    digitalWrite(MOTOR_LEFT, LOW);
    digitalWrite(MOTOR_RIGHT, LOW);
    previousTimeMotor = millis();

  }

}
