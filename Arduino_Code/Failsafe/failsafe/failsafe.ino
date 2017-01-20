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
int interruptCounter = 0;

void checkBluetoothConnection() {
  bluetoothConnectionFlag = false;
  bluetooth.print("AT");
  if (bluetooth.available()) {
    //char charFromBluetooth;
    while (bluetooth.available()) {
    delay(200);
    Serial.print(bluetooth.read());
    //messageFromBluetooth += charFromBluetooth;
    }
  }
  //Serial.println(messageFromBluetooth);
  if (messageFromBluetooth != "OK") {
    //Serial.println("Turn down motor");
  }
  messageFromBluetooth = "";
}

void interruptSetup() {
  noInterrupts();
  TCCR2B = 0;
  TCCR2B |= (1 << (CS22 + CS20));
  TIMSK2 |= (1 << TOIE1);
  interrupts();
}

ISR(TIMER2_OVF_vect) {
  noInterrupts();
  interruptCounter++;
  if (interruptCounter == 1000) {
    bluetoothConnectionFlag = true;
    interruptCounter = 0;
  }
  interrupts();
}

void setup() {
  //Initalize Serial Motor for Debugging Purposes
  Serial.begin(9600);

  //Sets Bluetooth to Specified Baud Rate
  bluetooth.begin(BLUETOOTH_BAUD_RATE);

  interruptSetup();
}

void loop() {
  //Serial.println(bluetoothConnectionFlag);
  if (bluetoothConnectionFlag) {
    checkBluetoothConnection();
  }
}
