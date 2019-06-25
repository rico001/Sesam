
from umqtt.simple import MQTTClient
from machine import Pin, PWM, ADC
import machine
import network
import ujson
import esp32
import time

SSID="XXXXXXXXXXXXXXXX"
PASSWORD="XXXXXXXXXXXXXXXXXXXXXX"

SERVER = "192.168.178.80"
CLIENT_ID = "yourClientID"

TOPIC_SETTINGS = b"Sesam/Settings/date"
TOPIC_ACTIVITY = b"Sesam/Esp/activity"
TOPIC_OPEN_NOW = b"Sesam/openDoorNow"

username='yourIotUserName'
password='yourIotPassword'

wakeUpPin=Pin(33, mode = Pin.IN)
relaisPin=Pin(27,Pin.OUT,value=1)

#LED for inputfeedback
pBlue = Pin(22, Pin.OUT)
pRed = Pin(21, Pin.OUT)
pGreen = Pin(19, Pin.OUT)

untilTime = 10 #10Sekunden warten auf mqttMessages
counterDoorRing = 1
c=None  #mqttlient

currentTime="00:00" #update with getCurrentTime
currentDate="01.01.1900" #update with getCurrentTime
timeCorrection=2  #for timezone

receivedSettings=""
wasOpened=False
duration=0
openForTitles=[]


class Date:
  def __init__(self,day,month,year):
    self.day = day
    self.month = month
    self.year = year
  def __str__(self):
    return str(self.day)+"."+str(self.month)+"."+str(self.year) #textuelle representation

class Activity:
  def __init__(self,time,date,ringNumber,wasOpened,batteryStatus,receivedSettings,openForTitles):
    self.receivedSettings = receivedSettings
    self.time = time
    self.date = date
    self.ringNumber = ringNumber
    self.wasOpened = wasOpened
    self.batteryStatus = batteryStatus
    self.openForTitles=openForTitles
  def __str__(self):
    return \
    "Time: "+str(self.time)+"\n"\
    +"Date: "+str(self.date)+"\n"\
    +"ringNumber: "+str(self.ringNumber)+"\n"\
    +"wasOpened: "+str(self.wasOpened)+"\n"\
    +"Battery status: "+str(self.batteryStatus)+"\n"\
    +"received settings: "+str(self.receivedSettings)+"\n"\
    +"openForTitles: "+str(self.openForTitles)+"\n"


def sub_cb(topic, msg):
  
  global duration
  global wasOpened
  global receivedSettings
  global openForTitles
  
  
  if topic == TOPIC_OPEN_NOW:
    msg=msg.decode("utf-8")
    print("AndroidApp: openDoor for "+msg+" seconds")
    wasOpened=True
    openForTitles.append("Sesam App: \"T\xfcr \xf6ffnen\"-Button") 
    receivedSettings=str(msg)
    duration=int(msg)
    return
  
  
  if topic == TOPIC_SETTINGS:
  
    jsonObj = ujson.loads(msg)
    
    receivedSettings=ujson.dumps(jsonObj)
    
    for i in jsonObj['timeWindows']:
      
      print("Zeitfenster "+"\""+i['title']+"\""+" checken: ")
      
      boolFromDate = compare_date(currentDate,">=",i['FromDate'])  #checke ab Datum
      #print(boolFromDate)
      if boolFromDate != True:
        printLine1()
        continue

      booltillDate = compare_date(currentDate,"<=",i['tillDate'])  #checke bis Datum
      #print(booltillDate)
      if booltillDate != True:
        printLine1()
        continue
      
      boolFromTime = compare_time(currentTime,">=",i['FromTime']) #checke von Zeit
      #print(boolFromTime)
      if boolFromTime != True:
        printLine1()
        continue
    
      boolTillTime = compare_time(currentTime,"<=",i['tillTime']) #checke ab Zeit
      #print(boolTillTime)
      if boolTillTime != True:
        printLine1()
        continue
      
      if i['ringNumber']==counterDoorRing:
        print("oeffnen fuer Zeitfenster:  "+i['title'])
        openForTitles.append(i['title']) 
        wasOpened=True
        duration = jsonObj['duration']
    
    if not wasOpened:
      #openForTitles.append("") 
      pRed.on()
      time.sleep(1)
      pRed.off()
  
  

def closeRelais(duration):
  pGreen.on()
  
  print("oeffnen fuer ",duration,"Sekunden")
  relaisPin.value(0)
  
  time.sleep(duration)
  relaisPin.value(1)
  
  pGreen.off()
  raise Exception("opened Door")
  
  
def printLine1():
  print("-------------------------------------------")
 
def compare_date(today,boolOp,foo):
  print("check?:  ",today,boolOp,foo)  
  today = dateStringInDateObj(today)
  foo = dateStringInDateObj(foo)
  
  if boolOp == "<=":               #checke ob heute <= foo ist
    
    if today.year > foo.year:      #checke jahre
      return False
    if today.year < foo.year:
      return True
    if today.year == foo.year:
      
      if today.month > foo.month:  #checke monate
        return False
      if today.month < foo.month:
        return True
      if today.month == foo.month:
      
        if today.day > foo.day:    #checke tage
          return False
        else:
          return True
          
  if boolOp == ">=":               #checke ob heute >= foo ist
    
    if today.year < foo.year:      #checke jahre
      return False
    if today.year > foo.year:
      return True
    if today.year == foo.year:
      
      if today.month < foo.month:  #checke monate
        return False
      if today.month > foo.month:
        return True
      if today.month == foo.month:
        
        if today.day < foo.day:    #checke tage
          return False
        else:
          return True
           
  raise Exception("something goes wrong")
  
