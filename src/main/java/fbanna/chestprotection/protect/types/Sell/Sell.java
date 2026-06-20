package fbanna.chestprotection.protect.types.Sell;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.ui.sell.SellSetup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Sell extends CheckProtected {

    private final Container protectedInventory;


    public Sell(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status) {
        this.protectedInventory = protectedInventory;
        super(stack, cpdata, status);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        // temp

        SimpleGui setupUI = new SellSetup((ServerPlayer) player, this);

        ChestProtection.LOGGER.info("need to open shop here!");
        return false;
    }
}
