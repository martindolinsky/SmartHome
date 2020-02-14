//****   Pin 10 --> Bluetooth TX **
//****   Pin 11 --> Bluetooth RX **


#include <SoftwareSerial.h> // Bluetooth knižnica
#include <LiquidCrystal_I2C.h> // LCD display knižnica
#include<Wire.h> // LCD Display knižnica 
#include <SPI.h> //RFID čítačka knižnica
#include "MFRC522.h" // RFID čítačka knižnica

SoftwareSerial mySerial(10, 11); // RX, TX

#define SS_PIN 53
#define RST_PIN 5

MFRC522 mfrc522(SS, UINT8_MAX);   // vytvorenie MFRC522
LiquidCrystal_I2C lcd(0x27, 2, 1, 0, 4, 5, 6, 7, 3, POSITIVE);//LCD Objekty  Parametre: (rs, enable, d4, d5, d6, d7)

// ........ TEMPERATURE FLAG .............


int temperature = 25;


// nastavenie čísla vstupných pinov v premenných
int led1 = 13;
int led2 = 12;
int led3 = 5;
int led4 = 6;
int led5 = 7;
const int cidloPin1 = 18;
const int cidloPin2 = 19;
int led1_cnt = 0;
int led2_cnt = 0;
int data;

void setup() {
  
  // ........... LCD ....................
  mySerial.begin(9600);
  Serial.begin(9600);
  SPI.begin();
  mfrc522.PCD_Init();

  lcd.begin(20, 4); // šírka a výška displaya } 
  lcd.setCursor(3,0);
  lcd.print("SMARTHOME v2.0");
  lcd.setCursor(0,1);
  lcd.print("Prilozte kartu: ");
  lcd.setCursor(0,3);
  lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
  // ........ END of LCD ...............

  // ............ HC ...................
  pinMode(cidloPin1, INPUT);
  pinMode(cidloPin2, INPUT);
  attachInterrupt(digitalPinToInterrupt(cidloPin1), detekcia1, RISING);
  attachInterrupt(digitalPinToInterrupt(cidloPin2), detekcia2, RISING);
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);
  pinMode(led4, OUTPUT);
  pinMode(led5, OUTPUT);
  pinMode(9, OUTPUT);
  // ......... END of HC...............
}

void detekcia1() {
  digitalWrite(led1, HIGH);
}
void detekcia2() {
  digitalWrite(led2, HIGH);
}



void loop() {

 
  if (digitalRead(led1) == HIGH && ++led1_cnt > 50) {
    digitalWrite(led1, LOW);
    led1_cnt = 0;
  }
  if (digitalRead(led2) == HIGH && ++led2_cnt > 50) {
    digitalWrite(led2, LOW);
    led2_cnt = 0;
  }

  delay(500);

  // Hľadať ďalšie karty
  if ( ! mfrc522.PICC_IsNewCardPresent()){
    
  }
  // výber jednej z kariet
  if ( ! mfrc522.PICC_ReadCardSerial()){
    
  }

 // Serial.print("UID tag :");
  String content = "";
  byte letter;
  for (byte i = 0; i < mfrc522.uid.size; i++) {
 //   Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
 //   Serial.print(mfrc522.uid.uidByte[i], HEX);
    content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
    content.concat(String(mfrc522.uid.uidByte[i], HEX));
  }

  // .............. GET USER ...............
  content.toUpperCase();
  if (content.substring(1) == "D0 72 7E 7A") {
    lcd.clear();
    lcd.setCursor(3,0);
    lcd.print("SMARTHOME v2.0");
    lcd.setCursor(0,1);
    lcd.print("Prilozte kartu: ");
    lcd.setCursor(0, 2);
    lcd.setCursor(0,3);
    lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
    lcd.setCursor(0,2);
    lcd.print("M.Rendvansky");
    mySerial.println("M. Rendvansky#");
  }
  
  else if (content.substring(1) == "C4 10 3F 3D") {
    lcd.clear();
    lcd.setCursor(3,0);
    lcd.print("SMARTHOME v2.0");
    lcd.setCursor(0,1);
    lcd.print("Prilozte kartu: ");
    lcd.setCursor(0,3);
    lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
    lcd.setCursor(0,2); // Sets the loc
    lcd.print("M.Dolinsky");
    mySerial.println("M. Dolinsky#");

  }
    else if (content.substring(1).length() > 0) {
    lcd.clear();
    lcd.setCursor(3,0);
    lcd.print("SMARTHOME v2.0");
    lcd.setCursor(0,1);
    lcd.print("Prilozte kartu: ");
    lcd.setCursor(0,3);
    lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
    lcd.setCursor(0,2); // Sets the loc
    lcd.print("Neznamy");
        mySerial.println("Neznamy#");

  }
  else   {
    lcd.clear();
    lcd.setCursor(3,0);
    lcd.print("SMARTHOME v2.0");
    lcd.setCursor(0,1);
    lcd.print("Prilozte kartu: ");
    lcd.setCursor(0,3);
    lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
    lcd.setCursor(0,2);
    lcd.print("");    

  }
  // ........... END of GET USER ...........

  //............ Bluetooth .................
  if(mySerial.available() > 0)
  {
    data = mySerial.readString().toInt(); 
    Serial.println(data);
    
      if(data == 0) 
      {     
         digitalWrite(led3, HIGH);
         }
         
      if(data == 1) 
      {     
         digitalWrite(led3, LOW);
         }
         
      if(data == 2) 
      {     
         digitalWrite(led4, HIGH);
         }
         
      if(data == 3) 
      {     
         digitalWrite(led4, LOW);
         }
         
      if(data == 4) 
      {     
         digitalWrite(led5, HIGH);
         }
         
      if(data == 5) 
      {     
         digitalWrite(led5, LOW);
         }
         
      if(data == 6) 
      {     
         temperature++;

         lcd.setCursor(0,3);
         lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
         }
         
      if(data == 7) 
      {             
          temperature--;
          lcd.setCursor(0,3);
          lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
         }

      if(data == 8) 
      {     
         temperature = temperature + 5;

         lcd.setCursor(0,3);
         lcd.print("Teplota: " + String(temperature) + (char)223 + "C");
         }

      if(data == 9) 
      {     
         temperature = temperature - 5;

         lcd.setCursor(0,3);
         lcd.print("Teplota: " + String(temperature) + (char)223 + "C"); 
         }
  }

  //............ END of BLUETOOTH ...........

}
