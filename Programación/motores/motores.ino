
int LEFT = 3;
int LEFT_IN3 = 4;
int LEFT_IN4 = 2;
int RIGHT = 6;
int RIGHT_IN1 = 7;
int RIGHT_IN2 = 5;

void setup() {
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
  delay(1500);

  digitalWrite(LEFT, LOW);
  digitalWrite(RIGHT, LOW);
  delay(1500);
  
  digitalWrite(LEFT, HIGH);
  digitalWrite(LEFT_IN3, LOW);
  digitalWrite(LEFT_IN4, HIGH);
  digitalWrite(RIGHT, HIGH);
  digitalWrite(RIGHT_IN1, LOW);
  digitalWrite(RIGHT_IN2, HIGH);
  delay(1500);

  digitalWrite(LEFT, LOW);
  digitalWrite(RIGHT, LOW);
  delay(1500);

  
  digitalWrite(RIGHT, HIGH);
  digitalWrite(RIGHT_IN1, LOW);
  digitalWrite(RIGHT_IN2, HIGH);
  delay(1500);

  digitalWrite(LEFT, LOW);
  digitalWrite(RIGHT, LOW);
  delay(1500);

  digitalWrite(LEFT, HIGH);
  digitalWrite(LEFT_IN3, LOW);
  digitalWrite(LEFT_IN4, HIGH);
  delay(1500);
  
  digitalWrite(LEFT, LOW);
  digitalWrite(RIGHT, LOW);
  delay(1500);
  
}
