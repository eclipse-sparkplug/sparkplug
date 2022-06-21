/*
 * Copyright © 2021 The Eclipse Foundation, Cirrus Link Solutions, and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sparkplug.tck;

import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.*;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.sparkplug.tck.test.TCK;

public class SparkplugClientLifecycleEventListener implements ClientLifecycleEventListener {

    private final static @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final TCK theTCK;
	
	public SparkplugClientLifecycleEventListener(TCK aTCK) {
		theTCK = aTCK;
	}

    @Override
    public void onMqttConnectionStart(ConnectionStartInput connectionStartInput) {
        //logger.info("Client {} connects.", connectionStartInput.getConnectPacket().getClientId());
        theTCK.onMqttConnectionStart(connectionStartInput);
    }

    @Override
    public void onAuthenticationSuccessful(AuthenticationSuccessfulInput authenticationSuccessfulInput) {
        //logger.info("Client {} authenticated successfully.", authenticationSuccessfulInput.getClientInformation().getClientId());
        theTCK.onAuthenticationSuccessful(authenticationSuccessfulInput); 
    }

    @Override
    public void onDisconnect(DisconnectEventInput disconnectEventInput) {
        //logger.info("Client {} disconnected.", disconnectEventInput.getClientInformation().getClientId());
        theTCK.onDisconnect(disconnectEventInput);
    }
}