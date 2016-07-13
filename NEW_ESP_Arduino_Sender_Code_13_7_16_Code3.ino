
#include <PortExpander_I2C.h> //Port Expander code
#include<EEPROM.h>
#include "Wire.h"
#include<SoftwareSerial.h>

///////////////NOTE: LED +ve connected to Vcc, LED -ve connected to PCF pin

PortExpander_I2C pe(0x20);
//PortExpander_I2C pe_Fs(0x27);
PortExpander_I2C pe_Fs(0x20);

//int pushButton_L1 = 2;   //light_1        // Digital pin 2 of arduino
int pushButton_L2 = 3;   //Light_2        // Digital pin 3 of arduino
int pushButton_F1 = 4;   //Fan            // Digital pin 4 of arduino
int pushButton_F_up = 5; //Fan_Speed Up   // Digital pin 5 of arduino
int pushButton_F_down = 6; //Fan_Speed Down // Digital pin 5 of arduino
int Fan_Speed;           // Stores the current fan speed

SoftwareSerial receiver(10, 11);
void Button();
void Update_Buttons();

// Touch Button

int capSensePin_L1 = 2;
int capSensePin_L2 = 3;
int capSensePin_F1 = 4;
int capSensePin_F_up = 5;
int capSensePin_F_down = 6;

int touchedCutoff = 65;
//bool stat = 0;

void setup()
{
  //EEPROM.write(3,0);  // intialising the fan speed to zero
  Serial.begin(57600);
  receiver.begin(57600);

  Wire.begin();
  //pinMode(pushButton_L1, INPUT);

  pinMode(capSensePin_L1, INPUT);
  pinMode(capSensePin_L2, INPUT);
  pinMode(capSensePin_F1, INPUT);
  pinMode(capSensePin_F_up, INPUT);
  pinMode(capSensePin_F_down, INPUT);
  /*
    pinMode(pushButton_L2, INPUT);
    pinMode(pushButton_F1, INPUT);
    pinMode(pushButton_F_up, INPUT);
    pinMode(pushButton_F_down, INPUT);
  */

  pe.init();
  int i = 0;
  for ( int i = 0; i < 8; i++ )
  {
    pe.pinMode(i, OUTPUT);
    pe.digitalWrite(i, HIGH);
  }


pe_Fs.init();  // pcf for Fan_Speed LEDs
int j = 0;
for ( int j = 0; j < 8; j++ )
{
  pe.pinMode(j, OUTPUT);
  pe.digitalWrite(j, HIGH);
}
}



String status_update = "";

void loop()
{
  /*
    while(1)
    if(Serial.available())
    Serial.print(char(Serial.read()));
  */
  Button();
  Update_Buttons();
}


