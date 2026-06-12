package fbanna.chestprotection.screens.profit;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import fbanna.chestprotection.check.CheckChest;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ProfitInventory extends SimpleContainer {

    private final CheckChest trade;
    //private ServerPlayerEntity player;

    //public static final Codec<List<ItemStack>> inventoryCodec = ItemStack.UNCOUNTED_CODEC.listOf();

    ContainerListener listener = new ContainerListener() {
        @Override
        public void containerChanged(Container sender) {
            //ChestProtection.LOGGER.info("SET");
            trade.writeProfitInventory();
        }
    };

    public String encode(){
        //DataResult<JsonElement> result = inventoryCodec.encodeStart(trade.world.getRegistryManager().getOps(JsonOps.INSTANCE), this.getHeldStacks());
        DataResult<JsonElement> result = ItemContainerContents.CODEC.encodeStart(trade.world.registryAccess().createSerializationContext(JsonOps.INSTANCE), ItemContainerContents.fromItems(this.getItems()));

        if(result.isSuccess()) {

            return result.getOrThrow().toString();
        }
        return "";
    }


    public ProfitInventory(CheckChest trade, int size, List<ItemStack> stacks) {

        super(size);
        this.trade = trade;

        for(int i = 0; i < stacks.size(); i++){
            this.items.set(i, stacks.get(i));
        }
    }

    public ProfitInventory(CheckChest trade, int size) {
        super(size);
        this.trade = trade;
    }

    public ProfitInventory clone(){

        NonNullList<ItemStack> clonedStacks = NonNullList.create();

        for(ItemStack stack: this.items){
            clonedStacks.add(stack.copy());
        }

        return new ProfitInventory(this.trade, this.getContainerSize(), clonedStacks);
    }

    public void open(ServerPlayer player){
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
