// ---------------------------------------------------------------------------------------------------------------------------------------------
// | Byte 00 | Byte 01 | Byte 02 | Byte 03 | Byte 04 | Byte 05 | Byte 06 | Byte  7 | Byte 08 | Byte 09 | Byte 10 | Byte 11 | Byte 12 | Byte 13 |
// ---------------------------------------------------------------------------------------------------------------------------------------------
// | MON  HH | MON  MM | TUE  HH | TUE  MM | WED  HH | WED  MM | THU  HH | THU  MM | FRI  HH | FRI  MM | SAT  HH | SAT  MM | SUN  HH | SUN  MM | 
// ---------------------------------------------------------------------------------------------------------------------------------------------

// 0xFF = Not Set


void saveAlarm(Time alarm) {
  int addr = (alarm.dow - 1) * 2;
  EEPROM.update(addr, alarm.hour);
  EEPROM.update(addr+1, alarm.min);
}

Time readAlarm(int dow) {
  int addr = (dow - 1) * 2;
  Time alarm;
  alarms[dow-1].dow = dow;
  alarms[dow-1].hour = EEPROM.read(addr);
  alarms[dow-1].min = EEPROM.read(addr+1);

  return alarm;
}

