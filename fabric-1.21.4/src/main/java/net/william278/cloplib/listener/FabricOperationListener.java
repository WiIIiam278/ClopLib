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

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.william278.cloplib.handler.Handler;
import net.william278.cloplib.handler.SpecialTypeChecker;
import net.william278.cloplib.handler.TypeChecker;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public abstract class FabricOperationListener implements OperationListener {

    private final Handler handler;
    private final TypeChecker checker;
    private final Map<InspectionTool, BiConsumer<OperationUser, OperationPosition>> inspectionToolHandlers;

    @SuppressWarnings("unused")
    public FabricOperationListener(@NotNull Handler handler, @NotNull ModContainer modContainer) {
        this(
                handler,
                SpecialTypeChecker.load(Objects.requireNonNull(
                        getSpecialTypes(modContainer),
                        "Failed to load special types file")
                ),
                Maps.newHashMap()
        );
    }

    @Nullable
    private static InputStream getSpecialTypes(@NotNull ModContainer modContainer) {
        return modContainer.findPath(SPECIAL_TYPES_FILE)
                .map(path -> {
                    try {
                        return Files.newInputStream(path);
                    } catch (IOException ignored) {
                        return null;
                    }
                })
                .orElse(FabricOperationListener.class.getClassLoader().getResourceAsStream(SPECIAL_TYPES_FILE));
    }

    /**
     * Returns the {@link OperationPosition} of a pos and world
     *
     * @param pos   the location
     * @param world the world
     * @return the OperationPosition of the location
     * @since 1.0.16
     */
    @NotNull
    public abstract OperationPosition getPosition(@NotNull Vec3d pos, float yaw, float pitch,
                                                  @NotNull net.minecraft.world.World world);

    /**
     * Returns the {@link OperationUser} of a {@link ServerPlayerEntity}
     *
     * @param player the player
     * @return the OperationUser of the player
     * @since 1.0.16
     */
    @NotNull
    public abstract OperationUser getUser(@NotNull ServerPlayerEntity player);

    /**
     * Set the callback for when a player inspects a block while holding something
     *
     * @param tool     the tool the user must be holding to trigger the callback
     * @param callback the callback to set
     * @since 1.0.16
     */
    @Override
    public void setInspectorCallback(@NotNull InspectionTool tool,
                                     @NotNull BiConsumer<OperationUser, OperationPosition> callback) {
        inspectionToolHandlers.put(tool, callback);
    }

}
