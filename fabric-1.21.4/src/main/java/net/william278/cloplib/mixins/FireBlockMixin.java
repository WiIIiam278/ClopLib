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

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.william278.cloplib.events.FireTickEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Carefully prevent fire from doing specific things.
@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Shadow @Final
    public static IntProperty AGE;

    // Prevent fire spread contextually
    @Inject(method = "trySpreadingFire", at = @At("HEAD"), cancellable = true)
    private void trySpreadingFireMixin(World world, BlockPos pos, int spreadFactor, Random random,
                                       int currentAge, CallbackInfo cir) {
        final ActionResult result = FireTickEvents.BEFORE_SPREAD.invoker().fireSpread(world, pos);
        if (result == ActionResult.FAIL) {
            cir.cancel();
        }
    }

    // Prevent fire spread contextually
    @Inject(method = "getBurnChance(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)I", at = @At("HEAD"), cancellable = true)
    private void getBurnChanceMixin(WorldView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        final ActionResult result = FireTickEvents.BEFORE_BURN.invoker().fireBurn((World) world, pos);
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(0);
            cir.cancel();
        }
    }

}

