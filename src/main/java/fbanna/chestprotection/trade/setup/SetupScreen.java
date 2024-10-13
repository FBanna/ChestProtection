package fbanna.chestprotection.trade.setup;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.component.ComponentType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import fbanna.chestprotection.ChestProtection;

import java.util.ArrayList;
import java.util.List;

public class SetupScreen extends SimpleGui {

    SetupInventory setupInventory;

    CheckChest trade;

    public SetupScreen(ServerPlayerEntity player, CheckChest trade) {

        super(ScreenHandlerType.GENERIC_9X5, player, false);
        this.setTitle(Text.of("Setup"));

        this.setupInventory = new SetupInventory(player, trade, this);
        this.trade = trade;

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
    }
}
