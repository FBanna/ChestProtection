package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class Lock extends CheckProtected {

    @Override
    public boolean open(ServerPlayer player) {

        ChestProtection.LOGGER.info("I want to lock this");

        player
            .sendOverlayMessage(
                Component.translatable("Locked by %s!".formatted(super.authorised.author)
            )
            .withStyle(ChatFormatting.RED));


        return false;
    }


}
