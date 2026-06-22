package fbanna.chestprotection.protect;

import com.mojang.serialization.DataResult;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.types.*;
import fbanna.chestprotection.protect.types.Error;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fbanna.chestprotection.protect.types.Lock.Lock;
import fbanna.chestprotection.protect.types.Lock.LockBook;
import fbanna.chestprotection.protect.types.Sell.Sell;
import fbanna.chestprotection.protect.types.Sell.SellBook;
import fbanna.chestprotection.protect.types.Sell.SellData;
import fbanna.chestprotection.protect.types.Sell.TradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CheckProtected {

    private static final String LOCKED_TITLE = "LOCK";
    private static final String SELL_TITLE = "SELL";

    public enum ProtectedStatus {
        CLEAR,
        ERROR,
        LOCK,
        SELL
//        SELL_ERROR
    }


    public CPdata cpdata;

    private final ItemStack stack;
    //private Container protectedInventory;
    private final ProtectedStatus status;




    /// Returns true if allowed to continue Vanilla openning
    /// Returns false if it is needed to become a new GUI (or other)
    public abstract boolean open(Player player, MinecraftServer server);


    /// Returns true if allowed to continue Vanilla openning
    /// Returns false if it is needed to become a new GUI (or other)
    public boolean playerBreak(Player player, MinecraftServer server) {
        if (this.status == ProtectedStatus.CLEAR){
            return true;
        }

        Authorised authorised = this.cpdata.getAuthorised();

        if (authorised.isAuthorised(player.getUUID())){
            return true;
        }


        Optional<NameAndId> authorProfileOption = server.services().nameToIdCache().get(authorised.getAuthor());


        if (authorProfileOption.isEmpty()) {
            return true;
        }

        player
            .sendOverlayMessage(
                Component.literal("Locked by %s!".formatted(authorProfileOption.get().name())
            ).withStyle(ChatFormatting.RED));

        return false;



    }

    public CheckProtected(ItemStack stack, CPdata cpdata, ProtectedStatus status){

        this.stack = stack;
        this.cpdata = cpdata;
        //this.protectedInventory = protectedInventory;
        this.status = status;

    }


    public static CheckProtected createBookOpen(ItemStack stack, Level level) {
        ProtectedStatus status = getStatus(stack);

        if (status == ProtectedStatus.CLEAR) {
            return new Clear(stack);
        }

        CPdata cpdata = getCPdataOrGenerate(stack, level);

        switch (status) {
            case ERROR -> {
                return new Error(stack, cpdata, status);
            }
            case LOCK -> {
                return new LockBook(stack, cpdata, status);
            }
            case SELL -> {

                return new SellBook(stack, cpdata, status);

            }
//            case SELL_ERROR -> {
//
//                // TODO
//
//                return new SellBook(stack, cpdata, status);
//            }
        }
        return new Clear(stack);
    }


    public static CheckProtected createContainerOpen(BlockPos position, Level level) {

        Container tempProtectedInventory = null;
        ItemStack tempStack = null;
        CPdata tempCPdata = null;



        // Finding book & matching its content
        BlockState state = level.getBlockState(position);

        BlockEntity entity = level.getBlockEntity(position);

        Block block = state.getBlock();

        if (!(entity instanceof Container)){
            return new Clear(tempStack);
        }

        if(block != Blocks.CHEST && block != Blocks.BARREL){
            return new Clear(tempStack);
        }

        tempProtectedInventory = (Container) entity;

        tempStack = tempProtectedInventory.getItem(0);

        ProtectedStatus status = getStatus(tempStack);

        if (status == ProtectedStatus.CLEAR){
            return new Clear(tempStack);
        }

        // Creating Authorisation

        tempCPdata = getCPdataOrGenerate(tempStack, level);


        return switch (status) {
            case CLEAR -> new Clear(tempStack);
            case ERROR -> new Error(tempStack, tempCPdata, status);
            case LOCK -> new Lock(tempStack, tempCPdata, status);
            case SELL -> new Sell(tempStack, tempCPdata, tempProtectedInventory, status);
            //case SELL_ERROR -> new SellError(tempStack, tempCPdata, status);
        };


    }

    public static ProtectedStatus getStatus(ItemStack potentialBook) {


        if (!(potentialBook.getItem() instanceof WrittenBookItem)) {
            return ProtectedStatus.CLEAR;
        }

        WrittenBookContent book = potentialBook.get(DataComponents.WRITTEN_BOOK_CONTENT);

        if (book == null) {
            return ProtectedStatus.ERROR;
        }

        String title = book.title().raw();

        return switch (title) {
            case LOCKED_TITLE -> ProtectedStatus.LOCK;
            case SELL_TITLE -> ProtectedStatus.SELL;
            default -> ProtectedStatus.CLEAR;
        };

    }

    protected void GenerateSellCPdata() {

        if (this.cpdata.sellData.isPresent()) {
            return;
        }

        this.cpdata = new CPdata(
                this.cpdata.getAuthorised(),
                Optional.of(new SellData(
                        Optional.empty(),
                        Optional.empty(),
                        ItemContainerContents.fromItems(NonNullList.withSize(SellData.PROFIT_INVENTORY_SIZE, ItemStack.EMPTY))
                ))
        );

        writeCPdata(this.cpdata, stack);
    }


    /// Must ensure that the book is NOT clear
    ///
    /// returns the CPdata or generates and saves it itself
    private static CPdata getCPdataOrGenerate(ItemStack stack, Level level){
        CPdata cpdata = getCPdata(stack);

        if (cpdata == null) {


            CPdata newData = new CPdata(
                    new Authorised(stack.get(DataComponents.WRITTEN_BOOK_CONTENT).author(), level.getServer()),
                    Optional.empty()
            );

            writeCPdata(newData, stack);
            return newData;

        } else {
            return cpdata;
        }


    }

    /// returns CPdata from given item stack. If error -> returns null
//    protected CPdata getCpdata() {
//        return getCPdata(this.stack);
//    }


    /// returns CPdata from given item stack. If error -> returns null
    private static CPdata getCPdata(ItemStack stack){

        if (!stack.has(DataComponents.CUSTOM_DATA)){
            return null;
        }

        CompoundTag data = stack.get(DataComponents.CUSTOM_DATA).copyTag();

        if (!data.contains("cpdata")){
            return null;
        }

        CompoundTag cpdata = data.getCompoundOrEmpty("cpdata");


        DataResult<CPdata> result = CPdata.CODEC.parse(NbtOps.INSTANCE, cpdata);

        if (result.isError()){

            ChestProtection.LOGGER.info("error parsing");
            return null;

        }

        return result.getOrThrow();



    }

//    protected void writeCPdata(CPdata newData){
//
//        writeCPdata(newData, this.stack);
//
//    }

    public void writeCPdata() {
        writeCPdata(this.cpdata, this.stack);
    }

    private static void writeCPdata(CPdata newData, ItemStack stack){

        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {

            DataResult<Tag> tag = CPdata.CODEC.encodeStart(NbtOps.INSTANCE, newData);

            if (tag.isError()){
                ChestProtection.LOGGER.error("Could not encode data to book!");
            }

            currentNbt.put("cpdata", tag.getOrThrow());

        }));


    }

    public void addAuthorised(UUID player){
        this.cpdata.getAuthorised().addAuthorised(player);
        this.writeCPdata();
    }


    public void removeAuthorised(UUID player){
        this.cpdata.getAuthorised().removeAuthorised(player);
        this.writeCPdata();
    }

    public ItemStack getStack() {
        return this.stack;
    }



