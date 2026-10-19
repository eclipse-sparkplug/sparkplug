// SPDX-License-Identifier: EPL-2.0

package org.eclipse.sparkplug.tck.test.edge;

import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.publish.RetainedMessageStore;
import com.hivemq.extension.sdk.api.services.publish.RetainedPublish;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.PropertyValue;
import org.mockito.MockedStatic;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class QualityValidationTest {
    private static final String TOPIC = "spBv1.0/group/DBIRTH/edge/device";

    @DataProvider(name = "qualityCodes")
    public Object[][] qualityCodes() {
        return new Object[][] {{0, true}, {192, true}, {500, true}, {1, false}, {-1, false}};
    }

    @Test(dataProvider = "qualityCodes")
    public void validatesQualityCodes(int code, boolean valid) {
        SendComplexDataTest validator = newValidator();
        check(validator, PropertyValue.newBuilder().setType(3).setIntValue(code).build());
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE, valid);
    }

    @Test
    public void acceptsInt32QualityDatatype() {
        SendComplexDataTest validator = newValidator();
        check(validator, PropertyValue.newBuilder().setType(3).setIntValue(192).build());
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE, true);
    }

    @DataProvider(name = "invalidProperties")
    public Object[][] invalidProperties() {
        return new Object[][] {
                {PropertyValue.newBuilder().setType(4).setIntValue(192).build(), false, true},
                {PropertyValue.newBuilder().setType(3).build(), true, false},
                {PropertyValue.newBuilder().setType(3).setLongValue(0).build(), true, false},
                {PropertyValue.newBuilder().setType(3).setFloatValue(0).build(), true, false}
        };
    }

    @Test(dataProvider = "invalidProperties")
    public void rejectsWrongTypeOrValueField(PropertyValue property, boolean validType, boolean validValue) {
        SendComplexDataTest validator = newValidator();
        check(validator, property);
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE, validType);
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE, validValue);
    }

    @Test
    public void permitsAbsentOptionalQuality() {
        SendComplexDataTest validator = newValidator();
        check(validator, null);
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE, true);
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE, true);
    }

    @DataProvider(name = "laterProperties")
    public Object[][] laterProperties() {
        return new Object[][] {{null}, {PropertyValue.newBuilder().setType(3).setIntValue(192).build()}};
    }

    @Test(dataProvider = "laterProperties")
    public void retainsFailuresAcrossMessages(PropertyValue laterProperty) {
        SendComplexDataTest validator = newValidator();
        check(validator, PropertyValue.newBuilder().setType(4).setIntValue(1).build());
        check(validator, laterProperty);
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE, false);
        assertVerdict(validator, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE, false);
    }

    private static SendComplexDataTest newValidator() {
        RetainedPublish state = mock(RetainedPublish.class);
        when(state.getPayload()).thenAnswer(invocation -> Optional.of(ByteBuffer.wrap(
                "{\"online\":true,\"timestamp\":1}".getBytes(StandardCharsets.UTF_8))));
        RetainedMessageStore store = mock(RetainedMessageStore.class);
        when(store.getRetainedMessage("spBv1.0/STATE/host"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(state)));
        try (MockedStatic<Services> services = mockStatic(Services.class)) {
            services.when(Services::retainedMessageStore).thenReturn(store);
            return new SendComplexDataTest(null, null, new String[] {"host", "group", "edge"}, null);
        }
    }

    private static void check(SendComplexDataTest validator, PropertyValue value) {
        Payload.PropertySet.Builder properties = Payload.PropertySet.newBuilder();
        if (value != null) {
            properties.addKeys("Quality").addValues(value);
        }
        Payload payload = Payload.newBuilder().addMetrics(Payload.Metric.newBuilder()
                .setName("temperature").setDatatype(9).setFloatValue(22).setProperties(properties)).build();
        PublishPacket packet = mock(PublishPacket.class);
        when(packet.getTopic()).thenReturn(TOPIC);
        when(packet.getPayload()).thenAnswer(invocation -> Optional.of(ByteBuffer.wrap(payload.toByteArray())));
        validator.checkPropertiesValidType(packet, TOPIC);
    }

    private static void assertVerdict(SendComplexDataTest validator, String requirement, boolean pass) {
        String verdict = validator.getResults().get(requirement);
        assertNotNull(verdict, "Missing verdict for " + requirement);
        assertEquals(verdict.split(" ", 2)[0], pass ? "PASS" : "FAIL", requirement);
    }
}
