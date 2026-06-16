package fbanna.chestprotection.protect.types.Sell;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.server.MinecraftServer;
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

        ChestProtection.LOGGER.info("need to open shop here!");
        return false;
    }
}
