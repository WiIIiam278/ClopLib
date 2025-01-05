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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.william278.cloplib.handler.Handler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;

/**
 * Utility interface for working with {@link OperationType}s, letting you register these and access the handler for
 * performing checks against them.
 */
@SuppressWarnings("unused")
public interface OperationTypeRegistry {

    /**
     * Get the set of registered {@link OperationType}s
     *
     * @return the set of {@link OperationType}s
     * @since 2.0
     */
    @Unmodifiable
    @NotNull
    default Collection<OperationType> getRegisteredOperationTypes() {
        return OperationType.getRegistered();
    }

    /**
     * Get an operation type from the given key
     *
     * @param key The key of the operation type
     * @return The operation type, or an empty optional if not found
     * @since 2.0
     */
    default Optional<OperationType> getOperationType(@NotNull Key key) {
        return OperationType.get(key);
    }

    /**
     * Get an operation type from the given key
     *
     * @param key The stringified key of the operation type
     * @return The operation type, or an empty optional if not found
     * @since 2.0
     */
    default Optional<OperationType> getOperationType(@NotNull @KeyPattern String key) {
        return OperationType.get(key);
    }

    /**
     * Get whether an Operation Type has been registered
     *
     * @param key The key of the operation type
     * @return {@code true}  if the type was registered
     * @since 2.0
     */
    default boolean isRegisteredOperationType(@NotNull Key key) {
        return OperationType.isRegistered(key);
    }

    /**
     * Get whether an Operation Type has been registered
     *
     * @param key The stringified key of the operation type
     * @return {@code true}  if the type was registered
     * @since 2.0
     */
    default boolean isRegisteredOperationType(@NotNull String key) {
        return OperationType.isRegistered(key);
    }

    /**
     * Create an operation type
     *
     * @param key    key to create
     * @param silent whether the type should be silent. The silent flag indicates whether players should be notified
     *               when an operation of that type was cancelled by default.
     * @return the registered operation type
     * @since 2.0
     */
    @NotNull
    default OperationType createOperationType(@NotNull Key key, boolean silent) {
        return OperationType.create(key, silent);
    }

    /**
     * Create an operation type (defaults to non-silent)
     *
     * @param key key to create
     * @return the registered operation type
     * @since 2.0
     */
    @NotNull
    default OperationType createOperationType(@NotNull Key key) {
        return OperationType.create(key, false);
    }

    /**
     * Register an operation type
     *
     * @param type the type to register
     * @since 2.0
     */
    default void registerOperationType(@NotNull OperationType type) {
        OperationType.register(type);
    }

    /**
     * Create and register an operation type
     *
     * @param key    key to create
     * @param silent whether the type should be silent. The silent flag indicates whether players should be notified
     *               when an operation of that type was cancelled by default.
     * @since 2.0
     */
    default void registerOperationType(@NotNull Key key, boolean silent) {
        registerOperationType(createOperationType(key, silent));
    }

    /**
     * Create and register an operation type (defaults to non-silent)
     *
     * @param key key to create
     * @since 2.0
     */
    default void registerOperationType(@NotNull Key key) {
        registerOperationType(createOperationType(key));
    }

    /**
     * Unregister an operation type
     *
     * @param key the type to unregister
     * @since 2.0
     */
    default void unregisterOperationType(@NotNull Key key) {
        OperationType.unregister(key);
    }

    /**
     * Get the platform handler which is implemented by the claim plugin to check whether operations can be performed.
     *
     * @since 2.0
     */
    @NotNull
    Handler getHandler();

}
