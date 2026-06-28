package fbanna.chestprotection.protect.types.lock;

import com.mojang.authlib.GameProfile;
import fbanna.chestprotection.protect.Authorised;
import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


import java.util.Optional;

public class Lock extends CheckProtected {

    public Lock(ItemStack stack, CPdata cpdata, ProtectedStatus status) {
        super(stack, cpdata, status);
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        Authorised authorised = this.cpdata.getAuthorised();



        if(!authorised.isAuthorised(player.getUUID())) {

            Optional<GameProfile> authorProfileOption = server.services().profileResolver().fetchById(authorised.getAuthor());

            if (authorProfileOption.isEmpty()) {
                return true;
            }

            player.sendOverlayMessage(Component.literal("Locked by %s!".formatted(authorProfileOption.get().name()))
                .withStyle(ChatFormatting.RED));

            return false;
        }


        return true;
    }



//    /// Funtion to open screen to edit Authorised players
//    public static void editAuthorised(Player player, CPdata cpdata, MinecraftServer server) {
//
//        Authorised authorised = cpdata.getAuthorised();
//
//
//        if (!authorised.isAuthorised(player.getUUID())) {
//
//            Optional<NameAndId> authorProfileOption = server.services().nameToIdCache().get(authorised.getAuthor());
//
//            if (authorProfileOption.isEmpty()) {
//                return;
//            }
//
//            player.sendOverlayMessage(Component.literal("Locked by %s!".formatted(authorProfileOption.get().name()))
//                .withStyle(ChatFormatting.RED));
//
//            return;
//        }
//
//
//
//        EditAuthorised gui = new EditAuthorised((ServerPlayer) player, cpdata);
//        gui.open();
//
//
//
//    }


}
