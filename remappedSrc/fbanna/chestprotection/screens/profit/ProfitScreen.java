package fbanna.chestprotection.screens.profit;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class ProfitScreen extends SimpleGui {
    ProfitInventory profitInventory;
    CheckChest trade;
    public ProfitScreen(ServerPlayer player, CheckChest trade) {
        super(MenuType.GENERIC_9x6, player, false);

        this.trade = trade;

        trade.setScreen(this);

        this.setTitle(Component.nullToEmpty("profits"));

        //this.profitInventory = new ProfitInventory(trade, player, 54);
        this.profitInventory = trade.profitInventory;
        this.profitInventory.open(player);

        for(int i = 0; i<this.getSize(); i++){
            setSlotRedirect(i, new Slot(this.profitInventory, i, 0,0));
        }
    }

    @Override
    public void onClose(){

        this.profitInventory.close();
        this.close();
        ChestProtection.SHOPS.remove(this.trade);

    }
}
