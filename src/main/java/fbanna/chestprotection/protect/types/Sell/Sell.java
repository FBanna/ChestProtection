package fbanna.chestprotection.protect.types.Sell;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.util.TradeInventory;
import fbanna.chestprotection.ui.sell.SellUI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Sell extends CheckProtected {

    public final TradeInventory protectedInventory;


    public Sell(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status) {
        this.protectedInventory =  new TradeInventory(protectedInventory);

        super(stack, cpdata, status);


        this.GenerateSellCPdata();

    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        if (!this.cpdata.isSellDataCorrect()) {

            SellBook book = this.toSellBook();

            return book.open(player, server);
        }


        //SimpleGui setupUI = new SellSetup((ServerPlayer) player, this.toSellBook());
//        setupUI.open();

        //ChestProtection.LOGGER.info("need to open shop here!");
        SellUI ui = new SellUI((ServerPlayer) player, this);
        ui.open();


        return false;
    }

    public SellBook toSellBook() {
        return new SellBook(this.getStack(), this.cpdata, ProtectedStatus.SELL);
    }
}
