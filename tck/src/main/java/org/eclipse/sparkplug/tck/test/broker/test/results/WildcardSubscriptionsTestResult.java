/*
 * Copyright 2019-present HiveMQ and the HiveMQ Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.sparkplug.tck.test.broker.test.results;

import org.jetbrains.annotations.NotNull;

public class WildcardSubscriptionsTestResult {
    private final boolean success;
    private final ComplianceTestResult plusWildcardTest;
    private final ComplianceTestResult hashWildcardTest;

    public @NotNull WildcardSubscriptionsTestResult(final @NotNull ComplianceTestResult plusWildcardTest,
                                                    final @NotNull ComplianceTestResult hashWildcardTest) {
        this.plusWildcardTest = plusWildcardTest;
        this.hashWildcardTest = hashWildcardTest;

        success = (plusWildcardTest == ComplianceTestResult.OK) && (hashWildcardTest == ComplianceTestResult.OK);
    }

    public boolean isSuccess() {
        return success;
    }

    public @NotNull ComplianceTestResult getPlusWildcardTest() {
        return plusWildcardTest;
    }

    public @NotNull ComplianceTestResult getHashWildcardTest() {
        return hashWildcardTest;
    }
}
