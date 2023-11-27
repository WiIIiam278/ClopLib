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

package net.william278.cloplib.handler;


import net.william278.annotaml.Annotaml;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
@YamlFile(header = "Internal resource config containing lists of special block and entity types")
public final class SpecialTypeChecker implements TypeChecker {

    @YamlKey("farm_blocks")
    private List<String> farmBlocks;

    @YamlKey("pressure_sensitive_blocks")
    private List<String> pressureSensitiveBlocks;

    @YamlKey("griefing_mobs")
    private List<String> griefingMobs;

    @SuppressWarnings("unused")
    private SpecialTypeChecker() {
    }

    @NotNull
    public static SpecialTypeChecker load(@NotNull InputStream data) throws IllegalArgumentException {
        try {
            return Annotaml.create(SpecialTypeChecker.class, data).get();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IOException e) {
            throw new IllegalArgumentException("Failed to load special types from data stream", e);
        }
    }

    @Override
    public boolean isFarmMaterial(@NotNull String material) {
        return farmBlocks.contains(formatKey(material));
    }

    @Override
    public boolean isPressureSensitiveMaterial(@NotNull String material) {
        return pressureSensitiveBlocks.contains(formatKey(material));
    }

    @Override
    public boolean isGriefingMob(@NotNull String mob) {
        return griefingMobs.contains(formatKey(mob));
    }

    @NotNull
    private String formatKey(@NotNull String key) {
        return key.trim().toLowerCase().replace("minecraft:", "");
    }

}
