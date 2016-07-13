
/*
  int pushButton_L1=2;     //light_1   // Digital pin 2 of arduino
  int pushButton_L2=3;     //Light_2   // Digital pin 3 of arduino
  int pushButton_F1=4;     //Fan      // Digital pin 4 of arduino
  int pushButton_F_up=5;   //Fan_Speed Up // Digital pin 5 of arduino
  int pushButton_F_down=6; //Fan_Speed Down // Digital pin 5 of arduino
  int Fan_Speed; // Stores the current fan speed
*/


#include <EEPROM.h>

#include <SoftwareSerial.h>

String data = "";

String send_to_main = "";
String String_1 = "";
String String_2 = "";

int Flag = 1;


void ESP_post();
void ESP_get();
bool Connect_To_Thingspeak();
bool start_transmission(int data_length);
String send_data(String data, char field);
bool Wifi_Connect();
ISR(TIMER1_OVF_vect);

const int RESET =  A0;
const int LED =  A1;
void Reset();

SoftwareSerial ESP(10, 11); //RX,TX of Arduino
const int Power_LED =  12;
const int WiFi_LED =  13;

int capSensePin = 7;   //Master Reset Button        // Digital pin 7 of arduino
int touchedCutoff = 65;

void setup()
{
  Serial.begin(57600);
  //ESP.begin(9600);
  ESP.println("AT+CIOBAUD=57600");
  delay(500);
  ESP.begin(57600);
  pinMode(Power_LED, OUTPUT);
  pinMode(WiFi_LED, OUTPUT);
  digitalWrite(Power_LED, HIGH);

  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);

  pinMode(A0, OUTPUT);
  digitalWrite(A0, HIGH);

  pinMode(RESET, OUTPUT);
  pinMode(LED, OUTPUT);

  pinMode(capSensePin, INPUT);  // Button for Master_Reset
}

