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
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class SpawnEvents {

    @NotNull
    public static final Event<BeforeMobSpawn> BEFORE_MOB_SPAWN = EventFactory.createArrayBacked(
            BeforeMobSpawn.class,
            (callbacks) -> (world, entity, reason) -> {
                for (BeforeMobSpawn listener : callbacks) {
                    final ActionResult result = listener.spawn(world, entity, reason);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface BeforeMobSpawn {

        @NotNull
        ActionResult spawn(World world, Entity entity, SpawnReason reason);

    }
}
