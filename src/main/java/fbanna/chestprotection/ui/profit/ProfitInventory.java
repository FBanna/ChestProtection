package fbanna.chestprotection.ui.profit;

import fbanna.chestprotection.protect.types.Sell.SellData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class ProfitInventory extends SimpleContainer implements ContainerListener {

    public ProfitInventory(SimpleContainer container) {

        super(SellData.PROFIT_INVENTORY_SIZE);
        //this.size = ;
        //this.items = container.getItems();
    }

    // remove ability to add items
//    @Override
//    public ItemStack addItem(ItemStack itemStack) {
//        return itemStack;
//    }
//
//    @Override
//    public void setItem(int slot, ItemStack itemStack) {
//        if (!itemStack.isEmpty()) {
//            return;
//        }
//
//        super.setItem(slot, itemStack);
//
//    }


    @Override
    public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {

    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int id, int value) {

    }
}
