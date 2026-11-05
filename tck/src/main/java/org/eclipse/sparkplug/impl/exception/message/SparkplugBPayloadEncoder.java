/********************************************************************************
 * Copyright (c) 2014-2022 Cirrus Link Solutions and others
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

package org.eclipse.sparkplug.impl.exception.message;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.sparkplug.impl.exception.model.DataSet;
import org.eclipse.sparkplug.impl.exception.model.DataSetDataType;
import org.eclipse.sparkplug.impl.exception.model.File;
import org.eclipse.sparkplug.impl.exception.model.MetaData;
import org.eclipse.sparkplug.impl.exception.model.Metric;
import org.eclipse.sparkplug.impl.exception.model.Parameter;
import org.eclipse.sparkplug.impl.exception.model.ParameterDataType;
import org.eclipse.sparkplug.impl.exception.model.PropertyDataType;
import org.eclipse.sparkplug.impl.exception.model.PropertySet;
import org.eclipse.sparkplug.impl.exception.model.PropertyValue;
import org.eclipse.sparkplug.impl.exception.model.Row;
import org.eclipse.sparkplug.impl.exception.model.SparkplugBPayload;
import org.eclipse.sparkplug.impl.exception.model.Template;
import org.eclipse.sparkplug.impl.exception.model.Value;
import org.eclipse.tahu.protobuf.SparkplugBProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

/**
 * A {@link PayloadDecode} implementation for encoding Sparkplug B payloads.
 */
public class SparkplugBPayloadEncoder implements PayloadEncoder<SparkplugBPayload> {

	private static final Logger logger = LoggerFactory.getLogger(SparkplugBPayloadEncoder.class.getName());

	public SparkplugBPayloadEncoder() {
		super();
	}

	public byte[] getBytes(SparkplugBPayload payload) throws IOException {

		SparkplugBProto.Payload.Builder protoMsg = SparkplugBProto.Payload.newBuilder();

		// Set the timestamp
		if (payload.getTimestamp() != null) {
			protoMsg.getHeaderBuilder().setTimestamp(payload.getTimestamp().getTime());
		}

		// Set the sequence number
		if (payload.getSeq() != null) {
			protoMsg.getHeaderBuilder().setSeq(payload.getSeq());
		}

		// Set the UUID if defined
		if (payload.getUuid() != null) {
			protoMsg.getHeaderBuilder().setUuid(payload.getUuid());
		}

		// Set the metrics
		for (Metric metric : payload.getMetrics()) {
			try {
				protoMsg.getContentBuilder().addMetrics(convertMetric(metric));
			} catch (Exception e) {
				logger.error("Failed to add metric: " + metric.getName(), e);
				throw new RuntimeException(e);
			}
		}

		// Set the body
		if (payload.getBody() != null) {
			protoMsg.getContentBuilder().setBody(ByteString.copyFrom(payload.getBody()));
		}

		return protoMsg.build().toByteArray();
	}

	private SparkplugBProto.Payload.Metric.Builder convertMetric(Metric metric) throws Exception {

		// build a metric
		SparkplugBProto.Payload.Metric.Builder builder = SparkplugBProto.Payload.Metric.newBuilder();

		// set the basic parameters
		builder.setDatatype(SparkplugBProto.DataType.forNumber(metric.getDataType().toIntValue()));
		builder = setMetricValue(builder, metric);

		// Set the name, data type, and value
		if (metric.hasName()) {
			builder.setName(metric.getName());
		} else {
			// name is an empty String by default and must be cleared
			builder.clearName();
		}

		// Set the alias
		if (metric.hasAlias()) {
			builder.setAlias(metric.getAlias());
		}

		// Set the timestamp
		if (metric.getTimestamp() != null) {
			builder.setTimestamp(metric.getTimestamp().getTime());
		}

		// Set isHistorical
		if (metric.getIsHistorical() != null) {
			builder.setIsHistorical(metric.isHistorical());
		}

		// Set isTransient
		if (metric.getIsTransient() != null) {
			builder.setIsTransient(metric.isTransient());
		}

		// Set isNull
		if (metric.getIsNull() != null) {
			builder.setIsNull(metric.isNull());
		}

		// Set the metadata
		if (metric.getMetaData() != null) {
			builder = setMetaData(builder, metric);
		}

		// Set the property set
		if (metric.getProperties() != null) {
			builder.setProperties(convertPropertySet(metric.getProperties()));
		}

		return builder;
	}

