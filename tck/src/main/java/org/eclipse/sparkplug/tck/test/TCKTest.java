/*******************************************************************************
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
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import com.hivemq.extension.sdk.api.packets.general.Qos;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.lang.System;

public abstract class TCKTest {
	
	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	
	public abstract void connect(String clientId, ConnectPacket packet);
	public abstract void subscribe(String clientId, SubscribePacket packet);
	public abstract void publish(String clientId, PublishPacket packet);
	
	public abstract String getName();
	public abstract String[] getTestIds();
	public abstract void endTest();
	
	public void reportResults(HashMap<String, String> results) {
		
		StringBuilder payload = new StringBuilder();
		String overall = "PASS";
		
		for (String key : results.keySet()) {
			String result = results.get(key);
			
		    payload.append(key);
		    payload.append(": ");
		    payload.append(result);
		    payload.append(";"+System.lineSeparator());
		    
		    if (!result.equals("PASS")) {
		    	overall = "FAIL";
		    }
		}
		
	    payload.append("OVERALL: ");
	    payload.append(overall);
	    payload.append(";"+System.lineSeparator());
	    
	    logger.info("Test results "+payload.toString());
		
		final PublishService publishService = Services.publishService();
		
		Publish message = Builders.publish().topic("SPARKPLUG_TCK/RESULT").qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload.toString().getBytes()))
				.build();
		publishService.publish(message);
	}

}