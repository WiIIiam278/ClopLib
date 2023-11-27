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
 * Represents a world chunk involved in an {@link Operation}
 *
 * @author William
 * @see Operation
 * @since 1.0
 */
public interface OperationChunk {

    /**
     * Get the X coordinate of the chunk
     *
     * @return the X coordinate
     * @since 1.0
     */
    int getX();

    /**
     * Get the Z coordinate of the chunk
     *
     * @return the Z coordinate
     * @since 1.0
     */
    int getZ();

    /**
     * Returns the distance between this chunk and the given {@link OperationChunk chunk}
     *
     * @param chunk the chunk to check
     * @return the distance between the two chunks
     * @since 1.0
     */
    default int distanceBetween(@NotNull OperationChunk chunk) {
        return Math.abs(getX() - chunk.getX()) + Math.abs(getZ() - chunk.getZ());
    }

    /**
     * Returns whether the given {@link OperationPosition position} is contained within this chunk
     *
     * @param position the OperationPosition to check
     * @return whether the OperationPosition is contained within this chunk
     * @since 1.0
     */
    default boolean contains(@NotNull OperationPosition position) {
        return position.getX() >= getX() * 16 && position.getX() < (getX() + 1) * 16
                && position.getZ() >= getZ() * 16 && position.getZ() < (getZ() + 1) * 16;
    }

}
