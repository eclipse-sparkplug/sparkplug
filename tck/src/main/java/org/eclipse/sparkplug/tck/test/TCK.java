/* ******************************************************************************
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
 ****************************************************************************** */

package org.eclipse.sparkplug.tck.test;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.events.client.parameters.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

import org.eclipse.sparkplug.tck.test.Monitor;

/**
 * @author Ian Craggs
 */
public class TCK {

	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

	private @Nullable TCKTest current = null;
	private final Monitor monitor = new Monitor();

	public void newTest(final @NotNull String profile, final @NotNull String test, final @NotNull String[] parms) {

		logger.info("Test requested " + profile + " " + test);

		try {
			final Class testClass = Class.forName("org.eclipse.sparkplug.tck.test." + profile + "." + test);
			final Class[] types = { this.getClass(), String[].class };
			final Constructor constructor = testClass.getConstructor(types);

			final Object[] parameters = { this, parms };
			current = (TCKTest) constructor.newInstance(parameters);
		} catch (final Exception e) {
			logger.error("Could not find or set test class " + profile + "." + test, e);
		}
	}

	public void endTest() {
		if (current != null) {
			logger.info("Test end requested for " + current.getName());
			current.endTest();
			current = null;
		} else {
			logger.info("Test end requested but no test active");
		}
	}

	public void onMqttConnectionStart(ConnectionStartInput connectionStartInput) {
		logger.info("Client {} connects.", connectionStartInput.getConnectPacket().getClientId());
		monitor.onMqttConnectionStart(connectionStartInput);
	}

	public void onAuthenticationSuccessful(AuthenticationSuccessfulInput authenticationSuccessfulInput) {
		logger.info("Client {} authenticated successfully.",
				authenticationSuccessfulInput.getClientInformation().getClientId());
		monitor.onAuthenticationSuccessful(authenticationSuccessfulInput);
	}

	public void onDisconnect(DisconnectEventInput disconnectEventInput) {
		logger.info("Client {} disconnected.", disconnectEventInput.getClientInformation().getClientId());
		monitor.onDisconnect(disconnectEventInput);
	}

	public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
		monitor.connect(clientId, packet);
		if (current != null) {
			current.connect(clientId, packet);
		}
		monitor.connect(clientId, packet);
	}

	public void disconnect(final @NotNull String clientId, final @NotNull DisconnectPacket packet) {
		if (current != null) {
			current.disconnect(clientId, packet);
		}
		monitor.disconnect(clientId, packet);
	}

	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
		if (current != null) {
			current.subscribe(clientId, packet);
		}
		monitor.subscribe(clientId, packet);
	}

	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		if (current != null) {
			current.publish(clientId, packet);
		}
		monitor.publish(clientId, packet);
	}
}
