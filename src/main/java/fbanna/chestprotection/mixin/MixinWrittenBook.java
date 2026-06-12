package fbanna.chestprotection.mixin;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.screens.lock.LockEditScreen;
import fbanna.chestprotection.screens.setup.SetupScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fbanna.chestprotection.ChestProtection;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public class MixinWrittenBook {




    @Inject(method = "useBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z", shift = At.Shift.AFTER))
    private void inject(ItemStack book, Hand hand, CallbackInfo ci) {
        ChestProtection.LOGGER.info("YOU DID IT >:(");

        String title = book.get(DataComponentTypes.WRITTEN_BOOK_CONTENT).title().raw();

        if(Objects.equals(title, "LOCK")) {

            new CheckChest()

            SimpleGui gui = new LockEditScreen((ServerPlayerEntity) (Object) this, book);

        }



    }
}
