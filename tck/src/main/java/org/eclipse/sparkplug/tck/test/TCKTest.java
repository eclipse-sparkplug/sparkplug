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

package org.eclipse.sparkplug.tck.test;

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_CONSOLE_PROMPT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_LOG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_RESULTS_TOPIC;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

/**
 * @author Ian Craggs
 * @author Lukas Brand
 * @author Anja Helmbrecht-Schaar
 */
public abstract class TCKTest {

	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");
	public static final String GROUP_SUMMARY = "Summary Test Results for ";
	protected final @NotNull Map<String, String> testResults = new TreeMap<>();
	protected String[] testIds;

	public final String[] getAllTestIds() {
		return testIds;
	}

	public void onMqttConnectionStart(ConnectionStartInput connectionStartInput) {
	}

	public void onAuthenticationSuccessful(AuthenticationSuccessfulInput authenticationSuccessfulInput) {
	}

	public void onDisconnect(DisconnectEventInput disconnectEventInput) {
	}

	public abstract void connect(String clientId, ConnectPacket packet);

	public abstract void disconnect(String clientId, DisconnectPacket packet);

	public abstract void subscribe(String clientId, SubscribePacket packet);

	public abstract void publish(String clientId, PublishPacket packet);

	public abstract String getName();

	public abstract Map<String, String> getResults();

	public abstract String[] getTestIds();

	public abstract void endTest(Map<String, String> results);

	public void log(String message) {
		logger.info("TCKTest log: " + message);
		final PublishService publishService = Services.publishService();
		final Publish payload = Builders.publish().topic(TCK_LOG_TOPIC).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(message.getBytes())).build();
		publishService.publish(payload);
	}

	public void prompt(String message) {
		final PublishService publishService = Services.publishService();
		final Publish payload = Builders.publish().topic(TCK_CONSOLE_PROMPT_TOPIC).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(message.getBytes())).build();
		publishService.publish(payload);
	}

	public void reportResults(final @NotNull Map<String, String> results) {
		logger.info(GROUP_SUMMARY + getName());

		final StringBuilder summary = Results.getSingleTestSummary(results);
		logger.info(summary.toString());
		summary.insert(0, new Timestamp(new Date().getTime()) + " Summary Test Results for " + getName()
				+ System.lineSeparator());

		final PublishService publishService = Services.publishService();
		final Publish message = Builders.publish().topic(TCK_RESULTS_TOPIC).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(summary.toString().getBytes())).build();
		publishService.publish(message);
	}
}