package fbanna.chestprotection.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(HopperBlockEntity.class)
public class MixinExtract {

    @Inject(method = "canExtract", at = @At("HEAD"), cancellable = true)
    private static void inject(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing, CallbackInfoReturnable<Boolean> cir){


        if (fromInventory.getStack(0).getItem() instanceof WrittenBookItem){
            WrittenBookContentComponent book = fromInventory.getStack(0).get(DataComponentTypes.WRITTEN_BOOK_CONTENT);

            if (Objects.equals(book.title().raw(), "LOCK")
                    || Objects.equals(book.title().raw(), "SELL")){

                cir.setReturnValue(false);
            }
        }



    }

}
