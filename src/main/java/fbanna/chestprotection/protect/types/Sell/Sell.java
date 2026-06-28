package fbanna.chestprotection.protect.types.Sell;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.ui.sell.SellUI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

import static fbanna.chestprotection.ChestProtection.OPEN_SHOPS;

public class Sell extends CheckProtected {

    public final Container protectedInventory;
    private final GlobalPos position;

    private final ArrayList<ServerPlayer> players = new ArrayList<>();

    public Sell(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status, GlobalPos position) {
        this.protectedInventory =  protectedInventory;
        this.position = position;

        super(stack, cpdata, status);
        this.GenerateSellCPdata();



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

        if(cp.isClear()) {
            this.removeShop();
            return false;
        }

        return true;
    }

}
