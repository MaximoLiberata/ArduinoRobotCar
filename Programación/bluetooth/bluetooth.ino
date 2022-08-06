#include <SoftwareSerial.h>

#define pinRX 10
#define pinTX 11

SoftwareSerial bluetooth(pinTX, pinRX);

void setup() {
  Serial.begin(9600);
  bluetooth.begin(9600);
}

void loop() {

  if(bluetooth.available() > 0){
    char letter = static_cast<char>(bluetooth.read());
    Serial.println(letter);    
  }

}
