package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Error extends CheckProtected {


    public Error(ItemStack stack, CPdata cpdata, ProtectedStatus status) {
        super(stack, cpdata, status);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        player
            .sendOverlayMessage(Component.literal("Chest is in error state!")
            .withStyle(ChatFormatting.RED));


        return false;
    }
}
