package fbanna.chestprotection.screens.setup;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import fbanna.chestprotection.ChestProtection;

public class SetupScreen extends SimpleGui {

    SetupInventory setupInventory;

    CheckChest trade;

    public SetupScreen(ServerPlayer player, CheckChest trade) {

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

        setSlotRedirect(18, new Slot(setupInventory, 0, 0,0));
        setSlotRedirect(26, new Slot(setupInventory, 1, 0,0));



        //ChestProtection.LOGGER.info(trade.product.getComponents().toString());







    }

    @Override
    public void onClose(){

        this.setupInventory.dropAll();
        this.close();
        ChestProtection.SHOPS.remove(this.trade);
    }
}
