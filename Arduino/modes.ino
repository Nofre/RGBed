uint8_t index = 0;
boolean gamma_back = false;

uint32_t FFT_COLORS[] = {
    0x00002200, 0x00004400, 0x00008800, 0x0000AA00,
    0x0022CC00, 0x0044CC00, 0x0066CC00, 0x0088CC00,
    0x00AACC00, 0x00CCCC00, 0x00DDDD00, 0x00DDCC00,
    0x00DDAA00, 0x00DD8800, 0x00DD6600, 0x00DD4400,
    0x00DD2200, 0x00EE0000, 0x00FF0000, 0x00FF0088,
    0x00FF00AA, 0x00FF00CC, 0x00FF00DD, 0x00FF00FF
};

/** Mode 0
 * Audio analyzer 
 */
void mode0() {
  if (fft_new_values) {
    for(int i=0; i<NUMPIXELS; i++){
      strip.setPixelColor(i, FFT_COLORS[fft_values[i]]);
    }
    strip.show();
    fft_new_values = false;
  }
  if (customDelay(10)) return;
}

/** Mode 1
 * Static color
 */
void mode1() {
  strip.setBrightness(brightness);

  if (person1 && !person2) {
    for(int i = 0; i < 10; i++) {
      strip.setPixelColor(i, strip.Color(currentRed, currentGreen, currentBlue));
    }
    for(int i = 10; i < NUMPIXELS; i++) {
      strip.setPixelColor(i, 0);
    }
  }
  else if (!person1 && person2) {
    for(int i = 0; i < 20; i++) {
      strip.setPixelColor(i, 0);
    }
    for(int i = 20; i < NUMPIXELS; i++) {
      strip.setPixelColor(i, strip.Color(currentRed, currentGreen, currentBlue));
    }
  }
  else {
    for(int i = 0; i < NUMPIXELS; i++) {
      strip.setPixelColor(i, strip.Color(currentRed, currentGreen, currentBlue));
    }
  }

  strip.show();

  if (customDelay(1000)) return;
}

/** Mode 2
 * 
 */
void mode2() {
  for (uint16_t i = 0; i < 255; ++i) {
    for(uint16_t j = 0; j < strip.numPixels(); j++) {
      strip.setPixelColor(j, strip.Color(currentRed, currentGreen, currentBlue, i));
    }
    strip.show();
    if (customDelay(50)) return;
  }
  
  for (uint16_t i = 255; i > 0; --i) {
    for(uint16_t j = 0; j < strip.numPixels(); j++) {
      strip.setPixelColor(j, strip.Color(currentRed, currentGreen, currentBlue, i));
    }
    strip.show();
    if (customDelay(50)) return;
  }
}

/** Mode 3
 * TODO
 */
void mode3() {
  colorWipe(strip.Color(currentRed, currentGreen, currentBlue), 50);
  colorWipe(strip.Color(0, 0, 0), 50);
}

/** Mode 4
 * TODO
 */
void mode4() {
  for (uint16_t i = 0; i < 1000; i+=20) {
    colorWipe(strip.Color((currentRed+i)%256, (currentGreen+i)%256, (currentBlue+i)%256), 50);
  }
}

/** Mode 5
 * TODO Rainbow 1
 */
void mode5() {
  whiteOverRainbow(20,75,5);
}

/** Mode 6
 * TODO Rainbow 2
 */
void mode6() {
  rainbowFade2White(3,3,0);
}

