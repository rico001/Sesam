# -*- coding:utf-8 -*-
import paho.mqtt.client as mqtt
import json as json
import time
import Queue

MQTT_SERVER = "localhost"
TOPIC_ACTIVITY = "Sesam/Esp/activity"
TOPIC_ACTIVITY_FEED = "Sesam/activityfeed"
fileHandle = None
queue = Queue.deque(maxlen=6)
activity_list = []


class ActivityWrapper:
    def __init__(self, list):
        self.activityFeedList = list

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


class Activity:
    def __init__(self, time, date, ringNumber, wasOpened, batteryStatus, openForTitles):
        self.time = time
        self.date = date
        self.ringNumber = ringNumber
        self.wasOpened = wasOpened
        self.batteryStatus = batteryStatus
        self.openForTitles = openForTitles

    def __str__(self):
        return \
            "Time: " + str(self.time) + "\n" \
            + "Date: " + str(self.date) + "\n" \
            + "ringNumber: " + str(self.ringNumber) + "\n" \
            + "wasOpened: " + str(self.wasOpened) + "\n" \
            + "Battery status: " + str(self.batteryStatus) + "\n" \
            + "openForTitles: " + str(self.openForTitles) + "\n"

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    client.subscribe(TOPIC_ACTIVITY)


def updateActivityFeed(activity):
    global activity_list

    queue.appendleft(activity)
    for x in range(len(queue)):
        print(queue[x])
        activity_list.append(queue[x])

    activitie_wrapper = ActivityWrapper(activity_list)
    activitie_wrapper_json = json.dumps(activitie_wrapper, default=lambda o: o.__dict__,sort_keys=True, indent=4)
    print(activitie_wrapper_json)

    activity_list = []
    client.publish(TOPIC_ACTIVITY_FEED, activitie_wrapper_json, 2, True)


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    global fileHandle

    if msg.topic == TOPIC_ACTIVITY:
        print("raw:         \n"+msg.payload+"\n")

        obj = json.loads(msg.payload)
        activity = Activity(obj['time'], obj['date'], obj['ringNumber'], obj['wasOpened'], obj['batteryStatus'], obj['openForTitles'])
        updateActivityFeed(activity)
        #print(activity)
        #jsonstrig=activity.to_json()
        #print(jsonstrig)

        fileHandle = open('activity_logger.txt', 'a')
        fileHandle.write("\n==================================================================\n")
        fileHandle.write("Time: %s \n" % activity.time)
        fileHandle.write("Date: %s \n" % activity.date)
        fileHandle.write("ringNumber: %s \n" % activity.ringNumber)
        fileHandle.write("was opened: %s \n" % activity.wasOpened)
        fileHandle.write("Battery: %s \n" % activity.batteryStatus)
        fileHandle.write("open for : ")
        for x in activity.openForTitles:
            fileHandle.write(x.encode('utf8')+", ")
        fileHandle.close()


print("Programm startet")
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(MQTT_SERVER, 1883, 60)
while 1:
    client.loop()
    #print("Waiting For Messages..")
    time.sleep(1)





