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
 * This is a utility to simulate a Sparkplug edge node and device for the Sparkplug TCK,
 * to help test a Sparkplug Host Application.
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

import org.eclipse.tahu.SparkplugException;
import org.eclipse.tahu.SparkplugInvalidTypeException;
import org.eclipse.tahu.message.SparkplugBPayloadDecoder;
import org.eclipse.tahu.message.SparkplugBPayloadEncoder;
import org.eclipse.tahu.message.model.*;
import org.eclipse.tahu.message.model.DataSet.DataSetBuilder;
import org.eclipse.tahu.message.model.Metric.MetricBuilder;
import org.eclipse.tahu.message.model.PropertySet.PropertySetBuilder;
import org.eclipse.tahu.message.model.Row.RowBuilder;
import org.eclipse.tahu.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;
import org.eclipse.tahu.message.model.Template.TemplateBuilder;
import org.eclipse.tahu.util.CompressionAlgorithm;
import org.eclipse.tahu.util.PayloadUtil;
import static org.eclipse.tahu.message.model.MetricDataType.*;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.nio.ByteBuffer;
import java.util.Date;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class Device {
	
	private static final boolean USING_COMPRESSION = false;
	private static final CompressionAlgorithm compressionAlgorithm = CompressionAlgorithm.GZIP;

	private String state = null;
	
	private String namespace = "spBv1.0";
	private String group_id = "SparkplugTCK";
	private String brokerURI = "tcp://localhost:1883";
	private String log_topic_name = "SPARKPLUG_TCK/LOG";
	
	private String controlId = "Sparkplug TCK device utility"; 
	private MqttClient control = null;
	private MqttTopic log_topic = null;
	private MessageListener control_listener = null;
	
	private MqttClient edge = null;
	private MqttTopic edge_topic = null;
	private MessageListener edge_listener = null;
	
	private int bdSeq = 0;
	private int seq = 0;

	public void log(String message) {
		try {
			MqttMessage mqttmessage = new MqttMessage(message.getBytes());
			log_topic.publish(mqttmessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {	
		new Device().run(args);
	}
	
	public void run(String[] args) {
		try {
			control = new MqttClient(brokerURI, controlId);
		    control_listener = new MessageListener();
		    control.setCallback(control_listener);
			log_topic = control.getTopic(log_topic_name);
			control.connect();
			log("starting");
			control.subscribe("SPARKPLUG_TCK/DEVICE_CONTROL");
			while (true) {
				MqttMessage msg = control_listener.getNextMessage();			
				if (msg != null) {
					System.out.println("got message "+msg.toString());
					String[] words = msg.toString().split(" ");
					if (words.length == 4 && words[0].equals("NEW") && words[1].equals("EDGE")) {
						log(msg.toString());
						edgeCreate(words[2], words[3]);
					}
					else {
						log("Command not understood: "+msg);
					}
				}
				Thread.sleep(100);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void edgeCreate(String host_application_id, String edge_node_id) throws Exception {
		if (edge != null) {
			log("edge node in use");
			return;
		}
		edge = new MqttClient(brokerURI, "Sparkplug TCK edge "+edge_node_id);
	    edge_listener = new MessageListener();
	    edge.setCallback(edge_listener);
		
		edge.connect();
		
		edge.subscribe("STATE/"+host_application_id); /* look for status of the host application we are to use */
		
		/* wait for retained message indicating state of host application under test */
		int count = 0;
		while (true) {
			MqttMessage msg = edge_listener.getNextMessage();
			
			if (msg != null) {
				if (msg.toString().equals("ONLINE")) {
					break;
				} else {
					log("Error: host application not online");
					return;
				}
			}
			Thread.sleep(100);
			if (++count >= 5) {
				log("Error: no host application state");
				return;
			}
		}
		
		// subscribe to NCMD topic
		edge.subscribe(namespace+"/"+group_id+"/NCMD/"+edge_node_id); 
		
		// issue NBIRTH for the edge node
		byte[] payload = createNodeBirthPayload();
		MqttMessage mqttmessage = new MqttMessage(payload);
		edge_topic = edge.getTopic(namespace+"/"+group_id+"/NBIRTH/"+edge_node_id);
		edge_topic.publish(mqttmessage);
	}
	
	private String newUUID() {
		return java.util.UUID.randomUUID().toString();
	}
	
	// Used to add the sequence number
	private long getSeqNum() throws Exception {
		System.out.println("seq: " + seq);
		if (seq == 256) {
			seq = 0;
		}
		return seq++;
	}

	public byte[] createNodeBirthPayload() throws Exception {
		// Reset the sequence number
		seq = 0;

		// Create the BIRTH payload and set the position and other metrics
		SparkplugBPayload payload =
				new SparkplugBPayload(new Date(), new ArrayList<Metric>(), getSeqNum(), newUUID(), null);

		payload.addMetric(new MetricBuilder("bdSeq", Int64, (long) bdSeq).createMetric());
		payload.addMetric(new MetricBuilder("Node Control/Rebirth", Boolean, false).createMetric());

		PropertySet propertySet = new PropertySetBuilder()
				.addProperty("engUnit", new PropertyValue(PropertyDataType.String, "My Units"))
				.addProperty("engLow", new PropertyValue(PropertyDataType.Double, 1.0))
				.addProperty("engHigh", new PropertyValue(PropertyDataType.Double, 10.0))
				/*
				 * .addProperty("CustA", new PropertyValue(PropertyDataType.String, "Custom A"))
				 * .addProperty("CustB", new PropertyValue(PropertyDataType.Double, 10.0)) .addProperty("CustC",
				 * new PropertyValue(PropertyDataType.Int32, 100))
				 */
				.createPropertySet();
		payload.addMetric(
				new MetricBuilder("MyMetric", String, "My Value").properties(propertySet).createMetric());
		
		payload.setTimestamp(new Date());
		SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();

		// Compress payload (optional)
		byte[] bytes = null;
		if (USING_COMPRESSION) {
			bytes = encoder.getBytes(PayloadUtil.compress(payload, compressionAlgorithm));
		} else {
			bytes = encoder.getBytes(payload);
		}
		
		return bytes;
	}
	
	public void deviceDestroy() throws MqttException {
		edge.disconnect();
		edge.close();
		edge = null;
	}
	
	class MessageListener implements MqttCallback {
		ArrayList<MqttMessage> messages;

		public MessageListener() {
			messages = new ArrayList<MqttMessage>();
		}

		public MqttMessage getNextMessage() {
			synchronized (messages) {
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
			log("message arrived: " + new String(message.getPayload()));

			synchronized (messages) {
				messages.add(message);
				messages.notifyAll();
			}
		}
	}

}
