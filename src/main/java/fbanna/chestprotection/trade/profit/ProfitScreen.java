package fbanna.chestprotection.trade.profit;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ProfitScreen extends SimpleGui {
    ProfitInventory profitInventory;
    public ProfitScreen(ServerPlayerEntity player, CheckChest trade) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

        this.setTitle(Text.of("profits"));

        this.profitInventory = new ProfitInventory(trade, player, 54);

        for(int i = 0; i<this.getSize(); i++){
            setSlotRedirect(i, new Slot(profitInventory, i, 0,0));
        }
    }
}
