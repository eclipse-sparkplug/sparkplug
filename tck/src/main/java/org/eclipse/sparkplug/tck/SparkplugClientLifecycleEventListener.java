/********************************************************************************
 * Copyright (c) 2021-2022 Cirrus Link Solutions and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Cirrus Link Solutions - initial implementation
 ********************************************************************************/

package org.eclipse.sparkplug.tck;

import org.eclipse.sparkplug.tck.test.TCK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;

public class SparkplugClientLifecycleEventListener implements ClientLifecycleEventListener {

	private final static @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final TCK theTCK;

	public SparkplugClientLifecycleEventListener(TCK aTCK) {
		theTCK = aTCK;
	}

	@Override
	public void onMqttConnectionStart(ConnectionStartInput connectionStartInput) {
		// logger.info("Client {} connects.", connectionStartInput.getConnectPacket().getClientId());
		theTCK.onMqttConnectionStart(connectionStartInput);
	}

	@Override
	public void onAuthenticationSuccessful(AuthenticationSuccessfulInput authenticationSuccessfulInput) {
		// logger.info("Client {} authenticated successfully.",
		// authenticationSuccessfulInput.getClientInformation().getClientId());
		theTCK.onAuthenticationSuccessful(authenticationSuccessfulInput);
	}

	@Override
	public void onDisconnect(DisconnectEventInput disconnectEventInput) {
		// logger.info("Client {} disconnected.", disconnectEventInput.getClientInformation().getClientId());
		theTCK.onDisconnect(disconnectEventInput);
	}
}