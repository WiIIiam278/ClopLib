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

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.william278.cloplib.events.ExplosionEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @ModifyArg(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;addAll(Ljava/util/Collection;)Z", remap = false))
    private Collection<BlockPos> collectBlocksAndDamageEntitiesMixin(Collection<BlockPos> affectedBlocks) {
        return ExplosionEvents.BEFORE_BLOCKS_BROKEN.invoker().explode((Explosion) (Object) this, new ArrayList<>(affectedBlocks));
    }

}
