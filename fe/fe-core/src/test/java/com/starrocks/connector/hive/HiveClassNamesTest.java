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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HiveClassNamesTest {
    @Test
    public void testJsonSerdeClassNames() {
        Assertions.assertEquals("org.apache.hive.hcatalog.data.JsonSerDe", HiveClassNames.TEXT_JSON_SERDE_CLASS);
        Assertions.assertEquals("org.openx.data.jsonserde.JsonSerDe", HiveClassNames.TEXT_JSON3_SERDE_CLASS);
        Assertions.assertEquals("org.openx.data.jsonserde.JsonSerDe", HiveClassNames.OPENXJSON_SERDE_CLASS);
        // TEXT_JSON3_SERDE_CLASS and OPENXJSON_SERDE_CLASS should be the same
        Assertions.assertEquals(HiveClassNames.TEXT_JSON3_SERDE_CLASS, HiveClassNames.OPENXJSON_SERDE_CLASS);
    }

    @Test
    public void testCsvSerdeClassName() {
        Assertions.assertEquals("org.apache.hadoop.hive.serde2.OpenCSVSerde", HiveClassNames.TEXT_CSV_SERDE_CLASS);
    }

    @Test
    public void testAllSerdeClassNames() {
        // Verify all SerDe class names are non-null and non-empty
        Assertions.assertNotNull(HiveClassNames.TEXT_JSON_SERDE_CLASS);
        Assertions.assertFalse(HiveClassNames.TEXT_JSON_SERDE_CLASS.isEmpty());
        Assertions.assertNotNull(HiveClassNames.TEXT_JSON3_SERDE_CLASS);
        Assertions.assertFalse(HiveClassNames.TEXT_JSON3_SERDE_CLASS.isEmpty());
        Assertions.assertNotNull(HiveClassNames.TEXT_CSV_SERDE_CLASS);
        Assertions.assertFalse(HiveClassNames.TEXT_CSV_SERDE_CLASS.isEmpty());
        Assertions.assertNotNull(HiveClassNames.OPENXJSON_SERDE_CLASS);
        Assertions.assertFalse(HiveClassNames.OPENXJSON_SERDE_CLASS.isEmpty());
    }

    @Test
    public void testSerdeClassNamesUniqueness() {
        // Verify different SerDe classes have different names
        Assertions.assertNotEquals(HiveClassNames.TEXT_JSON_SERDE_CLASS, HiveClassNames.TEXT_JSON3_SERDE_CLASS);
        Assertions.assertNotEquals(HiveClassNames.TEXT_JSON_SERDE_CLASS, HiveClassNames.TEXT_CSV_SERDE_CLASS);
        Assertions.assertNotEquals(HiveClassNames.TEXT_JSON3_SERDE_CLASS, HiveClassNames.TEXT_CSV_SERDE_CLASS);
        Assertions.assertNotEquals(HiveClassNames.TEXT_JSON_SERDE_CLASS, HiveClassNames.LAZY_SIMPLE_SERDE_CLASS);
        Assertions.assertNotEquals(HiveClassNames.TEXT_CSV_SERDE_CLASS, HiveClassNames.LAZY_SIMPLE_SERDE_CLASS);
    }
}

