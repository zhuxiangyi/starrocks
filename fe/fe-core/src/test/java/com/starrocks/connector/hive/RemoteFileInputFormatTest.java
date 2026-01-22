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

import com.starrocks.thrift.THdfsFileFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RemoteFileInputFormatTest {
    @Test
    public void testParquetFormat() {
        Assertions.assertSame(RemoteFileInputFormat.PARQUET, RemoteFileInputFormat
                .fromHdfsInputFormatClass("org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat"));
        Assertions.assertSame(RemoteFileInputFormat.ORC,
                RemoteFileInputFormat.fromHdfsInputFormatClass("org.apache.hadoop.hive.ql.io.orc.OrcInputFormat"));
    }

    @Test
    public void testUnknownFormat() {
        RemoteFileInputFormat format = RemoteFileInputFormat.UNKNOWN;
        Assertions.assertEquals(THdfsFileFormat.UNKNOWN, format.toThrift());
    }

    @Test
    public void testTextFormats() {
        Assertions.assertTrue(RemoteFileInputFormat.TEXTFILE.isTextFormat());
        Assertions.assertTrue(RemoteFileInputFormat.CSVTEXT.isTextFormat());
        Assertions.assertTrue(RemoteFileInputFormat.JSONTEXT.isTextFormat());
        Assertions.assertTrue(RemoteFileInputFormat.JSON3TEXT.isTextFormat());
        Assertions.assertFalse(RemoteFileInputFormat.PARQUET.isTextFormat());
        Assertions.assertFalse(RemoteFileInputFormat.ORC.isTextFormat());
        Assertions.assertFalse(RemoteFileInputFormat.AVRO.isTextFormat());
        Assertions.assertFalse(RemoteFileInputFormat.RCFILE.isTextFormat());
        Assertions.assertFalse(RemoteFileInputFormat.SEQUENCE.isTextFormat());
        Assertions.assertFalse(RemoteFileInputFormat.UNKNOWN.isTextFormat());
    }

    @Test
    public void testJsonTextFormat() {
        RemoteFileInputFormat format = RemoteFileInputFormat.JSONTEXT;
        Assertions.assertEquals(THdfsFileFormat.JSON_TEXT, format.toThrift());
        Assertions.assertTrue(format.isTextFormat());
    }

    @Test
    public void testJson3TextFormat() {
        RemoteFileInputFormat format = RemoteFileInputFormat.JSON3TEXT;
        Assertions.assertEquals(THdfsFileFormat.JSON3_TEXT, format.toThrift());
        Assertions.assertTrue(format.isTextFormat());
    }

    @Test
    public void testCsvTextFormat() {
        RemoteFileInputFormat format = RemoteFileInputFormat.CSVTEXT;
        Assertions.assertEquals(THdfsFileFormat.CSV_TEXT, format.toThrift());
        Assertions.assertTrue(format.isTextFormat());
    }

    @Test
    public void testAllFormatsToThrift() {
        Assertions.assertEquals(THdfsFileFormat.PARQUET, RemoteFileInputFormat.PARQUET.toThrift());
        Assertions.assertEquals(THdfsFileFormat.ORC, RemoteFileInputFormat.ORC.toThrift());
        Assertions.assertEquals(THdfsFileFormat.TEXT, RemoteFileInputFormat.TEXTFILE.toThrift());
        Assertions.assertEquals(THdfsFileFormat.AVRO, RemoteFileInputFormat.AVRO.toThrift());
        Assertions.assertEquals(THdfsFileFormat.RC_FILE, RemoteFileInputFormat.RCFILE.toThrift());
        Assertions.assertEquals(THdfsFileFormat.SEQUENCE_FILE, RemoteFileInputFormat.SEQUENCE.toThrift());
        Assertions.assertEquals(THdfsFileFormat.JSON_TEXT, RemoteFileInputFormat.JSONTEXT.toThrift());
        Assertions.assertEquals(THdfsFileFormat.JSON3_TEXT, RemoteFileInputFormat.JSON3TEXT.toThrift());
        Assertions.assertEquals(THdfsFileFormat.CSV_TEXT, RemoteFileInputFormat.CSVTEXT.toThrift());
        Assertions.assertEquals(THdfsFileFormat.UNKNOWN, RemoteFileInputFormat.UNKNOWN.toThrift());
    }
}
