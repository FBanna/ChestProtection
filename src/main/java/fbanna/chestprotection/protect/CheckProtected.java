package fbanna.chestprotection.protect;

import com.mojang.serialization.DataResult;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.data.Authorised;
import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.types.*;
import fbanna.chestprotection.protect.types.Error;

import java.util.Optional;
import java.util.UUID;

import fbanna.chestprotection.protect.types.lock.Lock;
import fbanna.chestprotection.protect.types.lock.LockBook;
import fbanna.chestprotection.protect.types.sell.Sell;
import fbanna.chestprotection.protect.types.sell.SellBook;
import fbanna.chestprotection.protect.data.sell.SellData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static fbanna.chestprotection.ChestProtection.OPEN_SHOPS;

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
        if (this.status == ProtectedStatus.CLEAR) {
            return true;
        }

        Authorised authorised = this.cpdata.getAuthorised();

        if (authorised.isAuthorised(player.getUUID())) {
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

    public CheckProtected(ItemStack stack, CPdata cpdata, ProtectedStatus status) {

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
        }
        return new Clear(stack);
    }

    public static CheckProtected createContainerOpen(BlockPos position, Level level) {
        return createContainerOpen(position, level, false);
    }


    protected static CheckProtected createContainerOpen(BlockPos position, Level level, boolean ignoreExisting) {


        Container tempProtectedInventory = null;
        ItemStack tempStack = null;
        CPdata tempCPdata = null;
        GlobalPos correctedPosition;


        // Finding book & matching its content
        BlockState state = level.getBlockState(position);

        if (!state.hasBlockEntity()){
            return new Clear(tempStack);
        }


        BlockEntity entity = level.getBlockEntity(position);


        Block block = state.getBlock();



        if (!(entity instanceof Container)) {

            return new Clear(tempStack);
        }

        if (block != Blocks.CHEST && block != Blocks.BARREL) {

            return new Clear(tempStack);
        }


        if (block == Blocks.CHEST) {


            if (ChestBlock.getBlockType(state) == DoubleBlockCombiner.BlockType.SECOND) {
                correctedPosition = GlobalPos.of(level.dimension(), ChestBlock.getConnectedBlockPos(position, state));
            } else {
                correctedPosition = GlobalPos.of(level.dimension(), position);
            }

            //tempProtectedInventory = (Container) ((ChestBlock) block).combine(state, level, position, true).;

            tempProtectedInventory = ChestBlock.getContainer((ChestBlock) block, state, level, position, true);

        } else {

            correctedPosition = GlobalPos.of(level.dimension(), position);
            tempProtectedInventory = (Container) entity;

        }


        tempStack = tempProtectedInventory.getItem(0);

        ProtectedStatus status = getStatus(tempStack);

        if (status == ProtectedStatus.CLEAR) {

            return new Clear(tempStack);
        }

        // Check if Sell aready exists for object

        if (OPEN_SHOPS.containsKey(correctedPosition) && !ignoreExisting) {
            ChestProtection.LOGGER.info("found pre-existing shop!");
            return OPEN_SHOPS.get(correctedPosition);
        }

        // Creating Authorisation

        tempCPdata = getCPdataOrGenerate(tempStack, level);


        return switch (status) {
            case CLEAR -> new Clear(tempStack);
            case ERROR -> new Error(tempStack, tempCPdata, status);
            case LOCK -> new Lock(tempStack, tempCPdata, status);
            case SELL -> new Sell(tempStack, tempCPdata, tempProtectedInventory, status, correctedPosition);
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
                        ItemContainerContents.EMPTY
                        //ItemContainerContents.fromItems(NonNullList.withSize(SellData.PROFIT_INVENTORY_SIZE, ItemStack.EMPTY))
                ))
        );

        writeCPdata(this.cpdata, stack);
    }


    /// Must ensure that the book is NOT clear
    ///
    /// returns the CPdata or generates and saves it itself
    private static CPdata getCPdataOrGenerate(ItemStack stack, Level level) {
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
    private static CPdata getCPdata(ItemStack stack) {

        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            return null;
        }

        CompoundTag data = stack.get(DataComponents.CUSTOM_DATA).copyTag();

        if (!data.contains("cpdata")) {
            return null;
        }

        CompoundTag cpdata = data.getCompoundOrEmpty("cpdata");


        DataResult<CPdata> result = CPdata.CODEC.parse(NbtOps.INSTANCE, cpdata);

        if (result.isError()) {

            ChestProtection.LOGGER.info("error parsing");
            return null;

        }

        return result.getOrThrow();


    }


    public void writeCPdata() {

        writeCPdata(this.cpdata, this.stack);
    }

    private static void writeCPdata(CPdata newData, ItemStack stack) {

        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {

            DataResult<Tag> tag = CPdata.CODEC.encodeStart(NbtOps.INSTANCE, newData);

            if (tag.isError()) {
                ChestProtection.LOGGER.error("Could not encode data to book!");
            }

            currentNbt.put("cpdata", tag.getOrThrow());

        }));


    }

    public void addAuthorised(UUID player) {
        this.cpdata.getAuthorised().addAuthorised(player);
        this.writeCPdata();
    }


    public void removeAuthorised(UUID player) {
        this.cpdata.getAuthorised().removeAuthorised(player);
        this.writeCPdata();
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public boolean isClear() {
        return (this.status == ProtectedStatus.CLEAR);
    }

    public boolean isShop() {
        return (this.status == ProtectedStatus.SELL);
    }

}