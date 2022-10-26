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

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatePayload {

	@JsonProperty("online")
	private Boolean online;

	@JsonProperty("bdSeq")
	private Integer bdSeq;

	@JsonProperty("timestamp")
	private Long timestamp;

	public StatePayload() {
		this.online = null;
		this.bdSeq = null;
		this.timestamp = null;
	}

	public StatePayload(Boolean online, Integer bdSeq, Long timestamp) {
		super();
		this.online = online;
		this.bdSeq = bdSeq;
		this.timestamp = timestamp;
	}

	public Boolean isOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public Integer getBdSeq() {
		return bdSeq;
	}

	public void setBdSeq(Integer bdSeq) {
		this.bdSeq = bdSeq;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StatePayload [online=");
		builder.append(online);
		builder.append(", bdSeq=");
		builder.append(bdSeq);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}
}