void loop()
{

Master_Reset:

  Reset();

  bool initialise = ESP_initialise();

  if (initialise == 1)
  {

    // connecting to mobile hotspot
    if (EEPROM.read(101) == 0)
    {
      bool HotspotConnect = Hotspot_Connect();
    }

    // Connecting to home wifi
    if (EEPROM.read(101) == 1)
    {
      bool WifiConnect = 0;

      while (WifiConnect == 0)
        WifiConnect = Wifi_Connect();
    }

    while (1)
    {
     
      if (readCapacitivePin(capSensePin) > touchedCutoff)
      {
        delay(3000);
        if (readCapacitivePin(capSensePin) > touchedCutoff)
        {
        Serial.println(readCapacitivePin(capSensePin));
        EEPROM.write(101,0);
        Serial.println("Master Reset Activated");
        goto Master_Reset;
      }
      }

      if (Serial.available())
      {
        ESP_post();
        //Serial.println("UP TOP");
      }
      else
      {
        ESP_get();
      }
    }
  }

}
/////////////////////////////////////////////////////////////////////////////////
void ESP_post()
{
  /*
    while(1)
    {
    while(!Serial.available());
    Serial.print(char(Serial.read()));
    }
  */

  //Serial.println(availableMemory());
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

  //Serial.println(stat);

  bool fwd;
  bool transmisson;

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  if (stat.substring(0) == "L1=0")  // Light_1
  {

    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
      else
        Wifi_Connect();
    }
    send_to_main = "";
    data = "";
    data = F("GET /update?api_key=VZGWTTS9IC63O7AG;&field1=0");  // light_1  // using F() so that the string initially is stored only in ROM and not copied in RAM

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
    {
      bool connection;
      while (connection != 1)
      {
        connection = Wifi_Connect();
      }
    }

    String L1 = send_data(data, '1');
   // Serial.println("Lt1 OFF");

  }
  else if (stat.substring(0) == "L1=1")
  {

    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
      else
        Wifi_Connect();
    }
    send_to_main = "";
    data = "";
    data = F("GET /update?api_key=VZGWTTS9IC63O7AG;&field1=1");  // light_1

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
    {
      bool connection;
      while (connection != 1)
      {
        connection = Wifi_Connect();
      }
    }

    String L1 = send_data(data, '1');

   // Serial.println("Lt1 ON");

  }

  ////////////////////////////////////////////////////////////////////////////////////////

  if (stat.substring(0) == "L2=0")  // Light_2
  {

    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
      else
        Wifi_Connect();
    }
    send_to_main = "";
    data = "";
    data = F("GET /update?api_key=VZGWTTS9IC63O7AG;&field2=0");  // light_2  // using F() so that the string initially is stored only in ROM and not copied in RAM

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
    {
      bool connection;
      while (connection != 1)
      {
        connection = Wifi_Connect();
      }
    }

    String L2 = send_data(data, '2');
   // Serial.println("Lt2 OFF");

  }
  else if (stat.substring(0) == "L2=1")
  {

    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
      else
        Wifi_Connect();
    }
    send_to_main = "";
    data = "";
    data = F("GET /update?api_key=VZGWTTS9IC63O7AG;&field2=1");  // light_2

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
    {
      bool connection;
      while (connection != 1)
      {
        connection = Wifi_Connect();
      }
    }

    String L2 = send_data(data, '2');

   // Serial.println("Lt2 ON");
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////

  if (stat.substring(0) == "F1=0")  // Fan_1
  {

    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
      else
        Wifi_Connect();
    }
    send_to_main = "";
    data = "";
    data = F("GET /update?api_key=VZGWTTS9IC63O7AG;&field3=0");  // Fan_1  // using F() so that the string initially is stored only in ROM and not copied in RAM

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
    {
      bool connection;
      while (connection != 1)
      {
        connection = Wifi_Connect();
      }
    }

    String F1 = send_data(data, '3');
    //Serial.println("Fan1 OFF");

  }
  else if (stat.substring(0) == "F1=1")
  {

    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
      else
        Wifi_Connect();
    }
    send_to_main = "";
    data = "";
    data = F("GET /update?api_key=VZGWTTS9IC63O7AG;&field3=1");  // Fan_1

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
    {
      bool connection;
      while (connection != 1)
      {
        connection = Wifi_Connect();
      }
    }

    String F1 = send_data(data, '3');
   // Serial.println("Fan1 ON");

  }

  ////////////////////////////////////////////////////////////////////////////////////////

  if (stat.substring(0) == "Fs=1" || stat.substring(0) == "Fs=2" || stat.substring(0) == "Fs=3" || stat.substring(0) == "Fs=4" || stat.substring(0) == "Fs=5" || stat.substring(0) == "Fs=6" || stat.substring(0) == "Fs=7" || stat.substring(0) == "Fs=8")
  {

    char Speed = stat.charAt(3);
    //Serial.print("Fanspeed=");
    //Serial.println(Speed);

    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
      else
        Wifi_Connect();
    }
    send_to_main = "";
    data = "";
    data = F("GET /update?api_key=VZGWTTS9IC63O7AG;&field4=");  // Fan_speed
    data += Speed;

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
    {
      bool connection;
      while (connection != 1)
      {
        connection = Wifi_Connect();
      }
    }

    String Fs = send_data(data, '4');
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

