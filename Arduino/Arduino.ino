#include <Adafruit_NeoPixel.h>
#include <DS1302.h>
#include <EEPROM.h>

#define  NUMPIXELS      30

// Strip with 30 LEDs connected at pin 10.
Adafruit_NeoPixel strip = Adafruit_NeoPixel(NUMPIXELS, 10, NEO_GRBW + NEO_KHZ800);

// Leds' control globals
char currentMode = '1';
int currentRed = 200;
int currentGreen = 200;
int currentBlue = 200;
int brightness = 240;

bool person1 = true;
bool person2 = true;

bool enabled = true;

int fft_values[NUMPIXELS];
bool fft_new_values = false;

// Clock and alarms
DS1302 rtc(11, 12, 13);
Time alarms[7];

void setCurrentColors() {
  Time t = rtc.getTime();

  if (t.hour <= 5) {
    currentRed = 0xFF;
    currentGreen = 0x32;
    currentBlue = 0x00;
  }
  else if (t.hour <= 7) {
    currentRed = 0xFF;
    currentGreen = 0x32 + (t.hour - 6) * 60 + t.min;
    currentBlue = 0x00;
  }
  else if (t.hour <= 9) {
    currentRed = 0xFF;
    currentGreen = 0xAA + (((t.hour - 8) * 60 + t.min) >> 1);
    currentBlue = 0x20 + (t.hour - 8) * 60 + t.min;
  }
  else if (t.hour == 23) {
    currentRed = 0xFF;
    currentGreen = 0x6E - t.min;
    currentBlue = 0x00;
  }
  else if (t.hour >= 21) {
    currentRed = 0xFF;
    currentGreen = 0xE6 - ((t.hour - 21) * 60 + t.min);
    currentBlue = 0x78 - ((t.hour - 21) * 60 + t.min);
  }
}

void setup() {

  rtc.halt(false);

  setCurrentColors();

  // Setup leds
  strip.setBrightness(brightness);
  strip.begin();
  strip.show();

  // Setup bluetooth
  Serial.begin(9600);

  // Read alarms to EEPROM and save them in 'alarms' vector
  readAlarms(alarms);
}

void loop() {

  // Parse received commands
  if(Serial.available()) {
    String input = Serial.readStringUntil('.');

    char person;
    String alarmsStr;

    switch(input.charAt(0)) {
      // Set color command
      case '#':
        brightness = strtol(input.substring(1, 3).c_str(), 0, 16);
        currentRed = strtol(input.substring(3, 5).c_str(), 0, 16);
        currentGreen = strtol(input.substring(5, 7).c_str(), 0, 16);
        currentBlue = strtol(input.substring(7, 9).c_str(), 0, 16);
        break;

      // Set mode command
      case 'M':
        currentMode = input.charAt(1);
        break;

      // Set person command
      case 'P':
        person = input.charAt(1);

        if (person == '0') {
          person1 = true;
          person2 = true;
        }
        else if (person == '1') {
          person1 = true;
          person2 = false;
        }
        else if (person == '2') {
          person1 = false;
          person2 = true;
        }

        break;

      // Set time command
      case 'S':
        setTime(input.substring(1, 16));
        break;

      // Get time command
      case 'G':
        //alarmsStr = "";
        Serial.print(getTime());
        //Serial.print(alarmsStr);
        Serial.flush();
        break;

      // Set alarms command
      case 'A':
        saveAlarms(input.substring(1, 43));
        break;

      // Set FFT values command
      case 'F':
        byte in_values[2+NUMPIXELS*2];
        input.toCharArray(in_values, 2+NUMPIXELS*2);

        // Custom loop for NUMPIXELS=30
        for (int i = 0; i < NUMPIXELS; ++i) {
          uint8_t in_index = (i*2) + 1;
          fft_values[i] = (in_values[in_index] - '0') * 10 + (in_values[in_index+1] - '0');
        }
        
        /*for (int i = 0; i < NUMPIXELS; i += 3) {
          uint8_t in_index = (i << 1) + 1;
          uint8_t first = (in_values[in_index] - '0') * 10 + (in_values[++in_index] - '0');
          uint8_t second = (in_values[++in_index] - '0') * 10 + (in_values[++in_index] - '0');
          uint8_t third = (in_values[++in_index] - '0') * 10 + (in_values[++in_index] - '0');

          fft_values[i] = first;
          fft_values[i+1] = second;
          fft_values[i+2] = third;
        }*/
        fft_new_values = true;
        break;

    }
  }

  // No check each loop iteration
  else if(!enabled && checkAlarms()) {
    currentRed = 200;
    currentGreen = 200;
    currentBlue = 200;
    mode1();
  }

  // Send values to strip
  switch(currentMode) {
    case '0':
      mode0();
      break;
    case '1':
      mode1();
      break;
    case '2':
      mode2();
      break;
    case '3':
      mode3();
      break;
    case '4':
      mode4();
      break;
    case '5':
      mode5();
      break;
    case '6':
      mode6();
      break;
  }


  delay(10);
}

