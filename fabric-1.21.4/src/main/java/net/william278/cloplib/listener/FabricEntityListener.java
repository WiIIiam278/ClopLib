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

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

public interface FabricEntityListener extends FabricListener {

    // List of spawn reasons that ClopLib will handle. Don't handle skeleton traps unlike bukkit
    Set<SpawnReason> CHECKED_SPAWN_REASONS = Set.of(
            SpawnReason.NATURAL,
            SpawnReason.REINFORCEMENT,
            SpawnReason.PATROL
    );

    @NotNull
    default ActionResult onCreatureSpawn(World world, Entity entity, SpawnReason spawnReason) {
        if (!CHECKED_SPAWN_REASONS.contains(spawnReason)) {
            return ActionResult.PASS;
        }

        // Cancel mob spawning
        return getHandler().cancelOperation(Operation.of(
                isMonster(entity) ? OperationType.MONSTER_SPAWN : OperationType.PASSIVE_MOB_SPAWN,
                getPosition(entity.getPos(), world, entity.getYaw(), entity.getPitch()))
        ) ? ActionResult.FAIL : ActionResult.PASS;
    }

    @NotNull
    default List<BlockPos> onExplosionBreakBlocks(Explosion explosion, @Unmodifiable List<BlockPos> blockPos) {
        final List<BlockPos> newList = Lists.newArrayList();
        for (BlockPos pos : blockPos) {
            if (!getHandler().cancelOperation(Operation.of(
                    getPlayerSource(explosion.getCausingEntity()).map(this::getUser).orElse(null),
                    isMonster(explosion.getCausingEntity())
                            ? OperationType.MONSTER_DAMAGE_TERRAIN : OperationType.EXPLOSION_DAMAGE_TERRAIN,
                    getPosition(pos, explosion.getWorld())
            ))) {
                newList.add(pos);
            }
        }
        return newList;
    }

}
