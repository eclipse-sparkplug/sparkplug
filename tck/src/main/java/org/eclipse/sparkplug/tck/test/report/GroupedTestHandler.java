package org.eclipse.sparkplug.tck.test.report;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.eclipse.sparkplug.tck.test.Monitor;
import org.eclipse.sparkplug.tck.test.broker.AwareBrokerTest;
import org.eclipse.sparkplug.tck.test.broker.CompliantBrokerTest;
import org.eclipse.sparkplug.tck.test.common.Requirements;
import org.eclipse.sparkplug.tck.test.host.MessageOrderingTest;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.eclipse.sparkplug.tck.test.report.ReportSummaryWriter.LOGGER;

/**
 * Reads all IDs that are uses in broker, host edge and monitor tests and is grouping them by profile and test
 */
public class GroupedTestHandler {
    public static final String EDGE = "Edge";
    public static final String HOST = "Host";
    public static final String BROKER = "Broker";
    public static final String[] TYPE_HINTS = new String[]{"MULTIPLE", "REORDERING", "TEMPLATE", "PROPERTY", "DBIRTH", "ALIAS", "AWARE"};
    public static final String[] HOST_HINTS = new String[]{"HOST", "STATE"};
    public static final String[] EDGE_HINTS = new String[]{"EDGE", "DEVICE", "NDATA", "DDATA", "DBIRTH", "NBIRTH", "DDEATH", "NDEATH", "RBE"};
    private static GroupedTestHandler INSTANCE;
    private final @NotNull TreeMap<String, TreeSet<GroupedTest>> allGroupedTests = new TreeMap<>();
    private final @NotNull List<String> allRequirementIdsOfHostTests = new ArrayList<>();
    private final @NotNull List<String> allRequirementIdsOfEdgeTests = new ArrayList<>();
    private boolean initialized = false;
    private HashMap<String, String> groupCategoryMap = new HashMap<>();

    private GroupedTestHandler() {
        initialize();
    }

