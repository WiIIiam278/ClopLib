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

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.william278.cloplib.events.ProjectileEvents;
import net.william278.cloplib.util.ProjectileUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {

//#if MC>=12106
      @Shadow @Nullable
      public abstract Entity getOwner();
//#else
//$$  @Shadow @Nullable
//$$  private Entity owner; // The shooter
//#endif

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    protected void onEntityHitMixin(EntityHitResult hit, CallbackInfo ci) {
        final Entity entity = hit.getEntity();
        //#if MC>=12106
        final Entity owner = getOwner();
        //#endif
        if (entity == owner) {
            return;
        }
        final ProjectileEntity projectile = (ProjectileEntity) (Object) this;
        final ActionResult result = ProjectileEvents.BEFORE_ENTITY_HIT.invoker().entityHit(
                entity, projectile, owner, ProjectileUtil.getOrigin(projectile)
        );
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
    private void onBlockHitMixin(BlockHitResult hit, CallbackInfo ci) {
        //#if MC>=12104
        if (hit.isAgainstWorldBorder()) {
            return;
        }
        //#endif
        final ProjectileEntity projectile = (ProjectileEntity) (Object) this;
        //#if MC>=12106
        final Entity owner = getOwner();
        //#endif
        final ActionResult result = ProjectileEvents.BEFORE_BLOCK_HIT.invoker().blockHit(
                hit.getBlockPos(), projectile.getWorld(), projectile, owner,
                ProjectileUtil.getOrigin(projectile)
        );
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }

}
