package fbanna.chestprotection.trade.profit;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.trade.TradeItem;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class ProfitInventory extends SimpleInventory {

    private final CheckChest trade;
    //private ServerPlayerEntity player;

    //public static final Codec<List<ItemStack>> inventoryCodec = ItemStack.UNCOUNTED_CODEC.listOf();

    InventoryChangedListener listener = new InventoryChangedListener() {
        @Override
        public void onInventoryChanged(Inventory sender) {
            //ChestProtection.LOGGER.info("SET");
            trade.writeProfitInventory();
        }
    };

    public String encode(){
        //DataResult<JsonElement> result = inventoryCodec.encodeStart(trade.world.getRegistryManager().getOps(JsonOps.INSTANCE), this.getHeldStacks());
        DataResult<JsonElement> result = ContainerComponent.CODEC.encodeStart(trade.world.getRegistryManager().getOps(JsonOps.INSTANCE), ContainerComponent.fromStacks(this.getHeldStacks()));

        if(result.isSuccess()) {

            return result.getOrThrow().toString();
        }
        return "";
    }


    public ProfitInventory(CheckChest trade, int size, List<ItemStack> stacks) {

        super(size);
        this.trade = trade;

        for(int i = 0; i < stacks.size(); i++){
            this.heldStacks.set(i, stacks.get(i));
        }
    }

    public ProfitInventory(CheckChest trade, int size) {
        super(size);
        this.trade = trade;
    }

    public ProfitInventory clone(){

        DefaultedList<ItemStack> clonedStacks = DefaultedList.of();

        for(ItemStack stack: this.heldStacks){
            clonedStacks.add(stack.copy());
        }

        return new ProfitInventory(this.trade, this.size(), clonedStacks);
    }

    public void open(ServerPlayerEntity player){
        //this.player = player;
        //CheckChest trade = this.trade;

        this.addListener(listener);

        //OPEN LOGIC
    }

    public void close(){
        //this.player = null;
        this.removeListener(listener);
    }

    /*
    public ProfitInventory(CheckChest trade, ServerPlayerEntity player, int size) {
        super(size);
        //trade.profitInventory = this;
        this.trade = trade;
        this.player = player;



        for(int i = 0; i < size; i++){

            //this.heldStacks.set(i, new ItemStack(this.trade.cost.getItem(), this.trade.profitInventory[i]));
            this.heldStacks.set(i, this.trade.tradeItems.getCostStack().copyWithCount(this.trade.profitInventory[i]));
        }
    }*/



}