    public static GroupedTestHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GroupedTestHandler();
        }
        return INSTANCE;
    }

    public void initialize() {
        if (!initialized) {
            initIds();
            createBrokerProfileGroup();
            createHostProfileGroup();
            createEdgeProfileGroup();
            initialized = true;
        }
    }

    public TreeSet<GroupedTest> getBrokerProfileGroup() {
        return allGroupedTests.get(BROKER);
    }

    public TreeSet<GroupedTest> getHostProfileGroup() {
        return allGroupedTests.get(HOST);
    }

    public TreeSet<GroupedTest> getEdgeProfileGroup() {
        return allGroupedTests.get(EDGE);
    }


    private void createBrokerProfileGroup() {
        TreeSet<GroupedTest> brokerTests = new TreeSet<>();
        brokerTests.add(new GroupedTest(BROKER, "Aware Group", AwareBrokerTest.testIds));
        brokerTests.add(new GroupedTest(BROKER, "_Main Group", CompliantBrokerTest.testIds));
        allGroupedTests.put(BROKER, brokerTests);
    }

    private void createHostProfileGroup() {
        TreeSet<GroupedTest> hostTests = new TreeSet<>();
        hostTests.add(new GroupedTest(HOST, "Message Reordering Group", MessageOrderingTest.testIds));
        hostTests.add(new GroupedTest(HOST, "_Main Group", allRequirementIdsOfHostTests));
        allGroupedTests.put(HOST, hostTests);
    }

    private void createEdgeProfileGroup() {
        TreeSet<GroupedTest> edgeTests = new TreeSet<>();
        HashMap<String, List<String>> edgeGrouping = new HashMap<>();
        for (String testId : allRequirementIdsOfEdgeTests) {
            final String group = isGroup(testId);
            if (!edgeGrouping.containsKey(group)) {
                edgeGrouping.put(group, new ArrayList<>());
            }
            edgeGrouping.get(group).add(testId);
        }

        //add Egde node classified test to profile edge
        edgeGrouping.keySet().forEach(group ->
                edgeTests.add(new GroupedTest(EDGE, group, edgeGrouping.get(group))));

        allGroupedTests.put(EDGE, edgeTests);

    }

    private void initIds() {
        initializeHostArray();
        initializeEdgeArray();
        classifyMonitoringAssertions();
        initializeGroupCategories();
    }

    private void initializeHostArray() {
        allRequirementIdsOfHostTests.addAll(MessageOrderingTest.testIds);
        allRequirementIdsOfHostTests.addAll(org.eclipse.sparkplug.tck.test.host.SessionTerminationTest.testIds);
        allRequirementIdsOfHostTests.addAll(org.eclipse.sparkplug.tck.test.host.SessionEstablishmentTest.testIds);
        allRequirementIdsOfHostTests.addAll(org.eclipse.sparkplug.tck.test.host.EdgeSessionTerminationTest.testIds);
        allRequirementIdsOfHostTests.addAll(org.eclipse.sparkplug.tck.test.host.SendCommandTest.testIds);
        allRequirementIdsOfHostTests.addAll(org.eclipse.sparkplug.tck.test.host.MultipleBrokerTest.testIds);
    }

    private void initializeEdgeArray() {
        allRequirementIdsOfEdgeTests.addAll(org.eclipse.sparkplug.tck.test.edge.MultipleBrokerTest.testIds);
        allRequirementIdsOfEdgeTests.addAll(org.eclipse.sparkplug.tck.test.edge.PrimaryHostTest.testIds);
        allRequirementIdsOfEdgeTests.addAll(org.eclipse.sparkplug.tck.test.edge.ReceiveCommandTest.testIds);
        allRequirementIdsOfEdgeTests.addAll(org.eclipse.sparkplug.tck.test.edge.SendComplexDataTest.testIds);
        allRequirementIdsOfEdgeTests.addAll(org.eclipse.sparkplug.tck.test.edge.SendDataTest.testIds);
        allRequirementIdsOfEdgeTests.addAll(org.eclipse.sparkplug.tck.test.edge.SessionEstablishmentTest.testIds);
        allRequirementIdsOfEdgeTests.addAll(org.eclipse.sparkplug.tck.test.edge.SessionTerminationTest.testIds);
    }

    private void initializeGroupCategories() {
        groupCategoryMap = new HashMap<>();
        groupCategoryMap.put("MULTIPLE", "Multiple Brokers");
        groupCategoryMap.put("REORDERING", "Message Reordering");
        groupCategoryMap.put("TEMPLATE", "Templates");
        groupCategoryMap.put("PROPERTY", "Properties");
        groupCategoryMap.put("DATASET", "Datasets");
        groupCategoryMap.put("ALIAS", "Aliases");
        groupCategoryMap.put("AWARE", "Aware");
    }

    private void classifyMonitoringAssertions() {
        Monitor.testIds.forEach(assertionId -> {
            AtomicBoolean found = new AtomicBoolean(false);
            Arrays.stream(EDGE_HINTS).forEach(hint -> {
                if (assertionId.toUpperCase().contains(hint)) {
                    allRequirementIdsOfEdgeTests.add(assertionId);
                    found.set(true);
                }
            });
            Arrays.stream(HOST_HINTS).forEach(hint -> {
                if (assertionId.contains(hint)) {
                    allRequirementIdsOfHostTests.add(assertionId);
                    found.set(true);
                }
            });
            if (!found.get()) {
                allRequirementIdsOfEdgeTests.add(assertionId);
                allRequirementIdsOfHostTests.add(assertionId);
            }
        });
    }

    private String getAssertionTypeFromID(String assertionId) {
        //Most are MANDATORY
        AtomicBoolean optional = new AtomicBoolean(false);
        optional.set(Arrays.stream(TYPE_HINTS).anyMatch(assertionId::contains));
        return getAssertionTypeFromDescription(assertionId, optional.get());
    }

    private String getAssertionTypeFromDescription(String assertionId, boolean isOptional) {
        final String declaredFieldName = assertionId.toUpperCase().replace("-", "_");
        String assertionType = "MUST";
        String description = "";
        try {
            Field field = Requirements.class.getDeclaredField(declaredFieldName);
            if (Objects.equals(field.getType(), String.class)) {
                description = (String) field.get(this);
            }
            //LOGGER.info(" Requirements - Field: " + field.getName() + " Description: " + description);
            if (description.contains("SHOULD")) {
                assertionType = isOptional ? "SHOULD" : "SHOULD optional ";
            } else if (description.contains("MAY")) {
                assertionType = isOptional ? "MAY" : "MAY optional ";
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Reporting: Requirements - Field: Cant read description: {} error:", declaredFieldName, e);
        }

        return assertionType;
    }

    private @NotNull String isGroup(String assertionId) {
        String upperAssertionId = assertionId.toUpperCase();
        AtomicReference<String> group = new AtomicReference<>("_Main");
        groupCategoryMap.forEach((key, val) -> {
            if (upperAssertionId.contains(key)) {
                group.set(val);
            }
        });
        return group.get() + " Group";
    }

    protected static class TestStat {

        private final int percent;
        private final int optionalPercent;
        private final int count;
        private int passed = 0;
        private int failed = 0;
        private int optionalCount = 0;
        private int optionalPassed = 0;

        public TestStat(GroupedTest item) {
            count = item.assertions.size();
            for (GroupedTest.Assertion ids : item.assertions) {
                if (ids.assertionType.contains("optional")) {
                    optionalCount++;
                }
                if (ids.result.contains("PASS")) {
                    if (ids.assertionType.contains("optional")) {
                        optionalPassed++;
                    } else {
                        passed++;
                    }
                } else if (ids.result.contains("FAIL")) {
                    failed++;
                }

            }
            percent = count > 0 ? ((100 * (passed + optionalPassed)) / count) : 0;
            optionalPercent = count - optionalCount > 0 ? ((100 * passed) / (count - optionalCount)) : 0;
        }

        @Override
        public String toString() {
            return " Assertion count: " + count +
                    ", Number passed:" + passed +
                    ", Number failed:" + failed +
                    ", Percent passed:" + percent + "%";
        }

        public String optionalToString() {
            return " Optional assertion count: " + optionalCount +
                    ", Optional number passed:" + optionalPassed +
                    ", Percent passed without optional:" + optionalPercent + "%";
        }

        public boolean hasOptionalTests() {
            return optionalCount > 0;
        }

        public String optionalRow() {
            return HtmlConstants.ROW + optionalCount + HtmlConstants.COLUMN + optionalPassed +
                    HtmlConstants.COLUMN + "-" +
                    HtmlConstants.COLUMN + optionalPercent + HtmlConstants.ROW_END;
        }

        public String asRow() {
            return HtmlConstants.ROW + count + HtmlConstants.COLUMN + passed +
                    HtmlConstants.COLUMN + failed + HtmlConstants.COLUMN + percent + HtmlConstants.ROW_END;
        }
    }

    public class GroupedTest implements Comparable<Object> {
        private final TreeSet<Assertion> assertions;
        private final String profile;
        private final String group;

        public GroupedTest(String profile, String group, List<String> testIds) {
            this.profile = profile;
            this.group = group;
            this.assertions = new TreeSet<>();
            testIds.forEach(id -> {
                final String assertionType = getAssertionTypeFromID(id.toUpperCase());
                assertions.add(new Assertion(id, assertionType, "", "", ""));
            });
        }

        public String getProfile() {
            return profile;
        }

        public String getGroup() {
            return group;
        }

        public TreeSet<Assertion> getAssertions() {
            return assertions;
        }

        public boolean hasAssertion(String testId) {
            for (Assertion assertion : assertions) {
                if (assertion.getId().equals(testId)) {
                    return true;
                }
            }
            return false;
        }

        public void setAssertion(String testId, String test, String dateTime, String result) {
            for (Assertion assertion : assertions) {
                if (assertion.getId().equals(testId)) {
                    assertion.setTestName(test);
                    assertion.setTimestamp(dateTime);
                    assertion.setResult(result);
                }
            }
        }

        @Override
        public int compareTo(@org.jetbrains.annotations.NotNull Object o) {
            return (this.group.compareTo(((GroupedTest) o).group));
        }

        @Override
        public String toString() {
            return "GroupedTest {" +
                    "profile='" + profile + '\'' +
                    ", group='" + group + '\'' +
                    ", assertions=" + assertions +
                    '}';
        }

        public class Assertion implements Comparable<Object> {
            private final String id;
            private final String assertionType;
            private String testName;
            private String timestamp;
            private String result;

            public Assertion(String id, String assertionType, String testName, String timestamp, String result) {
                this.id = id;
                this.assertionType = assertionType;
                this.testName = testName;
                this.timestamp = timestamp;
                this.result = result;
            }

            public String getId() {
                return id;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            public String getResult() {
                return result;
            }

            public void setResult(String result) {
                this.result = result;
            }

            public String getTestName() {
                return testName;
            }

            public void setTestName(String testName) {
                this.testName = testName;
            }

            public String getAssertionType() {
                return assertionType;
            }

            @Override
            public int compareTo(@org.jetbrains.annotations.NotNull Object o) {
                return (this.id.compareTo(((Assertion) o).id));
            }

        }
    }
}
