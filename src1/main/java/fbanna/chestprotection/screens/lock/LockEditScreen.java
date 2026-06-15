package fbanna.chestprotection.ui.lock;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckChest;
import fbanna.chestprotection.ui.profit.ProfitInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public class LockEditScreen extends SimpleGui {

    CheckChest trade;

    public LockEditScreen(ServerPlayer player, CheckChest trade) {
        super(MenuType.LECTERN, player, false);

        this.trade = trade;

        this.setTitle(Component.nullToEmpty("Edit Lock"));


    }
}



//public class ProfitScreen extends SimpleGui {
//    ProfitInventory profitInventory;
//    CheckChest trade;
//    public ProfitScreen(ServerPlayerEntity player, CheckChest trade) {
//        super(ScreenHandlerType.GENERIC_9X6, player, false);
//
//        this.trade = trade;
//
//        trade.setScreen(this);
//
//        this.setTitle(Text.of("profits"));
//
//        //this.profitInventory = new ProfitInventory(trade, player, 54);
//        this.profitInventory = trade.profitInventory;
//        this.profitInventory.open(player);
//
//        for(int i = 0; i<this.getSize(); i++){
//            setSlotRedirect(i, new Slot(this.profitInventory, i, 0,0));
//        }
//    }
//
//    @Override
//    public void onClose(){
//
//        this.profitInventory.close();
//        this.close();
//        ChestProtection.SHOPS.remove(this.trade);
//
//    }
//}