def dateStringInDateObj(dateString):  #works only for DD.MM.YYYY
  dateStringArray=dateString.split(".")
  if len(dateStringArray)!=3:
    raise Exception("dateString syntaxfehler: only DD.MM.YYYY as dateString is accepted")
  d = Date(int(dateStringArray[0]),int(dateStringArray[1]),int(dateStringArray[2]))
  return d

def compare_time(now,boolOp,foo):
  print("check?:  ",now,boolOp,foo)
  now=convertTimeInMin(now)
  foo=convertTimeInMin(foo)
  #print("check in min?:  ",now,boolOp,foo)
  if boolOp == ">=":
    if now >= foo:
      return True
    else:
      return False
  if boolOp == "<=":
    if now <= foo:
      return True
    else:
      return False
  if boolOp != ">=" and boolOp != "<=":
    raise Exception("boolOp syntaxfehler: only '>=' or '<=' as boolOp is accepted")


def convertTimeInMin(timeString):  #works only for HH:MM
  timeStringArray=timeString.split(":")
  if len(timeStringArray)!=2:
    raise Exception("timeString syntaxfehler: only HH:MM as timeString is accepted")
  timeInMin=(int(timeStringArray[0])*60)+int(timeStringArray[1])
  return timeInMin
  

def connectWifi(ssid,passwd):
  global wlan
  wlan=network.WLAN(network.STA_IF)         #create a wlan object
  wlan.active(True)                         #Activate the network interface
  wlan.disconnect()                         #Disconnect the last connected WiFi
  wlan.connect(ssid,passwd)                 #connect wifi (try 3 seconds)
  timeout= time.time()+3
  while(wlan.ifconfig()[0]=='0.0.0.0'):
    time.sleep(1)
    if time.time() > timeout:
      print("wlan connection failed")
      break


def connectMqtt():
  #global c
  server=SERVER
  c = MQTTClient(CLIENT_ID, server,0,username,password)     #create a mqtt client
  #c.set_last_will(TOPIC2,"0",retain=True,qos=0)            #feedback for App, that esp is off
  c.set_callback(sub_cb)                                    #set callback
  c.connect()                                               #connect mqtt
  #c.publish(TOPIC2,"1")                                    #feedback for App, that esp is currently off
  c.subscribe(TOPIC_SETTINGS)                               #client subscribes to a topic
  print("Connected to %s, subscribed to %s topic" % (server, TOPIC_SETTINGS))
  c.subscribe(TOPIC_OPEN_NOW) 
  print("Connected to %s, subscribed to %s topic" % (server, TOPIC_OPEN_NOW))
  return c
  

def pubActivity():
  print("\npublish Activity to MQTTServer:")
  adc = ADC(Pin(35))
  vBat = adc.read()/4096.0 * 7.445
  
  activity = Activity(currentTime,currentDate,counterDoorRing,wasOpened,vBat,receivedSettings,openForTitles)
  
  print(str(activity),"\n")
  c.publish(TOPIC_ACTIVITY,ujson.dumps(activity.__dict__).encode('ascii'))
  print("test")


def countDoorRing():
  global counterDoorRing
  timeout= time.time()+4
  print("zaehle Klingeln!")
  while True:
    if wakeUpPin.value():
      pBlue.on()
      counterDoorRing=counterDoorRing+1
      print("geklingelt:",counterDoorRing)
      time.sleep(0.5)
      pBlue.off()
    
    if time.time() > timeout:
      print("zaehle Klingeln beendet\n")
      break

  
def initDeepSleep(): 
  esp32.wake_on_ext0(wakeUpPin, level = esp32.WAKEUP_ANY_HIGH)
  if machine.reset_cause() == machine.DEEPSLEEP_RESET:
    print('woke from a deep sleep\n')
  else:
    print('power on or hard reset\n')
    #print("into deepsleep")
    #machine.deepsleep()

def getCurrentTime():
  print("get NTP Time")
  import ntptime
  ntptime.settime() #later finally blovk if not connection avaiable
  import utime
  now = utime.localtime()
  #adaption timezone (+2)
  t =utime.mktime(now)
  t += 2*3600
  now = utime.localtime(t)
  
  #print(now)
  
  global currentTime
  currentTime =str(now[3])+":"+str(now[4])
  print(currentTime,"Uhr")
  
  global currentDate
  currentDate = str(now[2])+"."+str(now[1])+"."+str(now[0])
  print(currentDate+"\n")
 
  
  
  
try:
  initDeepSleep()
  countDoorRing()
  connectWifi(SSID,PASSWORD)
  
  getCurrentTime()
  
  c = connectMqtt()
  timeout= time.time()+untilTime
  print("waiting for mqtt messages....\n")
  
  while True:
    
    c.check_msg()
    
    if duration>0:


      print("枚ffne")
      closeRelais(duration)
      
    if time.time() > timeout:
        print("timeout erreicht")
        break

finally:
  if(c is not None):
    pubActivity()
    time.sleep(1)
    c.disconnect()
  else:
    print("mqtt connection failed\n")
  wlan.disconnect()
  wlan.active(False)
  print("into deepsleep")
  machine.deepsleep()








































