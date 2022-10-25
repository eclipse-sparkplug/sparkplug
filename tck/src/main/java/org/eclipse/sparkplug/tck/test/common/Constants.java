/**
 * Copyright (c) 2022 Anja Helmbrecht-Schaar and others
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 * Anja Helmbrecht-Schaar - initial implementation and documentation
 */
package org.eclipse.sparkplug.tck.test.common;

import com.hivemq.extension.sdk.api.annotations.NotNull;

public class Constants {

	public static final @NotNull String TOPIC_ROOT_SP_BV_1_0 = "spBv1.0";
	public static final @NotNull String TOPIC_PATH_STATE = "STATE";
	public static final @NotNull String TOPIC_ROOT_STATE = TOPIC_ROOT_SP_BV_1_0 + "/" + TOPIC_PATH_STATE;
	public static final @NotNull String TOPIC_PATH_NBIRTH = "NBIRTH";
	public static final @NotNull String TOPIC_PATH_NDEATH = "NDEATH";
	public static final @NotNull String TOPIC_PATH_NCMD = "NCMD";
	public static final @NotNull String TOPIC_PATH_DCMD = "DCMD";
	public static final @NotNull String TOPIC_PATH_DBIRTH = "DBIRTH";
	public static final @NotNull String TOPIC_PATH_DDEATH = "DDEATH";
	public static final @NotNull String TOPIC_PATH_NDATA = "NDATA";
	public static final @NotNull String TOPIC_PATH_DDATA = "DDATA";

	public static final @NotNull String PASS = "PASS";
	public static final @NotNull String FAIL = "FAIL";
	public static final @NotNull String MAYBE = "MAYBE";
	public static final @NotNull String NOT_EXECUTED = "NOT EXECUTED";
	public static final @NotNull String NOT_YET_IMPLEMENTED = "NOT YET IMPLEMENTED";
	public static final @NotNull String EMPTY = "EMPTY";

	public static final String TCK_LOG_TOPIC = "SPARKPLUG_TCK/LOG";
	public static final String TCK_DEVICE_CONTROL_TOPIC = "SPARKPLUG_TCK/DEVICE_CONTROL";
	public static final String TCK_CONSOLE_PROMPT_TOPIC = "SPARKPLUG_TCK/CONSOLE_PROMPT";
	public static final String TCK_CONSOLE_TEST_CONTROL_TOPIC = "SPARKPLUG_TCK/TEST_CONTROL";
	public static final String TCK_RESULTS_TOPIC = "SPARKPLUG_TCK/RESULT";
	public static final String TCK_RESULTS_CONFIG_TOPIC = "SPARKPLUG_TCK/RESULT_CONFIG";
	public static final String TCK_CONFIG_TOPIC = "SPARKPLUG_TCK/CONFIG";
	public static final String TCK_CONSOLE_REPLY_TOPIC = "SPARKPLUG_TCK/CONSOLE_REPLY";

	public static final String TCK_HOST_CONTROL = "SPARKPLUG_TCK/HOST_CONTROL";
	public static final String SPARKPLUG_AWARE_ROOT = "$sparkplug/certificates/";

	public enum Profile {
		HOST,
		EDGE,
		BROKER
	}

	public enum TestStatus {
		NONE,
		CONSOLE_RESPONSE,
		CONNECTING_DEVICE,
		REQUESTED_NODE_DATA,
		REQUESTED_DEVICE_DATA,
		PUBLISHED_NODE_DATA,
		PUBLISHED_DEVICE_DATA,
		KILLING_DEVICE,
		EXPECT_NODE_REBIRTH,
		EXPECT_NODE_COMMAND,
		EXPECT_DEVICE_REBIRTH,
		EXPECT_DEVICE_COMMAND,
		NDEATH_MESSAGE_RECEIVED
	}
}
