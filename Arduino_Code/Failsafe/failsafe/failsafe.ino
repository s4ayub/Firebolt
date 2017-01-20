#include <SoftwareSerial.h>

//Pin Mappings
#define BLUETOOTH_RX                  0
#define BLUETOOTH_TX                  1

// Constants
#define BLUETOOTH_BAUD_RATE           9600

//Initialization of Bluetooth Module
SoftwareSerial bluetooth(BLUETOOTH_RX, BLUETOOTH_TX);

//Global variables
bool bluetoothConnectionFlag = 0;
String messageFromBluetooth;

void checkBluetoothConnection() {
  bluetooth.print("AT");
  if (bluetooth.available()) {
    char charFromBluetooth;
    while (bluetooth.available()) {
    delay(10);
    messageFromBluetooth += charFromBluetooth;
    }
  }
  if (messageFromBluetooth != "OK") {
    Serial.print("Turn down motor");
  }
  messageFromBluetooth = "";
}

void interruptSetup() {
  noInterrupts();
  TCCR2B = 0;
  TCCR2B |= CS20;
  TIMSK2 |= TOIE;
  interrupts();
}

//Possible alternatives: TIM2_OVF, Timer2_OVF_vect
void ISR(TIMER2_OVF) {
  bluetoothConnectionFlag ^= true;
}

void setup() {
  //Initalize Serial Motor for Debugging Purposes
  Serial.begin(9600);

  //Sets Bluetooth to Specified Baud Rate
  bluetooth.begin(BLUETOOTH_BAUD_RATE);
}

void loop() {
  bluetoothConnectionFlag ? checkBluetoothConnection :
}
