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

package net.william278.cloplib.mixins;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.william278.cloplib.events.PlayerMovementEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    private double lastTickX;
    @Shadow
    private double lastTickY;
    @Shadow
    private double lastTickZ;

    @Shadow
    private double lastTickRiddenX;
    @Shadow
    private double lastTickRiddenY;
    @Shadow
    private double lastTickRiddenZ;

    @Inject(method = "onPlayerMove", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getBoundingBox()Lnet/minecraft/util/math/Box;"), cancellable = true)
    private void onPlayerMoveMixin(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (!packet.changesPosition()) {
            return;
        }

        // Determine change in distance
        final Vec3d from = new Vec3d(this.lastTickX, this.lastTickY, this.lastTickZ);
        final Vec3d to = new Vec3d(packet.getX(from.getX()), packet.getY(from.getY()), packet.getZ(from.getZ()));
        double delta = Math.pow(this.lastTickX - to.getX(), 2) +
                       Math.pow(this.lastTickY - to.getY(), 2) +
                       Math.pow(this.lastTickZ - to.getZ(), 2);

        // Cancel the action
        if ((delta > 1f / 256) && !this.player.isImmobile()) {
            final ActionResult result = PlayerMovementEvents.BEFORE_MOVE.invoker().move(
                    player, player.getServerWorld(), from, to
            );
            if (result == ActionResult.FAIL) {
                player.setVelocity(Vec3d.ZERO);
                ci.cancel();
            }
        }
    }

    @Inject(method = "onVehicleMove", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/world/ServerWorld;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z", ordinal = 0))
    private void onVehicleMoveMixin(VehicleMoveC2SPacket packet, CallbackInfo ci) {
        // Determine change in distance
        final Vec3d from = new Vec3d(this.lastTickRiddenX, this.lastTickRiddenY, this.lastTickRiddenZ);
        final Vec3d to = packet.position();
        double delta = Math.pow(this.lastTickRiddenX - to.getX(), 2) +
                       Math.pow(this.lastTickRiddenY - to.getY(), 2) +
                       Math.pow(this.lastTickRiddenZ - to.getZ(), 2);

        // Cancel the action
        if ((delta > 1f / 256)) {
            final ActionResult result = PlayerMovementEvents.BEFORE_MOVE.invoker().move(
                    player, player.getServerWorld(), from, to
            );
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

}
