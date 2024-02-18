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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a type of {@link OperationType type} of server event that is taking place at an {@link OperationPosition}
 *
 * @author William
 * @since 1.0
 */
@SuppressWarnings("unused")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Operation {

    private final OperationType type;
    private final OperationPosition position;
    @Nullable
    private final OperationUser user;
    @Nullable
    private final OperationUser victim;
    private boolean silent;

    @ApiStatus.Internal
    private Operation(@Nullable OperationUser user, @Nullable OperationUser victim,
                      @NotNull OperationType type, @NotNull OperationPosition position) {
        this.user = user;
        this.victim = victim;
        this.type = type;
        this.position = position;
        this.silent = getType().isSilent();
    }

    @ApiStatus.Internal
    private Operation(@Nullable OperationUser user, @NotNull OperationType type, @NotNull OperationPosition position) {
        this(user, null, type, position);
    }

    @ApiStatus.Internal
    private Operation(@NotNull OperationType type, @NotNull OperationPosition position) {
        this(null, null, type, position);
    }

    /**
     * Create a new {@code Operation} from a {@link OperationType} and {@link OperationPosition}
     *
     * @param user     the user who performed the operation
     * @param type     the type of operation
     * @param position the OperationPosition of the operation; where it took place
     * @return the new {@code Operation}
     * @since 1.0
     */
    @NotNull
    public static Operation of(@Nullable OperationUser user, @NotNull OperationType type,
                               @NotNull OperationPosition position) {
        return new Operation(user, type, position);
    }

    /**
     * Create a new {@code Operation} from a {@link OperationType} and {@link OperationPosition}
     *
     * @param type     the type of operation
     * @param position the OperationPosition of the operation; where it took place
     * @return the new {@code Operation}
     * @since 1.0
     */
    @NotNull
    public static Operation of(@NotNull OperationType type, @NotNull OperationPosition position) {
        return new Operation(type, position);
    }

    /**
     * Create a new {@code Operation} from a {@link OperationType}, {@link OperationPosition}, and {@link OperationUser}
     *
     * @param user     the user who performed the operation
     * @param type     the type of operation
     * @param position the OperationPosition of the operation; where it took place
     * @param silent   whether the operation should be silent; not displayed to the user if it is canceled
     * @return the new {@code Operation}
     * @since 1.0
     */
    @NotNull
    public static Operation of(@Nullable OperationUser user, @NotNull OperationType type,
                               @NotNull OperationPosition position, boolean silent) {
        final Operation operation = of(user, type, position);
        operation.setSilent(silent);
        return operation;
    }

    /**
     * Create a new {@code Operation} from a {@link OperationUser user}, {@link OperationUser victim},
     * {@link OperationType type}, and {@link OperationPosition position}
     *
     * @param user     the user who performed the operation
     * @param victim   the user who was affected by the operation
     * @param type     the type of operation
     * @param position the OperationPosition of the operation; where it took place
     * @return the new {@code Operation}
     * @since 1.0.3
     */
    @NotNull
    public static Operation of(@Nullable OperationUser user, @Nullable OperationUser victim,
                               @NotNull OperationType type, @NotNull OperationPosition position) {
        return new Operation(user, victim, type, position);
    }

    /**
     * Create a new {@code Operation} from a {@link OperationType}, {@link OperationPosition}, and {@link OperationUser}
     *
     * @param type     the type of operation
     * @param position the OperationPosition of the operation; where it took place
     * @param silent   whether the operation should be silent; not displayed to the user if it is canceled
     * @return the new {@code Operation}
     * @since 1.0
     */
    @NotNull
    public static Operation of(@NotNull OperationType type, @NotNull OperationPosition position, boolean silent) {
        final Operation operation = of(type, position);
        operation.setSilent(silent);
        return operation;
    }

    /**
     * Set whether the operation should be silent; not displayed to the user if it is canceled
     *
     * @param silent whether the operation should be silent
     * @since 1.0
     */
    private void setSilent(boolean silent) {
        this.silent = silent;
    }

    /**
     * Get the {@link OperationType} of operation
     *
     * @return the type of operation
     * @since 1.0
     */
    @NotNull
    public OperationType getType() {
        return type;
    }

    /**
     * Get whether the operation should be displayed to the user if it is canceled
     *
     * @return {@code true} if the operation should be displayed to the user if it is canceled; {@code false} otherwise
     * @since 1.0
     */
    public boolean isVerbose() {
        return !silent;
    }

    /**
     * Get the {@link OperationPosition} of the operation; where it took place
     *
     * @return the OperationPosition of the operation
     * @since 1.0
     */
    @NotNull
    public OperationPosition getOperationPosition() {
        return position;
    }

    /**
     * Get the {@link OperationUser} who performed the operation, if any
     *
     * @return the user who performed the operation, wrapped in an {@link Optional}
     * @since 1.0
     */
    public Optional<OperationUser> getUser() {
        return Optional.ofNullable(user);
    }

    /**
     * Get the {@link OperationUser} who was affected by the operation, if any
     *
     * @return the user who was affected by the operation, wrapped in an {@link Optional}
     * @since 1.0
     */
    public Optional<OperationUser> getVictim() {
        return Optional.ofNullable(victim);
    }

}
