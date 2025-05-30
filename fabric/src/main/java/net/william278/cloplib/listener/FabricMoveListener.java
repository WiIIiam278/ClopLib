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

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface FabricMoveListener extends FabricListener {

    @NotNull
    default ActionResult onPlayerMove(ServerPlayerEntity player, World world, Vec3d from, Vec3d to) {
        if (from.equals(to)) {
            return ActionResult.PASS;
        }

        // Handle cancelling moving, if needed
        return getHandler().cancelMovement(
                getUser(player),
                getPosition(from, world, player.getYaw(), player.getPitch()),
                getPosition(to, world, player.getYaw(), player.getPitch())
        ) ? ActionResult.FAIL : ActionResult.PASS;
    }

}
