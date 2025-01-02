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

import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.william278.cloplib.operation.OperationPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FabricBlockMoveListener extends FabricListener {

    // Stop fluids from entering claims
    @NotNull
    default ActionResult onBlockFromTo(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        final OperationPosition blockPosition = getPosition(from, world);
        if (getHandler().cancelNature(
                blockPosition.getWorld(),
                blockPosition,
                getPosition(to, world))
        ) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @NotNull
    default ActionResult onPistonActuate(World world, BlockPos pistonBlock, Direction pistonDirection,
                                         List<BlockPos> affectedBlocks) {
        final OperationPosition pistonPos = getPosition(pistonBlock, world);
        for (final BlockPos blockPos : affectedBlocks) {
            if (getHandler().cancelNature(
                    pistonPos.getWorld(),
                    pistonPos,
                    getPosition(blockPos.offset(pistonDirection), world)
            )) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

}
