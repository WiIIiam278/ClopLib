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

import net.william278.cloplib.operation.OperationPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * An interface for providing callbacks for inspection actions.
 *
 * <p>
 * This should be used for inspecting chunks with tools such as sticks to view borders,
 * as well as for claim creation actions such as with a golden shovel
 * @since 1.0
 */
public interface InspectorCallbackProvider {

    /**
     * Get the distance that inspectors check for
     *
     * @return the distance
     * @since 1.0
     */
    int getInspectionDistance();

    /**
     * Set the distance that inspectors check for
     *
     * @param distance the distance to set
     * @since 1.0
     */
    void setInspectionDistance(int distance);

    /**
     * Get handler callbacks for inspection actions
     *
     * @return the handlers
     * @since 1.0
     */
    @NotNull
    Map<String, Consumer<OperationPosition>> getInspectionHandlers();

    /**
     * Sets a callback for use handling inspection actions
     *
     * @param material the material to set the callback for
     * @param callback the callback to set
     * @since 1.0
     */
    void setInspectorCallback(@NotNull String material, @NotNull Consumer<OperationPosition> callback);

}
