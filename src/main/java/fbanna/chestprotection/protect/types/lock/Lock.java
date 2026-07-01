package fbanna.chestprotection.protect.types.lock;

import com.mojang.authlib.GameProfile;
import fbanna.chestprotection.protect.data.Authorised;
import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


import java.util.Optional;

public class Lock extends CheckProtected {

    public Lock(ItemStack stack, CPdata cpdata) {
        super(stack, cpdata, ProtectedStatus.LOCK);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        Authorised authorised = this.cpdata.getAuthorised();



        if(!authorised.isAuthorised(player.getUUID()) && !isAlwaysAllowed(player)) {

//            Optional<GameProfile> authorProfileOption = server.services().profileResolver().fetchById(authorised.getAuthor());
//
//            if (authorProfileOption.isEmpty()) {
//                return true;
//            }

            player.sendOverlayMessage(Component.literal("Locked by %s!".formatted(authorised.getAuthorName(server)))
                .withStyle(ChatFormatting.RED));

            return false;
        }


        return true;
    }

}
