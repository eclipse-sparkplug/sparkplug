/*******************************************************************************
 * Copyright (c) 2022 Cirrus Link Solutions
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Wes Johnson - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.common;

import java.util.Optional;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;

public class HostUtils {

	public HostUtils() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isHostApplication(final @NotNull String expectedHostAppId,
			final @NotNull ConnectPacket packet) {
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