void ESP_get()
{
  // Finally fetch data for fields from the server

  //////////////////////   Light_1   //////////////////////

L1_again:

  bool fwd = 0;
  bool transmisson = 0;
  while (fwd == 0)
  {
    bool Connect = Connect_To_Thingspeak();
    if (Connect == 1)
      fwd = 1;
    else
      Connect_To_Thingspeak();
  }
  send_to_main = "";
  data = "";
  data = "GET /channels/103571/fields/1/last.json?";   // light_1

  transmisson = start_transmission(data.length());
  if (transmisson != 1)
  {
    bool connection;
    while (connection != 1)
    {
      connection = Wifi_Connect();
    }
  }

  String L1 = send_data(data, '1');

  if (L1 == "0" || L1 == "1")
  {
    send_to_main += "L1=";
    send_to_main += L1;
  }
  else
  {
    goto L1_again;
  }
  //Serial.print("Light 1=");
  //Serial.println(L1);

  if (L1 == "1")
  {

    digitalWrite(4, HIGH);
  }

  if (L1 == "0")
  {
    digitalWrite(4, LOW);
  }

  ////////////////////   LIGHT 2   //////////////////////

L2_again:

  fwd = 0;
  while (fwd == 0)
  {
    bool Connect = Connect_To_Thingspeak();
    if (Connect == 1)
      fwd = 1;
    else
      Connect_To_Thingspeak();
  }

  data = "";
  data = "GET /channels/103571/fields/2/last.json?";   // light_1

  transmisson = start_transmission(data.length());
  if (transmisson != 1)
  {
    bool connection;
    while (connection != 1)
    {
      connection = Wifi_Connect();
    }
  }

  String L2 = send_data(data, '2');

  if (L2 == "0" || L2 == "1")
  {
    send_to_main += "&L2=";
    send_to_main += L2;
  }
  else
  {
    goto L2_again;
  }
  //Serial.print("Light 2=");
  //Serial.println(L2);


  ////////////////////////   Fan   ////////////////////////////

F1_again:

  fwd = 0;
  while (fwd == 0)
  {
    bool Connect = Connect_To_Thingspeak();
    if (Connect == 1)
      fwd = 1;
    else
      Connect_To_Thingspeak();
  }

  data = "";
  data = "GET /channels/103571/fields/3/last.json?";   // Fan_1

  transmisson = start_transmission(data.length());
  if (transmisson != 1)
  {
    bool connection;
    while (connection != 1)
    {
      connection = Wifi_Connect();
    }
  }

  String F1 = send_data(data, '3');

  if (F1 == "0" || F1 == "1")
  {
    send_to_main += "&F1=";
    send_to_main += F1;
  }
  else
  {
    goto F1_again;
  }
  //Serial.print("Fan =");
  //Serial.println(F1);


  /////////////////////////   Fan Speed   ///////////////////////////

Fs_again:

  fwd = 0;
  while (fwd == 0)
  {
    bool Connect = Connect_To_Thingspeak();
    if (Connect == 1)
      fwd = 1;
    else
      Connect_To_Thingspeak();
  }

  data = "";
  data = "GET /channels/103571/fields/4/last.json?"; // fan_ speed

  transmisson = start_transmission(data.length());
  if (transmisson != 1)
  {
    bool connection;
    while (connection != 1)
    {
      connection = Wifi_Connect();
    }
  }

  String Fs = send_data(data, '4');

  if (Fs == "1" || Fs == "2" || Fs == "3" || Fs == "4" || Fs == "5" || Fs == "6" || Fs == "7" || Fs == "8")
  {
    send_to_main += "&Fs=";
    send_to_main += Fs;
  }
  else
  {
    goto Fs_again;
  }
  //Serial.print("Speed=");
  //Serial.println(Fs);


 

  //Serial.print(send_to_main);
  if (Flag == 1)
  {
    String_1 = send_to_main;
    Flag--;
Send_Again:
    Serial.print(String_1);

    delay(3000);
    /*
       if (!Serial.find("A"))
          {
            while (!Serial.available());
            delay(100);
          }
          if (!Serial.find("A2"))
          {
            while (!Serial.available());
            delay(100);
          }
          if (!Serial.find("A3"))
          {
            while (!Serial.available());
            delay(100);
          }
    */
    String Reply = "";
    while (Serial.available())
    {
      Reply += char(Serial.read());
    }
    Serial.print(Reply);

    //  if (!Serial.find("S"))
    if (Reply != "S")
      goto Send_Again;
    else
      Serial.print("R");
  }
  else
  {
    String_2 = send_to_main;
    String_Compare();
  }

  //String_Compare();

  while (ESP.available())
    ESP.read();
  ESP.flush();
  Serial.flush();

  delay(2000);

  //while (Serial.available())
  //Serial.read();

}

//////////////////////    ///////////////////////   ///////////////////////   ////////////////////////

bool Connect_To_Thingspeak()
{
  int tries = 6;
Connect_again:

  ESP.println("AT+CIPSTART=4,\"TCP\",\"184.106.153.149\",80");
  delay(1000);
  /*
    while(1)
    {
    while(ESP.available())
    Serial.print(char(ESP.read()));
    }
  */

  if (ESP.find("4,CONNECT"))
  {
    // Serial.println("Linked to Thingspeak.com");
    while (ESP.available())
      ESP.read();
    return 1;
  }
  else
  {
    //  Serial.println("Error connecting to Thingspeak.com");
    Serial.println("E");
    delay(1000);
    while (ESP.available())
      ESP.read();
    if (tries > 0)
    {
      Serial.println(tries);
      tries--;
      goto Connect_again;
      //Serial.print("after dec tries = ");
      //Serial.println(tries);
    }
    else
    {
      bool connection = 0;
      ESP_initialise();
      while (connection != 1)
        connection = Wifi_Connect();
    }
    return 0;

  }
}


///////////////////////////////////////////////////////////////////////////////

