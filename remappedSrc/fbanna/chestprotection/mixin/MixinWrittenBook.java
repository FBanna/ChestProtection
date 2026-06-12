package fbanna.chestprotection.mixin;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.screens.lock.LockEditScreen;
import fbanna.chestprotection.screens.setup.SetupScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fbanna.chestprotection.ChestProtection;

import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Mixin(ServerPlayer.class)
public class MixinWrittenBook {




    @Inject(method = "openItemGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z", shift = At.Shift.AFTER))
    private void inject(ItemStack book, InteractionHand hand, CallbackInfo ci) {
        ChestProtection.LOGGER.info("YOU DID IT >:(");

        String title = book.get(DataComponents.WRITTEN_BOOK_CONTENT).title().raw();

        if(Objects.equals(title, "LOCK")) {

            CheckChest chest = new CheckChest();

            SimpleGui gui = new LockEditScreen((ServerPlayer) (Object) this, chest);

        }



    }
}
