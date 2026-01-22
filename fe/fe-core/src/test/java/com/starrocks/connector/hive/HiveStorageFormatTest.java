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

package com.starrocks.connector.hive;

import com.google.common.collect.ImmutableMap;
import com.starrocks.common.ExceptionChecker;
import com.starrocks.connector.exception.StarRocksConnectorException;
import org.junit.jupiter.api.Test;

import com.starrocks.thrift.THdfsFileFormat;
import org.junit.jupiter.api.Assertions;

public class HiveStorageFormatTest {
    @Test
    public void checkHiveStorageFormat() {
        ExceptionChecker.expectThrowsWithMsg(StarRocksConnectorException.class,
                "Please use 'file_format' instead of 'format' in the table properties",
                () -> HiveStorageFormat.check(ImmutableMap.of("format", "csv")));

        HiveStorageFormat.check(ImmutableMap.of("file_format", "textfile"));
    }

    @Test
    public void testJsonTextStorageFormat() {
        HiveStorageFormat format = HiveStorageFormat.JSONTEXT;
        Assertions.assertEquals(HiveClassNames.TEXT_JSON_SERDE_CLASS, format.getSerde());
        Assertions.assertEquals(HiveClassNames.TEXT_INPUT_FORMAT_CLASS, format.getInputFormat());
        Assertions.assertEquals(HiveClassNames.HIVE_IGNORE_KEY_OUTPUT_FORMAT_CLASS, format.getOutputFormat());
        Assertions.assertEquals(THdfsFileFormat.TEXT, format.toFileFormatThrift());
        Assertions.assertTrue(format.isTextFormat());
    }

    @Test
    public void testJson3TextStorageFormat() {
        HiveStorageFormat format = HiveStorageFormat.JSON3TEXT;
        Assertions.assertEquals(HiveClassNames.TEXT_JSON3_SERDE_CLASS, format.getSerde());
        Assertions.assertEquals(HiveClassNames.TEXT_INPUT_FORMAT_CLASS, format.getInputFormat());
        Assertions.assertEquals(HiveClassNames.HIVE_IGNORE_KEY_OUTPUT_FORMAT_CLASS, format.getOutputFormat());
        Assertions.assertEquals(THdfsFileFormat.TEXT, format.toFileFormatThrift());
        Assertions.assertTrue(format.isTextFormat());
    }

    @Test
    public void testCsvTextStorageFormat() {
        HiveStorageFormat format = HiveStorageFormat.CSVTEXT;
        Assertions.assertEquals(HiveClassNames.TEXT_CSV_SERDE_CLASS, format.getSerde());
        Assertions.assertEquals(HiveClassNames.TEXT_INPUT_FORMAT_CLASS, format.getInputFormat());
        Assertions.assertEquals(HiveClassNames.HIVE_IGNORE_KEY_OUTPUT_FORMAT_CLASS, format.getOutputFormat());
        Assertions.assertEquals(THdfsFileFormat.TEXT, format.toFileFormatThrift());
        Assertions.assertTrue(format.isTextFormat());
    }

    @Test
    public void testGetStorageFormatWithSerializationLib() {
        // Test JSON format
        HiveStorageFormat format = HiveStorageFormat.get("textfile", HiveClassNames.TEXT_JSON_SERDE_CLASS);
        Assertions.assertEquals(HiveStorageFormat.JSONTEXT, format);

        // Test JSON3 format
        format = HiveStorageFormat.get("textfile", HiveClassNames.TEXT_JSON3_SERDE_CLASS);
        Assertions.assertEquals(HiveStorageFormat.JSON3TEXT, format);

        // Test CSV format
        format = HiveStorageFormat.get("textfile", HiveClassNames.TEXT_CSV_SERDE_CLASS);
        Assertions.assertEquals(HiveStorageFormat.CSVTEXT, format);

        // Test regular textfile
        format = HiveStorageFormat.get("textfile", HiveClassNames.LAZY_SIMPLE_SERDE_CLASS);
        Assertions.assertEquals(HiveStorageFormat.TEXTFILE, format);

        // Test unsupported format
        format = HiveStorageFormat.get("textfile", "unknown.serde.class");
        Assertions.assertEquals(HiveStorageFormat.UNSUPPORTED, format);
    }

    @Test
    public void testGetStorageFormatByName() {
        Assertions.assertEquals(HiveStorageFormat.JSONTEXT, HiveStorageFormat.get("jsontext"));
        Assertions.assertEquals(HiveStorageFormat.JSON3TEXT, HiveStorageFormat.get("json3text"));
        Assertions.assertEquals(HiveStorageFormat.CSVTEXT, HiveStorageFormat.get("csvtext"));
        Assertions.assertEquals(HiveStorageFormat.TEXTFILE, HiveStorageFormat.get("textfile"));
        Assertions.assertEquals(HiveStorageFormat.PARQUET, HiveStorageFormat.get("parquet"));
        Assertions.assertEquals(HiveStorageFormat.ORC, HiveStorageFormat.get("orc"));
        Assertions.assertEquals(HiveStorageFormat.UNSUPPORTED, HiveStorageFormat.get("unknown"));
    }

    @Test
    public void testToFileFormatThrift() {
        Assertions.assertEquals(THdfsFileFormat.PARQUET, HiveStorageFormat.PARQUET.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.ORC, HiveStorageFormat.ORC.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.TEXT, HiveStorageFormat.TEXTFILE.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.TEXT, HiveStorageFormat.JSONTEXT.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.TEXT, HiveStorageFormat.JSON3TEXT.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.TEXT, HiveStorageFormat.CSVTEXT.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.TEXT, HiveStorageFormat.OPENXJSON.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.AVRO, HiveStorageFormat.AVRO.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.RC_FILE, HiveStorageFormat.RCBINARY.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.RC_FILE, HiveStorageFormat.RCTEXT.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.SEQUENCE_FILE, HiveStorageFormat.SEQUENCE.toFileFormatThrift());
        Assertions.assertEquals(THdfsFileFormat.UNKNOWN, HiveStorageFormat.UNSUPPORTED.toFileFormatThrift());
    }
}
