// SPDX-License-Identifier: EPL-2.0

package org.eclipse.sparkplug.tck.test.common;

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

public class UtilsTest {
    @DataProvider(name = "uint32Values")
    public Object[][] uint32Values() {
        return new Object[][] {{0L}, {1L}, {2147483648L}, {4294967295L}};
    }

    @Test(dataProvider = "uint32Values")
    public void acceptsSpecifiedUInt32Encoding(long value) throws Exception {
        Metric metric = Metric.newBuilder().setDatatype(7).setIntValue((int) value).build();
        Metric decoded = Metric.parseFrom(metric.toByteArray());
        assertTrue(Utils.hasValidDatatype(decoded), "UInt32 int_value rejected: " + value);
    }

    @Test(dataProvider = "uint32Values")
    public void preservesLegacyUInt32Encoding(long value) throws Exception {
        Metric metric = Metric.newBuilder().setDatatype(7).setLongValue(value).build();
        assertTrue(Utils.hasValidDatatype(Metric.parseFrom(metric.toByteArray())),
                "Legacy UInt32 long_value rejected: " + value);
    }

    @DataProvider(name = "datatypeControls")
    public Object[][] datatypeControls() {
        return new Object[][] {
                {"null UInt32", Metric.newBuilder().setDatatype(7).setIsNull(true).build(), true},
                {"missing UInt32", Metric.newBuilder().setDatatype(7).build(), false},
                {"float in UInt32", Metric.newBuilder().setDatatype(7).setFloatValue(1).build(), false},
                {"UInt64 long_value", Metric.newBuilder().setDatatype(8).setLongValue(1).build(), true},
                {"UInt64 int_value", Metric.newBuilder().setDatatype(8).setIntValue(1).build(), false},
                {"negative Int32", Metric.newBuilder().setDatatype(3).setIntValue(-42).build(), true}
        };
    }

    @Test(dataProvider = "datatypeControls")
    public void preservesOtherDatatypeChecks(String description, Metric metric, boolean valid) throws Exception {
        assertEquals(Utils.hasValidDatatype(Metric.parseFrom(metric.toByteArray())), valid, description);
    }
}
