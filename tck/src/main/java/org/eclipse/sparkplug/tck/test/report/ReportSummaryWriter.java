package org.eclipse.sparkplug.tck.test.report;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.eclipse.sparkplug.tck.test.TCKTest.GROUP_SUMMARY;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_REPORT_DOWNLOAD_TOPIC;
import static org.eclipse.sparkplug.tck.test.report.HtmlConstants.*;

public class ReportSummaryWriter {
    static final Logger LOGGER = LoggerFactory.getLogger("TCKResultsSummary");
    private final TreeSet<String> warnings = new TreeSet<>();
    private final TreeSet<GroupedTestHandler.GroupedTest> edgeIds;
    private final TreeSet<GroupedTestHandler.GroupedTest> hostIds;
    private final TreeSet<GroupedTestHandler.GroupedTest> brokerIds;
    private final File sparkplugTCKLogFile;
    private final File sparkplugTCKSummaryFile;
    private final List<String> lines;
    private int allGroupedTestCnt = 0;

    public ReportSummaryWriter(final String sparkplugTCKLog) {
        this.sparkplugTCKLogFile = new File(sparkplugTCKLog);
        this.sparkplugTCKSummaryFile = new File("Summary-" + sparkplugTCKLogFile.getName() + ".html");
        try {
            lines = Files.readAllLines(sparkplugTCKLogFile.toPath());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
        this.brokerIds = GroupedTestHandler.getInstance().getBrokerProfileGroup();
        this.hostIds = GroupedTestHandler.getInstance().getHostProfileGroup();
        this.edgeIds = GroupedTestHandler.getInstance().getEdgeProfileGroup();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Test result log file must be the first argument");
            System.exit(-1);
        }
        try {
            System.out.println("Start writing TCK Summary from test result log: " + args[0]);
            (new ReportSummaryWriter(args[0])).writeReport();
        } catch (IOException e) {
            System.err.println("Error in writing Report: " + e);
            System.exit(-1);
        }

    }