	private SparkplugBProto.Payload.Template.Parameter.Builder convertParameter(Parameter parameter) throws Exception {

		// build a metric
		SparkplugBProto.Payload.Template.Parameter.Builder builder =
				SparkplugBProto.Payload.Template.Parameter.newBuilder();

		if (logger.isTraceEnabled()) {
			logger.trace("Adding parameter: " + parameter.getName());
			logger.trace("            type: " + parameter.getType());
		}

		// Set the name
		builder.setName(parameter.getName());

		// Set the type and value
		builder = setParameterValue(builder, parameter);

		return builder;
	}

	private SparkplugBProto.Payload.PropertySet.Builder convertPropertySet(PropertySet propertySet) throws Exception {
		SparkplugBProto.Payload.PropertySet.Builder setBuilder = SparkplugBProto.Payload.PropertySet.newBuilder();

		Map<String, PropertyValue> map = propertySet.getPropertyMap();
		for (String key : map.keySet()) {
			SparkplugBProto.Payload.TypedValue.Builder builder = SparkplugBProto.Payload.TypedValue.newBuilder();
			PropertyValue value = map.get(key);
			PropertyDataType type = value.getType();
			builder.setDatatype(SparkplugBProto.DataType.forNumber(type.toIntValue()));
			if (value.getValue() == null) {
				builder.setIsNull(true);
			} else {
				switch (type) {
					case Boolean:
						builder.setBooleanValue((Boolean) value.getValue());
						break;
					case DateTime:
						builder.setInt64Value(((Date) value.getValue()).getTime());
						break;
					case Double:
						builder.setDoubleValue((Double) value.getValue());
						break;
					case Float:
						builder.setFloatValue((Float) value.getValue());
						break;
					case Int8:
						builder.setInt32Value((Byte) value.getValue());
						break;
					case Int16:
						builder.setInt32Value((Short) value.getValue());
						break;
					case Int32:
						builder.setInt32Value((Integer) value.getValue());
						break;
					case Int64:
						builder.setInt64Value((Long) value.getValue());
						break;
					case UInt8:
						builder.setUint32Value(Short.toUnsignedInt((Short) value.getValue()));
						break;
					case UInt16:
						builder.setUint32Value((int) Integer.toUnsignedLong((Integer) value.getValue()));
						break;
					case UInt32:
						builder.setUint32Value((int) Long.parseUnsignedLong(Long.toUnsignedString((Long) value.getValue())));
						break;
					case UInt64:
						builder.setUint64Value(bigIntegerToUnsignedLong((BigInteger) value.getValue()));
						break;
					case String:
					case Text:
						builder.setStringValue((String) value.getValue());
						break;
					case PropertySet:
						builder.setPropertysetValue(convertPropertySet((PropertySet) value.getValue()));
						break;
					case PropertySetList:
						List<?> setList = (List<?>) value.getValue();
						SparkplugBProto.Payload.PropertySetList.Builder listBuilder =
								SparkplugBProto.Payload.PropertySetList.newBuilder();
						for (Object obj : setList) {
							listBuilder.addPropertyset(convertPropertySet((PropertySet) obj));
						}
						builder.setPropertysetsValue(listBuilder);
						break;
					case Unknown:
					default:
						logger.error("Unknown PropertyDataType: " + value.getType());
						throw new Exception("Failed to convert value " + value.getType());
				}
			}
			setBuilder.addKeys(key);
			setBuilder.addValues(builder);
		}
		return setBuilder;
	}

