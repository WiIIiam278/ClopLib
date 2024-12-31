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

import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.entity.SetBlockPropertiesEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.william278.cloplib.events.EnchantmentEffectEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// For emitting the place block operation when users have frost walker.
// Set and replace are both used here by the game and are nearly identical, so we mixin to both.
@Mixin(SetBlockPropertiesEnchantmentEffect.class)
public abstract class SetBlockPropertiesEnchantmentEffectMixin {

    @Shadow
    @Final
    private Vec3i offset;

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    void applyMixin(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos,
                    CallbackInfo ci) {
        final ActionResult result = EnchantmentEffectEvents.BEFORE_BLOCK_UPDATE.invoker().blockUpdate(user, world,
                BlockPos.ofFloored(pos).add(this.offset));
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }

}
