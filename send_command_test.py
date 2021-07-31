"""*******************************************************************************
 * Copyright (c) 2021 Ian Craggs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    https://www.eclipse.org/legal/epl-2.0/
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Ian Craggs - initial API and implementation and/or initial documentation
 *******************************************************************************"""


import paho.mqtt.client as mqtt
import time, sys

"""


"""
broker = "localhost"
port = 1883
host_id = "myhost"
edge_node_id = "TCK_edge"
device_id = "TCK_device"

subscribed = False
host_created = False

messages = []
def control_on_message(client, userdata, msg):
    messages.append(msg)
    global host_created
    if msg.topic == "SPARKPLUG_TCK/LOG":
        payload = msg.payload.decode("ascii")
        print("***", payload)
        if payload == "Host "+host_id+" successfully created":
            host_created = True
            print("Host created")
    elif msg.topic == "SPARKPLUG_TCK/CONSOLE_PROMPT":
        payload = msg.payload.decode("ascii")
        print("***", payload)

def control_on_connect(client, userdata, flags, rc):
    print("Control client connected with result code "+str(rc))
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    control_client.subscribe("SPARKPLUG_TCK/#")

def control_on_subscribe(client, userdata, mid, granted_qos):
    print("Control client subscribed")
    global subscribed
    subscribed = True

publish_count = 0
def control_on_publish(client, userdata, mid):
    print("Control client published")
    global publish_count
    publish_count += 1

control_client = mqtt.Client("sparkplug_control")
control_client.on_connect = control_on_connect
control_client.on_subscribe = control_on_subscribe
control_client.on_publish = control_on_publish
control_client.on_message = control_on_message
control_client.connect(broker, port)
control_client.loop_start()

def publish(topic, payload):
    global publish_count
    publish_count = 0
    print(topic, payload)
    control_client.publish(topic, payload)
    # wait for publish to complete
    while publish_count == 0:
        time.sleep(0.1)

while not subscribed:
    time.sleep(0.1)

# Ensure host application to test is running and connected.  We are using the host simulator here
subscribed = False
messages = []
control_client.subscribe("STATE/"+host_id)
while not subscribed:
    time.sleep(0.1)
count = 0
online = False
while True:
    count += 1
    if count == 10:
        break
    time.sleep(0.1)
    if len(messages) > 0:
        msg = messages[-1]
        payload = msg.payload.decode("ascii")
        print("message", msg.topic, payload)
        if msg.topic == "STATE/"+host_id and payload == "ONLINE":
            online = True
            break

if not online:
    publish("SPARKPLUG_TCK/HOST_CONTROL", "New host "+host_id)
    while not host_created:
        time.sleep(0.1)

publish("SPARKPLUG_TCK/TEST_CONTROL", "New host SendCommandTest "+host_id+" "+edge_node_id+" "+device_id)

print("Hit enter to continue")
cmdline = input()

print("Stopping MQTT client")
control_client.loop_stop()




