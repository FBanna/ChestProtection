package fbanna.chestprotection.ui.setup;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import fbanna.chestprotection.ChestProtection;

public class SetupScreen extends SimpleGui {

    SetupInventory setupInventory;

    CheckProtected trade;

    public SetupScreen(ServerPlayer player, CheckProtected trade) {

        super(MenuType.GENERIC_9x5, player, false);
        this.setTitle(Component.nullToEmpty("Setup"));

        this.setupInventory = new SetupInventory(player, trade, this);
        this.trade = trade;

        trade.setScreen(this);

        int[] panes = {19,20,21,22,23,24,25};
        for(int slot: panes) {
            setSlot(slot, new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1));
        }

        /*for(int i = 27; i < 35; i++) {
            setSlot(i, new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1));
        }*/


        setSlot(18, new Slot(setupInventory, 0, 0,0));
        setSlot(26, new Slot(setupInventory, 1, 0,0));



        //ChestProtection.LOGGER.info(trade.product.getComponents().toString());







    }

    @Override
    public void close(boolean skipSync){

        this.setupInventory.dropAll();
        this.close();
        ChestProtection.SHOPS.remove(this.trade);
    }
}
