package fbanna.chestprotection.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.CheckProtected;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fbanna.chestprotection.ChestProtection;

import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(ServerPlayer.class)
//public class MixinWrittenBook {
//
//
//
//
//    @Inject(method = "openItemGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z", shift = At.Shift.AFTER))
//    private void inject(ItemStack book, InteractionHand hand, CallbackInfo ci) {
//        ChestProtection.LOGGER.info("YOU DID IT >:(");
//
//
//
////        String title = book.get(DataComponents.WRITTEN_BOOK_CONTENT).title().raw();
////
////        if(Objects.equals(title, "LOCK")) {
////
////            CheckProtected chest = new CheckProtected();
////
////            SimpleGui gui = new LockEditScreen((ServerPlayer) (Object) this, chest);
////
////        }
//
//
//
//    }
//}

@Mixin(WrittenBookItem.class)
public class MixinWrittenBook extends Item {


    public MixinWrittenBook(Properties properties) {
        super(properties);
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BY, by = 2), cancellable = true)
    private void inject(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local(name = "itemStack") ItemStack itemStack) {

        //boolean result = CheckProtected.openBook(itemStack, player, level);

        boolean result = CheckProtected.createBookOpen(itemStack, level).open(player, level.getServer());

        if (!result){

            ChestProtection.LOGGER.info("Openning edit page!");
            cir.setReturnValue(InteractionResult.SUCCESS);
        }


    }
}