void Button()
{

  ///////////////////////////////////// Light_1//////////////////////////////////////////

  bool L1 = EEPROM.read(0);                      // read the value which was last saved in EEPROM
  if (L1 == 1)
    pe.digitalWrite(0, LOW);
  else
    pe.digitalWrite(0, HIGH);

  //int buttonState_L1 = digitalRead(pushButton_L1);
  // int buttonState_L1 = digitalRead(capSensePin_L1);
  int buttonState_L1;
  if (readCapacitivePin(capSensePin_L1) > touchedCutoff)
  {
    //    stat = !stat;
    //digitalWrite(LEDPin, stat);
    Serial.println(readCapacitivePin(capSensePin_L1));
    buttonState_L1 = 1;
    delay(300);

  }
  else
  {
    buttonState_L1 = 0;
  }

  // Every 500 ms, print the value of the capacitive sensor
  if ( (millis() % 500) == 0)
  {
    //  Serial.print("Capacitive Sensor on Pin 2 reads: ");
    //  Serial.println(readCapacitivePin(capSensePin_L1));
  }

  if (buttonState_L1 == 1)                        // if switch is pressed then check the value in EEPROM
  {
    //Serial.println("Entered if statement");
    if (L1 == 1)                                  // if data in EEPROM is LOW ie Led is on then turn off the led
    {
      pe.digitalWrite(0, HIGH);
      EEPROM.write(0, L1 ^ 1);
      Serial.print("L1=0");
      receiver.print("L1=0");
    }
    else                                          // if data in EEPROM is HIGH ie Led is off then turn on the led
    {
      pe.digitalWrite(0, LOW);
      EEPROM.write(0, L1 ^ 1);
      Serial.print("L1=1");
      receiver.print("L1=1");
    }
  }

  //while (digitalRead(pushButton_L1) == 1);
  while (digitalRead(capSensePin_L1) == 1);

  status_update += "L1=";
  status_update += L1;


  //////////////////////////////////////// Light_2///////////////////////////////////////////

  bool L2 = EEPROM.read(1);                        // read the value which was last saved in EEPROM
  if (L2 == 1)
    pe.digitalWrite(1, LOW);
  else
    pe.digitalWrite(1, HIGH);

  // int buttonState_L2 = digitalRead(pushButton_L2);

  int buttonState_L2;
  if (readCapacitivePin(capSensePin_L2) > touchedCutoff)
  {
    // stat = !stat;
    //digitalWrite(LEDPin, stat);
    Serial.println(readCapacitivePin(capSensePin_L2));
    buttonState_L2 = 1;
    delay(300);
  }
  else
  {
    buttonState_L2 = 0;
  }

  // Every 500 ms, print the value of the capacitive sensor
  if ( (millis() % 500) == 0)
  {
    // Serial.print("Capacitive Sensor on Pin 2 reads: ");
    // Serial.println(readCapacitivePin(capSensePin_L2));
  }

  if (buttonState_L2 == 1)                         // if switch is pressed then check the value in EEPROM
  {
    if (L2 == 1)                                   // if data in EEPROM is LOW ie Led is on then turn off the led
    {
      pe.digitalWrite(1, HIGH);
      EEPROM.write(1, L2 ^ 1);
      Serial.print("L2=0");
      receiver.print("L2=0");
    }
    else                                           // if data in EEPROM is HIGH ie Led is off then turn on the led
    {
      pe.digitalWrite(1, LOW);
      EEPROM.write(1, L2 ^ 1);
      Serial.print("L2=1");
      receiver.print("L2=1");
    }
  }
  // while (digitalRead(pushButton_L2) == 1);
  while (digitalRead(capSensePin_L2) == 1);


  status_update += "&L2=";
  status_update += L2;


  /////////////////////////////////////// Fan_1///////////////////////////////////////

  bool F1 = EEPROM.read(2);                        // read the value which was last saved in EEPROM
  if (F1 == 1)
    pe.digitalWrite(2, LOW);
  else
    pe.digitalWrite(2, HIGH);

  //int buttonState_F1 = digitalRead(pushButton_F1);


  int buttonState_F1;
  if (readCapacitivePin(capSensePin_F1) > touchedCutoff)
  {
    // stat = !stat;
    //digitalWrite(LEDPin, stat);
    Serial.println(readCapacitivePin(capSensePin_F1));
    buttonState_F1 = 1;
    delay(300);
  }
  else
  {
    buttonState_F1 = 0;
  }

  // Every 500 ms, print the value of the capacitive sensor
  if ( (millis() % 500) == 0)
  {
    //  Serial.print("Capacitive Sensor on Pin 2 reads: ");
    //  Serial.println(readCapacitivePin(capSensePin_F1));
  }


  if (buttonState_F1 == 1)                         // if switch is pressed then check the value in EEPROM
  {
    if (F1 == 1)                                   // if data in EEPROM is LOW ie Led is on then turn off the led
    {
      pe.digitalWrite(2, HIGH);
      EEPROM.write(2, F1 ^ 1);
      Serial.print("F1=0");
      receiver.print("F1=0");
    }
    else                                           // if data in EEPROM is HIGH ie Led is off then turn on the led
    {
      pe.digitalWrite(2, LOW);
      EEPROM.write(2, F1 ^ 1);
      Serial.print("F1=1");
      receiver.print("F1=1");
    }
  }
  // while (digitalRead(pushButton_F1) == 1);
  while (digitalRead(capSensePin_F1) == 1);

  status_update += "&F1=";
  status_update += F1;


  ////////////////////////////////////////////Fan_Speed///////////////////////////////////////////////////

  Fan_Speed = EEPROM.read(3);  // it will telll the fan speed to the dimmer circuit

  for (int i = 0; i < Fan_Speed; i++) // Fan_Speed LEDs
    pe_Fs.digitalWrite(i, LOW);

  for (int j = 7; j >= Fan_Speed; j--)
    pe_Fs.digitalWrite(j, HIGH);

 if (Fs == "1")
  {
    digitalWrite(2, HIGH);  //D3
    digitalWrite(3, HIGH);   //D2
    digitalWrite(4, HIGH);  //D1
    digitalWrite(5, LOW);   //D0
  }

if (Fs == "2")
  {
    digitalWrite(2, HIGH);  //D3
    digitalWrite(3, HIGH);   //D2
    digitalWrite(4, LOW);  //D1
    digitalWrite(5, LOW);   //D0
  }

 if (Fs == "3")
  {
    digitalWrite(2, HIGH);  //D3
    digitalWrite(3, LOW);   //D2
    digitalWrite(4, HIGH);  //D1
    digitalWrite(5, LOW);   //D0
  }

 if (Fs == "4")
  {
    digitalWrite(2, HIGH);  //D3
    digitalWrite(3, LOW);   //D2
    digitalWrite(4, LOW);  //D1
    digitalWrite(5, LOW);   //D0
  }

  if (Fs == "5")
  {
    digitalWrite(2, LOW);  //D3
    digitalWrite(3, HIGH);   //D2
    digitalWrite(4, HIGH);  //D1
    digitalWrite(5, LOW);   //D0
  }

  if (Fs == "6")
  {
    digitalWrite(2, LOW);  //D3
    digitalWrite(3, HIGH);   //D2
    digitalWrite(4, LOW);  //D1
    digitalWrite(5, LOW);   //D0
  }

  if (Fs == "7")
  {
    digitalWrite(2, LOW);  //D3
    digitalWrite(3, LOW);   //D2
    digitalWrite(4, HIGH);  //D1
    digitalWrite(5, LOW);   //D0
  }

if (Fs == "8")
  {
    digitalWrite(2, LOW);  //D3
    digitalWrite(3, LOW);   //D2
    digitalWrite(4, LOW);  //D1
    digitalWrite(5, LOW);   //D0
  }
    

  //////////////////////////////////// Fan_1 Speed_Up ////////////////////////////////////

  //int buttonState_F_up = digitalRead(pushButton_F_up);


  int buttonState_F_up;
  if (readCapacitivePin(capSensePin_F_up) > touchedCutoff)
  {
    // stat = !stat;
    //digitalWrite(LEDPin, stat);
    Serial.println(readCapacitivePin(capSensePin_F_up));
    buttonState_F_up = 1;
    delay(300);
  }
  else
  {
    buttonState_F_up = 0;
  }

  // Every 500 ms, print the value of the capacitive sensor
  if ( (millis() % 500) == 0)
  {
    //  Serial.print("Capacitive Sensor on Pin 2 reads: ");
    //  Serial.println(readCapacitivePin(capSensePin_F_up));
  }


  if (buttonState_F_up == 1 && Fan_Speed < 8)                       // if switch is pressed then check the value in EEPROM
  {
    Fan_Speed = EEPROM.read(3);                                     // read the value which was last saved in EEPROM
    //Serial.print("Increased Fan Speed is =");
    //Serial.print("F_up is=");
    Serial.print("Fs=");
    Fan_Speed = Fan_Speed + 1;
    Serial.print(Fan_Speed);

    receiver.print("Fs=");
    receiver.print(Fan_Speed);
    EEPROM.write(3, Fan_Speed);                                    // write the increased value in EEPROM

    pe.digitalWrite(3, LOW);                                         //Toggle led when switch is pressed
    delay(1000);
    pe.digitalWrite(3, HIGH);

    for (int i = 0; i < Fan_Speed; i++) // Fan_Speed LEDs
      pe_Fs.digitalWrite(i, LOW);

    for (int j = 7; j >= Fan_Speed; j--)
      pe_Fs.digitalWrite(j, HIGH);

  }
  //  while (digitalRead(pushButton_F_up) == 1);
  while (digitalRead(capSensePin_F_up) == 1);


  /////////////////////////////////// Fan_1 Speed_Down////////////////////////////////////

  //int buttonState_F_down = digitalRead(pushButton_F_down);

  int buttonState_F_down;
  if (readCapacitivePin(capSensePin_F_down) > touchedCutoff)
  {
    // stat = !stat;
    //digitalWrite(LEDPin, stat);
    Serial.println(readCapacitivePin(capSensePin_F_down));
    buttonState_F_down = 1;
    delay(300);
  }
  else
  {
    buttonState_F_down = 0;
  }

  // Every 500 ms, print the value of the capacitive sensor
  if ( (millis() % 500) == 0)
  {
    //  Serial.print("Capacitive Sensor on Pin 2 reads: ");
    //  Serial.println(readCapacitivePin(capSensePin_F_down));
  }


  if (buttonState_F_down == 1 && Fan_Speed > 1)                       // if switch is pressed then check the value in EEPROM
  {
    Fan_Speed = EEPROM.read(3);
    //Serial.print("Decreased Fan Speed is =");
    // Serial.print("F_down is=");
    Serial.print("Fs=");
    Fan_Speed = Fan_Speed - 1;
    Serial.print(Fan_Speed);

    receiver.print("Fs=");
    receiver.print(Fan_Speed);
    EEPROM.write(3, Fan_Speed);

    pe.digitalWrite(4, LOW);
    delay(1000);
    pe.digitalWrite(4, HIGH);

    for (int i = 0; i < Fan_Speed; i++) // Fan_Speed LEDs
      pe_Fs.digitalWrite(i, LOW);

    for (int j = 7; j >= Fan_Speed; j--)
      pe_Fs.digitalWrite(j, HIGH);

  }
  // while (digitalRead(pushButton_F_down) == 1);
  while (digitalRead(capSensePin_F_down) == 1);

  status_update += "&Fs=";
  status_update += Fan_Speed;

}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

