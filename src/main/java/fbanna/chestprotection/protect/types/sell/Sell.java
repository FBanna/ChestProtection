package fbanna.chestprotection.protect.types.sell;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.protect.data.sell.ProfitInventory;
import fbanna.chestprotection.ui.sell.SellUI;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

import static fbanna.chestprotection.ChestProtection.OPEN_SHOPS;
import static fbanna.chestprotection.protect.data.sell.SellData.PROFIT_INVENTORY_SIZE;

public class Sell extends CheckProtected {

    private final Container protectedInventory;

    private final ProfitInventory profitInventory;
    private final GlobalPos position;

    private final ArrayList<ServerPlayer> players = new ArrayList<>();

    public Sell(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status, GlobalPos position) {
        this.protectedInventory =  protectedInventory;
        this.position = position;

        super(stack, cpdata, status);
        this.GenerateSellCPdata();

        NonNullList<ItemStack> out = NonNullList.withSize(PROFIT_INVENTORY_SIZE, ItemStack.EMPTY);

        this.cpdata.sellData.get().getProfitInventory().copyInto(out);
        this.profitInventory = new ProfitInventory(this, out.toArray(ItemStack[]::new));



    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        if (!this.cpdata.isSellDataCorrect()) {

            //this.removeFromOpenShops();


            SellBook book = this.toSellBook();

            return book.open(player, server);
        }

        this.addAndUpdatePlayers((ServerPlayer) player);

        SellUI ui = new SellUI((ServerPlayer) player, this);
        ui.open();


        return false;
    }

    private void removeFromOpenShops() {
        assert(this.position != null);

        if(!OPEN_SHOPS.containsKey(this.position)){
            return;
        }

        //ChestProtection.LOGGER.info("REMOVE open shop at: " + this.position.pos().toShortString());
        OPEN_SHOPS.remove(this.position);
    }

    private void addToOpenShops() {
        assert(this.position != null);

        if(OPEN_SHOPS.containsKey(this.position)){
            return;
        }

        //ChestProtection.LOGGER.info("ADD open shop at: " + this.position.pos().toShortString());
        OPEN_SHOPS.put(this.position, this);
    }

    public void addAndUpdatePlayers(ServerPlayer player) {
        this.players.add(player);
        updatePlayers();

        addToOpenShops();
    }

    public void removeAndUpdatePlayers(ServerPlayer player) {
        this.players.remove(player);
        updatePlayers();

        if (this.players.isEmpty()) {
            removeFromOpenShops();

            // save CP data
            this.writeCPdata();
        }
    }

    private void removeShop() {
        this.players.clear();
        removeFromOpenShops();
        this.writeCPdata();
    }

    private void updatePlayers() {
        for (ServerPlayer player: this.players) {
            if (player.hasDisconnected()) {
                this.players.remove(player);
            }
        }
    }

    public SellBook toSellBook() {
        return new SellBook(this.getStack(), this.cpdata, ProtectedStatus.SELL);
    }

    public void openProtectedInventory(ServerPlayer player) {
        Level dimension =  player.level().getServer().getLevel(this.position.dimension());
        BlockState blockState = dimension.getBlockState(this.position.pos());
        player.openMenu(blockState.getMenuProvider(dimension, this.position.pos()));
    }

    public boolean isPresentAndUpdate(MinecraftServer server) {

        CheckProtected cp = CheckProtected.createContainerOpen(this.position.pos(), server.getLevel(this.position.dimension()), true);

        if(!cp.equals(this)) {
            ChestProtection.LOGGER.info("noticed that they do not match!");
            this.removeShop();
            return false;
        }

        return true;
    }

    public ProfitInventory getProfitInventory() {
        return this.profitInventory;
    }

    public Container getProtectedInventory() {
        return this.protectedInventory;
    }

    protected GlobalPos getPosition() {
        return this.position;
    }

    @Override
    public void writeCPdata() {

        this.cpdata.getSellData().get().setProfitInventory(
                ItemContainerContents.fromItems(this.profitInventory.items)
        );

        super.writeCPdata();
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Sell)) {

            return false;
        }

        Sell other = (Sell)o;

        GlobalPos otherPos = other.getPosition();

        if (!this.position.isCloseEnough(otherPos.dimension(), otherPos.pos(),0)){

            return false;
        }

        if(!this.cpdata.equals(other.cpdata)) {

            return false;
        }

        return true;


    }


}
