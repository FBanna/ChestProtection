package fbanna.chestprotection.ui.sellsetup;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class SellSetupInventory extends SimpleContainer{

    private final SellSetup parentUI;


    @Override
    public void setItem(int slot, @NonNull ItemStack itemStack) {

        super.setItem(slot, itemStack);
        parentUI.setTradeItem(slot, itemStack);



    }




    public SellSetupInventory(SellSetup parentUI) {
        this.parentUI = parentUI;
        super(2);
    }

    public void dropAll(ServerPlayer player) {

        for(ItemStack stack: this.items) {
            player.handleExtraItemsCreatedOnUse(stack);
        }

        this.clearContent();
    }

}