void Update_Buttons()
{
  /*
    while(1)
    if(receiver.available())
    Serial.print(char(receiver.read()));
  */
  if (receiver.available())
  {

    if (receiver.find("L1="))  // Light_1
    {

      while (!receiver.available());
      char L1 = receiver.read();
      if (L1 == '0')
      {
        EEPROM.write(0, LOW);
      }
      if (L1 == '1')
      {
        EEPROM.write(0, HIGH);
      }

    }

    if (receiver.find("L2="))  // Light_2
    {
      while (!receiver.available());
      char L2 = receiver.read();
      if (L2 == '0')
      {
        EEPROM.write(1, LOW);
      }
      if (L2 == '1')
      {
        EEPROM.write(1, HIGH);
      }

    }

    if (receiver.find("F1="))  // Fan_1
    {
      while (!receiver.available());
      char F1 = receiver.read();
      if (F1 == '0')
      {
        EEPROM.write(2, LOW);
      }
      if (F1 == '1')
      {
        EEPROM.write(2, HIGH);
      }

    }

    if (receiver.find("Fs="))  // Fan_Speed
    {
      while (!receiver.available());
      char Fs = receiver.read();

      if (Fs == '1')
      {
        EEPROM.write(3, 1);

        pe_Fs.digitalWrite(0, LOW);  //Fan Speed LEDs
        for (int i = 7; i > 0; i--)
          pe_Fs.digitalWrite(i, HIGH);
      }

      else if (Fs == '2')
      {
        EEPROM.write(3, 2);

        pe_Fs.digitalWrite(0, LOW);  //Fan Speed LEDs
        pe_Fs.digitalWrite(1, LOW);
        for (int i = 7; i > 1; i--)
          pe_Fs.digitalWrite(i, HIGH);

      }

      else if (Fs == '3')
      {
        EEPROM.write(3, 3);

        pe_Fs.digitalWrite(0, LOW);  //Fan Speed LEDs
        pe_Fs.digitalWrite(1, LOW);
        pe_Fs.digitalWrite(2, LOW);
        for (int i = 7; i > 2; i--)
          pe_Fs.digitalWrite(i, HIGH);

      }

      else if (Fs == '4')
      {
        EEPROM.write(3, 4);

        pe_Fs.digitalWrite(0, LOW);  //Fan Speed LEDs
        pe_Fs.digitalWrite(1, LOW);
        pe_Fs.digitalWrite(2, LOW);
        pe_Fs.digitalWrite(3, LOW);
        for (int i = 7; i > 3; i--)
          pe_Fs.digitalWrite(i, HIGH);
      }

      else if (Fs == '5')
      {
        EEPROM.write(3, 5);

        for ( int n = 0; n < 5; n++ )  //Fan Speed LEDs
          pe_Fs.digitalWrite(n, LOW);
        for (int i = 7; i > 4; i--)
          pe_Fs.digitalWrite(i, HIGH);

      }

      else if (Fs == '6')
      {
        EEPROM.write(3, 6);

        for ( int i = 0; i < 6; i++ )  //Fan Speed LEDs
          pe_Fs.digitalWrite(i, LOW);
        for (int i = 7; i > 5; i--)
          pe_Fs.digitalWrite(i, HIGH);

      }

      else if (Fs == '7')
      {
        EEPROM.write(3, 7);

        for ( int i = 0; i < 7; i++ )  //Fan Speed LEDs
          pe_Fs.digitalWrite(i, LOW);
        for (int i = 7; i > 6; i--)
          pe_Fs.digitalWrite(i, HIGH);
      }

      else if (Fs == '8')
      {
        EEPROM.write(3, 8);

        for ( int m = 0; m < 8; m++ )  //Fan Speed LEDs
          pe_Fs.digitalWrite(m, LOW);

      }

      Serial.print("S");
      receiver.print("S");

    }
  }
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Touch Sensor
uint8_t readCapacitivePin(int pinToMeasure) {
  // This is how you declare a variable which
  //  will hold the PORT, PIN, and DDR registers
  //  on an AVR
  volatile uint8_t* port;
  volatile uint8_t* ddr;
  volatile uint8_t* pin;
  // Here we translate the input pin number from
  //  Arduino pin number to the AVR PORT, PIN, DDR,
  //  and which bit of those registers we care about.
  byte bitmask;
  if ((pinToMeasure >= 0) && (pinToMeasure <= 7)) {
    port = &PORTD;
    ddr = &DDRD;
    bitmask = 1 << pinToMeasure;
    pin = &PIND;
  }
  if ((pinToMeasure > 7) && (pinToMeasure <= 13)) {
    port = &PORTB;
    ddr = &DDRB;
    bitmask = 1 << (pinToMeasure - 8);
    pin = &PINB;
  }
  if ((pinToMeasure > 13) && (pinToMeasure <= 19)) {
    port = &PORTC;
    ddr = &DDRC;
    bitmask = 1 << (pinToMeasure - 13);
    pin = &PINC;
  }
  // Discharge the pin first by setting it low and output
  *port &= ~(bitmask);
  *ddr  |= bitmask;
  delay(1);
  // Make the pin an input WITHOUT the internal pull-up on
  *ddr &= ~(bitmask);
  // Now see how long the pin to get pulled up
  int cycles = 16000;
  for (int i = 0; i < cycles; i++) {
    if (*pin & bitmask) {
      cycles = i;
      break;
    }
  }
  // Discharge the pin again by setting it low and output
  //  It's important to leave the pins low if you want to
  //  be able to touch more than 1 sensor at a time - if
  //  the sensor is left pulled high, when you touch
  //  two sensors, your body will transfer the charge between
  //  sensors.
  *port &= ~(bitmask);
  *ddr  |= bitmask;

  return cycles;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