	private SparkplugBProto.Payload.Template.Parameter.Builder setParameterValue(
			SparkplugBProto.Payload.Template.Parameter.Builder builder, Parameter parameter) throws Exception {
		ParameterDataType type = parameter.getType();
		builder.getTypedValueBuilder()
				.setDatatype(SparkplugBProto.DataType.forNumber(type.toIntValue()));

		Object value = parameter.getValue();
		value = type == ParameterDataType.String && value == null ? "" : value;
		if (value != null) {
			switch (type) {
				case Boolean:
					builder.getTypedValueBuilder().setBooleanValue(toBoolean(value));
					break;
				case DateTime:
					builder.getTypedValueBuilder().setInt64Value(((Date) value).getTime());
					break;
				case Double:
					builder.getTypedValueBuilder().setDoubleValue((Double) value);
					break;
				case Float:
					builder.getTypedValueBuilder().setFloatValue((Float) value);
					break;
				case Int8:
					builder.getTypedValueBuilder().setInt32Value((Byte) value);
					break;
				case Int16:
					builder.getTypedValueBuilder().setInt32Value((Short) value);
					break;
				case Int32:
					builder.getTypedValueBuilder().setInt32Value((Integer) value);
					break;
				case Int64:
					builder.getTypedValueBuilder().setInt64Value((Long) value);
					break;
				case UInt8:
					builder.getTypedValueBuilder().setUint32Value(Short.toUnsignedInt((Short) value));
					break;
				case UInt16:
					builder.getTypedValueBuilder().setUint32Value((int) Integer.toUnsignedLong((Integer) value));
					break;
				case UInt32:
					builder.getTypedValueBuilder().setUint32Value((int) ((BigInteger) value).longValue());
					break;
				case UInt64:
					builder.getTypedValueBuilder().setUint64Value(bigIntegerToUnsignedLong((BigInteger) value));
					break;
				case Text:
				case String:
					builder.getTypedValueBuilder().setStringValue((String) value);
					break;
				case Unknown:
				default:
					logger.error("Unknown Type: " + type);
					throw new Exception("Failed to encode");

			}
		}
		return builder;
	}

