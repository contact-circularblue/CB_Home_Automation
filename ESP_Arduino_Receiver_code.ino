/*
  int pushButton_L1=2;     //light_1   // Digital pin 2 of arduino
  int pushButton_L2=3;     //Light_2   // Digital pin 3 of arduino
  int pushButton_F1=4;     //Fan      // Digital pin 4 of arduino
  int pushButton_F_up=5;   //Fan_Speed Up // Digital pin 5 of arduino
  int pushButton_F_down=6; //Fan_Speed Down // Digital pin 5 of arduino
  int Fan_Speed; // Stores the current fan speed
*/

#include <SoftwareSerial.h>

String data = "";
void ESP_post();
void ESP_get();

SoftwareSerial ESP(10, 11); //RX,TX of Arduino
const int Power_LED =  12;
const int WiFi_LED =  13;

void setup()
{
  Serial.begin(250000);
  ESP.begin(9600);
  pinMode(Power_LED, OUTPUT);
  pinMode(WiFi_LED, OUTPUT);
  digitalWrite(Power_LED, HIGH);

  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);

}

void loop()
{

  if (Serial.available())
  {
    ESP_post();
  }
  else
  {
    ESP_get();
  }
}


void ESP_post()
{
  String stat = "";                     // create a string to store the data available on serial port
  while (!Serial.available());
  int count = 0;

  while (1)
  {
    if (Serial.available() && count < 4)
    {
      stat += char(Serial.read());
      count++;
      if (count == 4)
        break;
    }
  }

  Serial.println(stat);

  if (stat.substring(0) == "L1=0")  // Light_1
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field1=0");  // using F() so that the string initially is stored only in ROM and not copied in RAM
    Serial.println("L1 is OFF");
    Serial.println(data);
  }
  else if (stat.substring(0) == "L1=1")
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field1=1");
    Serial.println("L1 is ON");
    Serial.println(data);
  }

  else if (stat.substring(0) == "L2=0")  // light_2
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field2=0");
    Serial.println("L2 is OFF");
    Serial.println(data);
  }
  else if (stat.substring(0) == "L2=1")
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field2=1");
    Serial.println("L2 is ON");
    Serial.println(data);
  }

  else if (stat.substring(0) == "F1=0") // Fan_1
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field3=0");
    Serial.println("F1 is OFF");
    Serial.println(data);
  }
  else if (stat.substring(0) == "F1=1")
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field3=1");
    Serial.println("F1 is ON");
    Serial.println(data);
  }


  else if (stat.substring(0) == "Fs=1") // Fan_Speed
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=1");
    Serial.println("F_speed is=1");
    Serial.println(data);
  }
  else if (stat.substring(0) == "Fs=2") // Fan_Speed
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=2");
    Serial.println("F_speed is=2");
    Serial.println(data);
  }
  else if (stat.substring(0) == "Fs=3") // Fan_Speed
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=3");
    Serial.println("F_speed is=3");
    Serial.println(data);
  }
  else if (stat.substring(0) == "Fs=4") // Fan_Speed
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=4");
    Serial.println("F_speed is=4");
    Serial.println(data);
  }
  else if (stat.substring(0) == "Fs=5") // Fan_Speed is increased
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=5");
    Serial.println("F_speed is=5");
    Serial.println(data);
  }
  else if (stat.substring(0) == "Fs=6") // Fan_Speed is increased
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=6");
    Serial.println("F_speed is=6");
    Serial.println(data);
  }
  else if (stat.substring(0) == "Fs=7") // Fan_Speed is increased
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=7");
    Serial.println("F_speed is=7");
    Serial.println(data);
  }
  else if (stat.substring(0) == "Fs=8") // Fan_Speed is increased
  {
    data = F("https://api.thingspeak.com/update?api_key=VZGWTTS9IC63O7AG;&field4=8");
    Serial.println("F_speed is=8");
    Serial.println(data);
  }
}


void ESP_get()
{
  //start:
  while (ESP.available())
    ESP.read();
  delay(1000);
  
  ESP.println (F("AT+CIOBAUD=9600"));
  delay(1000);
  if (ESP.find("OK"))
    Serial.println(F("Baud rate = 9600"));
  else
    Serial.println(F("Error"));
  while (ESP.available())
    ESP.read();

  ESP.println(F("ATE0"));
  delay(1000);
  if (ESP.find("OK"))
    Serial.println(F("Echo OFF"));
  else
    Serial.println(F("Error"));
  while (ESP.available())
    ESP.read();


  ESP.println(F("AT"));
  delay(500);
  if (ESP.find("OK"))
    Serial.println(F("OK"));
  else
    Serial.println(F("Error"));
  while (ESP.available())
    ESP.read();

  ESP.println(F("AT+RST"));
  delay(3000);
  while (ESP.available())
    ESP.read();

  ESP.println(F("AT"));
  delay(500);
  if (ESP.find("OK"))
    Serial.println(F("OK"));
  else
    Serial.println(F("Error"));
  while (ESP.available())
    ESP.read();

CIPMUX:
  ESP.println(F("AT+CIPMUX=1"));
  delay(1000);
  if (ESP.find("OK"))
  {
    Serial.println(F("MUX IS SET TO 1"));
    while (ESP.available())
      ESP.read();
  }
  else
  {
    Serial.println(F("Error"));
    while (ESP.available())
      ESP.read();
    goto CIPMUX;
  }

  ESP.println(F("AT+CWMODE=1"));
  delay(500);
  if (ESP.find("OK"))
    Serial.println(F("MODE is set to 1"));
  while (ESP.available())
    ESP.read();



  //while(1);

  ////////////////////////
Wifi_Connect:
  noInterrupts();           // disable all interrupts
  TCCR1A = 0;
  TCCR1B = 0;

  TCNT1 = 34286;            // preload timer 65536-16MHz/256/2Hz
  TCCR1B |= (1 << CS12);    // 256 prescaler
  TIMSK1 |= (1 << TOIE1);   // enable timer overflow interrupt
  interrupts();


  Serial.println(F("Connecting to WiFi"));
  ESP.println(F("AT+CWJAP=\"Circular Blue\",\"Vibgyor1!\""));
  delay(10000);

  while (ESP.available())
    ESP.read();


  ESP.println(F("AT+CWJAP?"));
  delay(5000);

  bool a = ESP.find("Circular");
  //noInterrupts();

  if (a == 1)
  {

    Serial.println(F("Wifi connection Successful"));

    while (ESP.available())
      ESP.read();
    TCCR1B = 0;
    //noInterrupts();
    digitalWrite(WiFi_LED, HIGH);
  }

  else
  {
    Serial.println(F("Error connecting to wifi"));

    while (ESP.available())
      ESP.read();
    goto Wifi_Connect;
  }
  
}

//////////////////////    ///////////////////////   ///////////////////////   ////////////////////////

ISR(TIMER1_OVF_vect)
{
  TCNT1 = 20000;            // preload timer
  digitalWrite(WiFi_LED, digitalRead(WiFi_LED) ^ 1);
}


