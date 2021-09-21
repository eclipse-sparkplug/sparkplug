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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import org.eclipse.sparkplug.tck.test.TCK;
import java.util.ArrayList;

public class PublishInterceptor implements PublishInboundInterceptor {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private TCK theTCK = null;
	
	public PublishInterceptor(TCK aTCK) {
		theTCK = aTCK;
	}
	
	private int pos = 0;
	private String getToken(String payload) {
		String rc = null;

		try {
			while (payload.charAt(pos) == ' ')
				pos++;

			int startpos = -1;
			int endpos = -1;
			if (payload.charAt(pos) == '\"') {
				startpos = ++pos;
				while (payload.charAt(pos) != '\"') {
					pos++;
				}
				endpos = pos++;
			} else {
				startpos = pos;
				while (payload.charAt(pos) != ' ') {
					pos++;
				}
				endpos = pos;
			}
			rc = payload.substring(startpos, endpos);
		} catch (Exception e) {

		}
		if (rc == null) {
			pos = 0;
		}
		return rc;
	}

	@Override
	public void onInboundPublish(@NotNull PublishInboundInput publishInboundInput,
			@NotNull PublishInboundOutput publishInboundOutput) {
		try {
			String clientId = publishInboundInput.getClientInformation().getClientId();
			logger.info("Inbound publish from '{}'", clientId);
					
			PublishPacket packet = publishInboundInput.getPublishPacket();
			
			String topic = packet.getTopic();
			logger.info("\tTopic {}", topic);
			
			String payload = null;
			ByteBuffer bpayload = packet.getPayload().orElseGet(null);
			if (bpayload != null) {
				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
			}
			logger.info("\tPayload {}", payload);
			
			if (topic.equals("SPARKPLUG_TCK/LOG")) {
				logger.info(clientId + ": " + payload);  // display log messsage
			}
			
			if (topic.equals("SPARKPLUG_TCK/TEST_CONTROL")) {
				String cmd = "NEW_TEST";
				if (payload.toUpperCase().startsWith(cmd)) {				
					ArrayList<String> parmarray = new ArrayList<String>();
					String token = getToken(payload);
					while (token != null) {
						parmarray.add(token);
						token = getToken(payload);
					}
					String[] strings = parmarray.toArray(new String[parmarray.size()]);
					
					if (strings.length < 3) {
						throw new Exception("New test syntax is: NEW_TEST profile testname <parameters>");
					}
					int no_parms = strings.length - 3;
					String[] parms = new String[no_parms];
					if (no_parms > 0) {
						System.arraycopy(strings, 3, parms, 0, no_parms);
					}
					theTCK.newTest(strings[1], strings[2], parms);
				} else {	
					cmd = "END_TEST";
					if (payload.toUpperCase().trim().equals(cmd)) {
						theTCK.endTest();
					}	
				}
			}
			else 
				theTCK.publish(clientId, packet);
			
		} catch (Exception e) {
			logger.error("Exception", e);
		} catch (Error e) {
			System.out.println("Error");
		}
	}
}