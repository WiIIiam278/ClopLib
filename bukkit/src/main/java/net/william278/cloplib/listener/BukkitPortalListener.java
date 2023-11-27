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
import net.william278.cloplib.operation.OperationUser;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface BukkitPortalListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onPlayerCreatePortal(@NotNull PortalCreateEvent e) {
        if (e.getReason() != PortalCreateEvent.CreateReason.NETHER_PAIR) {
            return;
        }

        // Get the list of locations in distinct chunks
        final List<OperationPosition> locations = new ArrayList<>();
        final List<Chunk> chunks = new ArrayList<>();
        for (BlockState state : e.getBlocks()) {
            if (chunks.contains(state.getChunk())) {
                continue;
            }
            chunks.add(state.getChunk());
            locations.add(getPosition(state.getLocation()));
        }

        // Cancel the portal creation operation, specifying the player if applicable
        final OperationUser player = e.getEntity() instanceof Player ? getUser((Player) e.getEntity()) : null;
        locations.forEach(position -> {
            if (getHandler().cancelOperation(Operation.of(
                    player,
                    OperationType.BLOCK_PLACE,
                    position
            ))) {
                e.setCancelled(true);
            }
        });
    }

}
