package fbanna.chestprotection.protect.data.sell;

import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.protect.types.sell.Sell;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ProfitInventory extends SimpleContainer {


    private final Sell cp;


    public ProfitInventory(Sell cp, ItemStack... itemstacks) {
        this.cp = cp;
        super(itemstacks);
    }



    @Override
    public void setChanged() {

        this.cp.writeCPdata();
        super.setChanged();

    }


    @Override
    public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
        return false;
    }

//    @Override
//    public boolean canTakeItem(final Container into, final int slot, final ItemStack itemStack) {
//
//        CheckProtected cp = CheckProtected.createContainerOpen(this.cp.getPosition().pos(), this.s.getLevel(this.cp.getPosition().dimension()), true);
//
//        return false;
//    }

}
