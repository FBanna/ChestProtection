package fbanna.chestprotection.mixin;


import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Containers.class)
public class MixinBreak {

    @Inject(method = "updateNeighboursAfterDestroy", at = @At("TAIL"))
    private static void inject(BlockState state, Level world, BlockPos pos, CallbackInfo ci){
        //ChestProtection.LOGGER.info(String.valueOf(ChestProtection.SHOPS.size()));
        //ChestProtection.LOGGER.info("1hello>???");

        //for(CheckChest shop: ChestProtection.SHOPS) {
        for(int i = ChestProtection.SHOPS.size()-1; i >= 0; i--) {
            CheckProtected shop = ChestProtection.SHOPS.get(i);
            //ChestProtection.LOGGER.info("1" + shop.position + shop.world + ", " + pos + world);
            Optional<CheckProtected> potentialChest = shop.checkSame(pos, world);


            // found the chest
            if (potentialChest.isPresent()) {

                CheckProtected chest = potentialChest.get();

                Optional<SimpleGui> optionalScreen = chest.getScreen();

                if (optionalScreen.isPresent()) {

                    SimpleGui screen = optionalScreen.get();

                    screen.close();
                }

                return; //break out of loop

            }
        }
    }
}
