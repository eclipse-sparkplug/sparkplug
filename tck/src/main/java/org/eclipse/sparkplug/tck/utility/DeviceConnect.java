/*******************************************************************************
 * Copyright (c) 2021 Ian Craggs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Ian Craggs - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.utility;

/*
 * This is a utility to connect an MQTT client to a broker.
 * 
 * There will be a prompt to the person executing the test to send a command to 
 * a device and edge node we will connect.
 * 
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.nio.ByteBuffer;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class DeviceConnect {

	private String state = null;
	
	private String brokerURI = "tcp://localhost:1883";
	private String log_topic = "SPARKPLUG_TCK/LOG";
	
	private String controlId = "Sparkplug TCK device utility"; 
	private MqttClient control = null;
	private MqttTopic control_topic = null;
	private MessageListener control_listener = null;
	
	private MqttClient edge = null;
	private MqttTopic edge_topic = null;
	private MessageListener edge_listener = null;

	public void log(String message) {
		try {
			MqttMessage mqttmessage = new MqttMessage(message.getBytes());
			control_topic.publish(mqttmessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {	
		new DeviceConnect().run(args);
	}
	
	public void run(String[] args) {
		try {
			control = new MqttClient(brokerURI, controlId);
		    control_listener = new MessageListener();
		    control.setCallback(control_listener);
			control_topic = control.getTopic(log_topic);
			control.connect();
			log("Sparkplug device utility starting");
			control.subscribe("SPARKPLUG_TCK/DEVICE_CONTROL");

			while (true) {
				MqttMessage msg = control_listener.getNextMessage();
				
				if (msg != null && msg.toString().equals("NEW DEVICE")) {
					log("NEW DEVICE");
					deviceCreate("hostid");
				}
				Thread.sleep(100);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deviceCreate(String host_application_id) {
		edge = new MqttClient(brokerURI, "Sparkplug TCK edge node 1");
	    edge_listener = new MessageListener();
	    edge.setCallback(edge_listener);
		String host_topic = edge.getTopic("STATE/#");
		
		edge.connect();
		
		edge.subscribe("STATE/"+host_application_id); /* look for status of the host application we are to use */
		
		/* wait for retained message indicating state of host application under test */
		int count = 0;
		while (true) {
			MqttMessage msg = edge_listener.getNextMessage();
			
			if (msg != null) {
				if (!msg.toString().equals("ONLINE")) {
					log("Error: host application not online");
					return;
				}
			}
			Thread.sleep(100);
			if (count >= 5) {
				log("Error: no host application state");
				return;
			}
		}
		
		// subscribe to NCMD topic
		edge.subscribe(namespace+"/"+group_id+"/NCMD/"+edge_node_id); 
		
		// issue NBIRTH for the edge node
		String payload = "";
		MqttMessage mqttmessage = new MqttMessage(payload.getBytes());
		edge_topic.publish(mqttmessage);
	}

	class MessageListener implements MqttCallback {
		ArrayList<MqttMessage> messages;

		public MessageListener() {
			messages = new ArrayList<MqttMessage>();
		}

		public MqttMessage getNextMessage() {
			synchronized (messages) {
				if (messages.size() == 0) {
					try {
						messages.wait(1000);
					}
					catch (InterruptedException e) {
						// empty
					}
				}

				if (messages.size() == 0) {
					return null;
				}
				return messages.remove(0);
			}
		}

		public void connectionLost(Throwable cause) {
			log("connection lost: " + cause.getMessage());
		}

		public void deliveryComplete(IMqttDeliveryToken token) {
			
		}

		public void messageArrived(String topic, MqttMessage message) throws Exception {
			log("message arrived: " + new String(message.getPayload()) + "'");

			synchronized (messages) {
				messages.add(message);
				messages.notifyAll();
			}
		}
	}

}
