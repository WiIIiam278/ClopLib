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
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BukkitBreakListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onPlayerBreakBlock(@NotNull BlockBreakEvent e) {
        if (getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                getChecker().isFarmMaterial(e.getBlock().getType().getKey().toString())
                        ? OperationType.FARM_BLOCK_BREAK : OperationType.BLOCK_BREAK,
                getPosition(e.getBlock().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onPlayerFillBucket(@NotNull PlayerBucketFillEvent e) {
        if (getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.FILL_BUCKET,
                getPosition(e.getBlock().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onPlayerBreakHangingEntity(@NotNull HangingBreakByEntityEvent e) {
        switch (e.getCause()) {
            case ENTITY -> {
                if (e.getRemover() == null) {
                    return;
                }

                final Optional<Player> player = getPlayerSource(e.getRemover());
                if (player.isPresent()) {
                    if (getHandler().cancelOperation(Operation.of(
                            getUser(player.get()),
                            OperationType.BREAK_HANGING_ENTITY,
                            getPosition(e.getEntity().getLocation())
                    ))) {
                        e.setCancelled(true);
                    }
                    return;
                }
                final OperationPosition damaged = getPosition(e.getEntity().getLocation());
                final OperationPosition damaging = getPosition(e.getRemover().getLocation());
                if (getHandler().cancelNature(damaging.getWorld(), damaged, damaging)) {
                    e.setCancelled(true);
                }
            }
            case EXPLOSION -> {
                if (getHandler().cancelOperation(Operation.of(
                        OperationType.EXPLOSION_DAMAGE_TERRAIN,
                        getPosition(e.getEntity().getLocation())
                ))) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
