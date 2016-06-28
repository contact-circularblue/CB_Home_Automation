#include <SoftwareSerial.h>
SoftwareSerial ESP(10, 11);

const int Power_LED =  12;
const int WiFi_LED =  13;

void setup()// put your setup code here, to run once:
{
 //Serial.begin(9600);
 Serial.begin(115200);
 ESP.begin(9600);
 pinMode(Power_LED, OUTPUT);
 pinMode(WiFi_LED, OUTPUT);
 digitalWrite(Power_LED,HIGH);
 
 pinMode(5,OUTPUT);
 pinMode(6,OUTPUT);
 pinMode(7,OUTPUT);
 pinMode(8,OUTPUT);

}

void loop() // put your main code here, to run repeatedly:
{
 //start:
 while (ESP.available())
   ESP.read();
 delay(1000);

 ESP.println("AT+CIOBAUD=9600");
 delay(1000);
 if (ESP.find("OK"))
   Serial.println("Baud rate = 9600");
 else
   Serial.println("Error");
 while (ESP.available())
   ESP.read();

 ESP.println("ATE0");
 delay(1000);
 if (ESP.find("OK"))
   Serial.println("Echo OFF");
 else
   Serial.println("Error");
 while (ESP.available())
   ESP.read();


 ESP.println("AT");
 delay(500);
 if (ESP.find("OK"))
   Serial.println("OK");
 else
   Serial.println("Error");
 while (ESP.available())
   ESP.read();

 ESP.println("AT+RST");
 delay(3000);
 while (ESP.available())
   ESP.read();

 ESP.println("AT");
 delay(500);
 if (ESP.find("OK"))
   Serial.println("OK");
 else
   Serial.println("Error");
 while (ESP.available())
   ESP.read();

CIPMUX:
 ESP.println("AT+CIPMUX=1");
 delay(1000);
 if (ESP.find("OK"))
 {
   Serial.println("MUX IS SET TO 1");
   while (ESP.available())
     ESP.read();
 }
 else
 {
   Serial.println("Error");
   while (ESP.available())
     ESP.read();
   goto CIPMUX;
 }

 ESP.println("AT+CWMODE=1");
 delay(500);
 if (ESP.find("OK"))
   Serial.println("MODE is set to 1");
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


 Serial.println("Connecting to WiFi");
 ESP.println("AT+CWJAP=\"AndroidAP\",\"Vibgyor1!\"");
 delay(10000);
 
 while (ESP.available())
   ESP.read();


 ESP.println("AT+CWJAP?");
 delay(5000);

 bool a = ESP.find("AndroidAP");
 //noInterrupts();

 if (a == 1)
 {
   
   Serial.println("Wifi connection Successful");
   
   while (ESP.available())
     ESP.read();
     TCCR1B = 0;
    //noInterrupts(); 
   digitalWrite(WiFi_LED,HIGH);
 }

 else
 {
   Serial.println("Error connecting to wifi");


   while (ESP.available())
     ESP.read();
   goto Wifi_Connect;
 }
////////////////////////////  
 Connect_Thingspeak:
 bool Connect=Connect_To_Thingspeak();
 if(Connect!=1)
 goto Connect_Thingspeak;
 
 String data="GET /channels/103571/fields/7/last.json?";

 bool transmisson=start_transmission(data.length());
 if (transmisson!=1)
 goto Wifi_Connect;  
 
 String integer=send_data(data, '7');
 Serial.print("integer=");
 Serial.println(integer);
 if(integer=="1")
 {
   Get_ssid:
 int fwd=0;
 while(fwd==0)
 {
 bool Connect=Connect_To_Thingspeak();
 if(Connect==1)
 fwd=1;
 }
 data="";
 data="GET /channels/103571/fields/5/last.json?";

 transmisson=start_transmission(data.length());
 if (transmisson!=1)
 goto Wifi_Connect;  
 
 String ssid=send_data(data, '5');
 Serial.print("ssid=");
 Serial.println(ssid);
 
 if(ssid.length()<4)
 goto Get_ssid;
 
 
 Get_Password:
 fwd=0;
 while(fwd==0)
 {
 bool Connect=Connect_To_Thingspeak();
 if(Connect==1)
 fwd=1;
 }
 
 data="";
 data="GET /channels/103571/fields/6/last.json?";

 transmisson=start_transmission(data.length());
 if (transmisson!=1)
 goto Wifi_Connect;  
 
 String password=send_data(data, '6');
 Serial.print("password=");
 Serial.println(password);
 
 if(password.length()<4)
 goto Get_Password;
 
 while (ESP.available())
 ESP.read();
 
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
 String Wifi_details="AT+CWJAP=\"";
 Wifi_details+=ssid;
 Wifi_details+="\",\"";
 Wifi_details+=password;
 Wifi_details+="\"";
 
 ESP.println(Wifi_details);
 delay(10000);
 /*
 if(ESP.find("GOT IP"))
 Serial.println("CONNECTED TO HOME WIFI");
 else
 Serial.println("NOT CONNECTED");
 */
 while (ESP.available())
   ESP.read();


 ESP.println("AT+CWJAP?");
 delay(5000);

 bool a = ESP.find("Circular");


 if (a == 1)
 {
   Serial.println("HOME Wifi connection Successful");
   while (ESP.available())
     ESP.read();
     TCCR1B = 0;
    //noInterrupts(); 
   digitalWrite(WiFi_LED,HIGH);
   
   
 while(1)                 // Finally fetch data for fields from the server
{  
   fwd=0;
 while(fwd==0)
 {
 bool Connect=Connect_To_Thingspeak();
 if(Connect==1)
 fwd=1;
 }
 
 data="";
 data="GET /channels/103571/fields/4/last.json?";   // fan_ speed

 transmisson=start_transmission(data.length());
 if (transmisson!=1)
 goto Wifi_Connect;  
 
 String fan_speed=send_data(data, '4');
 Serial.print("Speed=");
 Serial.println(fan_speed);
 
 if(fan_speed=="1")
 {
 digitalWrite(5, HIGH);
 digitalWrite(6, LOW);
 digitalWrite(7, HIGH);
 digitalWrite(8, LOW);
 }
 
   if(fan_speed=="2")
   {
 digitalWrite(5, HIGH);
 digitalWrite(6, LOW);
 digitalWrite(7, LOW);
 digitalWrite(8, HIGH);
   }
   
   
   fwd=0;
 while(fwd==0)
 {
 bool Connect=Connect_To_Thingspeak();
 if(Connect==1)
 fwd=1;
 }
 
 data="";
 data="GET /channels/103571/fields/1/last.json?";     // light_1

 transmisson=start_transmission(data.length());
 if (transmisson!=1)
 goto Wifi_Connect;  
 
 String L1=send_data(data, '1');
 Serial.print("Light 1=");
 Serial.println(L1);
 
 if(L1=="1")
 {
 
 digitalWrite(4, HIGH);
 }
 
   if(L1=="0")
   {
 digitalWrite(4, LOW);
   }
} 
 }

 else
 {
   Serial.println("Error connecting to wifi");

   while (ESP.available())
     ESP.read();
   goto Wifi_Connect2;
 }
 
 
 while(1);
 }
 else
 {
   goto Connect_Thingspeak;
   Serial.println("Reseting Connection");
 }
 //while (1);

}

