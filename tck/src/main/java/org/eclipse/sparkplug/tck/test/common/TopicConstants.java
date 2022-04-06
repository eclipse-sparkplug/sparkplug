/**
 * Copyright (c) 2022 Anja Helmbrecht-Schaar
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

public class TopicConstants {

    public static final @NotNull String TOPIC_ROOT_SP_BV_1_0 = "spBv1.0";
    public static final @NotNull String TOPIC_ROOT_STATE = "STATE";
    public static final @NotNull String TOPIC_PATH_NBIRTH = "NBIRTH";
    public static final @NotNull String TOPIC_PATH_NDEATH = "NDEATH";
    public static final @NotNull String TOPIC_PATH_NCMD = "NCMD";
    public static final @NotNull String TOPIC_PATH_DCMD = "DCMD";
    public static final @NotNull String TOPIC_PATH_DBIRTH = "DBIRTH";
    public static final @NotNull String TOPIC_PATH_NDATA = "NDATA";
    public static final @NotNull String TOPIC_PATH_DDATA = "DDATA";

    public static final @NotNull String PASS = "PASS";
    public static final @NotNull String FAIL = "FAIL";
    public static final @NotNull String EMPTY = "EMPTY";

    public static final String TCK_LOG_TOPIC = "SPARKPLUG_TCK/LOG";
    public static final String TCK_DEVICE_CONTROL_TOPIC = "SPARKPLUG_TCK/DEVICE_CONTROL";
    public static final String TCK_CONSOLE_PROMPT_TOPIC = "SPARKPLUG_TCK/CONSOLE_PROMPT";
    public static final String TCK_CONSOLE_TEST_CONTROL_TOPIC = "SPARKPLUG_TCK/TEST_CONTROL";
    public static final String TCK_HOST_CONTROL = "SPARKPLUG_TCK/HOST_CONTROL";
    public static final String SP_BV_1_0_SPARKPLUG_TCK_NCMD_TOPIC = "spBv1.0/SparkplugTCK/NCMD/";
    public static final String SP_BV_1_0_SPARKPLUG_TCK_DCMD_TOPIC = "spBv1.0/SparkplugTCK/DCMD/";
}
