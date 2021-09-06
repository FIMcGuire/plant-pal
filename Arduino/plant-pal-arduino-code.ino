//include libraries
#include <ThingSpeak.h>
#include <SPI.h>
#include "DHT.h"
#include <WiFi101.h>
#include <RTCZero.h>
#define DHTPIN 2     // Digital pin connected to the DHT sensor
#define DHTTYPE DHT11   // DHT 11

//setup rtc
RTCZero rtc;

//setup RTCZero variables
const byte seconds = 0;
const byte minutes = 0;
const byte hours = 0;
const byte day = 13;
const byte month = 11;
const byte year = 19;

//create boolean matched, set it to false
bool matched = false;

//setup DHT sensor
DHT dht(DHTPIN, DHTTYPE);

//Declare PIN variables
int lightPin = A1;
int soilPin = A6;
int soilPower = 1;

//Declare variables
unsigned long currentTime;
float h;
float t;
int reading;
int val;
int interval = 6;
int statusCode = 0;
float tMean;
float hMean;
int readingMean;
int valMean;

//create int status equal to variable WL_IDLE_STATUS
int status = WL_IDLE_STATUS;

//Cant connect to house wifi, must connect to PC WiFi Hotspot (Change these variables to appropriate WiFi network)
WiFiClient client;
char ssid[] = "Fraser's PC";
char pass[] = "marc0p0l0";

//create variables for ThingSpeak
unsigned long myChannelNumber = 908469;
unsigned long myIntervalChannelNumber = 908472;
const char * myWriteAPIKey = "EBSFHFBU7EHYGPH8";
const char * myReadAPIKey = "45OMXHJYN2CQFH5X";

//setup method
void setup() {
  //setup serial monitor
  Serial.begin(9600);
  //setup dht sensor
  dht.begin();
  //setup soil moisture sensor power pin
  pinMode(soilPower, OUTPUT);
  digitalWrite(soilPower, LOW);

  //setup thingspeak
  ThingSpeak.begin(client);
  delay(1000);
  //connect to WiFi
  WiFi.begin(ssid, pass);
  delay(5000);

   //call method sense()
  sense();
  
  delay(1000);

  //begin RTCZero
  rtc.begin();

  //set current time for RTCZero alarm
  rtc.setTime(hours, minutes, seconds);
  rtc.setDate(day, month, year);

  //rtc.setAlarmTime(00, interval, 00);
  //rtc.enableAlarm(rtc.MATCH_MMSS);

  //set RTCZero alarm time equal to interval variable for hours parameter
  rtc.setAlarmTime(interval, 00, 00);
  //Enable RTCZero alarm with Hours, mins and seconds match type
  rtc.enableAlarm(rtc.MATCH_HHMMSS);

  //attatch the interrupt to method alarmMatch
  rtc.attachInterrupt(alarmMatch);
}

void loop() {
  //if statement with condition of matched being true
  if (matched) {
    //set matched to false
    matched = false;
    //blink onboard LED twice
    digitalWrite(LED_BUILTIN, HIGH);
    delay(500);
    digitalWrite(LED_BUILTIN, LOW);
    delay(500);
    Serial.println(F("Matched"));
    digitalWrite(LED_BUILTIN, HIGH);
    delay(500);
    digitalWrite(LED_BUILTIN, LOW);
    delay(500);

    //call method sense()
    sense();
    
    delay(1000);

    //create int alarmHours, set it equal to int returned from getHours() method
    int alarmHours = rtc.getHours();
    //alarmHours equal to itself plus interval
    alarmHours += interval;
    //if statement with condition of alarmHours being greater than or equal to 24
    if (alarmHours >= 24) {
      //alarmHours equal to itself minus 24
      alarmHours -= 24;
    }

    /*int alarmMins = rtc.getMinutes();
    alarmMins += interval;
    if (alarmMins >= 60) {
      alarmMins -= 60;
    }*/

    //set new AlarmTime with alarmHours variable and current minutes and seconds using appropriate methods
    rtc.setAlarmTime(alarmHours, rtc.getMinutes(), rtc.getSeconds());
    //rtc.setAlarmTime(rtc.getHours(), alarmMins, rtc.getSeconds());
  }
}

//method for RTCZero Alarm interrupt
void alarmMatch() {
  //set matched to true
  matched = true;
}

//method for getting temp and humidity readings
void getTemp() {
  // Reading temperature or humidity takes about 250 milliseconds!
  // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
  h = dht.readHumidity();
  // Read temperature as Celsius (the default)
  t = dht.readTemperature();

  // Check if any reads failed and exit early (to try again).
  if (isnan(h) || isnan(t)) {
    Serial.println(F("Failed to read from DHT sensor!"));
    return;
  }

  //print values
  Serial.print(F("Humidity: "));
  Serial.print(F(h));
  Serial.print(F("%  Temperature: "));
  Serial.print(F(t));
  Serial.println(F("°C "));
}

