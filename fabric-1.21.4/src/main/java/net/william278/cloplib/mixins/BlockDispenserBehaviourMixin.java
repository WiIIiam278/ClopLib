package net.william278.cloplib.mixins;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.william278.cloplib.events.DispenserEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {BlockPlacementDispenserBehavior.class, ShearsDispenserBehavior.class})
public abstract class BlockDispenserBehaviourMixin {

    @Inject(method = "dispenseSilently", at = @At("HEAD"), cancellable = true)
    private void dispenseSilentlyMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        final Direction direction = pointer.state().get(DispenserBlock.FACING);
        final ActionResult result = DispenserEvents.BEFORE_PLACE.invoker().place(
                pointer.world(), pointer.pos(), pointer.pos().offset(direction)
        );
        if (result == ActionResult.FAIL) {
            ((FallibleItemDispenserBehavior) (Object) this).setSuccess(false);
            cir.setReturnValue(stack);
            cir.cancel();
        }
    }

}
