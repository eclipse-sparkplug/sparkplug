/*******************************************************************************
 * Copyright (c) 2021, 2022 Ian Craggs
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

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/*
 * This the Sparkplug Host Application utility.
 * 
 * It mimics the behavior of a Host Application for use in Device/Edge node tests.
 * 
 */

import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.StatePayload;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.1-SNAPSHOT")
public class HostApplication {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");

	private String brokerURI = "tcp://localhost:1883";
	private String hostApplicationId = null;

	private MqttClient host = null;
	private MqttTopic stateTopic = null;
	private MessageListener listener = null;

	private byte[] birthPayload = null;
	private byte[] deathPayload = null;

	public String getName() {
		return "Sparkplug TCK Host Application utility";
	}

	public HostApplication() {
		logger.info("{} starting", getName());
	}

	public HostApplication(String brokerURI) {
		this.brokerURI = brokerURI;
	}

	/*
	 * Connect as an MQTT client but not as a Host Application
	 * Subscribe to Sparkplug messages so that they can be observed
	 */
	public void connect() throws MqttException {
		if (host != null) {
			logger.info("client application in use");
			return;
		}
		host = new MqttClient(brokerURI, "Sparkplug_TCK_Second");
		listener = new MessageListener();
		host.setCallback(listener);
		MqttConnectOptions connectOptions = new MqttConnectOptions();
		host.connect(connectOptions);
	}

	public void disconnect() throws MqttException {
		if (host == null) {
			logger.info("nothing to disconnect from");
			return;
		}
		host.disconnect(200);
		host.close();
		host = null;
	}

	/*
	 * prepare host messages but don't send
	 */
	public void hostPrepare(String host_application_id, boolean forceReset) throws MqttException {
		if (host != null) {
			logger.info("host application in use");

			if (forceReset) {
				logger.error("Forcing host reset");
				hostOffline();
			} else {
				return;
			}
		}
		logger.info("Creating new host \"" + host_application_id + "\"");
		hostApplicationId = host_application_id;
		host = new MqttClient(brokerURI, "Sparkplug_TCK_" + host_application_id);
		listener = new MessageListener();
		host.setCallback(listener);

		stateTopic = host.getTopic(Constants.TOPIC_ROOT_STATE + "/" + host_application_id);

		// Set up the BIRTH and DEATH payloads
		try {
			long now = System.currentTimeMillis();
			ObjectMapper mapper = new ObjectMapper();
			StatePayload birthStatePayload = new StatePayload(true, now);
			birthPayload = mapper.writeValueAsString(birthStatePayload).getBytes();
			StatePayload deathStatePayload = new StatePayload(false, now);
			deathPayload = mapper.writeValueAsString(deathStatePayload).getBytes();
		} catch (Exception e) {
			logger.error("Failed to construct Host ID payloads - not starting", e);
			return;
		}

		MqttConnectOptions connectOptions = new MqttConnectOptions();
		connectOptions.setWill(stateTopic, deathPayload, 1, true);
		host.connect(connectOptions);
		logger.info("Host " + host_application_id + " successfully created");
	}

	private void send(byte[] payload) throws MqttException {
		MqttMessage msg = new MqttMessage(payload);
		msg.setQos(1);
		msg.setRetained(true);
		MqttDeliveryToken token = stateTopic.publish(msg);
		token.waitForCompletion(1000L);
	}

	private byte[] getOldMessage(boolean online) {
		ObjectMapper mapper = new ObjectMapper();
		StatePayload oldonline = new StatePayload(online, 16671135L);
		byte[] payload = null;
		try {
			payload = mapper.writeValueAsString(oldonline).getBytes();
		} catch (JsonProcessingException e) {
			logger.error("Failed to construct Host ID payloads", e);
		}
		return payload;
	}

	public void hostSendOldOnline() throws MqttException {
		if (host == null) {
			logger.error("hostSendOldOnline: no host application");
			return;
		}
		// send old online state message
		send(getOldMessage(true));
	}

	public void hostSendOldOffline() throws MqttException {
		if (host == null) {
			logger.error("hostSendOldOffline: no host application");
			return;
		}
		// send old offline state message
		send(getOldMessage(false));
	}

	public void hostSendOnline() throws MqttException {
		if (host == null) {
			logger.error("hostOnlineSend: no host application");
			return;
		}
		// send online state message
		send(birthPayload);
	}

	/* send offline message, but don't disconnect so we 
	 * can send the online message if we want
	 */
	public void hostSendOffline() throws MqttException {
		if (host == null) {
			logger.error("hostOfflineSend: no host application");
			return;
		}
		send(deathPayload);
	}

	public void hostOnline(String host_application_id, boolean forceReset) throws MqttException {
		if (host != null) {
			logger.info("host application in use");
			return;
		}
		hostPrepare(host_application_id, forceReset);
		hostSendOnline();
		logger.info("Host " + host_application_id + " successfully online");
	}

	public void hostOffline() throws MqttException {
		if (host == null) {
			logger.info("no host application");
			return;
		}
		// send OFFLINE state message
		send(deathPayload);
		logger.info("Host " + hostApplicationId + " successfully stopped");
		host.disconnect();
		host.close();
		host = null;
	}

	public class Message {
		private String topic;
		private MqttMessage message;

		public Message(String topic, MqttMessage message) {
			this.topic = topic;
			this.message = message;
		}

		public String getTopic() {
			return topic;
		}

		public MqttMessage getMqttMessage() {
			return message;
		}

	}

	public Message getNextMessage() {
		return listener.getNextMessage();
	}

	class MessageListener implements MqttCallbackExtended {

		ArrayList<Message> messages;

		public MessageListener() {
			messages = new ArrayList<Message>();
		}

		public Message getNextMessage() {
			synchronized (messages) {
				if (messages.size() == 0) {
					return null;
				}
				return messages.remove(0);
			}
		}

		@Override
		public void connectComplete(boolean reconnect, String serverURI) {
			logger.info("{} connected to {}", getName(), brokerURI);

			try {
				// subscribe to Sparkplug namespace
				host.subscribe(Constants.TOPIC_ROOT_SP_BV_1_0 + "/#");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void connectionLost(Throwable cause) {
			logger.info("connection lost: " + cause.getMessage());
		}

		public void deliveryComplete(IMqttDeliveryToken token) {

		}

		public void messageArrived(String topic, MqttMessage message) throws Exception {
			// log("message arrived: " + new String(message.getPayload()));

			synchronized (messages) {
				messages.add(new Message(topic, message));
				messages.notifyAll();
			}
		}
	}

}
