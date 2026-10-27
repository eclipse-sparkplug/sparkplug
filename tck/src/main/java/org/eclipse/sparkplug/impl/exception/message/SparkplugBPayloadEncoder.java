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
						builder.setUint64Value(((Date) value.getValue()).getTime());
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
					builder.getTypedValueBuilder().setUint64Value(((Date) value).getTime());
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
					metricBuilder.getValueBuilder().setUint64Value(((Date) metric.getValue()).getTime());
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
								if (values == null || col >= values.size()) {
									continue;
								}
								addDataSetValue(columnBuilder, columnType, values.get(col));
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
				case Int8Array:
					Byte[] int8ArrayValue = (Byte[]) metric.getValue();
					ByteBuffer int8ByteBuffer =
							ByteBuffer.allocate(int8ArrayValue.length).order(ByteOrder.LITTLE_ENDIAN);
					for (Byte value : int8ArrayValue) {
						int8ByteBuffer.put(value);
					}
					if (int8ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(int8ByteBuffer.array()));
					}
					break;
				case Int16Array:
					Short[] int16ArrayValue = (Short[]) metric.getValue();
					ByteBuffer int16ByteBuffer =
							ByteBuffer.allocate(int16ArrayValue.length * 2).order(ByteOrder.LITTLE_ENDIAN);
					for (Short value : int16ArrayValue) {
						int16ByteBuffer.putShort(value);
					}
					if (int16ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(int16ByteBuffer.array()));
					}
					break;
				case Int32Array:
					Integer[] int32ArrayValue = (Integer[]) metric.getValue();
					ByteBuffer int32ByteBuffer =
							ByteBuffer.allocate(int32ArrayValue.length * 4).order(ByteOrder.LITTLE_ENDIAN);
					for (Integer value : int32ArrayValue) {
						int32ByteBuffer.putInt(value);
					}
					if (int32ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(int32ByteBuffer.array()));
					}
					break;
				case Int64Array:
					Long[] int64ArrayValue = (Long[]) metric.getValue();
					ByteBuffer int64ByteBuffer =
							ByteBuffer.allocate(int64ArrayValue.length * 8).order(ByteOrder.LITTLE_ENDIAN);
					for (Long value : int64ArrayValue) {
						int64ByteBuffer.putLong(value);
					}
					if (int64ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(int64ByteBuffer.array()));
					}
					break;
				case UInt8Array:
					Short[] uInt8ArrayValue = (Short[]) metric.getValue();
					ByteBuffer uInt8ByteBuffer =
							ByteBuffer.allocate(uInt8ArrayValue.length).order(ByteOrder.LITTLE_ENDIAN);
					for (Short value : uInt8ArrayValue) {
						uInt8ByteBuffer.put((byte) (value & 0xffff));
					}
					if (uInt8ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(uInt8ByteBuffer.array()));
					}
					break;
				case UInt16Array:
					Integer[] uInt16ArrayValue = (Integer[]) metric.getValue();
					ByteBuffer uInt16ByteBuffer =
							ByteBuffer.allocate(uInt16ArrayValue.length * 2).order(ByteOrder.LITTLE_ENDIAN);
					for (Integer value : uInt16ArrayValue) {
						uInt16ByteBuffer.putShort((short) (value & 0xffffffff));
					}
					if (uInt16ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(uInt16ByteBuffer.array()));
					}
					break;
				case UInt32Array:
					Long[] uInt32ArrayValue = (Long[]) metric.getValue();
					ByteBuffer uInt32ByteBuffer =
							ByteBuffer.allocate(uInt32ArrayValue.length * 4).order(ByteOrder.LITTLE_ENDIAN);
					for (Long value : uInt32ArrayValue) {
						uInt32ByteBuffer.putInt((int) (value & 0xffffffffffffffffL));
					}
					if (uInt32ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(uInt32ByteBuffer.array()));
					}
					break;
				case UInt64Array:
					BigInteger[] uInt64ArrayValue = (BigInteger[]) metric.getValue();
					ByteBuffer uInt64ByteBuffer =
							ByteBuffer.allocate(uInt64ArrayValue.length * 8).order(ByteOrder.LITTLE_ENDIAN);
					for (BigInteger value : uInt64ArrayValue) {
						uInt64ByteBuffer.putLong(bigIntegerToUnsignedLong(value));
					}
					if (uInt64ByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(uInt64ByteBuffer.array()));
					}
					break;
				case FloatArray:
					Float[] floatArrayValue = (Float[]) metric.getValue();
					ByteBuffer floatByteBuffer =
							ByteBuffer.allocate(floatArrayValue.length * 4).order(ByteOrder.LITTLE_ENDIAN);
					for (Float value : floatArrayValue) {
						floatByteBuffer.putFloat(value);
					}
					if (floatByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(floatByteBuffer.array()));
					}
					break;
				case DoubleArray:
					Double[] doubleArrayValue = (Double[]) metric.getValue();
					ByteBuffer doubleByteBuffer =
							ByteBuffer.allocate(doubleArrayValue.length * 8).order(ByteOrder.LITTLE_ENDIAN);
					for (Double value : doubleArrayValue) {
						doubleByteBuffer.putDouble(value);
					}
					if (doubleByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(doubleByteBuffer.array()));
					}
					break;
				case BooleanArray:
					Boolean[] booleanArrayValue = (Boolean[]) metric.getValue();
					int numberOfBytes = (int) Math.ceil((double) booleanArrayValue.length / 8);
					ByteBuffer booleanByteBuffer =
							ByteBuffer.allocate(4 + numberOfBytes).order(ByteOrder.LITTLE_ENDIAN);

					// The first 4 bytes is the number of booleans in the array
					booleanByteBuffer.putInt(booleanArrayValue.length);

					// Get the remaining bytes
					for (int i = 0; i < numberOfBytes; i++) {
						byte nextByte = 0;
						for (int bit = 0; bit < 8; bit++) {
							int index = i * 8 + bit;
							if (index < booleanArrayValue.length && booleanArrayValue[index]) {
								nextByte |= (128 >> bit);
							}
						}
						booleanByteBuffer.put(nextByte);
					}

					metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(booleanByteBuffer.array()));
					break;
				case StringArray:
					String[] stringArrayValue = (String[]) metric.getValue();
					int size = 0;
					for (String string : stringArrayValue) {
						size = size + string.length() + 1;
					}
					ByteBuffer stringByteBuffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
					for (String value : stringArrayValue) {
						byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
						stringByteBuffer.put(stringBytes);
						stringByteBuffer.put((byte) 0);
					}
					if (stringByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(stringByteBuffer.array()));
					}
					break;
				case DateTimeArray:
					Date[] dateTimeArrayValue = (Date[]) metric.getValue();
					ByteBuffer dateTimeByteBuffer =
							ByteBuffer.allocate(dateTimeArrayValue.length * 8).order(ByteOrder.LITTLE_ENDIAN);
					for (Date value : dateTimeArrayValue) {
						dateTimeByteBuffer.putLong(value.getTime());
					}
					if (dateTimeByteBuffer.hasArray()) {
						metricBuilder.getValueBuilder().setBytesValue(ByteString.copyFrom(dateTimeByteBuffer.array()));
					}
					break;
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
		Object raw = value == null ? null : value.getValue();
		if (raw == null) {
			return;
		}

		switch (type) {
			case Int8:
				columnBuilder.addIntValues((Byte) raw);
				break;
			case Int16:
				columnBuilder.addIntValues((Short) raw);
				break;
			case Int32:
				columnBuilder.addIntValues((Integer) raw);
				break;
			case UInt8:
				columnBuilder.addIntValues(Short.toUnsignedInt((Short) raw));
				break;
			case UInt16:
				columnBuilder.addIntValues((int) Integer.toUnsignedLong((Integer) raw));
				break;
			case UInt32:
				if (raw instanceof Long) {
					columnBuilder.addIntValues(((Long) raw).intValue());
				} else {
					columnBuilder.addIntValues((Integer) raw);
				}
				break;
			case Int64:
				columnBuilder.addLongValues((Long) raw);
				break;
			case UInt64:
				if (raw instanceof BigInteger) {
					columnBuilder.addLongValues(bigIntegerToUnsignedLong((BigInteger) raw));
				} else {
					columnBuilder.addLongValues((Long) raw);
				}
				break;
			case DateTime:
				columnBuilder.addLongValues(((Date) raw).getTime());
				break;
			case Float:
				columnBuilder.addFloatValues((Float) raw);
				break;
			case Double:
				columnBuilder.addDoubleValues((Double) raw);
				break;
			case Boolean:
				columnBuilder.addBooleanValues(toBoolean(raw));
				break;
			case String:
			case Text:
				columnBuilder.addStringValues((String) raw);
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
