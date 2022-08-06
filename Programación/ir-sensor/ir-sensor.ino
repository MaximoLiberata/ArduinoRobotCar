#include <IRremote.h>


const byte Sensor = 11;
uint16_t remoteValue = 0;
enum remoteType {
    UP    = 70,
    LEFT  = 68,
    DOWN  = 21,
    RIGHT = 67,
    OK    = 64,
    ONE   = 22,
    TWO   = 25,
    THREE = 13,
    FOUR  = 12,
    FIVE  = 24,
    SIX   = 94,
    SEVEN = 8,
    EIGHT = 28,
    NINE  = 90,
    CERO  = 82
};

void setup() {
    Serial.begin(9600);
    IrReceiver.begin(Sensor);
}

void loop() {

    if (IrReceiver.decode()) {

        switch(IrReceiver.decodedIRData.command) {
            case remoteType::UP: {
                Serial.println("UP");
                break;
            }
            case remoteType::DOWN: {
                Serial.println("DOWN");
                break;
            }
            case remoteType::LEFT: {
                Serial.println("LEFT");
                break;
            }
            case remoteType::RIGHT: {
                Serial.println("RIGHT");
                break;
            }
        }

        IrReceiver.resume();

    }

}
