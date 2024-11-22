package fbanna.chestprotection.check;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.trade.TradeItem;
import fbanna.chestprotection.trade.TradeInventory;
import fbanna.chestprotection.trade.TradeItemList;
import fbanna.chestprotection.trade.profit.ProfitInventory;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckChest {

    public enum status{
        CLEAR,
        ERROR,
        SELL,
        LOCK
    }

    public status chestStatus = status.CLEAR;
    public String author;
    //public ItemStack cost;
    //public ItemStack product;
    public TradeItemList tradeItems;
    public Inventory chestInventory;
    //public int[] profitInventory = new int[54];
    //public DefaultedList<ItemStack> profitInventory = DefaultedList.ofSize(54);
    public ProfitInventory profitInventory;
    public World world;
    public BlockPos position;
    //private WrittenBookContentComponent book;
    private ItemStack stack;





    public CheckChest(BlockPos position, World world) {

        if(world.getBlockEntity(position) instanceof ChestBlockEntity) {

            this.world = world;
            this.position = position;
            this.chestInventory = ChestBlock.getInventory((ChestBlock) world.getBlockState(position).getBlock(), world.getBlockState(position), world, position, true);
            this.stack = this.chestInventory.getStack(0);


            // IF ITS A BOOK
            if(this.stack.getItem() instanceof WrittenBookItem){

                WrittenBookContentComponent book;

                book = this.stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);


                // IF ITS A LOCK
                if(Objects.equals(book.title().raw(), "LOCK")) {

                    this.chestStatus = status.LOCK;

                    this.author = book.author();

                // IF ITS A SELL

                } else if (Objects.equals(book.title().raw(), "SELL")) {

                    this.chestStatus = status.ERROR;

                    // minecraft:diamond-64->minecraft:stick-1

                    this.author = book.author();

                    List<RawFilteredPair<Text>> pages = book.pages();

                    // CHECK SIZE
                    if(pages.size() == 2){

                        /*

                        // GET STRINGS

                        String page1 = pages.get(0).raw().getString();
                        String page2 = pages.get(1).raw().getString();

                        //try {
                        JsonElement element1 = JsonParser.parseString(page1);
                        DataResult<ItemStack> resultPage1 = ItemStack.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element1);

                        JsonElement element2 = JsonParser.parseString(page2);
                        DataResult<ItemStack> resultPage2 = ItemStack.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element2);

                        if(resultPage1.isSuccess() && resultPage2.isSuccess()) {
                            this.cost = resultPage1.getOrThrow();
                            this.product = resultPage2.getOrThrow();
                            this.chestStatus = status.SELL;
                        }*/



                        String[] pageList = {pages.get(0).raw().getString(), pages.get(1).raw().getString()};
                        //ItemStack[] out = new ItemStack[2];
                        TradeItem[] out = new TradeItem[2];
                        boolean success = true;


                        for (int i = 0; i < 2; i++){
                            TradeItem saveItem;
                            JsonElement element;

                            try{
                                element = JsonParser.parseString(pageList[i]);
                            } catch (Exception e) {
                                success = false;
                                break;
                            }


                            //DataResult<ItemStack> result = ItemStack.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element);
                            DataResult<TradeItem> result = TradeItem.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element);

                            if(result.isSuccess()){
                                saveItem = result.getOrThrow();

                                if(!saveItem.getIsItem()) {
                                    for (ComponentType<?> type : saveItem.getStack().copy().getComponents().getTypes()) {


                                        // ADD MORE DEFAULTS
                                        if (type.equals(DataComponentTypes.CONTAINER)) {
                                            saveItem.setItem(Items.SHULKER_BOX);
                                            //item = item.copyComponentsToNewStack(Items.SHULKER_BOX, item.getCount());
                                        }
                                    }
                                }

                            } else {
                                ChestProtection.LOGGER.info("Error in parsing, when someone opened a chest! ChestProtection");
                                success = false;
                                break;
                            }



                            out[i] = saveItem;
                        }

                        if(success){
                            //this.cost = out[0].getStack();
                            //this.product = out[1].getStack();
                            this.tradeItems = new TradeItemList(out);
                            this.chestStatus = status.SELL;
                        }






                        /*
                        try {
                            String[] tempCost = page1.split("-");
                            String[] tempProduct = page2.split("-");

                            if(tempCost.length == 2 && tempProduct.length == 2) {

                                this.cost = new ItemStack(
                                        //Registries.ITEM.get(new Identifier(tempCost[0])),
                                        Registries.ITEM.get(Identifier.of(tempCost[0])),
                                        Integer.parseInt(tempCost[1])
                                );

                                this.product = new ItemStack(
                                        //Registries.ITEM.get(new Identifier(tempProduct[0])),
                                        Registries.ITEM.get(Identifier.of(tempProduct[0])),
                                        Integer.parseInt(tempProduct[1])
                                );

                            } else {
                                this.chestStatus = status.CLEAR;
                            }

                        } catch (Exception e){
                            this.chestStatus = status.CLEAR;
                        }*/
                    }


                    if(this.stack.contains(DataComponentTypes.CUSTOM_DATA)){
                        NbtComponent data = this.stack.get(DataComponentTypes.CUSTOM_DATA);

                        if (data != null){

                            NbtCompound nbt = data.copyNbt();

                            if(book.generation() != 0 || nbt.get("profitInventory").getType()==NbtElement.INT_ARRAY_TYPE) {
                                this.stack.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, new WrittenBookContentComponent(book.title(),book.author(),0,book.pages(),book.resolved()));


                                /*this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                                    currentNbt.putIntArray("profitInventory", this.profitInventory);
                                }));*/


                                this.profitInventory = new ProfitInventory(this, 54);
                                this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                                    //currentNbt.putIntArray("profitInventory", this.profitInventory);
                                    //currentNbt.put("profitInventory", NbtElement.COMPOUND_TYPE)
                                    currentNbt.putString("profitInventory", profitInventory.encode());
                                }));

                            } else if(data.contains("profitInventory")){



                                /*this.profitInventory = new ProfitInventory(this, 54);
                                if() {
                                    ChestProtection.LOGGER.info("OLD BOOK CONVERTING!");
                                    this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                                        currentNbt.putString("profitInventory", profitInventory.encode());
                                    }));
                                }*/

                                String string = nbt.getString("profitInventory");

                                JsonElement element;

                                try {
                                    element = JsonParser.parseString(string);

                                    DataResult<ContainerComponent> result = ContainerComponent.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element);

                                    if(result.isSuccess()){
                                        //ChestProtection.LOGGER.info(String.valueOf(stacks));
                                        this.profitInventory = new ProfitInventory(this, 54, result.getOrThrow().stream().toList());
                                    } else {
                                        this.profitInventory = new ProfitInventory(this, 54);
                                    }

                                } catch (Exception e) {
                                    this.profitInventory = new ProfitInventory(this, 54);
                                }





                                //DataResult<List<ItemStack>> result = ProfitInventory.inventoryCodec.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element);





                                //if (this.profitInventory.length != 54){
                                //    this.profitInventory = new int[54];
                                //}
                            }
                        }
                    } else {
                        this.profitInventory = new ProfitInventory(this, 54);
                        this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                            currentNbt.putString("profitInventory", profitInventory.encode());
                        }));
                    }
                }
            }
        }
    }

    public boolean isStock(TradeItem product) {
        /*

        int count = stack.getCount();

        int total = this.chestInventory.count(stack.getItem());

        if(count <= total){
            return true;
        } else {
            return false;
        }*/

        int total = 0;
        ItemStack stack;

        for(int i = 0; i < this.chestInventory.size(); i++){
            stack = this.chestInventory.getStack(i);
            if(TradeInventory.ItemsEqual(stack, product)) {
                total += stack.getCount();
            }

        }
        /*} else if (this.getStack(index).getItem() == this.trade.cost.getItem()){
            return index;
        }*/

        if (product.getStack().getCount() <= total){
            return true;
        } else {
            return false;
        }
    }

    public void saveTrade(boolean[] isItem,ItemStack[] stacks) {
        List<RawFilteredPair<Text>> newPages = new ArrayList<>();
        WrittenBookContentComponent book = this.stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);

        /*

        //ChestProtection.LOGGER.info(stacks[1].encode(world.getRegistryManager()).toString());

        DataResult<JsonElement> result1 = ItemStack.CODEC.encodeStart(world.getRegistryManager().getOps(JsonOps.INSTANCE), stacks[1]);

        //if (result1.isSuccess()) {
        ChestProtection.LOGGER.info("SUCCESS " + result1.getPartialOrThrow().toString());*/

        //}

        int i = 0;

        for (ItemStack transactionStack: stacks) {
            if (transactionStack == null || transactionStack.isEmpty()) {

                if(book.pages().size()>=i){
                    newPages.add(book.pages().get(i));
                } else {
                    newPages.add(RawFilteredPair.of(Text.empty()));
                }

            } else {
                TradeItem saveItemCodec = new TradeItem(isItem[i], transactionStack);
                DataResult<JsonElement> result = TradeItem.CODEC.encodeStart(world.getRegistryManager().getOps(JsonOps.INSTANCE), saveItemCodec);
                //DataResult<JsonElement> result = ItemStack.CODEC.encodeStart(world.getRegistryManager().getOps(JsonOps.INSTANCE), transactionStack);
                JsonElement jsonElement = result.getOrThrow();
                String json = jsonElement.toString();
                newPages.add(RawFilteredPair.of(Text.of(json)));

            }


            i++;
        }

        WrittenBookContentComponent book1 = new WrittenBookContentComponent(
                book.title(),
                book.author(),
                book.generation(),
                newPages,
                book.resolved()
                );

        this.stack.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, book1);

    }

    /*
    public void setProfitInventory(SimpleInventory inventory) {

        int[] arrayInventory = new int[inventory.size()];

        for(int i = 0; i < inventory.size(); i++){
            //if (inventory.getStack(i).getItem() == this.cost.getItem()){
            if (inventory.getStack(i).getItem() == this.tradeItems.getCostStack().getItem()) {
                arrayInventory[i] = inventory.getStack(i).getCount();
            }
        }
        this.profitInventory = arrayInventory;

        writeProfitInventory();
    }*/



    public void writeProfitInventory() {

        /*this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
            currentNbt.remove("profitInventory");
            currentNbt.putIntArray("profitInventory", this.profitInventory);
        }));*/


        this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
            currentNbt.putString("profitInventory", profitInventory.encode());
        }));

    }

    /*
    public void insertProfitInventory(ItemStack stack){

        int itemCountdown = stack.getCount();
        int slot;

        for (int i = 0; i < this.profitInventory.length; i++){
            slot = this.profitInventory[i];

            if(slot == 0){
                if(itemCountdown > stack.getMaxCount()){
                    itemCountdown -= stack.getMaxCount();
                    this.profitInventory[i] = stack.getMaxCount();
                } else {

                    this.profitInventory[i] = itemCountdown;
                    itemCountdown = 0;

                }
            } else if (slot < stack.getMaxCount()) {

                if (itemCountdown + slot <= stack.getMaxCount()){

                    this.profitInventory[i] = itemCountdown + slot;
                    itemCountdown = 0;
                } else {

                    this.profitInventory[i] = stack.getMaxCount();
                    itemCountdown -= (stack.getMaxCount() - slot);
                }

            }

        }

        writeProfitInventory();

    }*/
}