bool Connect_To_Thingspeak()
{
 ESP.println("AT+CIPSTART=4,\"TCP\",\"184.106.153.149\",80");
 delay(1500);

 if(ESP.find("CONNECT"))
 {
 Serial.println("Linked to Thingspeak.com");
 while(ESP.available())
 ESP.read();
 return 1;
 }
 else
 {
 Serial.println("Error connecting to Thingspeak.com");
    delay(1000);
 while(ESP.available())
 ESP.read();
 return 0;
 //goto CIPMUX;
 }
}

bool start_transmission(int data_length)
{
 data_length=data_length+2;
 String data="";
 data+="AT+CIPSEND=4,";
 data+=data_length;
 
 ESP.println(data);
 delay(1000);
 if (ESP.find(">"))
 {
   Serial.println("> received");
   while (ESP.available())
   ESP.read();
   return 1;
 }
 else
 {
   Serial.println("Error in command AT+CIPSEND");
   while (ESP.available())
   ESP.read();
   return 0;
 }
 
}

String send_data(String data, char field)
{
 String field_check="";
 field_check+="\"field";
 field_check+=field;
 field_check+="\":\"";
   
 char buff[field_check.length()+1];
 field_check.toCharArray(buff,field_check.length()+1);
 
 //delay(500);
 ESP.println(data);
 
 /*
 while(1)
 while(ESP.available())
 Serial.print(char(ESP.read()));
 */
 
 
 if (ESP.find("+IPD"))
 Serial.println("found +IPD");
 
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
 //Serial.println("11111");
 Serial.flush();
 return field_read;
}


ISR(TIMER1_OVF_vect)        
{
 TCNT1 = 20000;            // preload timer
 digitalWrite(WiFi_LED, digitalRead(WiFi_LED) ^ 1);
}