bool start_transmission(int data_length)
{
  data_length = data_length + 2;
  String data = "";
  data += "AT+CIPSEND=4,";
  data += data_length;

  ESP.println(data);
  delay(1000);
  if (ESP.find(">"))
  {
    //Serial.println("> received");
    while (ESP.available())
      ESP.read();
    return 1;
  }
  else
  {
    //Serial.println("Error in command AT+CIPSEND");
    while (ESP.available())
      ESP.read();
    return 0;
  }

}

///////////////////////////////////////////////////////////////////////////////////////

String send_data(String data, char field)
{
  String field_check = "";
  field_check += "\"field";
  field_check += field;
  field_check += "\":\"";

  char buff[field_check.length() + 1];
  field_check.toCharArray(buff, field_check.length() + 1);

  ESP.println(data);

  /*
    while(1)
    while(ESP.available())
    Serial.print(char(ESP.read()));
  */

  String field_read = "";

  if (ESP.find(buff))
  {
    //Serial.println("+IPD received");
    delay(200);
    while (ESP.available())
    {
      char c = ESP.read();
      if (c == '\"')
        break;
      field_read += c;
      delay(100);
    }
  }

  //Serial.flush();
  return field_read;
}

/////////////////////////////////////////////////////////////////

bool ESP_initialise()
{
  while (ESP.available())
    ESP.read();
  delay(1000);
  /*
    ESP.println (F("AT+CIOBAUD=115200"));
    delay(1000);
    if (ESP.find("OK"))
      Serial.println(F("Baud rate = 115200"));
    else
      Serial.println(F("Error"));
    while (ESP.available())
      ESP.read();
  */
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
  {
    Serial.println(F("MODE is set to 1"));
    while (ESP.available())
      ESP.read();
    return 1;
  }
  else
    return 0;

}

//////////////////////////////////////////////////////////////////


bool Hotspot_Connect()
{

Hotspot_Connect:
  noInterrupts();           // disable all interrupts
  TCCR1A = 0;
  TCCR1B = 0;

  TCNT1 = 34286;            // preload timer 65536-16MHz/256/2Hz
  TCCR1B |= (1 << CS12);    // 256 prescaler
  TIMSK1 |= (1 << TOIE1);   // enable timer overflow interrupt
  interrupts();


  //Serial.println(F("Connecting to Hotspot"));
  ESP.println(F("AT+CWJAP=\"Redmi\",\"kartik123\""));
  delay(10000);

  while (ESP.available())
    ESP.read();


  ESP.println(F("AT+CWJAP?"));
  delay(5000);

  bool a = ESP.find("Redmi");
  //noInterrupts();

  if (a == 1)
  {

    Serial.println(F("Hotspot connection Successful"));

    while (ESP.available())
      ESP.read();
    TCCR1B = 0;
    //noInterrupts();
    digitalWrite(WiFi_LED, HIGH);
  }

  else
  {
    Serial.println(F("Error connecting to Hotspot"));

    while (ESP.available())
      ESP.read();
    goto Hotspot_Connect;
  }

Connect_Thingspeak:
  bool Connect = Connect_To_Thingspeak();
  if (Connect != 1)
  {
    ESP_initialise();
    goto  Connect_Thingspeak;
  }


  String data = "GET /channels/103571/fields/7/last.json?";

  bool transmisson = start_transmission(data.length());
  if (transmisson != 1)
    goto Hotspot_Connect;


  String integer = send_data(data, '7');
  //Serial.print("integer=");
  //Serial.println(integer);
  if (integer == "1")
  {
Get_SSID:
    int fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
    }
    data = "";
    data = "GET /channels/103571/fields/5/last.json?";

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
      goto Hotspot_Connect;

    String ssid = send_data(data, '5');
    //Serial.print("ssid=");
    //Serial.println(ssid);

    if (ssid.length() < 4)
      goto Get_SSID;

    int ssid_l = ssid.length();          // for storing ssid in eeprom
    for (int i = 20; i < (20 + ssid_l); i++)
    {
      EEPROM.write(i, ssid.charAt(i - 20));
    }
    EEPROM.write(50, ssid_l);            // for storing length of ssid in eeprom

    String check_ssid = "";
    for (int i = 20; i < (20 + EEPROM.read(50)); i++)
    {
      check_ssid += char(EEPROM.read(i));
    }

    // Serial.println(check_ssid);

Get_Password:
    fwd = 0;
    while (fwd == 0)
    {
      bool Connect = Connect_To_Thingspeak();
      if (Connect == 1)
        fwd = 1;
    }

    data = "";
    data = "GET /channels/103571/fields/6/last.json?";

    transmisson = start_transmission(data.length());
    if (transmisson != 1)
      goto Hotspot_Connect;

    String password = send_data(data, '6');
    // Serial.print("password=");
    // Serial.println(password);

    if (password.length() < 4)
      goto Get_Password;

    int password_l = password.length();          // for storing ssid in eeprom
    for (int i = 51; i < (51 + password_l); i++)
    {
      EEPROM.write(i, password.charAt(i - 51));
    }
    EEPROM.write(100, password_l);


    EEPROM.write(101, 1);
    //Serial.println(EEPROM.read(101));

    while (ESP.available())
      ESP.read();
  }
  else
    goto Connect_Thingspeak;
}

