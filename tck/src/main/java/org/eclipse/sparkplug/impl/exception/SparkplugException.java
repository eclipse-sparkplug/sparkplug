/********************************************************************************
 * Copyright (c) 2022 Cirrus Link Solutions and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Cirrus Link Solutions - initial implementation
 ********************************************************************************/

package org.eclipse.sparkplug.impl.exception;

public class SparkplugException extends Exception {

	private static final long serialVersionUID = 1L;

	private SparkplugErrorCode code;

	public SparkplugException() {
		super();
	}

	public SparkplugException(SparkplugErrorCode code) {
		super();
		this.code = code;
	}

	public SparkplugException(SparkplugErrorCode code, String message, Throwable e) {
		super("ErrorCode: " + code.toString() + " - Message: " + message, e);
		this.code = code;
	}

	public SparkplugException(SparkplugErrorCode code, Throwable e) {
		super(code.toString(), e);
		this.code = code;
	}

	public SparkplugException(SparkplugErrorCode code, String message) {
		super(message);
		this.code = code;
	}

	public String getDetails() {
		return getMessage();
	}

	public SparkplugErrorCode getSparkplugErrorCode() {
		return code;
	}
}
