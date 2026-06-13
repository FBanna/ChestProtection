package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.server.level.ServerPlayer;

public class Sell extends CheckProtected {


    @Override
    public boolean open(ServerPlayer player) {

        ChestProtection.LOGGER.info("need to open shop here!");
        return false;
    }
}