/////////////////////////////////////////////////////////////////////////////

bool Wifi_Connect()

{
Wifi_Connect2:

  Serial.println("Connecting to HOME Wifi");

  noInterrupts();           // disable all interrupts
  TCCR1A = 0;
  TCCR1B = 0;

  TCNT1 = 34286;            // preload timer 65536-16MHz/256/2Hz
  TCCR1B |= (1 << CS12);    // 256 prescaler
  TIMSK1 |= (1 << TOIE1);   // enable timer overflow interrupt
  interrupts();

  // Connect to home WiFi network

  String check_ssid = "";
  for (int i = 20; i < (20 + EEPROM.read(50)); i++)
  {
    check_ssid += char(EEPROM.read(i));
  }

  String check_password = "";
  for (int i = 51; i < (51 + EEPROM.read(100)); i++)
  {
    check_password += char(EEPROM.read(i));
  }


  String ssid = "";
  ssid = check_ssid;
  String password = "";
  password = check_password;

  Serial.println(ssid);
  Serial.println(password);

  //while(1);
  String Wifi_details = "AT+CWJAP=\"";
  Wifi_details += ssid;
  Wifi_details += "\",\"";
  Wifi_details += password;
  Wifi_details += "\"";

  while (ESP.available())
    ESP.read();

  ESP.println(Wifi_details);

  delay(3000);
  /*
     while (1)
     {
       if (ESP.available())
         Serial.print(char(ESP.read()));
     }
  */

  bool Connection_Success = 0;
  if (ESP.find("GOT IP"))
    //Serial.println("CONNECTED TO HOME WIFI");
    Connection_Success = 1;
  else
    Serial.println("NOT CONNECTED");

  while (ESP.available())
    ESP.read();



  if (Connection_Success == 1)
  {

    Serial.println("HOME Wifi connection Successful");
    while (ESP.available())
      ESP.read();
    TCCR1B = 0;
    //noInterrupts();
    digitalWrite(WiFi_LED, HIGH);
    delay(3000);

    // while (Serial.available())
    // Serial.read();
    ESP.flush();

    return 1;

  }
  else
    return 0;
}

////////////////////////////////////////////////////////////////////////////////

ISR(TIMER1_OVF_vect)
{
  TCNT1 = 20000;            // preload timer
  digitalWrite(WiFi_LED, digitalRead(WiFi_LED) ^ 1);
}

int availableMemory()
{
  int size = 8192;
  byte *buf;
  while ((buf = (byte *) malloc(--size)) == NULL);
  free(buf);
  return size;
}

/////////////////////////////////////////////////////////////////////////////////
void Reset()
{
  digitalWrite(RESET, HIGH);
  digitalWrite(LED, HIGH);
  delay(500);
  digitalWrite(RESET, LOW);
  digitalWrite(LED, LOW);
  delay(3000);
}


///////////////////////////////////////////////////////////////////////////////////

bool String_Compare()
{
  if (String_1 != String_2)
  {

    String_1 = String_2;
Resend:
    Serial.print(String_1);

    delay(4000);
    /*
        if (Serial.available())
        {
          //Serial.print("Searching for acknowledgement");
          if (!Serial.find("A"))
          {

            while (!Serial.available());
            delay(100);
          }
          if (!Serial.find("A2"))
          {
            while (!Serial.available());
            delay(100);
          }
          if (!Serial.find("A3"))
          {
            while (!Serial.available());
            delay(100);
          }
    */
    String Reply_2 = "";
    while (Serial.available())
    {
      Reply_2 += char(Serial.read());
    }

    Serial.print(Reply_2);

    //  if (!Serial.find("S"))
    if (Reply_2 != "S")
      goto Resend;
    else
      Serial.print("r");


  }
}
///////////////////////////////////////////////////////////////////////////////////

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

///////////////////////////////////////////////////////////////////////////////
