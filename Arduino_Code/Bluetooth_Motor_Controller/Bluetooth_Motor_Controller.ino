#include <SoftwareSerial.h>
#include <Servo.h>

//Pin Mappings
#define BLUETOOTH_RX                  0
#define BLUETOOTH_TX                  1
#define VICTOR_SP_PIN                 9

//Constants
#define BLUETOOTH_BAUD_RATE           9600
#define MAX_REQUESTED_SPEED_FORWARD   100
#define MAX_REQUESTED_SPEED_REVERSE   -100
#define MAX_MOTOR_SPEED_FORWARD       121
#define MAX_MOTOR_SPEED_REVERSE       61
#define MOTOR_STOP_VALUE              91
#define INCREMENT_DELAY               20

//Initialization of Bluetooth Module
SoftwareSerial bluetooth(BLUETOOTH_RX, BLUETOOTH_TX);

//Initialization of Motor Controller through PWM
Servo victorSP;

//Global Variables
String requestedSpeed;
int currentMotorSpeed = MOTOR_STOP_VALUE;

//Function to Check if String is A Positive or Negative Number
bool isValidNumber (String str) {
  int startingCounter = 0;
  if (str.charAt(startingCounter) == '-'){
    startingCounter = 1;
  }
  for (int i = startingCounter; i < str.length(); i++){
    if (!isDigit(str.charAt(i))){
      return false;
    }
  }
  return true;
}

void setup() {
  //Initalize Serial Motor for Debugging Purposes
  Serial.begin(9600);

  //Sets Bluetooth to Specified Baud Rate
  bluetooth.begin(BLUETOOTH_BAUD_RATE);

  //Assigns Motor Controller to Specified Pin
  victorSP.attach(VICTOR_SP_PIN);
}

void loop() {

  //Checks for incoming bytes from bluetooth, saves into string
  if (bluetooth.available()) {
    char inputFromBluetoothChar;
    while (bluetooth.available()) {
      delay(10); //Delay Required for Baud Rate of 9600
      inputFromBluetoothChar = bluetooth.read();
      requestedSpeed += inputFromBluetoothChar; //Concatenate Characters
    }
  }

  //Checks if anything exists inside requestedSpeed variable.
  if(requestedSpeed != "") {

    //Serial.println(requestedSpeed);
    
    //When requestedSpeed is a valid number, sets motor to requestedSpeed
    if(isValidNumber(requestedSpeed)){
      int newMotorSpeed = map(requestedSpeed.toInt(), MAX_REQUESTED_SPEED_REVERSE, MAX_REQUESTED_SPEED_FORWARD, MAX_MOTOR_SPEED_REVERSE, MAX_MOTOR_SPEED_FORWARD);
      if (currentMotorSpeed < newMotorSpeed) {
        for (int i = currentMotorSpeed; i < newMotorSpeed; i++){
          victorSP.write(i);
          delay(INCREMENT_DELAY);
        }
      }
      else {
        for (int i = currentMotorSpeed; i > newMotorSpeed; i--){
          victorSP.write(i);
          delay(INCREMENT_DELAY);
        }
      }
    }

    //When Stop Button on App is Pressed, Stops Motor
    else if(requestedSpeed == "STOP"){
      victorSP.write(MOTOR_STOP_VALUE);
    }
    
    requestedSpeed = "";
  }
   
}
