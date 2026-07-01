package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Clear extends CheckProtected {


    public Clear(ItemStack stack) {
        super(stack, null, ProtectedStatus.CLEAR);
    }



    @Override
    public boolean open(Player player, MinecraftServer server) {
        return true;
    }
}