//method sense() to gather sensor data and send to ThingSpeak
void sense() {
  //set Mean variables to 0
  tMean = 0;
  hMean = 0;
  readingMean = 0;
  valMean = 0;

  //----------------- Network -----------------//
  //if statement with condition of RSSI() method returning less than or equal to 0
  if (WiFi.RSSI() <= 0)
  {
    //call end() method
    WiFi.end();
    delay(1000);
    //do while loop with condition of status() not returning variable WL_CONNECTED
    while (WiFi.status() != WL_CONNECTED)
    {
      //print connection, attempt to connect using begin() sending ssid and pass as parameters
      Serial.print(F("Connecting to "));
      Serial.print(F(ssid));
      Serial.println(F(" ...."));
      WiFi.begin(ssid, pass);
      delay(5000);
    }
    Serial.println("Connected to Wi-Fi Succesfully.");
  }
  //--------- End of Network connection--------//
  
  //---------------- Read from Thingspeak ----------------//
  //set interval equal to value returned from readLongField() method, sending myIntervalChannelNumber, 1 and myReadAPIKey as parameters
  interval = ThingSpeak.readLongField(myIntervalChannelNumber, 1, myReadAPIKey);
  //create int statusCode equal to value returned from getLastReadStatus()
  int statusCode = ThingSpeak.getLastReadStatus();
  //if statement with condition of statusCode being equal to 200
  if (statusCode == 200)
  {
    //print interval
    Serial.print(F("New Interval: "));
    Serial.println(F(interval));
  }
  else
  {
    //send error print
    Serial.print(F("Unable to read channel / No internet connection"));
    Serial.println(F("Using default Interval of 60000 milliseconds"));
  }
  delay(1000);
  //-------------- End of Read from Thingspeak -------------//

  //-------------- Read Data from Sensors -------------//
  //set currentTime equal to value from millis() method (millis() returns milliseconds since Arduino initialisation)
  currentTime = millis();
  //do while loop with condition of millis() returning a number smaller than currentTime variable plus 10000
  while(millis() < currentTime + 10000)
  {
    //call getTemp() method
    getTemp();
    //Mean variables equal to themselves plus t, and h respectively
    tMean += t;
    hMean += h;
    delay(1000);
  }
  delay(1000);

  //set currentTime equal to value from millis() method
  currentTime = millis();
  //do while loop with condition of millis() returning a number smaller than currentTime variable plus 10000
  while(millis() < currentTime + 10000)
  {
    //set reading equal to value from analogRead() of lightPin
    reading  = analogRead(lightPin);
    Serial.println(F(reading));
    //set readingMean equal to itself plus reading
    readingMean += reading;
    delay(1000);
  }
  delay(1000);

  //set currentTime equal to value from millis()
  currentTime = millis();
  //do while loop with condition of millis() returning a number smaller than currentTime variable plus 10000
  while(millis() < currentTime + 10000)
  {
    //digitalWrite() soilPower pin HIGH, i.e. send power down said pin
    digitalWrite(soilPower, HIGH);
    delay(10);
    //set val equal to analogRead() of soilPin
    val = analogRead(soilPin);
    Serial.println(F(val));
    //digitalWrite() soilPower pin LOW, i.e. stop sending power down said pin
    digitalWrite(soilPower, LOW);
    //set valMean equal to itself plus val variable
    valMean += val;
    delay(1000);
  }

  //set variables equal to Means divided by 10
  t = tMean / 10;
  h = hMean / 10;
  reading = readingMean / 10;
  val = valMean / 10;

  //print values
  Serial.println(F(t));
  Serial.println(F(h));
  Serial.println(F(reading));
  Serial.println(F(val));
  
  delay(1000);
  //-------------- End of Read Data from Sensors -------------//

  //-------------- Send Data to ThingSpeak -------------//
  delay(1000);
  //setField() methods, setting field number and values
  ThingSpeak.setField(1, t);
  ThingSpeak.setField(2, h);
  ThingSpeak.setField(3, reading);
  ThingSpeak.setField(4, val);

  //create int code equal to value returned from writeFields() method (This method sends data to thingspeak, sending channelnumber and API key to method along with values from setField())
  int code = ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);

  //if statement with condition of code not being 200
  if (code != 200)
  {
    //send api request again, this time using alternative method
    code = ThingSpeak.writeRaw(myChannelNumber, "https://api.thingspeak.com/update?&field1=" + String(t) + "&field2=" + String(h) + "&field3=" + String(reading) + "&field4=" + String(val), myWriteAPIKey);

    //print code
    //code = ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);
    Serial.println(F("Return code: "));
    Serial.println(F(code));
    delay(1000);
  }

  //print code
  Serial.println(F("Return code: "));
  Serial.println(F(code));
  //-------------- End of Send Data to ThingSpeak -------------//
  
  Serial.println(F("RELOOP"));
}
