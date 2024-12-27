package net.william278.cloplib.mixins;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.william278.cloplib.events.PlayerTakeLecternBook;
import org.jetbrains.annotations.NotNull;

public class TrackedLecternScreenHandler extends LecternScreenHandler {

    private final World lecternWorld;
    private final BlockPos lecternPos;

    TrackedLecternScreenHandler(int syncId, @NotNull Inventory inventory, @NotNull PropertyDelegate propertyDelegate,
                                @NotNull BlockEntity blockEntity) {
        super(syncId, inventory, propertyDelegate);
        this.lecternWorld = blockEntity.getWorld();
        this.lecternPos = blockEntity.getPos();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == TAKE_BOOK_BUTTON_ID) {
            final ActionResult result = PlayerTakeLecternBook.EVENT.invoker().take(lecternWorld, lecternPos, player);
            if (result == ActionResult.FAIL) {
                return false;
            }
        }
        return super.onButtonClick(player, id);
    }

}
