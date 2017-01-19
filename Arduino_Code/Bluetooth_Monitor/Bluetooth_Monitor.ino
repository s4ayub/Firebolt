#include <SoftwareSerial.h>

//Pin Mappings
#define BLUETOOTH_RX 0
#define BLUETOOTH_TX 1

//Constants
#define BLUETOOTH_BAUD_RATE   57600

//Initialization of Bluetooth Module
SoftwareSerial bluetooth(BLUETOOTH_RX, BLUETOOTH_TX);

//Global Variables
String inputFromBluetoothString; //Concatenated string from bluetooth

void setup() {
  Serial.begin(9600);
  bluetooth.begin(BLUETOOTH_BAUD_RATE);
}

void loop() {

  //Checks for incoming bytes from bluetooth, saves into string
  if (bluetooth.available()) {
    char inputFromBluetoothChar;
    while (bluetooth.available()) {
      delay(10);
      Serial.println(bluetooth.read());
      inputFromBluetoothChar = bluetooth.read();
      inputFromBluetoothString += inputFromBluetoothChar;
    }
  }

  //Prints existing string from bluetooth to serial monitor
  if(inputFromBluetoothString != "") {
    Serial.println(inputFromBluetoothString);
    inputFromBluetoothString = "";
  }
   
}
