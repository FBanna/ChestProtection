package fbanna.chestprotection.ui.sellsetup;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class SellSetupInventory extends SimpleContainer{

    private final SellSetupUI parentUI;


//    @Override
//    public void setItem(int slot, @NonNull ItemStack itemStack) {
//
//        super.setItem(slot, itemStack);
//        parentUI.setTradeItem(slot, itemStack);
//
//
//
//    }

    @Override
    public void setChanged() {

        this.parentUI.setTradeItems(this.items);

        super.setChanged();
    }




    public SellSetupInventory(SellSetupUI parentUI) {
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
