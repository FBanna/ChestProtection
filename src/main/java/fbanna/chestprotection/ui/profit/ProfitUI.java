package fbanna.chestprotection.ui.profit;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class ProfitUI extends SimpleGui {

    public ProfitUI(ServerPlayer player, SimpleContainer profitInventoryOld) {
        super(MenuType.GENERIC_9x3, player, false);

        ProfitInventory profitInventory = new ProfitInventory(profitInventoryOld);

        for (int i = 0; i < this.getSize(); i++) {
            this.setSlot(i, new Slot(profitInventory, i, 0,0));
        }


    }


}
