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

/**
 * Represents a OperationPosition involved in an {@link Operation}
 *
 * @author William
 * @see Operation
 * @since 1.0
 */
public interface OperationPosition {

    /**
     * Get the X coordinate of the position
     *
     * @return the X coordinate
     * @since 1.0
     */
    double getX();

    /**
     * Get the Y coordinate of the position
     *
     * @return the Y coordinate
     * @since 1.0
     */
    double getY();

    /**
     * Get the Z coordinate of the position
     *
     * @return the Z coordinate
     * @since 1.0
     */
    double getZ();

    /**
     * Get the yaw angle of the position
     *
     * @return the yaw angle
     * @since 1.0
     */
    default float getYaw() {
        return 0f;
    }

    /**
     * Get the pitch angle of the position
     *
     * @return the pitch angle
     * @since 1.0
     */
    default float getPitch() {
        return 0f;
    }

    /**
     * Get the {@link OperationWorld world} the OperationPosition is in
     *
     * @return the world
     * @since 1.0
     */
    @NotNull
    OperationWorld getWorld();

    /**
     * Get the {@link OperationChunk chunk} the OperationPosition is in
     *
     * @return the chunk
     * @since 1.0
     */
    @NotNull
    OperationChunk getChunk();

    /**
     * Returns the distance between this OperationPosition and the given {@link OperationPosition position}
     *
     * @param other the OperationPosition to check
     * @return the distance between the two positions
     * @since 1.0
     */
    default double distanceBetween(@NotNull OperationPosition other) {
        return Math.sqrt(
                Math.pow(getX() - other.getX(), 2)
                        + Math.pow(getY() - other.getY(), 2)
                        + Math.pow(getZ() - other.getZ(), 2)
        );
    }

}
