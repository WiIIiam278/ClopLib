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
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.jetbrains.annotations.NotNull;

public interface BukkitWorldListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onRaidTriggered(@NotNull RaidTriggerEvent e) {
        if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.START_RAID,
                getPosition(e.getRaid().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onLightningStrike(@NotNull LightningStrikeEvent e) {
        if (!getHandler().cancelOperation(Operation.of(
                switch (e.getCause()) {
                    case TRIDENT -> OperationType.BLOCK_BREAK;
                    case TRAP -> OperationType.MONSTER_DAMAGE_TERRAIN;
                    default -> OperationType.FIRE_SPREAD;
                },
                getPosition(e.getLightning().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

}
