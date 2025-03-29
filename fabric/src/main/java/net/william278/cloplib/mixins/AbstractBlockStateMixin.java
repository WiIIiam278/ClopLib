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

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
//#if MC>=12105
import net.minecraft.entity.EntityCollisionHandler;
//#endif
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.ActionResult;
import net.william278.cloplib.events.PressureBlockEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    //#if MC==12105
    private void onEntityCollisionMixin(World world, BlockPos pos, Entity entity, EntityCollisionHandler ech, CallbackInfo ci) {
    //#else
    //$$ private void onEntityCollisionMixin(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
    //#endif
        final BlockState state = world.getBlockState(pos);
        if (state == null || state.isAir()) {
            return;
        }

        final ActionResult result = PressureBlockEvents.BEFORE_COLLISION.invoker().collide(world, pos, state, entity);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }

}
