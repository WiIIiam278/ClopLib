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

import net.william278.cloplib.operation.OperationChunk;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Handler} that deals with regions that use {@link OperationChunk}s
 *
 * @since 1.0
 */
public interface ChunkHandler extends Handler {

    @Override
    @ApiStatus.Internal
    default boolean cancelMovement(@NotNull OperationUser user, @NotNull OperationPosition from,
                                   @NotNull OperationPosition to) {
        return cancelChunkChange(user, from.getChunk(), to.getChunk());
    }

    /**
     * Cancel a {@link OperationUser}'s movement between two {@link OperationChunk}s
     *
     * @param user the {@link OperationUser} who is moving
     * @param from the {@link OperationChunk} the {@link OperationUser} is moving from
     * @param to   the {@link OperationChunk} the {@link OperationUser} is moving to
     * @return whether the movement should be canceled
     */
    boolean cancelChunkChange(@NotNull OperationUser user, @NotNull OperationChunk from, @NotNull OperationChunk to);


}
