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

import net.william278.cloplib.handler.Handler;
import net.william278.cloplib.handler.TypeChecker;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface BukkitListener extends Listener, InspectorCallbackProvider {

    @NotNull
    OperationPosition getPosition(@NotNull Location location);

    @NotNull
    OperationUser getUser(@NotNull Player player);

    @NotNull
    Handler getHandler();

    @NotNull
    TypeChecker getTypeChecker();

    default Optional<Player> getPlayerSource(@Nullable Entity e) {
        if (e == null) {
            return Optional.empty();
        }
        if (e instanceof Player player) {
            return Optional.of(player);
        }
        if (e instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            return Optional.of(player);
        }
        return e.getPassengers().stream()
                .filter(p -> p instanceof Player)
                .map(p -> (Player) p)
                .findFirst();
    }

}