	private SparkplugBProto.Payload.Metric.Builder setMetricValue(SparkplugBProto.Payload.Metric.Builder metricBuilder,
			Metric metric) throws Exception {

		// Set the data type
		metricBuilder.setDatatype(SparkplugBProto.DataType.forNumber(metric.getDataType().toIntValue()));

		if (metric.getValue() == null) {
			metricBuilder.setIsNull(true);
		} else {
			switch (metric.getDataType()) {
				case Boolean:
					metricBuilder.getValueBuilder().setBooleanValue(toBoolean(metric.getValue()));
					break;
				case DateTime:
					metricBuilder.getValueBuilder().setInt64Value(((Date) metric.getValue()).getTime());
					break;
				case File:
					metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(((File) metric.getValue()).getBytes()));
					SparkplugBProto.Payload.MetaData.Builder metaDataBuilder =
							SparkplugBProto.Payload.MetaData.newBuilder();
					metaDataBuilder.setFileName(((File) metric.getValue()).getFileName());
					metricBuilder.setMetadata(metaDataBuilder);
					break;
				case Float:
					metricBuilder.getValueBuilder().setFloatValue((Float) metric.getValue());
					break;
				case Double:
					metricBuilder.getValueBuilder().setDoubleValue((Double) metric.getValue());
					break;
				case Int8:
					metricBuilder.getValueBuilder().setInt32Value((Byte) metric.getValue());
					break;
				case Int16:
					metricBuilder.getValueBuilder().setInt32Value((Short) metric.getValue());
					break;
				case Int32:
					metricBuilder.getValueBuilder().setInt32Value((Integer) metric.getValue());
					break;
				case Int64:
					metricBuilder.getValueBuilder().setInt64Value((Long) metric.getValue());
					break;
				case UInt8:
					metricBuilder.getValueBuilder().setUint32Value(Short.toUnsignedInt((Short) metric.getValue()));
					break;
				case UInt16:
					metricBuilder.getValueBuilder().setUint32Value((int) Integer.toUnsignedLong((Integer) metric.getValue()));
					break;
				case UInt32:
					metricBuilder.getValueBuilder().setUint32Value((int) Long.parseUnsignedLong(Long.toUnsignedString((Long) metric.getValue())));
					break;
				case UInt64:
					metricBuilder.getValueBuilder().setUint64Value(bigIntegerToUnsignedLong((BigInteger) metric.getValue()));
					break;
				case String:
				case Text:
				case UUID:
					metricBuilder.getValueBuilder().setStringValue((String) metric.getValue());
					break;
				case Bytes:
					metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom((byte[]) metric.getValue()));
					break;
				case DataSet:
					DataSet dataSet = (DataSet) metric.getValue();
					SparkplugBProto.Payload.DataSet.Builder dataSetBuilder =
							SparkplugBProto.Payload.DataSet.newBuilder();

					// v4 DataSets are column oriented: each Column carries its own name, datatype,
					// and every value of that column, so the row-major model is transposed here.
					List<String> columnNames = dataSet.getColumnNames();
					List<DataSetDataType> columnTypes = dataSet.getTypes();
					List<Row> rows = dataSet.getRows();
					int columnCount = columnTypes == null ? 0 : columnTypes.size();

					for (int col = 0; col < columnCount; col++) {
						SparkplugBProto.Payload.DataSet.Column.Builder columnBuilder =
								SparkplugBProto.Payload.DataSet.Column.newBuilder();
						if (columnNames != null && col < columnNames.size()) {
							columnBuilder.setName(columnNames.get(col));
						}
						DataSetDataType columnType = columnTypes.get(col);
						columnBuilder.setDatatype(
								SparkplugBProto.DataType.forNumber(columnType.toIntValue()));

						if (rows != null) {
							for (Row row : rows) {
								List<Value<?>> values = row.getValues();
								// Exactly one value per row, so that index i of every Column is
								// row i, as payloads-dataset-row-correlation requires. A v4 Column
								// has no per-cell absence, so a missing cell holds its position
								// with the zero value for the column's datatype.
								Value<?> cell = (values == null || col >= values.size())
										? null
										: values.get(col);
								addDataSetValue(columnBuilder, columnType, cell);
							}
						}
						dataSetBuilder.addColumns(columnBuilder);
					}

					// Finally add the dataset
					metricBuilder.getValueBuilder().setDatasetValue(dataSetBuilder);
					break;
				case Template:
					Template template = (Template) metric.getValue();
					SparkplugBProto.Payload.Template.Builder templateBuilder =
							SparkplugBProto.Payload.Template.newBuilder();

					// Set isDefinition
					templateBuilder.setIsDefinition(template.isDefinition());

					// Set Version
					if (template.getVersion() != null) {
						templateBuilder.setVersion(template.getVersion());
					}

					// Set Template Reference
					if (template.getTemplateRef() != null) {
						templateBuilder.setTemplateRef(template.getTemplateRef());
					}

					// Set the template metrics
					if (template.getMetrics() != null) {
						for (Metric templateMetric : template.getMetrics()) {
							templateBuilder.addMetrics(convertMetric(templateMetric));
						}
					}

					// Set the template parameters
					if (template.getParameters() != null) {
						for (Parameter parameter : template.getParameters()) {
							templateBuilder.addParameters(convertParameter(parameter));
						}
					}

					// Add the template to the metric
					metricBuilder.getValueBuilder().setTemplateValue(templateBuilder);
					break;
				case Int8Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaInt8Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iInt8Array = 0;
					for (Byte element : (Byte[]) metric.getValue()) {
						if (element == null) {
							vaInt8Array.addNullIndexes(iInt8Array);
							vaInt8Array.addSint32Values(0);
						} else {
							vaInt8Array.addSint32Values(element);
						}
						iInt8Array++;
					}
					break;
				}
				case Int16Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaInt16Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iInt16Array = 0;
					for (Short element : (Short[]) metric.getValue()) {
						if (element == null) {
							vaInt16Array.addNullIndexes(iInt16Array);
							vaInt16Array.addSint32Values(0);
						} else {
							vaInt16Array.addSint32Values(element);
						}
						iInt16Array++;
					}
					break;
				}
				case Int32Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaInt32Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iInt32Array = 0;
					for (Integer element : (Integer[]) metric.getValue()) {
						if (element == null) {
							vaInt32Array.addNullIndexes(iInt32Array);
							vaInt32Array.addSint32Values(0);
						} else {
							vaInt32Array.addSint32Values(element);
						}
						iInt32Array++;
					}
					break;
				}
				case Int64Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaInt64Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iInt64Array = 0;
					for (Long element : (Long[]) metric.getValue()) {
						if (element == null) {
							vaInt64Array.addNullIndexes(iInt64Array);
							vaInt64Array.addSint64Values(0L);
						} else {
							vaInt64Array.addSint64Values(element);
						}
						iInt64Array++;
					}
					break;
				}
				case UInt8Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaUInt8Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iUInt8Array = 0;
					for (Short element : (Short[]) metric.getValue()) {
						if (element == null) {
							vaUInt8Array.addNullIndexes(iUInt8Array);
							vaUInt8Array.addUint32Values(0);
						} else {
							vaUInt8Array.addUint32Values(Short.toUnsignedInt(element));
						}
						iUInt8Array++;
					}
					break;
				}
				case UInt16Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaUInt16Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iUInt16Array = 0;
					for (Integer element : (Integer[]) metric.getValue()) {
						if (element == null) {
							vaUInt16Array.addNullIndexes(iUInt16Array);
							vaUInt16Array.addUint32Values(0);
						} else {
							vaUInt16Array.addUint32Values((int) Integer.toUnsignedLong(element));
						}
						iUInt16Array++;
					}
					break;
				}
				case UInt32Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaUInt32Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iUInt32Array = 0;
					for (Long element : (Long[]) metric.getValue()) {
						if (element == null) {
							vaUInt32Array.addNullIndexes(iUInt32Array);
							vaUInt32Array.addUint32Values(0);
						} else {
							vaUInt32Array.addUint32Values(element.intValue());
						}
						iUInt32Array++;
					}
					break;
				}
				case UInt64Array: {
					SparkplugBProto.Payload.ValueArray.Builder vaUInt64Array =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iUInt64Array = 0;
					for (BigInteger element : (BigInteger[]) metric.getValue()) {
						if (element == null) {
							vaUInt64Array.addNullIndexes(iUInt64Array);
							vaUInt64Array.addUint64Values(0L);
						} else {
							vaUInt64Array.addUint64Values(bigIntegerToUnsignedLong(element));
						}
						iUInt64Array++;
					}
					break;
				}
				case FloatArray: {
					SparkplugBProto.Payload.ValueArray.Builder vaFloatArray =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iFloatArray = 0;
					for (Float element : (Float[]) metric.getValue()) {
						if (element == null) {
							vaFloatArray.addNullIndexes(iFloatArray);
							vaFloatArray.addFloatValues(0f);
						} else {
							vaFloatArray.addFloatValues(element);
						}
						iFloatArray++;
					}
					break;
				}
				case DoubleArray: {
					SparkplugBProto.Payload.ValueArray.Builder vaDoubleArray =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iDoubleArray = 0;
					for (Double element : (Double[]) metric.getValue()) {
						if (element == null) {
							vaDoubleArray.addNullIndexes(iDoubleArray);
							vaDoubleArray.addDoubleValues(0d);
						} else {
							vaDoubleArray.addDoubleValues(element);
						}
						iDoubleArray++;
					}
					break;
				}
				case BooleanArray: {
					SparkplugBProto.Payload.ValueArray.Builder vaBooleanArray =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iBooleanArray = 0;
					for (Boolean element : (Boolean[]) metric.getValue()) {
						if (element == null) {
							vaBooleanArray.addNullIndexes(iBooleanArray);
							vaBooleanArray.addBooleanValues(false);
						} else {
							vaBooleanArray.addBooleanValues(element);
						}
						iBooleanArray++;
					}
					break;
				}
				case StringArray: {
					SparkplugBProto.Payload.ValueArray.Builder vaStringArray =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iStringArray = 0;
					for (String element : (String[]) metric.getValue()) {
						if (element == null) {
							vaStringArray.addNullIndexes(iStringArray);
							vaStringArray.addStringValues("");
						} else {
							vaStringArray.addStringValues(element);
						}
						iStringArray++;
					}
					break;
				}
				case DateTimeArray: {
					SparkplugBProto.Payload.ValueArray.Builder vaDateTimeArray =
							metricBuilder.getValueBuilder().getArrayValueBuilder();
					int iDateTimeArray = 0;
					for (Date element : (Date[]) metric.getValue()) {
						if (element == null) {
							vaDateTimeArray.addNullIndexes(iDateTimeArray);
							vaDateTimeArray.addInt64Values(0L);
						} else {
							vaDateTimeArray.addInt64Values(element.getTime());
						}
						iDateTimeArray++;
					}
					break;
				}
				case Unknown:
				default:
					logger.error("Unsupported MetricDataType: " + metric.getDataType());
					throw new Exception("Failed to encode");

			}
		}
		return metricBuilder;
	}

	private SparkplugBProto.Payload.Metric.Builder setMetaData(SparkplugBProto.Payload.Metric.Builder metricBuilder,
			Metric metric) throws Exception {
		// If the builder has been built already - use it
		SparkplugBProto.Payload.MetaData.Builder metaDataBuilder = metricBuilder.getMetadataBuilder() != null
				? metricBuilder.getMetadataBuilder()
				: SparkplugBProto.Payload.MetaData.newBuilder();

		MetaData metaData = metric.getMetaData();
		if (metaData.getContentType() != null) {
			metaDataBuilder.setContentType(metaData.getContentType());
		}
		if (metaData.getSize() != null) {
			metaDataBuilder.setSize(metaData.getSize());
		}
		if (metaData.getSeq() != null) {
			metaDataBuilder.setSeq(metaData.getSeq());
		}
		if (metaData.getFileName() != null) {
			metaDataBuilder.setFileName(metaData.getFileName());
		}
		if (metaData.getFileType() != null) {
			metaDataBuilder.setFileType(metaData.getFileType());
		}
		if (metaData.getMd5() != null) {
			metaDataBuilder.setMd5(metaData.getMd5());
		}
		if (metaData.isMultiPart() != null) {
			metaDataBuilder.setIsMultiPart(metaData.isMultiPart());
		}
		if (metaData.getDescription() != null) {
			metaDataBuilder.setDescription(metaData.getDescription());
		}
		metricBuilder.setMetadata(metaDataBuilder);

		return metricBuilder;
	}

	// A v4 DataSet.Column holds all of its values in the one repeated field matching its datatype,
	// so a value is appended to that array rather than wrapped in a per-cell message.
	private void addDataSetValue(SparkplugBProto.Payload.DataSet.Column.Builder columnBuilder,
			DataSetDataType type, Value<?> value) throws Exception {
		SparkplugBProto.Payload.ValueArray.Builder va = columnBuilder.getValuesBuilder();
		Object raw = value == null ? null : value.getValue();
		if (raw == null) {
			// Record the index as null, then write a placeholder so the column keeps one entry
			// per row. A consuming application ignores the placeholder.
			va.addNullIndexes(nextRowIndex(va, type));
			addDataSetPlaceholder(columnBuilder, type);
			return;
		}

		switch (type) {
			case Int8:
				va.addSint32Values((Byte) raw);
				break;
			case Int16:
				va.addSint32Values((Short) raw);
				break;
			case Int32:
				va.addSint32Values((Integer) raw);
				break;
			case UInt8:
				va.addUint32Values(Short.toUnsignedInt((Short) raw));
				break;
			case UInt16:
				va.addUint32Values((int) Integer.toUnsignedLong((Integer) raw));
				break;
			case UInt32:
				if (raw instanceof Long) {
					va.addUint32Values(((Long) raw).intValue());
				} else {
					va.addUint32Values((Integer) raw);
				}
				break;
			case Int64:
				va.addSint64Values((Long) raw);
				break;
			case UInt64:
				if (raw instanceof BigInteger) {
					va.addUint64Values(bigIntegerToUnsignedLong((BigInteger) raw));
				} else {
					va.addUint64Values((Long) raw);
				}
				break;
			case DateTime:
				va.addInt64Values(((Date) raw).getTime());
				break;
			case Float:
				va.addFloatValues((Float) raw);
				break;
			case Double:
				va.addDoubleValues((Double) raw);
				break;
			case Boolean:
				va.addBooleanValues(toBoolean(raw));
				break;
			case String:
			case Text:
				va.addStringValues((String) raw);
				break;
			default:
				throw new Exception("Unknown DataSet column type: " + type);
		}
	}

	// The index the next appended element will occupy in the array this datatype uses.
	private int nextRowIndex(SparkplugBProto.Payload.ValueArray.Builder va, DataSetDataType type) {
		switch (type) {
			case Int8:
			case Int16:
			case Int32:
				return va.getSint32ValuesCount();
			case UInt8:
			case UInt16:
			case UInt32:
				return va.getUint32ValuesCount();
			case Int64:
				return va.getSint64ValuesCount();
			case UInt64:
				return va.getUint64ValuesCount();
			case DateTime:
				return va.getInt64ValuesCount();
			case Float:
				return va.getFloatValuesCount();
			case Double:
				return va.getDoubleValuesCount();
			case Boolean:
				return va.getBooleanValuesCount();
			default:
				return va.getStringValuesCount();
		}
	}

	// Writes the placeholder for a null cell, into the same typed array the column's datatype
	// uses, so the column keeps one entry per row. The zero itself carries no meaning: the cell
	// is recorded as null in null_indexes, and a consumer ignores the placeholder.
	private void addDataSetPlaceholder(SparkplugBProto.Payload.DataSet.Column.Builder columnBuilder,
			DataSetDataType type) throws Exception {
		SparkplugBProto.Payload.ValueArray.Builder va = columnBuilder.getValuesBuilder();
		switch (type) {
			case Int8:
			case Int16:
			case Int32:
				va.addSint32Values(0);
				break;
			case UInt8:
			case UInt16:
			case UInt32:
				va.addUint32Values(0);
				break;
			case Int64:
				va.addSint64Values(0L);
				break;
			case UInt64:
				va.addUint64Values(0L);
				break;
			case DateTime:
				va.addInt64Values(0L);
				break;
			case Float:
				va.addFloatValues(0f);
				break;
			case Double:
				va.addDoubleValues(0d);
				break;
			case Boolean:
				va.addBooleanValues(false);
				break;
			case String:
			case Text:
				va.addStringValues("");
				break;
			default:
				throw new Exception("Unknown DataSet column type: " + type);
		}
	}

	private Boolean toBoolean(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			return ((Integer) value).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Long) {
			return ((Long) value).longValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Float) {
			return ((Float) value).floatValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Double) {
			return ((Double) value).doubleValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Short) {
			return ((Short) value).shortValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Byte) {
			return ((Byte) value).byteValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof String) {
			return Boolean.parseBoolean(value.toString());
		}
		return (Boolean) value;
	}

	private long bigIntegerToUnsignedLong(BigInteger bigInteger) {
		BigInteger bref = BigInteger.ONE.shiftLeft(64);
		if (bigInteger.compareTo(BigInteger.ZERO) < 0)
			bigInteger = bigInteger.add(bref);
		if (bigInteger.compareTo(bref) >= 0 || bigInteger.compareTo(BigInteger.ZERO) < 0)
			throw new RuntimeException("Out of range: " + bigInteger);
		return bigInteger.longValue();
	}
}
