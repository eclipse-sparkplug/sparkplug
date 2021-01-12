/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2020 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package org.eclipse.sparkplug.tck.host;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.Services;

public class SparkplugExtension implements ExtensionMain {

	private static Logger logger = LoggerFactory.getLogger(SparkplugExtension.class.getName());

	@Override
	public void extensionStart(final @NotNull ExtensionStartInput extensionStartInput,
			final @NotNull ExtensionStartOutput extensionStartOutput) {

		try {
			logger.info("Starting Extension");
			final TestInterceptor testInterceptor = new TestInterceptor();
			Services.interceptorRegistry().setConnectInboundInterceptorProvider(input -> {
				return testInterceptor;
			});

		} catch (Exception e) {
			logger.error("Exception thrown at extension start: ", e);
		}
	}

	@Override
	public void extensionStop(@com.hivemq.extension.sdk.api.annotations.NotNull ExtensionStopInput extensionStopInput,
			@com.hivemq.extension.sdk.api.annotations.NotNull ExtensionStopOutput extensionStopOutput) {
		logger.info("Stopping Extension");
	}
}
