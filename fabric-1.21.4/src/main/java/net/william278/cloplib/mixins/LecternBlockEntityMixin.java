package net.william278.cloplib.mixins;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin {

    @Shadow
    @Final
    private Inventory inventory;

    @Shadow
    @Final
    private PropertyDelegate propertyDelegate;

    @Redirect(method = "createMenu", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/LecternScreenHandler;<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V"))
    private ScreenHandler createMenuMixin(int i, Inventory inventory, PropertyDelegate propertyDelegate) {
        return new TrackedLecternScreenHandler(i, this.inventory, this.propertyDelegate, ((BlockEntity) (Object) this));
    }

}
