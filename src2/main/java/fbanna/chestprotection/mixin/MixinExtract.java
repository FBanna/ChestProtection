package fbanna.chestprotection.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

@Mixin(HopperBlockEntity.class)
public class MixinExtract {

    @Inject(method = "canTakeItemFromContainer", at = @At("HEAD"), cancellable = true)
    private static void inject(Container hopperInventory, Container fromInventory, ItemStack stack, int slot, Direction facing, CallbackInfoReturnable<Boolean> cir){


        if (fromInventory.getItem(0).getItem() instanceof WrittenBookItem){
            WrittenBookContent book = fromInventory.getItem(0).get(DataComponents.WRITTEN_BOOK_CONTENT);

            if (Objects.equals(book.title().raw(), "LOCK")
                    || Objects.equals(book.title().raw(), "SELL")){

                cir.setReturnValue(false);
            }
        }



    }

}
