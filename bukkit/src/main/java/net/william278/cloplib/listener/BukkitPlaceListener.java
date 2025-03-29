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
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.jetbrains.annotations.NotNull;

public interface BukkitPlaceListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onPlayerPlaceBlock(@NotNull BlockPlaceEvent e) {
        if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                getChecker().isFarmMaterial(e.getBlockPlaced().getType().getKey().toString())
                        ? OperationType.FARM_BLOCK_PLACE : OperationType.BLOCK_PLACE,
                getPosition(e.getBlock().getLocation())
        ))) {
            e.setCancelled(true);
            e.setBuild(false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onPlayerEmptyBucket(@NotNull PlayerBucketEmptyEvent e) {
        if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.EMPTY_BUCKET,
                getPosition(e.getBlock().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onPlayerPlaceHangingEntity(@NotNull HangingPlaceEvent e) {
        if (e.getPlayer() == null || isPlayerNpc(e.getPlayer())) {
            return;
        }

        if (getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.PLACE_HANGING_ENTITY,
                getPosition(e.getEntity().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

}
