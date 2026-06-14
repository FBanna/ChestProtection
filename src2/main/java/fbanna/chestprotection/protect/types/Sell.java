package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Sell extends CheckProtected {


    public Sell(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status) {
        super(stack, cpdata, protectedInventory, status);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        ChestProtection.LOGGER.info("need to open shop here!");
        return false;
    }
}
