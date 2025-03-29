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
import com.google.common.collect.Maps;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationType;
import net.william278.cloplib.operation.OperationUser;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface BukkitPortalListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onPlayerCreatePortal(@NotNull PortalCreateEvent e) {
        if (e.getReason() != PortalCreateEvent.CreateReason.NETHER_PAIR) {
            return;
        }

        // Get the list of locations in distinct X/Z columns
        final List<OperationPosition> locations = Lists.newArrayList();
        final Map<Integer, Integer> coordinates = Maps.newHashMap();
        for (BlockState state : e.getBlocks()) {
            if (coordinates.containsKey(state.getX()) && coordinates.get(state.getX()) == state.getZ()) {
                continue;
            }
            coordinates.put(state.getX(), state.getZ());
            locations.add(getPosition(state.getLocation()));
        }

        // Cancel the portal creation operation, specifying the player if applicable
        final OperationUser player = e.getEntity() instanceof Player p && !isPlayerNpc(p) ? getUser(p) : null;
        for (OperationPosition position : locations) {
            if (getHandler().cancelOperation(Operation.of(
                    player,
                    OperationType.BLOCK_PLACE,
                    position
            ))) {
                e.setCancelled(true);
                break;
            }
        }
    }

}
