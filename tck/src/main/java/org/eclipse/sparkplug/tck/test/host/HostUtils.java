/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2022 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package org.eclipse.sparkplug.tck.test.host;

import java.util.Optional;

import org.eclipse.sparkplug.tck.test.common.Constants;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;

public class HostUtils {

	public HostUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean isHostApplication(final @NotNull String expectedHostAppId, final @NotNull ConnectPacket packet) {
		final Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			final WillPublishPacket willPublishPacket = willPublishPacketOptional.get();

			// Topic is spBv1.0/STATE/{host_application_id}
			if (willPublishPacket.getTopic().equals(
					Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + Constants.TOPIC_PATH_STATE + "/" + expectedHostAppId)) {
				return true;
			}
		}
		return false;
	}

}
