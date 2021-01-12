/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2020 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package org.eclipse.sparkplug.tck.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundOutput;

public class TestInterceptor implements ConnectInboundInterceptor {

	private static Logger logger = LoggerFactory.getLogger(TestInterceptor.class.getName());

	@Override
	public void onConnect(@NotNull ConnectInboundInput connectInboundInput,
			@NotNull ConnectInboundOutput connectInboundOutput) {
		logger.info("Inbound connect from '{}'", connectInboundInput.getClientInformation().getClientId());
		logger.info("\tInet Address {}", connectInboundInput.getConnectionInformation().getInetAddress());
		logger.info("\tMQTT Version {}", connectInboundInput.getConnectionInformation().getMqttVersion());
		logger.info("\tClean Start {}", connectInboundInput.getConnectPacket().getCleanStart());
		logger.info("\tKeep Alive {}", connectInboundInput.getConnectPacket().getKeepAlive());
	}
}