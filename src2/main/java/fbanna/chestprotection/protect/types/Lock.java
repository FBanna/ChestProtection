package fbanna.chestprotection.protect.types;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.Authorised;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.UUIDLookup;

import java.util.Optional;

public class Lock extends CheckProtected {

    public Lock(ItemStack stack, CPdata cpdata, Container protectedInventory, ProtectedStatus status) {
        super(stack, cpdata, protectedInventory, status);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        ChestProtection.LOGGER.info("I want to lock this");

        Authorised authorised = this.cpdata.getAuthorised();



        if(!authorised.isAuthorised(player.getUUID())) {

            Optional<GameProfile> authorProfileOption = server.services().profileResolver().fetchById(authorised.getAuthor());

            if (authorProfileOption.isEmpty()) {
                return true;
            }

            player
                .sendOverlayMessage(
                    Component.literal("Locked by %s!".formatted(authorProfileOption.get().name())
                )
                .withStyle(ChatFormatting.RED));

            return false;
        }


        return true;
    }


}
