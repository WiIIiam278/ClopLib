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

import net.william278.cloplib.operation.*;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a handler for processing {@link Operation}
 */
public interface Handler {

    /**
     * Returns whether an operation should be canceled
     *
     * @param operation the operation to check
     * @return whether the operation should be canceled
     * @since 1.0
     */
    boolean cancelOperation(@NotNull Operation operation);

    /**
     * Returns whether a movement should be canceled
     *
     * @param user the user who is moving
     * @param from the location the user is moving from
     * @param to   the location the user is moving to
     * @return whether the movement should be canceled
     * @since 1.0
     */
    boolean cancelMovement(@NotNull OperationUser user,
                           @NotNull OperationPosition from, @NotNull OperationPosition to);

    /**
     * Returns whether a nature operation should be canceled
     *
     * @param world     the world the operation is taking place in
     * @param position1 the first position
     * @param position2 the second position
     * @return whether the operation should be canceled
     * @since 1.0
     */
    boolean cancelNature(@NotNull OperationWorld world,
                         @NotNull OperationPosition position1, @NotNull OperationPosition position2);

    /**
     * Get the {@link TypeChecker} for this handler
     *
     * @return the {@link TypeChecker}
     */
    @NotNull
    TypeChecker getTypeChecker();

}
