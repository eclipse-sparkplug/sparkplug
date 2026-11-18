/**
 * Copyright (c) 2026 Cirrus Link Solutions and others
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 * Cirrus Link Solutions - initial implementation
 */

package org.eclipse.sparkplug.tck.test.common;

import static org.eclipse.sparkplug.tck.test.common.Constants.EMPTY;
import static org.eclipse.sparkplug.tck.test.common.Constants.FAIL;
import static org.eclipse.sparkplug.tck.test.common.Constants.MAYBE;
import static org.eclipse.sparkplug.tck.test.common.Constants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.Constants.PASS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Covers how a result is recorded for a requirement, in particular that a recorded failure is not
 * replaced by a later success.
 *
 * These helpers are scored from loops over metrics, parameters and messages, and from the
 * per-message publish() callbacks, so the same requirement is commonly written more than once in a
 * test. Both guards previously compared the stored value with the bare FAIL or MAYBE constant while
 * a recorded failure reads "FAIL <requirement> (<call site>)", so the comparison never matched and
 * every failure was overwritten. Nothing detected that, because the TCK scores assertions only at
 * runtime against a live broker.
 */
public class UtilsResultTest {

	private static final String ID = "requirement-id";
	private static final String DESC = "the requirement text";

	private final Map<String, String> results = new HashMap<>();

	@Test
	@DisplayName("a recorded failure survives a later success")
	public void failIsNotReplacedByPass() {
		Utils.setResultIfNotFail(results, false, ID, DESC);
		Utils.setResultIfNotFail(results, true, ID, DESC);

		assertTrue(results.get(ID).startsWith(FAIL), "a later pass must not erase a failure");
	}

	@Test
	@DisplayName("a failure is recorded over an earlier success")
	public void passIsReplacedByFail() {
		Utils.setResultIfNotFail(results, true, ID, DESC);
		Utils.setResultIfNotFail(results, false, ID, DESC);

		assertTrue(results.get(ID).startsWith(FAIL));
	}

	@Test
	@DisplayName("a failure survives the middle of a loop")
	public void failSurvivesSurroundingPasses() {
		Utils.setResultIfNotFail(results, true, ID, DESC);
		Utils.setResultIfNotFail(results, false, ID, DESC);
		Utils.setResultIfNotFail(results, true, ID, DESC);

		assertTrue(results.get(ID).startsWith(FAIL),
				"one bad metric in a loop must decide the requirement");
	}

	@Test
	@DisplayName("repeated successes stay a success")
	public void passStaysPass() {
		Utils.setResultIfNotFail(results, true, ID, DESC);
		Utils.setResultIfNotFail(results, true, ID, DESC);

		assertEquals(PASS, results.get(ID));
	}

	@Test
	@DisplayName("a requirement not yet judged is still replaceable")
	public void unjudgedStatesAreReplaceable() {
		for (final String unjudged : List.of(NOT_EXECUTED, EMPTY)) {
			results.put(ID, unjudged);
			Utils.setResultIfNotFail(results, true, ID, DESC);

			assertEquals(PASS, results.get(ID), unjudged + " must not be mistaken for a failure");
		}
	}

	@Test
	@DisplayName("a bare FAIL preset is also retained")
	public void bareFailConstantIsRetained() {
		// SessionTerminationTest.clearResults seeds ids with the undecorated constant
		results.put(ID, FAIL);
		Utils.setResultIfNotFail(results, true, ID, DESC);

		assertEquals(FAIL, results.get(ID));
	}

	@Test
	@DisplayName("a deliberate pending failure is cleared by an unconditional write")
	public void seededFailIsClearedByPlainSetResult() {
		// PrimaryHostTest seeds a pending failure before the births are expected and clears it
		// on success, which only works through the unconditional helper
		Utils.setResult(results, false, ID, DESC);
		Utils.setResult(results, true, ID, DESC);

		assertEquals(PASS, results.get(ID));
	}

	@Test
	@DisplayName("a recorded MAYBE survives a later success")
	public void maybeIsNotReplacedByPass() {
		Utils.setShouldResultIfNotFail(results, false, ID, DESC);
		Utils.setShouldResultIfNotFail(results, true, ID, DESC);

		assertTrue(results.get(ID).startsWith(MAYBE));
	}

	@Test
	@DisplayName("a passing SHOULD does not erase a failing MUST")
	public void shouldPassDoesNotEraseAHardFailure() {
		Utils.setResultIfNotFail(results, false, ID, DESC);
		Utils.setShouldResultIfNotFail(results, true, ID, DESC);

		assertTrue(results.get(ID).startsWith(FAIL),
				"a SHOULD check must not clear a MUST failure for the same requirement");
	}

	@Test
	@DisplayName("no result state is a prefix of another")
	public void resultStatesAreDistinguishableByPrefix() {
		// Both guards test a prefix, so one state must never begin with another
		final List<String> states = List.of(PASS, FAIL, MAYBE, NOT_EXECUTED, EMPTY);
		for (final String a : states) {
			for (final String b : states) {
				if (!a.equals(b)) {
					assertFalse(a.startsWith(b), a + " starts with " + b);
				}
			}
		}
	}
}
