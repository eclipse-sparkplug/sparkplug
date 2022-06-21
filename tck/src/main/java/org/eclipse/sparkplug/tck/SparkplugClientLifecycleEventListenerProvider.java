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

import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListenerProvider;
import com.hivemq.extension.sdk.api.events.client.parameters.ClientLifecycleEventListenerProviderInput;

import org.eclipse.sparkplug.tck.test.TCK;

public class SparkplugClientLifecycleEventListenerProvider implements ClientLifecycleEventListenerProvider {

	TCK theTCK;
	
	public SparkplugClientLifecycleEventListenerProvider(TCK aTCK) {
		theTCK = aTCK;
	}
	
    @Override
    public SparkplugClientLifecycleEventListener getClientLifecycleEventListener(ClientLifecycleEventListenerProviderInput input) {
        return new SparkplugClientLifecycleEventListener(theTCK);
    }
}