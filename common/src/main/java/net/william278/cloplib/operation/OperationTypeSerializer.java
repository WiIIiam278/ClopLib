/*
 * This file is part of ClopLib, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.cloplib.operation;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class OperationTypeSerializer extends TypeAdapter<OperationType> {

    @Override
    public void write(JsonWriter out, OperationType value) throws IOException {
        if (value == null || value.getKey() == null) {
            out.nullValue();
            return;
        }
        out.value(value.getKey().asString());
    }

    @Override
    public OperationType read(JsonReader in) throws IOException {
        final String next = in.nextString();
        if (next == null) {
            return null;
        }
        return OperationType.getOrCreate(next);
    }

}
