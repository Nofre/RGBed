void setTime(String timestamp) {
  rtc.writeProtect(false);
  rtc.setDate(atoi(timestamp.substring(6, 8).c_str()), atoi(timestamp.substring(4, 6).c_str()), atoi(timestamp.substring(0, 4).c_str()));
  rtc.setDOW(atoi(timestamp.charAt(14)));
  rtc.setTime(atoi(timestamp.substring(8, 10).c_str()), atoi(timestamp.substring(10, 12).c_str()), atoi(timestamp.substring(12, 14).c_str()));
}


String getTime() {
  String dow = rtc.getDOWStr(FORMAT_LONG);
  String time = rtc.getTimeStr(FORMAT_LONG);
  String date = rtc.getDateStr(FORMAT_LONG);
  return dow + " " + time + " " + date;
}

void saveAlarms(String alarms) {
  for (int dow = 1; dow <= 7; dow++) {
    int i = (dow - 1) * 2;
    Time alarm;
    alarm.dow = dow;
    alarm.hour = alarms.charAt(i) - '0';
    alarm.min = alarms.charAt(i+1) - '0';
    saveAlarm(alarm);
  }
}


void readAlarms(Time* alarms) {
  for (int dow = 1; dow <= 7; dow++) {
    alarms[dow-1] = readAlarm(dow);    
  }
}

bool checkAlarms() {
  Time t = rtc.getTime();
  Time alarm = alarms[t.dow];

  return t.hour == alarm.hour && t.min == alarm.min;
}