    public void publishDownloadSummary() {

        LOGGER.info("Reporting: Download summary {} ", sparkplugTCKSummaryFile.toPath());
        final byte[] summary;
        try {
            summary = Files.readAllBytes(sparkplugTCKSummaryFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final PublishService publishService = Services.publishService();
        final Publish message = Builders.publish().topic(TCK_REPORT_DOWNLOAD_TOPIC).qos(Qos.AT_LEAST_ONCE)
                .payload(ByteBuffer.wrap(summary)).build();
        publishService.publish(message);
        LOGGER.info("Reporting: pushed content of {} to topic {}, bytes: {} ", sparkplugTCKSummaryFile.toPath(), TCK_REPORT_DOWNLOAD_TOPIC, summary.length);
    }

    public String writeReport() throws IOException {
        //we have all the testIds for each source file in the groupedTestIds
        //we run over the sparkplug result log and check line by lien against all existing testIds
        final File report = new File(sparkplugTCKSummaryFile.toPath().toString());
        try {
            LOGGER.info("Reporting: Start creating Summary to: {} ", report.getAbsolutePath());

            this.processSparkplugTCKLogfile();

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.");
            df.format(System.currentTimeMillis());

            ArrayList<String> lines = new ArrayList<>();
            lines.add(HtmlConstants.HTML_PREFIX);
            lines.add(HtmlConstants.TITLE);
            lines.add(REPORT_TITLE);
            lines.add(HtmlConstants.TITLE_END);
            lines.add("Date: " + df.format(System.currentTimeMillis()));
            if( !warnings.isEmpty())
                lines.add(HtmlConstants.CAP + warnings + HtmlConstants.CAP_END);

            lines.add(exportAsHtml(brokerIds));
            lines.add(exportAsHtml(hostIds));
            lines.add(exportAsHtml(edgeIds));

            lines.add(HtmlConstants.CAP + "Overall count: " + this.allGroupedTestCnt + HtmlConstants.CAP_END);
            lines.add(HtmlConstants.HTML_POSTFIX);

            Files.write(report.toPath(), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            LOGGER.info("Finish Reporting Summary to: {} ", report.getAbsolutePath());
        } catch (Exception all) {
            LOGGER.error("Reporting: Can't open Test result log file at: " + sparkplugTCKLogFile.getAbsolutePath() + " error: ", all);
        }
        return report.getPath();
    }

    private void processSparkplugTCKLogfile() {
        LOGGER.info("Reporting: Processing logfile...");
        final TreeMap<String, String> executedTests = new TreeMap<>();

        for (int index = 0; index < lines.size(); index++) {
            String currentLine = lines.get(index).strip();
            if (currentLine.contains(GROUP_SUMMARY)) {
                //2022-12-02 11:05:15.155 Summary Test Results for Edge SessionEstablishment
                String[] headLine = currentLine.split(" ", 8);
                String date = headLine[0];
                String time = headLine[1];
                String profile = headLine[6];
                String test = headLine[7];
                if (executedTests.containsKey(test)) {
                    String logLine = "Warning: test: " + profile + "." + test + " logged more than once. Any previously failing assertion will not be overwritten by a later success.";
                    MessageFormat messageFormat = new MessageFormat(logLine);
                    String warn = messageFormat.format(new Object[]{profile, test});
                    LOGGER.info("Reporting: {}", warn);
                    warnings.add(warn);
                }
                executedTests.put(test, date + " " + time);
                processLoggedTest(profile.toLowerCase(), test, date + " " + time, index);
            }
        }

    }

    private String exportAsHtml(final @NotNull TreeSet<GroupedTestHandler.GroupedTest> group) {
        StringBuilder lines = new StringBuilder(HtmlConstants.SUBTITLE)
                .append("Sparkplug Profile: ")
                .append(group.first().getProfile()).append(HtmlConstants.SUBTITLE_END);
        for (GroupedTestHandler.GroupedTest item : group) {
            allGroupedTestCnt += item.getAssertions().size();
            GroupedTestHandler.TestStat groupedTest = new GroupedTestHandler.TestStat(item);
            lines.append(HtmlConstants.CAP).append(item.getGroup().replaceAll("_", "")).append(HtmlConstants.CAP_END);

            lines.append(HtmlConstants.TABLE).append(STATS_HEADER).append(groupedTest.asRow());
            if (groupedTest.hasOptionalTests()) {
                lines.append(OPTIONAL_STATS_HEADER)
                        .append(groupedTest.optionalRow());
            }
            lines.append(HtmlConstants.TABLE_END).append("\n<br/>");

            lines.append(HtmlConstants.TABLE);
            lines.append(HtmlConstants.TABLE_TITLE);
            for (GroupedTestHandler.GroupedTest.Assertion assertion : item.getAssertions()) {
                lines.append(HtmlConstants.ROW).append("tck-id-").append(assertion.getId());
                lines.append(HtmlConstants.COLUMN).append(assertion.getAssertionType());
                lines.append(HtmlConstants.COLUMN).append(assertion.getTestName());
                lines.append(HtmlConstants.COLUMN).append(assertion.getTimestamp());
                lines.append(HtmlConstants.COLUMN).append(assertion.getResult());
                lines.append(HtmlConstants.ROW_END);
            }
            lines.append(HtmlConstants.TABLE_END);
            lines.append("\n");
        }
        return lines.toString();
    }


    private void processLoggedTest(String profile, String test, String dateTime, int index) {
        String currentLine = lines.get(++index).strip();
        while (currentLine != null && !currentLine.contains("OVERALL")) {
            //Conformance-mqtt-aware-nbirth-mqtt-retain: PASS;
            //Monitor:payloads-timestamp-in-UTC: PASS;
            String[] resultArray = currentLine.split(": ", 2);
            String assertionId = resultArray[0];
            String result = resultArray[1].replaceAll(";", "");
            if (assertionId.startsWith("Monitor:")) {
                assertionId = assertionId.replace("Monitor:", "");
            }
            LOGGER.trace("Reporting: Check Assertion {} from: {}.{} with result {}", assertionId, profile, test, result);
            boolean hasSet = setCurrentTest(test, dateTime, assertionId, result,
                    ("broker".equals(profile)) ?
                            brokerIds : ("host".equals(profile)) ? hostIds : edgeIds);

            if (!(hasSet)) {
                LOGGER.error("Reporting: Assertion not found in tests: Assertion {} from: {}.{}", assertionId, profile, test);
            }
            currentLine = lines.get(++index).strip();
        }
    }

    private boolean setCurrentTest(String test, String dateTime, String assertionId, String result, TreeSet<GroupedTestHandler.GroupedTest> tests) {
        for (GroupedTestHandler.GroupedTest groupedTest : tests) {
            if (groupedTest.hasAssertion(assertionId)) {
                groupedTest.setAssertion(assertionId, test, dateTime, result);
                return true;
            }
        }
        return false;
    }
}


