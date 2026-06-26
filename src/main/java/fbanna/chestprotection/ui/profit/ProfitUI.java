package fbanna.chestprotection.ui.profit;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.types.Sell.ProfitInventory;
import fbanna.chestprotection.protect.types.Sell.Sell;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class ProfitUI extends SimpleGui {

    private final Sell cp;
    private final ProfitInventory profitInventory;

    public ProfitUI(ServerPlayer player, Sell cp) {
        super(MenuType.GENERIC_9x3, player, false);

        //this.profitInventory = new ProfitInventory(profitInventoryOld);
        this.profitInventory = (ProfitInventory) cp.cpdata.sellData.get().getProfitInventory();
        this.cp = cp;

        this.setTitle(Component.literal("Profits"));

        updateSlots();

    }

    protected void updateSlots() {

        for (int i = 0; i < this.getSize(); i++) {

            if (this.profitInventory.getItem(i).isEmpty()) {
                this.clearSlot(i);
            } else {
                this.setSlot(i, new Slot(this.profitInventory, i, 0,0));
            }


        }

    }

    protected void saveProfitInventory() {

    }


}
