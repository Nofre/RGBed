// Custom delay that also checks if there are data available from bluetooth.
bool customDelay(int d) {

  const int minDelay = 50;

  int r = d % minDelay;

  for (int i = 0; i < d-r; i += minDelay) {
    if (Serial.available()) return true;
    delay(minDelay);
  }

  if (Serial.available()) return true;
  
  if (r) {
    delay(r);
    if (Serial.available()) return true;
  }

  return false;
}

