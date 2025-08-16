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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.jetbrains.annotations.NotNull;

public interface BukkitBlockMoveListener extends BukkitListener {

    // Stop fluids from entering claims
    @EventHandler(ignoreCancelled = true)
    default void onBlockFromTo(@NotNull BlockFromToEvent e) {
        final Material material = e.getBlock().getType();
        if (material == Material.LAVA || material == Material.WATER) {
            final OperationPosition blockPosition = getPosition(e.getBlock().getLocation());
            if (getHandler().cancelNature(
                    blockPosition.getWorld(),
                    blockPosition,
                    getPosition(e.getToBlock().getLocation()))
            ) {
                e.setCancelled(true);
            }
        }
    }

    // Stop people from pushing blocks into claims
    @EventHandler(ignoreCancelled = true)
    default void onPistonPush(@NotNull BlockPistonExtendEvent e) {
        final OperationPosition pistonLocation = getPosition(e.getBlock().getLocation());

        if (getHandler().cancelOperation(Operation.of(
                OperationType.REDSTONE_ACTUATE,
                pistonLocation,
                true
        ))) {
            e.setCancelled(true);
            return;
        }

        for (final Block pushed : e.getBlocks()) {
            if (getHandler().cancelNature(
                    pistonLocation.getWorld(),
                    pistonLocation,
                    getPosition(pushed.getRelative(e.getDirection(), 1).getLocation())
            )) {
                e.setCancelled(true);
                return;
            }
        }
    }

    // Stop people from pulling blocks from claims
    @EventHandler(ignoreCancelled = true)
    default void onPistonPull(@NotNull BlockPistonRetractEvent e) {
        final OperationPosition pistonLocation = getPosition(e.getBlock().getLocation());

        if (getHandler().cancelOperation(Operation.of(
                OperationType.REDSTONE_ACTUATE,
                pistonLocation,
                true
        ))) {
            e.setCancelled(true);
            return;
        }

        for (final Block pulled : e.getBlocks()) {
            if (getHandler().cancelNature(
                    pistonLocation.getWorld(),
                    pistonLocation,
                    getPosition(pulled.getLocation())
            )) {
                e.setCancelled(true);
                return;
            }
        }
    }

    // Stop dispensers from dispensing onto unsuspecting claims
    @EventHandler(ignoreCancelled = true)
    default void onBlockDispense(@NotNull BlockDispenseEvent e) {
        final OperationPosition blockPosition = getPosition(e.getBlock().getLocation());

        if (getHandler().cancelOperation(Operation.of(
                OperationType.REDSTONE_ACTUATE,
                blockPosition,
                true
        ))) {
            e.setCancelled(true);
            return;
        }

        final OperationPosition facingPosition = getPosition(e.getBlock().getRelative(
                ((Directional) e.getBlock().getBlockData()).getFacing()
        ).getLocation());
        if (getHandler().cancelNature(
                blockPosition.getWorld(),
                blockPosition,
                facingPosition
        )) {
            e.setCancelled(true);
        }
    }

}
