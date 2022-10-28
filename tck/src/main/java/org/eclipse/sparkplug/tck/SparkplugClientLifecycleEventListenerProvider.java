/********************************************************************************
 * Copyright (c) 2021-2022 Cirrus Link Solutions and others
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

package org.eclipse.sparkplug.tck;

import org.eclipse.sparkplug.tck.test.TCK;

import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListenerProvider;
import com.hivemq.extension.sdk.api.events.client.parameters.ClientLifecycleEventListenerProviderInput;

public class SparkplugClientLifecycleEventListenerProvider implements ClientLifecycleEventListenerProvider {

	TCK theTCK;

	public SparkplugClientLifecycleEventListenerProvider(TCK aTCK) {
		theTCK = aTCK;
	}

	@Override
	public SparkplugClientLifecycleEventListener getClientLifecycleEventListener(
			ClientLifecycleEventListenerProviderInput input) {
		return new SparkplugClientLifecycleEventListener(theTCK);
	}
}