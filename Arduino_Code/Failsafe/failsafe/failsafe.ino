//Pin Mappings
#define BLUETOOTH_RX                  0
#define BLUETOOTH_TX                  1

// Constants
#define BLUETOOTH_BAUD_RATE           9600

//Initialization of Bluetooth Module
SoftwareSerial bluetooth(BLUETOOTH_RX, BLUETOOTH_TX);

void setup() {
  //Initalize Serial Motor for Debugging Purposes
  Serial.begin(9600);

  //Sets Bluetooth to Specified Baud Rate
  bluetooth.begin(BLUETOOTH_BAUD_RATE);
}

void loop() {
  // put your main code here, to run repeatedly:

}
