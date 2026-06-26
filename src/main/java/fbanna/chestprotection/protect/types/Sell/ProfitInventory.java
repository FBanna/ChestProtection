package fbanna.chestprotection.protect.types.Sell;

import fbanna.chestprotection.ui.profit.ProfitUI;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ProfitInventory extends SimpleContainer {
//
//    @Override
//    public void up

//    public ProfitInventory(int size) {
//
//        super(size);
//        //this.size = ;
//        //this.items = container.getItems();
//    }


    public ProfitInventory(ItemStack... itemstacks) {

        super(itemstacks);
    }


    @Override
    public void setChanged() {

        super.setChanged();
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


//    @Override
//    public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {
//
//    }
//
//    @Override
//    public void dataChanged(AbstractContainerMenu container, int id, int value) {
//
//    }
}
