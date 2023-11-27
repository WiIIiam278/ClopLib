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

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a world involved in an {@link Operation}
 *
 * @author William
 * @see Operation
 * @since 1.0
 */
public interface OperationWorld {

    // Default environment name for the overworld
    String OVERWORLD_ENVIRONMENT = "NORMAL";

    /**
     * Get the name of the world
     *
     * @return the name of the world
     * @since 1.0
     */
    @NotNull
    String getName();

    /**
     * Get the {@link UUID} of the world
     *
     * @return the UUID of the world
     * @since 1.0
     */
    @NotNull
    UUID getUuid();

    /**
     * Get the environment of the world
     *
     * @return the environment of the world
     * @since 1.0
     */
    @NotNull
    default String getEnvironment() {
        return OVERWORLD_ENVIRONMENT;
    }

}
