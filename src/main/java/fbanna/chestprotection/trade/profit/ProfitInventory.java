package fbanna.chestprotection.trade.profit;

import fbanna.chestprotection.check.CheckChest;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ProfitInventory extends SimpleInventory {

    private final CheckChest trade;
    private final ServerPlayerEntity player;
    public ProfitInventory(CheckChest trade, ServerPlayerEntity player, int size) {
        super(size);
        this.trade = trade;
        this.player = player;

        for(int i = 0; i < size; i++){

            this.heldStacks.set(i, new ItemStack(this.trade.cost.getItem(), this.trade.profitInventory[i]));
        }

    }



    @Override
    public void setStack(int slot, ItemStack stack) {


        if(stack.isEmpty()){
            super.setStack(slot, stack);



            this.trade.setProfitInventory(this);
        } else {

            this.player.dropItem(stack, false);

        }

    }


}
