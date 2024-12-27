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

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FabricBreakListener extends FabricListener {

    // We listen to both AFTER the block has broken and when the player STARTS breaking a block
    default boolean onPlayerBreakBlock(World world, PlayerEntity playerEntity, BlockPos pos,
                                       @Nullable BlockState block, @Nullable BlockEntity tileEntity) {
        if (block != null && getHandler().cancelOperation(Operation.of(
                getUser(playerEntity),
                getChecker().isFarmMaterial(Registries.BLOCK.getId(block.getBlock()).toString())
                        ? OperationType.FARM_BLOCK_BREAK : OperationType.BLOCK_BREAK,
                getPosition(pos, world)
        ))) {
            this.sendTileEntityUpdate(tileEntity, playerEntity);
            return false;
        }
        return true;
    }

    @NotNull
    default ActionResult onPlayerAttackBlock(PlayerEntity playerEntity, World world, Hand hand,
                                             BlockPos pos, Direction direction) {
        return onPlayerBreakBlock(world, playerEntity, pos, world.getBlockState(pos), world.getBlockEntity(pos))
                ? ActionResult.PASS : ActionResult.FAIL;
    }


    // Send an update packet to the client when breaking blocks w/ tile entities to fix desync
    private void sendTileEntityUpdate(@Nullable BlockEntity tileEntity, @NotNull PlayerEntity playerEntity) {
        if (tileEntity == null || !(playerEntity instanceof ServerPlayerEntity player)) {
            return;
        }
        final Packet<ClientPlayPacketListener> tileUpdate = tileEntity.toUpdatePacket();
        if (tileUpdate != null) {
            player.networkHandler.sendPacket(tileUpdate);
        }
    }

}
