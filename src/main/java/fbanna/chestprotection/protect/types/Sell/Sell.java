package fbanna.chestprotection.protect.types.Sell;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Sell extends CheckProtected {

    private final Container protectedInventory;


    public Sell(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status) {
        this.protectedInventory = protectedInventory;
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

        ChestProtection.LOGGER.info("need to open shop here!");
        return false;
    }

    public SellBook toSellBook() {
        return new SellBook(this.getStack(), this.cpdata, ProtectedStatus.SELL);
    }
}
