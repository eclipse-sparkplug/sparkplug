/*
 * Copyright © 2021 The Eclipse Foundation, Cirrus Link Solutions, and others
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sparkplug.tck.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundOutput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;

import org.eclipse.sparkplug.tck.host.test.TCK;

public class ConnectInterceptor implements ConnectInboundInterceptor {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private TCK theTCK = null;
	
	public ConnectInterceptor(TCK aTCK) {
		theTCK = aTCK;
	}

	@Override
	public void onConnect(@NotNull ConnectInboundInput connectInboundInput,
			@NotNull ConnectInboundOutput connectInboundOutput) {
		try {
			String clientId = connectInboundInput.getClientInformation().getClientId();
					
			logger.info("Inbound connect from '{}'", clientId);
			logger.info("\tInet Address {}", connectInboundInput.getConnectionInformation().getInetAddress());
			logger.info("\tMQTT Version {}", connectInboundInput.getConnectionInformation().getMqttVersion());
			logger.info("\tClean Start {}", connectInboundInput.getConnectPacket().getCleanStart());
			logger.info("\tKeep Alive {}", connectInboundInput.getConnectPacket().getKeepAlive());
			
			ConnectPacket packet = connectInboundInput.getConnectPacket();
			theTCK.connect(clientId, packet);

		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
}