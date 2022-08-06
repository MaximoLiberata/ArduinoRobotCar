
byte PIN_LINE_LEFT = 0;
byte PIN_LINE_MIDDLE = 9;
byte PIN_LINE_RIGHT = 8;
byte LINE_LEFT;
byte LINE_MIDDLE;
byte LINE_RIGHT;

void setup() {
  Serial.begin(9600);
  pinMode(PIN_LINE_LEFT, INPUT);
  pinMode(PIN_LINE_MIDDLE, INPUT);
  pinMode(PIN_LINE_RIGHT, INPUT);
}

void loop() {
  LINE_LEFT = digitalRead(PIN_LINE_LEFT);
  LINE_MIDDLE = digitalRead(PIN_LINE_MIDDLE);
  LINE_RIGHT = digitalRead(PIN_LINE_RIGHT);

  Serial.print("Left: ");
  Serial.print(LINE_LEFT);
  Serial.print(", Middle: ");
  Serial.print(LINE_MIDDLE);
  Serial.print(", Rigth: ");
  Serial.print(LINE_RIGHT);
  Serial.println();
}
