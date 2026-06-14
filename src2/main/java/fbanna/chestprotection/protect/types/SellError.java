package fbanna.chestprotection.protect.types;

import com.mojang.authlib.GameProfile;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.Authorised;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class SellError extends CheckProtected {


    public SellError(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status) {
        super(stack, cpdata, protectedInventory, status);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        Authorised authorised = this.cpdata.getAuthorised();

        UUID author = authorised.getAuthor();

        if (authorised.isAuthorised(player.getUUID())) {

            ChestProtection.LOGGER.info("now open the shop configurator");

        } else {

            Optional<GameProfile> authorProfileOption = server.services().profileResolver().fetchById(authorised.getAuthor());

            if (authorProfileOption.isEmpty()) {
                return false;
            }

            player.sendOverlayMessage(
                Component.literal("Shop is in an error state. Contact %s!".formatted(authorProfileOption.get().name())
            ).withStyle(ChatFormatting.RED));


        }



        return false;
    }
}
