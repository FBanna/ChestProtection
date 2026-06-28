package fbanna.chestprotection.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fbanna.chestprotection.protect.CheckProtected;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



// List of other mixins

// Level.destroyBlock
// Container.canTakeItem


@Mixin(WrittenBookItem.class)
public class MixinWrittenBook extends Item {


    public MixinWrittenBook(Properties properties) {
        super(properties);
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openItemGui(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void inject(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local(name = "itemStack") ItemStack itemStack) {

        boolean result = CheckProtected.createBookOpen(itemStack, level).open(player, level.getServer());

        if (!result){

            //ChestProtection.LOGGER.info("Openning edit page!");
            cir.setReturnValue(InteractionResult.SUCCESS);
        }


    }
}