//
//    public CheckProtected(BlockPos position, Level world) {
//
//        if(world.getBlockEntity(position) instanceof ChestBlockEntity) {
//
//            this.world = world;
//            this.position = position;
//            this.ProtectedInventory = ChestBlock.getContainer((ChestBlock) world.getBlockState(position).getBlock(), world.getBlockState(position), world, position, true);
//            this.stack = this.ProtectedInventory.getItem(0);
//
//
//
//            new CheckProtected();
//
//
//        }
//    }
//
//    public CheckProtected() {
//        // IF ITS A BOOK
//        if(this.stack.getItem() instanceof WrittenBookItem){
//
//            WrittenBookContent book;
//
//            book = this.stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
//
//
//            // IF ITS A LOCK
//            if(Objects.equals(book.title().raw(), "LOCK")) {
//
//                this.chestStatus = ProtectedStatus.LOCK;
//
//                this.author = book.author();
//
//                // IF ITS A SELL
//
//            } else if (Objects.equals(book.title().raw(), "SELL")) {
//
//                this.chestStatus = ProtectedStatus.ERROR;
//
//                // minecraft:diamond-64->minecraft:stick-1
//
//                this.author = book.author();
//
//                List<Filterable<Component>> pages = book.pages();
//
//                // CHECK SIZE
//                if(pages.size() == 2){
//
//                        /*
//
//                        // GET STRINGS
//
//                        String page1 = pages.get(0).raw().getString();
//                        String page2 = pages.get(1).raw().getString();
//
//                        //try {
//                        JsonElement element1 = JsonParser.parseString(page1);
//                        DataResult<ItemStack> resultPage1 = ItemStack.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element1);
//
//                        JsonElement element2 = JsonParser.parseString(page2);
//                        DataResult<ItemStack> resultPage2 = ItemStack.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element2);
//
//                        if(resultPage1.isSuccess() && resultPage2.isSuccess()) {
//                            this.cost = resultPage1.getOrThrow();
//                            this.product = resultPage2.getOrThrow();
//                            this.chestStatus = status.SELL;
//                        }*/
//
//
//
//                    String[] pageList = {pages.get(0).raw().getString(), pages.get(1).raw().getString()};
//                    //ItemStack[] out = new ItemStack[2];
//                    TradeItem[] out = new TradeItem[2];
//                    boolean success = true;
//
//
//                    for (int i = 0; i < 2; i++){
//                        TradeItem saveItem;
//                        JsonElement element;
//
//                        if(pageList[i].isEmpty()){
//                            success = false;
//                            break;
//                        }
//
//                        try{
//                            element = JsonParser.parseString(pageList[i]);
//                        } catch (Exception e) {
//                            success = false;
//                            break;
//                        }
//
//
//                        //DataResult<ItemStack> result = ItemStack.CODEC.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element);
//                        DataResult<TradeItem> result = TradeItem.CODEC.parse(world.registryAccess().createSerializationContext(JsonOps.INSTANCE), element);
//
//                        if(result.isSuccess()){
//                            saveItem = result.getOrThrow();
//
//                            if(!saveItem.getIsItem()) {
//
//                                if (saveItem.getStack().copy().getComponents().has(DataComponents.CONTAINER)) {
//                                    saveItem.setItem(Items.SHULKER_BOX);
//                                }
////                                    for (ComponentType<?> type : saveItem.getStack().copy().getComponents().getTypes()) {
////
////
////                                        // ADD MORE DEFAULTS
////                                        if (type.equals(DataComponentTypes.CONTAINER)) {
////
////                                            //item = item.copyComponentsToNewStack(Items.SHULKER_BOX, item.getCount());
////                                        }
////                                    }
//                            }
//
//                        } else {
//                            ChestProtection.LOGGER.info("Error in parsing, when someone opened a chest! ChestProtection");
//                            success = false;
//                            break;
//                        }
//
//
//
//                        out[i] = saveItem;
//                    }
//
//                    if(success){
//                        //this.cost = out[0].getStack();
//                        //this.product = out[1].getStack();
//                        this.tradeItems = new TradeItemList(out);
//                        this.chestStatus = ProtectedStatus.SELL_ERROR;
//                    }
//
//
//
//
//
//
//                        /*
//                        try {
//                            String[] tempCost = page1.split("-");
//                            String[] tempProduct = page2.split("-");
//
//                            if(tempCost.length == 2 && tempProduct.length == 2) {
//
//                                this.cost = new ItemStack(
//                                        //Registries.ITEM.get(new Identifier(tempCost[0])),
//                                        Registries.ITEM.get(Identifier.of(tempCost[0])),
//                                        Integer.parseInt(tempCost[1])
//                                );
//
//                                this.product = new ItemStack(
//                                        //Registries.ITEM.get(new Identifier(tempProduct[0])),
//                                        Registries.ITEM.get(Identifier.of(tempProduct[0])),
//                                        Integer.parseInt(tempProduct[1])
//                                );
//
//                            } else {
//                                this.chestStatus = status.CLEAR;
//                            }
//
//                        } catch (Exception e){
//                            this.chestStatus = status.CLEAR;
//                        }*/
//                }
//
//
//                if(this.stack.has(DataComponents.CUSTOM_DATA)){
//                    CustomData data = this.stack.get(DataComponents.CUSTOM_DATA);
//
//                    if (data != null){
//
//                        CompoundTag nbt = data.copyTag();
//
//                        if(book.generation() != 0 || nbt.get("profitInventory").getId()==Tag.TAG_INT_ARRAY) {
//                            this.stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(book.title(),book.author(),0,book.pages(),book.resolved()));
//
//
//                                /*this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
//                                    currentNbt.putIntArray("profitInventory", this.profitInventory);
//                                }));*/
//
//
//                            this.profitInventory = new ProfitInventory(this, 54);
//                            this.stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {
//                                //currentNbt.putIntArray("profitInventory", this.profitInventory);
//                                //currentNbt.put("profitInventory", NbtElement.COMPOUND_TYPE)
//                                currentNbt.putString("profitInventory", profitInventory.encode());
//                            }));
//
//                        } else if(data.copyTag().contains("profitInventory")){
//
//
//
//                                /*this.profitInventory = new ProfitInventory(this, 54);
//                                if() {
//                                    ChestProtection.LOGGER.info("OLD BOOK CONVERTING!");
//                                    this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
//                                        currentNbt.putString("profitInventory", profitInventory.encode());
//                                    }));
//                                }*/
//
//
//
//                            String string = nbt.getString("profitInventory").get();
//                            JsonElement element;
//
//                            try {
//                                element = JsonParser.parseString(string);
//
//                                DataResult<ItemContainerContents> result = ItemContainerContents.CODEC.parse(world.registryAccess().createSerializationContext(JsonOps.INSTANCE), element);
//
//                                if(result.isSuccess()){
//                                    //ChestProtection.LOGGER.info(String.valueOf(stacks));
//                                    this.profitInventory = new ProfitInventory(this, 54, result.getOrThrow().allItemsCopyStream().toList());
//                                } else {
//                                    this.profitInventory = new ProfitInventory(this, 54);
//                                }
//
//                            } catch (Exception e) {
//                                this.profitInventory = new ProfitInventory(this, 54);
//                            }
//
//
//
//
//
//                            //DataResult<List<ItemStack>> result = ProfitInventory.inventoryCodec.parse(world.getRegistryManager().getOps(JsonOps.INSTANCE), element);
//
//
//
//
//
//                            //if (this.profitInventory.length != 54){
//                            //    this.profitInventory = new int[54];
//                            //}
//                        }
//                    }
//                } else {
//                    this.profitInventory = new ProfitInventory(this, 54);
//                    this.stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {
//                        currentNbt.putString("profitInventory", profitInventory.encode());
//                    }));
//                }
//            }
//        }
//    }
//
//    public boolean isStock(TradeItem product) {
//        /*
//
//        int count = stack.getCount();
//
//        int total = this.chestInventory.count(stack.getItem());
//
//        if(count <= total){
//            return true;
//        } else {
//            return false;
//        }*/
//
//        int total = 0;
//        ItemStack stack;
//
//        for(int i = 0; i < this.ProtectedInventory.getContainerSize(); i++){
//            stack = this.ProtectedInventory.getItem(i);
//            if(TradeInventory.ItemsEqual(stack, product)) {
//                total += stack.getCount();
//            }
//
//        }
//        /*} else if (this.getStack(index).getItem() == this.trade.cost.getItem()){
//            return index;
//        }*/
//
//        if (product.getStack().getCount() <= total){
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public void setScreen(SimpleGui screen){
//        this.screen = screen;
//    }
//
//    public Optional<SimpleGui> getScreen(){
//        if(this.screen != null) {
//            return Optional.of(this.screen);
//        }
//        return Optional.empty();
//    }
//
//    public Optional<CheckProtected> checkSame(BlockPos pos, Level world) {
//        if(pos.equals(this.position) && world.equals(this.world)) {
//            return Optional.of(this);
//        }
//
//        return Optional.empty();
//    }
//
//    public void saveTrade(boolean[] isItem,ItemStack[] stacks) {
//        List<Filterable<Component>> newPages = new ArrayList<>();
//        WrittenBookContent book = this.stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
//
//
//
//        /*
//
//        //ChestProtection.LOGGER.info(stacks[1].encode(world.getRegistryManager()).toString());
//
//        DataResult<JsonElement> result1 = ItemStack.CODEC.encodeStart(world.getRegistryManager().getOps(JsonOps.INSTANCE), stacks[1]);
//
//        //if (result1.isSuccess()) {
//        ChestProtection.LOGGER.info("SUCCESS " + result1.getPartialOrThrow().toString());*/
//
//        //}
//
//        //ChestProtection.LOGGER.info("STACK LENGTH: " + stacks.length);
//
//        int i = 0;
//
//        for (ItemStack transactionStack: stacks) {
//            if (transactionStack == null || transactionStack.isEmpty()) {
//
//
//                if(book != null && book.pages().size()>i){
//                    newPages.add(book.pages().get(i));
//                } else {
//                    newPages.add(Filterable.passThrough(Component.empty()));
//                }
//
//            } else {
//
//                TradeItem saveItemCodec = new TradeItem(isItem[i], transactionStack);
//                DataResult<JsonElement> result = TradeItem.CODEC.encodeStart(world.registryAccess().createSerializationContext(JsonOps.INSTANCE), saveItemCodec);
//                //DataResult<JsonElement> result = ItemStack.CODEC.encodeStart(world.getRegistryManager().getOps(JsonOps.INSTANCE), transactionStack);
//                JsonElement jsonElement = result.getOrThrow();
//                String json = jsonElement.toString();
//                newPages.add(Filterable.passThrough(Component.nullToEmpty(json)));
//
//            }
//
//
//            i++;
//        }
//
//        ChestProtection.LOGGER.info(newPages.toString());
//
//        WrittenBookContent book1 = new WrittenBookContent(
//                book.title(),
//                book.author(),
//                book.generation(),
//                newPages,
//                book.resolved()
//                );
//
//        this.stack.set(DataComponents.WRITTEN_BOOK_CONTENT, book1);
//
//    }
//
//    /*
//    public void setProfitInventory(SimpleInventory inventory) {
//
//        int[] arrayInventory = new int[inventory.size()];
//
//        for(int i = 0; i < inventory.size(); i++){
//            //if (inventory.getStack(i).getItem() == this.cost.getItem()){
//            if (inventory.getStack(i).getItem() == this.tradeItems.getCostStack().getItem()) {
//                arrayInventory[i] = inventory.getStack(i).getCount();
//            }
//        }
//        this.profitInventory = arrayInventory;
//
//        writeProfitInventory();
//    }*/
//
//
//
//    public void writeProfitInventory() {
//
//        /*this.stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
//            currentNbt.remove("profitInventory");
//            currentNbt.putIntArray("profitInventory", this.profitInventory);
//        }));*/
//
//
//        this.stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {
//            currentNbt.putString("profitInventory", profitInventory.encode());
//        }));
//
//    }

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
