/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2022 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package org.eclipse.sparkplug.tck.test.common;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentUtils {

	private static Logger logger = LoggerFactory.getLogger(PersistentUtils.class.getName());

	private static final String SPARKPLUG_DIRNAME = "Sparkplug_TCK_Temp_Dir";

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private static final String HOST_BD_SEQ_NUM_FILE_NAME =
			TMP_DIR + SPARKPLUG_DIRNAME + FILE_SEPARATOR + "HOST_BD_SEQ_NUM";

	public static int getNextHostDeathBdSeqNum() {
		try {
			File bdSeqNumFile = new File(HOST_BD_SEQ_NUM_FILE_NAME);
			if (bdSeqNumFile.exists()) {
				int bdSeqNum = Integer.parseInt(FileUtils.readFileToString(bdSeqNumFile, StandardCharsets.UTF_8));
				logger.info("Next Host Death bdSeq number: {}", bdSeqNum);
				return bdSeqNum;
			} else {
				return 0;
			}
		} catch (Exception e) {
			logger.error("Failed to get the bdSeq number from the persistent directory", e);
			return 0;
		}
	}

	public static void setNextHostDeathBdSeqNum(int bdSeqNum) {
		try {
			File bdSeqNumFile = new File(HOST_BD_SEQ_NUM_FILE_NAME);
			FileUtils.write(bdSeqNumFile, Long.toString(bdSeqNum), StandardCharsets.UTF_8, false);
		} catch (Exception e) {
			logger.error("Failed to write the Host bdSeq number to the persistent directory", e);
		}
	}

}
