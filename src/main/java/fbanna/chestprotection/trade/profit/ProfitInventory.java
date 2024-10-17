package fbanna.chestprotection.trade.profit;

import com.mojang.serialization.Codec;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ProfitInventory extends SimpleInventory {

    private final CheckChest trade;
    private final ServerPlayerEntity player;

    Codec<List<ItemStack>> inventoryCodec = ItemStack.CODEC.listOf();
    public ProfitInventory(CheckChest trade, ServerPlayerEntity player, int size) {
        super(size);
        this.trade = trade;
        this.player = player;

        for(int i = 0; i < size; i++){

            //this.heldStacks.set(i, new ItemStack(this.trade.cost.getItem(), this.trade.profitInventory[i]));
            this.heldStacks.set(i, this.trade.tradeItems.getCostStack().copyWithCount(this.trade.profitInventory[i]));
        }

        NbtCompound nbt = Inventories.writeNbt(new NbtCompound(), this.heldStacks, this.trade.world.getRegistryManager());






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
