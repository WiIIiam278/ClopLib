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

//#if MC>=12104
package net.william278.cloplib.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.ExplosionImpl;
import net.william278.cloplib.events.ExplosionEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionImplMixin {

    @Shadow
    protected abstract List<BlockPos> getBlocksToDestroy();

    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionImpl;getBlocksToDestroy()Ljava/util/List;"))
    private List<BlockPos> explodeMixin(ExplosionImpl instance) {
        return ExplosionEvents.BEFORE_BLOCKS_BROKEN.invoker().explode(instance, getBlocksToDestroy());
    }

}
//#endif