package fbanna.chestprotection.ui.sell;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SellInventory extends SimpleContainer {

    private final SellUI parentUI;

    public SellInventory(SellUI parentUI) {
        this.parentUI = parentUI;
        super(21);
    }

    @Override
    public void setChanged() {
        this.parentUI.updateButton();
        super.setChanged();
    }

    public void dropAll(ServerPlayer player) {

        for(ItemStack stack: this.items) {
            player.handleExtraItemsCreatedOnUse(stack);
        }

        this.clearContent();

    }

}
