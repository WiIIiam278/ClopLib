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

import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public interface BukkitEntityListener extends BukkitListener {

    // List of special reasons that are ignored when handling monster spawn checks
    Set<CreatureSpawnEvent.SpawnReason> IGNORED_SPAWN_REASONS = Set.of(
            CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
            CreatureSpawnEvent.SpawnReason.COMMAND,
            CreatureSpawnEvent.SpawnReason.CUSTOM,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
            CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
            CreatureSpawnEvent.SpawnReason.BUILD_WITHER
    );

    @EventHandler(ignoreCancelled = true)
    default void onBlockExplosion(@NotNull BlockExplodeEvent e) {
        final HashSet<Block> blocksToRemove = new HashSet<>();
        for (Block block : e.blockList()) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.EXPLOSION_DAMAGE_TERRAIN,
                    getPosition(block.getLocation())
            ))) {
                blocksToRemove.add(block);
            }
        }
        for (Block block : blocksToRemove) {
            e.blockList().remove(block);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onEntityExplode(@NotNull EntityExplodeEvent e) {
        final HashSet<Block> blocksToRemove = new HashSet<>();
        for (Block block : e.blockList()) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.MONSTER_DAMAGE_TERRAIN,
                    getPosition(block.getLocation())
            ))) {
                blocksToRemove.add(block);
            }
        }
        for (Block block : blocksToRemove) {
            e.blockList().remove(block);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onEntityChangeBlock(@NotNull EntityChangeBlockEvent e) {
        if (getChecker().isGriefingMob(e.getEntity().getType().getKey().toString())) {
            final Block block = e.getBlock();
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.MONSTER_DAMAGE_TERRAIN,
                    getPosition(block.getLocation())
            ))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onMobSpawn(@NotNull CreatureSpawnEvent e) {
        // Check against ignored spawn reasons
        final CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();
        if (IGNORED_SPAWN_REASONS.contains(reason)) {
            return;
        }

        // Cancel passive mob spawning
        final Entity entity = e.getEntity();
        if (!(entity instanceof Monster)) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.PASSIVE_MOB_SPAWN,
                    getPosition(entity.getLocation())
            ))) {
                e.setCancelled(true);
            }
            return;
        }

        // Cancel hostile spawning
        final Location location = e.getLocation();
        if (getHandler().cancelOperation(Operation.of(
                OperationType.MONSTER_SPAWN,
                getPosition(location)
        ))) {
            e.setCancelled(true);
        }
    }

}
