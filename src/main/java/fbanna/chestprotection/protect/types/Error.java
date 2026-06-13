package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.protect.Authorised;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class Error extends CheckProtected {


    @Override
    public boolean open(ServerPlayer player) {

        player
            .sendOverlayMessage(Component.translatable("Shop is in an error state. Contact %s!".formatted(super.authorised.author))
            .withStyle(ChatFormatting.RED));


        return false;
    }
}
