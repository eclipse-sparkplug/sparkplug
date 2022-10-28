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
import org.eclipse.sparkplug.tck.test.common.PersistentUtils;
import org.eclipse.sparkplug.tck.test.common.StatePayload;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class HostApplication {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");

	private String state = null;

	private String group_id = "SparkplugTCK";
	private String brokerURI = "tcp://localhost:1883";
	private String hostApplicationId = null;

	private MqttTopic log_topic = null;

	private MqttClient host = null;
	private MqttTopic stateTopic = null;
	private MessageListener host_listener = null;

	private byte[] birthPayload = null;
	private byte[] deathPayload = null;
	private int bdSeq;

	public HostApplication() {
		logger.info("Sparkplug TCK Host Application utility");
	}

	/*
	 * prepare host messages but don't send
	 */
	public void hostPrepare(String host_application_id) throws MqttException {
		if (host != null) {
			logger.info("host application in use");
			return;
		}
		logger.info("Creating new host \"" + host_application_id + "\"");
		hostApplicationId = host_application_id;
		host = new MqttClient(brokerURI, "Sparkplug TCK host application " + host_application_id);
		host_listener = new MessageListener();
		// host.setCallback(host_listener);

		stateTopic = host.getTopic(Constants.TOPIC_ROOT_STATE + "/" + host_application_id);

		// Set up the BIRTH and DEATH payloads
		bdSeq = PersistentUtils.getNextHostDeathBdSeqNum();
		try {
			ObjectMapper mapper = new ObjectMapper();
			StatePayload birthStatePayload = new StatePayload(true, bdSeq, System.currentTimeMillis());
			birthPayload = mapper.writeValueAsString(birthStatePayload).getBytes();
			StatePayload deathStatePayload = new StatePayload(false, bdSeq, System.currentTimeMillis());
			deathPayload = mapper.writeValueAsString(deathStatePayload).getBytes();
		} catch (Exception e) {
			logger.error("Failed to construct Host ID payloads - not starting", e);
			return;
		} finally {
			bdSeq++;
			PersistentUtils.setNextHostDeathBdSeqNum(bdSeq);
		}

		MqttConnectOptions connectOptions = new MqttConnectOptions();
		connectOptions.setWill(stateTopic, deathPayload, 1, true);
		host.connect(connectOptions);
		logger.info("Host " + host_application_id + " successfully created");
	}

	public void hostSendOnline() throws MqttException {
		if (host == null) {
			logger.error("hostOnlineSend: no host application");
			return;
		}
		// send online state message
		MqttMessage online = new MqttMessage(birthPayload);
		online.setQos(1);
		online.setRetained(true);
		MqttDeliveryToken token = stateTopic.publish(online);
		token.waitForCompletion(1000L);
	}

	/* send offline message, but don't disconnect so we 
	 * can send the online message if we want
	 */
	public void hostSendOffline() throws MqttException {
		if (host == null) {
			logger.error("hostOfflineSend: no host application");
			return;
		}
		MqttMessage offline = new MqttMessage(deathPayload);
		offline.setQos(1);
		offline.setRetained(true);
		MqttDeliveryToken token = stateTopic.publish(offline);
		token.waitForCompletion(1000L);
	}

	public void hostOnline(String host_application_id) throws MqttException {
		if (host != null) {
			logger.info("host application in use");
			return;
		}
		hostPrepare(host_application_id);
		hostSendOnline();
		logger.info("Host " + host_application_id + " successfully online");
	}

	public void hostOffline() throws MqttException {
		// send OFFLINE state message
		MqttMessage offline = new MqttMessage(deathPayload);
		offline.setQos(1);
		offline.setRetained(true);
		MqttDeliveryToken token = stateTopic.publish(offline);
		token.waitForCompletion(1000L);
		logger.info("Host " + hostApplicationId + " successfully stopped");
		host.disconnect();
		host.close();
		host = null;
	}

	class MessageListener implements MqttCallbackExtended {
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

		@Override
		public void connectComplete(boolean reconnect, String serverURI) {
			System.out.println("Connected!");

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
				messages.add(message);
				messages.notifyAll();
			}
		}
	}

}
