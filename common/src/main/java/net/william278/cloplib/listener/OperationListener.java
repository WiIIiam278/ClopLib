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

package net.william278.cloplib.listener;

import net.william278.cloplib.handler.Handler;
import net.william278.cloplib.handler.TypeChecker;
import net.william278.cloplib.operation.Operation;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a listener for handling {@link Operation operations} that take place on the server
 *
 * @see Operation
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface OperationListener extends InspectorCallbackProvider {

    /**
     * Get the {@link Handler} for this listener
     *
     * @return the Handler
     * @since 1.0
     */
    @NotNull
    Handler getHandler();

    /**
     * Get the {@link TypeChecker} for this listener
     *
     * @return the TypeChecker
     * @since 1.0
     */
    @NotNull
    TypeChecker getChecker();

}
