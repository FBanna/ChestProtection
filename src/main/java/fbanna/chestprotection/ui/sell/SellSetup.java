package fbanna.chestprotection.ui.sell;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.types.Sell.Sell;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public class SellSetup extends SimpleGui {

    private final Sell cp;

    public SellSetup(ServerPlayer player, Sell cp) {
        this.cp = cp;
        super(MenuType.GENERIC_9x3, player, false);
    }
}
