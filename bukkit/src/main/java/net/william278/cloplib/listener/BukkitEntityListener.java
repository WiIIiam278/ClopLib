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

import com.google.common.collect.Sets;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationType;
import net.william278.cloplib.operation.OperationUser;
import org.bukkit.block.Block;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public interface BukkitEntityListener extends BukkitListener {

    // List of spawn reasons that the CreatureSpawnEvent will handle
    Set<SpawnReason> CHECKED_SPAWN_REASONS = Set.of(
            SpawnReason.NATURAL,
            SpawnReason.TRAP,
            SpawnReason.REINFORCEMENTS,
            SpawnReason.PATROL
    );

    @EventHandler(ignoreCancelled = true)
    default void onBlockExplosion(@NotNull BlockExplodeEvent e) {
        final HashSet<Block> blocksToRemove = Sets.newHashSet();
        for (Block block : e.blockList()) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.EXPLOSION_DAMAGE_TERRAIN,
                    getPosition(block.getLocation())
            ))) {
                blocksToRemove.add(block);
            }
        }
        // Don't destroy protected blocks (but allow the explosion to continue)
        blocksToRemove.forEach(e.blockList()::remove);
    }

    @EventHandler(ignoreCancelled = true)
    default void onEntityExplode(@NotNull EntityExplodeEvent e) {
        final HashSet<Block> blocksToRemove = Sets.newHashSet();
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
        final OperationPosition position = getPosition(e.getBlock().getLocation());
        final Optional<OperationUser> user = getPlayerSource(e.getEntity()).map(this::getUser);

        // Handle projectiles breaking blocks (chorus fruit, decorated pots)
        if (user.isPresent()) {
            if (getHandler().cancelOperation(Operation.of(
                    user.get(),
                    OperationType.BLOCK_BREAK,
                    position
            ))) {
                e.setCancelled(true);
            }
            return;
        }

        // Handle mob griefing (Endermen, etc.)
        if (getChecker().isGriefingMob(e.getEntity().getType().getKey().toString())) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.MONSTER_DAMAGE_TERRAIN,
                    position
            ))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onEggThrow(@NotNull PlayerEggThrowEvent e) {
        final OperationPosition position = getPosition(e.getEgg().getLocation());
        final Optional<OperationUser> user = getPlayerSource(e.getPlayer()).map(this::getUser);

        // Handle the chance of entities spawning from a thrown egg projectile
        if (user.isPresent() && getHandler().cancelOperation(Operation.of(
                user.get(),
                OperationType.USE_SPAWN_EGG,
                position
        ))) {
            e.setHatching(false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onMobSpawn(@NotNull CreatureSpawnEvent e) {
        // This event fires *a lot*, so we only check against important reason for performance
        if (!CHECKED_SPAWN_REASONS.contains(e.getSpawnReason())) {
            return;
        }

        // Cancel mob spawning
        if (getHandler().cancelOperation(Operation.of(
                e.getEntity() instanceof Monster
                        ? OperationType.MONSTER_SPAWN
                        : OperationType.PASSIVE_MOB_SPAWN,
                getPosition(e.getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

}
