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

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.Services;

public class LifecycleExtension implements ExtensionMain {

	private static Logger logger = LoggerFactory.getLogger(LifecycleExtension.class.getName());

	@Override
	public void extensionStart(final @NotNull ExtensionStartInput extensionStartInput,
			final @NotNull ExtensionStartOutput extensionStartOutput) {

		try {
			logger.info("Starting Extension");
			final LifecycleInterceptor lifecycleInterceptor = new LifecycleInterceptor();
			Services.interceptorRegistry().setConnectInboundInterceptorProvider(input -> {
				return lifecycleInterceptor;
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