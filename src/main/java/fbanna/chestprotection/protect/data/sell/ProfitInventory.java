package fbanna.chestprotection.protect.data.sell;

import fbanna.chestprotection.protect.types.sell.Sell;
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

    private final Sell cp;


    public ProfitInventory(Sell cp, ItemStack... itemstacks) {
        this.cp = cp;
        super(itemstacks);
    }

//    public ProfitInventory(int size, Sell cp) {
//        this.cp = cp;
//        super(size);
//    }


    @Override
    public void setChanged() {

        this.cp.writeCPdata();
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
