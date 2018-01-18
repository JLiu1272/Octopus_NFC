/**************************************************************************/
/*! 
    @file     readMifare.pde
    @author   Jennifer Liu, Adafruit Industries  
	  @license  BSD (see license.txt)

    This example will wait for any ISO14443A card or tag, and
    depending on the size of the UID will attempt to read from it.
   
    If the card has a 4-byte UID it is probably a Mifare
    Classic card, and the following steps are taken:
   
    - Authenticate block 4 (the first block of Sector 1) using
      the default KEYA of 0XFF 0XFF 0XFF 0XFF 0XFF 0XFF
    - If authentication succeeds, we can then read any of the
      4 blocks in that sector (though only block 4 is read here)
	 
    If the card has a 7-byte UID it is probably a Mifare
    Ultralight card, and the 4 byte pages can be read directly.
    Page 4 is read by default since this is the first 'general-
    purpose' page on the tags.


This is an example sketch for the Adafruit PN532 NFC/RFID breakout boards
This library works with the Adafruit NFC breakout 
  ----> https://www.adafruit.com/products/364
 
Check out the links above for our tutorials and wiring diagrams 
These chips use SPI or I2C to communicate.

Adafruit invests time and resources providing this open source code, 
please support Adafruit and open-source hardware by purchasing 
products from Adafruit!

*/
/**************************************************************************/
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_PN532.h>

// If using the breakout with SPI, define the pins for SPI communication.
#define PN532_SCK  (2)
#define PN532_MOSI (3)
#define PN532_SS   (4)
#define PN532_MISO (5)

// If using the breakout or shield with I2C, define just the pins connected
// to the IRQ and reset lines.  Use the values below (2, 3) for the shield!
#define PN532_IRQ   (2)
#define PN532_RESET (3)  // Not connected by default on the NFC Shield

// Uncomment just _one_ line below depending on how your breakout or shield
// is connected to the Arduino:

// Use this line for a breakout with a software SPI connection (recommended):
//Adafruit_PN532 nfc(PN532_SCK, PN532_MISO, PN532_MOSI, PN532_SS);

// Use this line for a breakout with a hardware SPI connection.  Note that
// the PN532 SCK, MOSI, and MISO pins need to be connected to the Arduino's
// hardware SPI SCK, MOSI, and MISO pins.  On an Arduino Uno these are
// SCK = 13, MOSI = 11, MISO = 12.  The SS line can be any digital IO pin.
//Adafruit_PN532 nfc(PN532_SS);

// Or use this line for a breakout or shield with an I2C connection:
Adafruit_PN532 nfc(PN532_IRQ, PN532_RESET);

#if defined(ARDUINO_ARCH_SAMD)
// for Zero, output on USB Serial console, remove line below if using programming port to program the Zero!
// also change #define in Adafruit_PN532.cpp library file
   #define Serial SerialUSB
#endif

void setup(void) {
  #ifndef ESP8266
    while (!Serial); // for Leonardo/Micro/Zero
  #endif
  Serial.begin(115200);
  Serial.println("-----Peer to Peer HCE-------------");

  nfc.begin();

  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1); // halt
  }
  // Got ok data, print it out!
  Serial.print("Found chip PN5"); Serial.println((versiondata>>24) & 0xFF, HEX); 
  Serial.print("Firmware ver. "); Serial.print((versiondata>>16) & 0xFF, DEC); 
  Serial.print('.'); Serial.println((versiondata>>8) & 0xFF, DEC);
  
  // configure board to read RFID tags
  nfc.SAMConfig();
}


void loop(void) {
  bool success;
  
  uint8_t responseLength = 32;
  
  Serial.println("Waiting for an ISO14443-4 Card ...");
  
  // set shield to inListPassiveTarget 
  success = nfc.inListPassiveTarget();
  
  if(success) {
   
     Serial.println("Found something!");
     
    /*
      ADPU that matches the one specified on application
    */
    uint8_t selectApdu[] = { 
      0x00, /* CLA */
      0xA4, /* INS */
      0x04, /* P1  */
      0x00, /* P2  */
      0x05, /* Length of AID  */
      0xF2, 0x22, 0x22, 0x22, 0x22,
      0x01 /*LE*/
    };
                              
    uint8_t response[32];  
     
    success = nfc.inDataExchange(selectApdu, sizeof(selectApdu), response, &responseLength);
    
    if(success) {
      
      Serial.print("responseLength: "); Serial.println(responseLength);
       
      nfc.PrintHexChar(response, responseLength);
      
      do {
        uint8_t apdu[] = "SOS";
        uint8_t back[32];
        uint8_t length = 32; 

        success = nfc.inDataExchange(apdu, sizeof(apdu), back, &length);
        
        Serial.println(apdu[5]);
        
        Serial.print("EX_RES:");
        Serial.println(success);
        
        if(success) {
          Serial.print("responseLength: "); 
          Serial.println(responseLength);
          for(int i=0; i<responseLength; i++){
              Serial.print(response[i]);
              Serial.print(", ");
          }
          Serial.println();
          Serial.println("========================");
        }
        else {
            Serial.println("Broken Connection?"); 
        }
      }
      while(success);
    }
    else {
     
      Serial.println("Failed sending SELECT AID"); 
    }
  }
  else {
   
    Serial.println("Didn't find anything!");
  }

  delay(1000);
}

void printResponse(uint8_t *response, uint8_t responseLength) {
  
   String respBuffer;

    for (int i = 0; i < responseLength; i++) {
      
      if (response[i] < 0x10) 
        respBuffer = respBuffer + "0"; //Adds leading zeros if hex value is smaller than 0x10
      
      respBuffer = respBuffer + String(response[i], HEX) + " ";                        
    }

    Serial.print("response: "); Serial.println(respBuffer);
}