package net.william278.cloplib.mixins;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.william278.cloplib.util.ProjectileUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileDispenserBehavior.class)
public abstract class ProjectileDispenserBehaviourMixin {

    @Final
    @Shadow
    private ProjectileItem projectile;

    @Final
    @Shadow
    private ProjectileItem.Settings projectileSettings;

    @Redirect(method = "dispenseSilently", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;spawnWithVelocity(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;DDDFF)Lnet/minecraft/entity/projectile/ProjectileEntity;"))
    private ProjectileEntity dispenseSilentlyMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        ServerWorld serverWorld = pointer.world();
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        Position position = this.projectileSettings.positionFunction().getDispensePosition(pointer, direction);
        final ProjectileEntity entity = ProjectileEntity.spawnWithVelocity(this.projectile.createEntity(serverWorld, position, stack, direction), serverWorld, stack, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ(), this.projectileSettings.power(), this.projectileSettings.uncertainty());
        ProjectileUtil.markOrigin(entity, pointer.pos());
        return entity;
    }

}
