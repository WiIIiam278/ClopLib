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
 * Represents a user involved in an {@link Operation}
 *
 * @author William
 * @see Operation
 * @since 1.0
 */
public interface OperationUser {

    /**
     * Get the name of the user
     *
     * @return the name of the user
     * @since 1.0
     */
    @NotNull
    String getName();

    /**
     * Get the {@link UUID} of the user
     *
     * @return the UUID of the user
     * @since 1.0
     */
    @NotNull
    UUID getUuid();

    /**
     * Get the {@link OperationPosition position} of the user
     *
     * @return the OperationPosition of the user
     * @since 1.0
     */
    @NotNull
    OperationPosition getPosition();

    /**
     * Get the {@link OperationWorld world} the user is in
     *
     * @return the world the user is in
     * @since 1.0
     */
    @NotNull
    default OperationWorld getWorld() {
        return getPosition().getWorld();
    }

}
