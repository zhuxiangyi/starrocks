// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.starrocks.hive.reader;

import com.starrocks.jni.connector.ColumnType;
import com.starrocks.jni.connector.ColumnValue;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.io.TimestampWritableV2;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DateObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public class HiveColumnValue implements ColumnValue {
    private final Object fieldData;
    private final ObjectInspector fieldInspector;
    private final String timeZone;

    HiveColumnValue(ObjectInspector fieldInspector, Object fieldData, String timeZone) {
        this.fieldInspector = fieldInspector;
        this.fieldData = fieldData;
        this.timeZone = timeZone;
    }

    private PrimitiveObjectInspector primitiveInspector() {
        return (PrimitiveObjectInspector) fieldInspector;
    }

    private Object inspectObject() {
        return primitiveInspector().getPrimitiveJavaObject(fieldData);
    }

    @Override
    public boolean getBoolean() {
        return PrimitiveObjectInspectorUtils.getBoolean(fieldData, primitiveInspector());
    }

    @Override
    public short getShort() {
        try {
            return PrimitiveObjectInspectorUtils.getShort(fieldData, primitiveInspector());
        } catch (NumberFormatException e) {
            return (short) parseScientificNumberAsLong(e);
        }
    }

    @Override
    public int getInt() {
        try {
            return PrimitiveObjectInspectorUtils.getInt(fieldData, primitiveInspector());
        } catch (NumberFormatException e) {
            return (int) parseScientificNumberAsLong(e);
        }
    }

    @Override
    public float getFloat() {
        return PrimitiveObjectInspectorUtils.getFloat(fieldData, primitiveInspector());
    }

    @Override
    public long getLong() {
        try {
            return PrimitiveObjectInspectorUtils.getLong(fieldData, primitiveInspector());
        } catch (NumberFormatException e) {
            return parseScientificNumberAsLong(e);
        }
    }

    @Override
    public double getDouble() {
        return PrimitiveObjectInspectorUtils.getDouble(fieldData, primitiveInspector());
    }

    @Override
    public String getString(ColumnType.TypeValue type) {
        Object o = inspectObject();
        return o.toString();
    }

    @Override
    public byte[] getBytes() {
        return (byte[]) inspectObject();
    }

    @Override
    public void unpackArray(List<ColumnValue> values) {
        ListObjectInspector inspector = (ListObjectInspector) fieldInspector;
        List<?> items = inspector.getList(fieldData);
        ObjectInspector itemInspector = inspector.getListElementObjectInspector();
        for (Object item : items) {
            HiveColumnValue cv = null;
            if (item != null) {
                cv = new HiveColumnValue(itemInspector, item, timeZone);
            }
            values.add(cv);
        }
    }

    @Override
    public void unpackMap(List<ColumnValue> keys, List<ColumnValue> values) {
        MapObjectInspector inspector = (MapObjectInspector) fieldInspector;
        ObjectInspector keyObjectInspector = inspector.getMapKeyObjectInspector();
        ObjectInspector valueObjectInspector = inspector.getMapValueObjectInspector();
        Map<?, ?> map = inspector.getMap(fieldData);
        for (Map.Entry<?, ?> kv : map.entrySet()) {
            HiveColumnValue cv0 = null;
            HiveColumnValue cv1 = null;
            if (kv.getKey() != null) {
                cv0 = new HiveColumnValue(keyObjectInspector, kv.getKey(), timeZone);
            }
            if (kv.getValue() != null) {
                cv1 = new HiveColumnValue(valueObjectInspector, kv.getValue(), timeZone);
            }
            keys.add(cv0);
            values.add(cv1);
        }
    }

    @Override
    public void unpackStruct(List<Integer> structFieldIndex, List<ColumnValue> values) {
        StructObjectInspector inspector = (StructObjectInspector) fieldInspector;
        List<? extends StructField> fields = inspector.getAllStructFieldRefs();
        for (int i = 0; i < structFieldIndex.size(); i++) {
            Integer idx = structFieldIndex.get(i);
            HiveColumnValue cv = null;
            if (idx != null) {
                StructField sf = fields.get(idx);
                Object o = inspector.getStructFieldData(fieldData, sf);
                if (o != null) {
                    cv = new HiveColumnValue(sf.getFieldObjectInspector(), o, timeZone);
                }
            }
            values.add(cv);
        }
    }

    @Override
    public byte getByte() {
        try {
            return PrimitiveObjectInspectorUtils.getByte(fieldData, primitiveInspector());
        } catch (NumberFormatException e) {
            return (byte) parseScientificNumberAsLong(e);
        }
    }

    private long parseScientificNumberAsLong(NumberFormatException original) {
        Object value = inspectObject();
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            BigDecimal decimal = new BigDecimal(value.toString());
            return decimal.longValue();
        } catch (NumberFormatException | NullPointerException | ArithmeticException e) {
            throw original;
        }
    }

    @Override
    public BigDecimal getDecimal() {
        return ((HiveDecimal) inspectObject()).bigDecimalValue();
    }

    @Override
    public LocalDate getDate() {
        return LocalDate.ofEpochDay((((DateObjectInspector) fieldInspector).getPrimitiveJavaObject(fieldData))
                .toEpochDay());
    }

    @Override
    public LocalDateTime getDateTime(ColumnType.TypeValue type) {
        if (fieldData instanceof Timestamp) {
            return ((Timestamp) fieldData).toLocalDateTime();
        } else if (fieldData instanceof TimestampWritableV2) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond((((TimestampObjectInspector) fieldInspector)
                    .getPrimitiveJavaObject(fieldData)).toEpochSecond()), ZoneId.of(timeZone));
        } else {
            org.apache.hadoop.hive.common.type.Timestamp timestamp =
                    ((TimestampObjectInspector) fieldInspector).getPrimitiveJavaObject(fieldData);
            return LocalDateTime.of(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(),
                    timestamp.getHours(), timestamp.getMinutes(), timestamp.getSeconds());
        }
    }
}
