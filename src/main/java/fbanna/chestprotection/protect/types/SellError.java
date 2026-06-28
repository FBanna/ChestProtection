package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.Authorised;
import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class SellError extends CheckProtected {


    public SellError(ItemStack stack, CPdata cpdata, ProtectedStatus status) {
        super(stack, cpdata, status);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        Authorised authorised = this.cpdata.getAuthorised();

        UUID author = authorised.getAuthor();

        if (authorised.isAuthorised(player.getUUID())) {

            ChestProtection.LOGGER.info("now open the shop configurator");

        } else {

            Optional<NameAndId> authorProfileOption = server.services().nameToIdCache().get(authorised.getAuthor());

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
