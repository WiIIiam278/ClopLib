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

package net.william278.cloplib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class PistonEvents {

    @NotNull
    public static final Event<BeforeActuationCallback> BEFORE_ACTUATION = EventFactory.createArrayBacked(
            BeforeActuationCallback.class,
            (callbacks) -> (world, pistonPos, pistonDirection, affectedBlocks) -> {
                for (BeforeActuationCallback listener : callbacks) {
                    final ActionResult result = listener.actuate(world, pistonPos, pistonDirection, affectedBlocks);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface BeforeActuationCallback {

        @NotNull
        ActionResult actuate(World world, BlockPos pistonPos, Direction pistonDirection,
                             @Unmodifiable List<BlockPos> affectedBlocks);

    }

}
