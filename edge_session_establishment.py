import paho.mqtt.client as mqtt
import time

"""
"""
broker = "localhost"
port = 1883
host_application_id = "testing"
group_id = "test_group"
edge_node_id = "test_edge"

def control_on_message(client, userdata, msg):
    if msg.topic == "SPARKPLUG_TCK/RESULT":
        print("*** Result ***",  msg.payload)

def control_on_connect(client, userdata, flags, rc):
    print("Control client connected with result code "+str(rc))
    client.subscribe("SPARKPLUG_TCK/#")

def control_on_subscribe(client, userdata, mid, granted_qos):
    print("Control client subscribed")
    rc = client.publish("SPARKPLUG_TCK/TEST_CONTROL", "NEW_TEST edge SessionEstablishment %s %s %s " % (host_application_id, group_id, edge_node_id), qos=1)

published = False
def control_on_publish(client, userdata, mid):
    print("Control client published")
    global published
    published = True

control_client = mqtt.Client("sparkplug_control")
control_client.on_connect = control_on_connect
control_client.on_subscribe = control_on_subscribe
control_client.on_publish = control_on_publish
control_client.on_message = control_on_message
control_client.connect(broker, port)
control_client.loop_start()
test = False

# infinite sleep
while test == False:
    time.sleep(0.1)

published = False
control_client.publish("SPARKPLUG_TCK/TEST_CONTROL", "END TEST")
while published == False:
    time.sleep(0.1)

control_client.loop_stop()
