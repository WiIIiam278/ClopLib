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

import lombok.Builder;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * An interface for providing callbacks for inspection actions.
 *
 * <p>
 * This should be used for inspecting chunks with tools such as sticks to view borders,
 * as well as for claim creation actions such as with a golden shovel
 *
 * @since 1.0
 */
@SuppressWarnings("unused")
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
     * Get handler callbacks for inspection actions, as a map of {@link InspectionTool} to {@link BiConsumer}s
     *
     * @return the handlers
     * @since 1.0.5
     */
    @NotNull
    Map<InspectionTool, BiConsumer<OperationUser, OperationPosition>> getInspectionToolHandlers();

    /**
     * Get handler callbacks for inspection actions, as a map of material key strings to {@link BiConsumer}s
     *
     * @return the handlers
     * @since 1.0
     * @deprecated use {@link #getInspectionToolHandlers()} instead
     */
    @Deprecated(forRemoval = true, since = "1.0.5")
    @Unmodifiable
    @NotNull
    default Map<String, BiConsumer<OperationUser, OperationPosition>> getInspectionHandlers() {
        return getInspectionToolHandlers().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().material(), Map.Entry::getValue));
    }

    /**
     * Sets a callback for use handling inspection actions
     *
     * @param tool     the tool to set the callback for
     * @param callback the callback to set
     * @since 1.0.5
     */
    void setInspectorCallback(@NotNull InspectionTool tool,
                              @NotNull BiConsumer<OperationUser, OperationPosition> callback);

    /**
     * Sets a callback for use handling inspection actions
     *
     * @param material the material to set the callback for
     * @param callback the callback to set
     * @since 1.0
     */
    default void setInspectorCallback(@NotNull String material,
                                      @NotNull BiConsumer<OperationUser, OperationPosition> callback) {
        setInspectorCallback(InspectionTool.builder().material(material).build(), callback);
    }

    /**
     * Record for a tool used for inspection, consisting of a material key string and optional custom model data
     *
     * @param material           the material key string; may or may not be namespaced
     *                           (e.g. "minecraft:stick" or "golden_shovel")
     * @param useCustomModelData whether the tool uses custom model data
     * @param customModelData    the custom model data value (an integer)
     * @since 1.0.5
     */
    @Builder
    record InspectionTool(@NotNull String material, boolean useCustomModelData, int customModelData) {

        public InspectionTool(@NotNull String material, boolean useCustomModelData, int customModelData) {
            this.material = material.startsWith("minecraft:") ? material.substring(10) : material;
            this.useCustomModelData = useCustomModelData;
            this.customModelData = useCustomModelData ? customModelData : -1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            final InspectionTool that = (InspectionTool) obj;
            return material.equals(that.material) && (!useCustomModelData || customModelData == that.customModelData);
        }

    }

}
