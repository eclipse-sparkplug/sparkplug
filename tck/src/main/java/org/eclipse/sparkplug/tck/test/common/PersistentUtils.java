/*******************************************************************************
 * Copyright (c) 2022 Cirrus Link Solutions
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Wes Johnson - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.common;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentUtils {

	private static Logger logger = LoggerFactory.getLogger(PersistentUtils.class.getName());

	private static final String SPARKPLUG_DIRNAME = "Sparkplug_TCK_Temp_Dir";

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private static final String HOST_BD_SEQ_NUM_FILE_NAME =
			TMP_DIR + FILE_SEPARATOR + SPARKPLUG_DIRNAME + FILE_SEPARATOR + "HOST_BD_SEQ_NUM";

	public static int getNextHostDeathBdSeqNum() {
		try {
			File bdSeqNumFile = new File(HOST_BD_SEQ_NUM_FILE_NAME);
			if (bdSeqNumFile.exists()) {
				int bdSeqNum = Integer
						.parseInt(Files.readString(Paths.get(HOST_BD_SEQ_NUM_FILE_NAME), StandardCharsets.UTF_8));
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
			Files.writeString(Paths.get(HOST_BD_SEQ_NUM_FILE_NAME), Long.toString(bdSeqNum),
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception e) {
			logger.error("Failed to write the Host bdSeq number to the persistent directory", e);
		}
	}

	public static int getNextHostDeathBdSeqNum(String hostName) {
		try {
			File bdSeqNumFile = new File(HOST_BD_SEQ_NUM_FILE_NAME + hostName);
			if (bdSeqNumFile.exists()) {
				int bdSeqNum = Integer
						.parseInt(Files.readString(Paths.get(HOST_BD_SEQ_NUM_FILE_NAME), StandardCharsets.UTF_8));
				logger.info("Next Host Death bdSeq number: {}", bdSeqNum);
				return bdSeqNum;
			} else {
				return -1;
			}
		} catch (Exception e) {
			logger.error("Failed to get the bdSeq number from the persistent directory", e);
			return -1;
		}
	}

	public static void setNextHostDeathBdSeqNum(String hostName, int bdSeqNum) {
		try {
			Files.writeString(Paths.get(HOST_BD_SEQ_NUM_FILE_NAME), Long.toString(bdSeqNum),
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception e) {
			logger.error("Failed to write the Host bdSeq number to the persistent directory", e);
		}
	}
}
