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
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class ExplosionEvents {

    @NotNull
    public static final Event<BeforeBlocksBrokenCallback> BEFORE_BLOCKS_BROKEN = EventFactory.createArrayBacked(
            BeforeBlocksBrokenCallback.class,
            (callbacks) -> (explosion, blocks) -> {
                for (BeforeBlocksBrokenCallback listener : callbacks) {
                    blocks = listener.explode(explosion, blocks);
                }
                return blocks;
            }
    );

    @NotNull
    public static final Event<BeforeDamageEntity> BEFORE_DAMAGE_ENTITY = EventFactory.createArrayBacked(
            BeforeDamageEntity.class,
            (callbacks) -> (explosion, entity) -> {
                for (BeforeDamageEntity listener : callbacks) {
                    final ActionResult result = listener.damage(explosion, entity);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface BeforeDamageEntity {

        @NotNull
        ActionResult damage(Explosion explosion, Entity entity);

    }

    @FunctionalInterface
    public interface BeforeBlocksBrokenCallback {

        @NotNull
        List<BlockPos> explode(Explosion explosion, @Unmodifiable List<BlockPos> blocksToDestroy);

    }

}