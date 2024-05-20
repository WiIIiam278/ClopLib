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

import org.jetbrains.annotations.NotNull;

public interface TypeChecker {

    /**
     * Returns whether a block constitutes a farm material
     *
     * @param materialId the material ID of the block
     * @return whether the block constitutes a farm material
     * @since 1.0
     */
    boolean isFarmMaterial(@NotNull String materialId);

    /**
     * Returns whether a mob is a griefing mob
     *
     * @param mobId the mob ID
     * @return whether the mob is a griefing mob
     * @since 1.0
     */
    boolean isGriefingMob(@NotNull String mobId);

    /**
     * Returns whether a material is pressure-sensitive
     *
     * @param materialId the material ID
     * @return whether the material is pressure sensitive
     * @since 1.0
     */
    boolean isPressureSensitiveMaterial(@NotNull String materialId);

}
