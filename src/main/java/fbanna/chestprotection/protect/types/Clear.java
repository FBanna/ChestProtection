package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.protect.ProfitInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Clear extends CheckProtected {


    public Clear(ItemStack stack) {
        super(stack, null, ProtectedStatus.CLEAR);
    }

//    private CheckProtected cp;
//
//    public Clear(CheckProtected cp){
//
//        this.cp = cp;
//
//    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        //player.sendSystemMessage(Component.literal("all clear!"));

        return true;
    }
}
