package fbanna.chestprotection.ui.profit;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.types.Sell.ProfitInventory;
import fbanna.chestprotection.protect.types.Sell.Sell;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class ProfitUI extends SimpleGui {

    private final Sell cp;
    //private final ProfitInventory profitInventory;

    public ProfitUI(ServerPlayer player, Sell cp) {
        super(MenuType.GENERIC_9x3, player, false);

        //this.profitInventory = new ProfitInventory(profitInventoryOld);
        this.cp = cp;

        this.setTitle(Component.literal("Profits"));


        for (int i = 0; i < this.getSize(); i++) {
            this.setSlot(i, new ProfitSlot(cp.getProfitInventory(), i));
        }

    }

    @Override
    public void onTick() {
        boolean isPresent = this.cp.isPresentAndUpdate(this.player.level().getServer());

        if (!isPresent) {
            this.close();
        }
    }


    @Override
    public void onRemoved() {
        this.cp.removeAndUpdatePlayers(this.player);

        super.onRemoved();
    }


}
