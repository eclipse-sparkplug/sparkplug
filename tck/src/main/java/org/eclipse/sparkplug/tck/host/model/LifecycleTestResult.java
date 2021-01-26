/*
 * Copyright © 2021 The Eclipse Foundation, Cirrus Link Solutions, and others
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sparkplug.tck.host.model;

import com.google.gson.annotations.SerializedName;

public class LifecycleTestResult {

	@SerializedName("host-topic-phid-death-payload")
	private String hostTopicPhidDeathPayload;

	@SerializedName("host-topic-phid-death-qos")
	private String hostTopicPhidDeathQos;

	@SerializedName("host-topic-phid-death-retain")
	private String hostTopicPhidDeathRetain;

	public LifecycleTestResult() {
	}

	public String getHostTopicPhidDeathPayload() {
		return hostTopicPhidDeathPayload;
	}

	public void setHostTopicPhidDeathPayload(String hostTopicPhidDeathPayload) {
		this.hostTopicPhidDeathPayload = hostTopicPhidDeathPayload;
	}

	public String getHostTopicPhidDeathQos() {
		return hostTopicPhidDeathQos;
	}

	public void setHostTopicPhidDeathQos(String hostTopicPhidDeathQos) {
		this.hostTopicPhidDeathQos = hostTopicPhidDeathQos;
	}

	public String getHostTopicPhidDeathRetain() {
		return hostTopicPhidDeathRetain;
	}

	public void setHostTopicPhidDeathRetain(String hostTopicPhidDeathRetain) {
		this.hostTopicPhidDeathRetain = hostTopicPhidDeathRetain;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LifecycleTestResult [hostTopicPhidDeathPayload=");
		builder.append(hostTopicPhidDeathPayload);
		builder.append(", hostTopicPhidDeathQos=");
		builder.append(hostTopicPhidDeathQos);
		builder.append(", hostTopicPhidDeathRetain=");
		builder.append(hostTopicPhidDeathRetain);
		builder.append("]");
		return builder.toString();
	}
}
