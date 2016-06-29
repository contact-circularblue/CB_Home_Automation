/*
void loop()
{
 int buttonState = digitalRead(pushButton);
if(buttonState==1)
{
// pe.digitalWrite(0,LOW);
 pe.digitalWrite(0,LOW);
 
 delay(1000);
 Serial.print('H');
 delay(1000);
 Serial.print('L');
 delay(1000);
}
else
{
 pe.digitalWrite(0,HIGH);/////Turn ON LED
 delay(1000);
}
}
*/

//////////////////////   ///////////////////////   ///////////////////////   ///////////////////////   //////////////////////

#include <PortExpander_I2C.h> //Port Expander code
#include<EEPROM.h>
#include "Wire.h"

///////////////NOTE: LED +ve connected to Vcc, LED -ve connected to PCF pin

PortExpander_I2C pe(0x20);
int pushButton_L1=2;     //light_1        // Digital pin 2 of arduino
int pushButton_L2=3;     //Light_2        // Digital pin 3 of arduino
int pushButton_F1=4;     //Fan            // Digital pin 4 of arduino
int pushButton_F_up=5;   //Fan_Speed Up   // Digital pin 5 of arduino
int pushButton_F_down=6; //Fan_Speed Down // Digital pin 5 of arduino
int Fan_Speed;           // Stores the current fan speed

 
void setup()
{
 //EEPROM.write(3,0);  // intialising the fan speed to zero
 Serial.begin(115200); 
 Wire.begin();
 pinMode(pushButton_L1,INPUT);
 pinMode(pushButton_L2,INPUT);
 pinMode(pushButton_F1,INPUT);
 pinMode(pushButton_F_up,INPUT);
 pinMode(pushButton_F_down,INPUT);
 
 pe.init();
 int i=0;
 for( int i = 0; i < 8; i++ )
 {
   pe.pinMode(i,OUTPUT);
   pe.digitalWrite(i,HIGH);
 }
}


void loop()
{
  /*
  while(1)
  if(Serial.available())
  Serial.print(char(Serial.read()));
  */
  
  String stat="";
  
if(Serial.available())
{
  if(Serial.find("L1"))
  {
    Serial.print("String found");
  }
  /*
  while (1)
  {
    int count=0;
    if (Serial.available() && count < 20)
    {
      char a = char(Serial.read());
      if(a>47 && a<123)
      {
      stat += a;
      count++;
      }
      if (count == 20)
        break;
    }
  }*/
}
  //Serial.println(stat);
  
// Light_1
bool L1=EEPROM.read(0);                        // read the value which was last saved in EEPROM 
if(L1==1)
 pe.digitalWrite(0,LOW);
else
  pe.digitalWrite(0,HIGH);

int buttonState_L1 = digitalRead(pushButton_L1);
if(buttonState_L1==1)                           // if switch is pressed then check the value in EEPROM
{
   //Serial.println("Entered if statement");
  if(L1==1)                                     // if data in EEPROM is LOW ie Led is on then turn off the led  
  {
   pe.digitalWrite(0,HIGH);
   EEPROM.write(0,L1^1);
   Serial.print("L1=0");
  }
  else                                          // if data in EEPROM is HIGH ie Led is off then turn on the led  
  {
   pe.digitalWrite(0,LOW);
   EEPROM.write(0,L1^1);
   Serial.print("L1=1");
  }
  //Serial.println("Exited if statement");
}
while(digitalRead(pushButton_L1)==1);


// Light_2
bool L2=EEPROM.read(1);                          // read the value which was last saved in EEPROM
if(L2==1)
 pe.digitalWrite(1,LOW);
else
  pe.digitalWrite(1,HIGH);

int buttonState_L2 = digitalRead(pushButton_L2);
if(buttonState_L2==1)                            // if switch is pressed then check the value in EEPROM
{
  if(L2==1)                                      // if data in EEPROM is LOW ie Led is on then turn off the led  
  {
   pe.digitalWrite(1,HIGH);
   EEPROM.write(1,L2^1);
   Serial.print("L2=0");
  }
  else                                           // if data in EEPROM is HIGH ie Led is off then turn on the led  
  {
   pe.digitalWrite(1,LOW);
   EEPROM.write(1,L2^1);
   Serial.print("L2=1");
  }
}
while(digitalRead(pushButton_L2)==1);


// Fan_1 
bool F1=EEPROM.read(2);                          // read the value which was last saved in EEPROM
if(F1==1)
 pe.digitalWrite(2,LOW);
else
  pe.digitalWrite(2,HIGH);

int buttonState_F1 = digitalRead(pushButton_F1);
if(buttonState_F1==1)                            // if switch is pressed then check the value in EEPROM
{
  if(F1==1)                                      // if data in EEPROM is LOW ie Led is on then turn off the led  
  {
   pe.digitalWrite(2,HIGH);
   EEPROM.write(2,F1^1);
   Serial.print("F1=0");
  }
  else                                           // if data in EEPROM is HIGH ie Led is off then turn on the led  
  {
   pe.digitalWrite(2,LOW);
   EEPROM.write(2,F1^1);
   Serial.print("F1=1");
  }
}
while(digitalRead(pushButton_F1)==1);

//Fan_Speed
Fan_Speed= EEPROM.read(3);   // it will telll the fan speed to the dimmer circuit
if(F1==1)
{
//Serial.print("Current Fan_Speed is=");
//Serial.println(Fan_Speed);
}


// Fan_1 Speed_Up
int buttonState_F_up = digitalRead(pushButton_F_up);
if(buttonState_F_up==1 && Fan_Speed<8)                            // if switch is pressed then check the value in EEPROM
{
  Fan_Speed=EEPROM.read(3);                                       // read the value which was last saved in EEPROM
  //Serial.print("Increased Fan Speed is =");
  //Serial.print("F_up is=");
  Serial.print("Fs=");
   
  Serial.print(Fan_Speed+1);
  EEPROM.write(3,Fan_Speed+1);                                     // write the increased value in EEPROM
  
  pe.digitalWrite(3,LOW);                                          //Toggle led when switch is pressed
  delay(1000);
  pe.digitalWrite(3,HIGH);
}
while(digitalRead(pushButton_F_up)==1);


// Fan_1 Speed_Down
int buttonState_F_down = digitalRead(pushButton_F_down);
if(buttonState_F_down==1 && Fan_Speed>0)                            // if switch is pressed then check the value in EEPROM
{
  Fan_Speed=EEPROM.read(3);
  //Serial.print("Decreased Fan Speed is ="); 
 // Serial.print("F_down is=");
  Serial.print("Fs=");

  Serial.print(Fan_Speed-1);
  EEPROM.write(3,Fan_Speed-1); 
 
  pe.digitalWrite(4,LOW);
  delay(1000);
  pe.digitalWrite(4,HIGH);
}
while(digitalRead(pushButton_F_down)==1);

}











