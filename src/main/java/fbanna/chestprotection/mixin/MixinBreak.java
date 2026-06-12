package fbanna.chestprotection.mixin;


import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.block.BlockState;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ItemScatterer.class)
public class MixinBreak {

    @Inject(method = "onStateReplaced", at = @At("TAIL"))
    private static void inject(BlockState state, World world, BlockPos pos, CallbackInfo ci){
        //ChestProtection.LOGGER.info(String.valueOf(ChestProtection.SHOPS.size()));
        //ChestProtection.LOGGER.info("1hello>???");

        //for(CheckChest shop: ChestProtection.SHOPS) {
        for(int i = ChestProtection.SHOPS.size()-1; i >= 0; i--) {
            CheckChest shop = ChestProtection.SHOPS.get(i);
            //ChestProtection.LOGGER.info("1" + shop.position + shop.world + ", " + pos + world);
            Optional<CheckChest> potentialChest = shop.checkSame(pos, world);


            // found the chest
            if (potentialChest.isPresent()) {

                CheckChest chest = potentialChest.get();

                Optional<SimpleGui> optionalScreen = chest.getScreen();

                if (optionalScreen.isPresent()) {

                    SimpleGui screen = optionalScreen.get();

                    screen.onClose();
                }

                return; //break out of loop

            }
        }
    }
}
