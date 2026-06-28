package fbanna.chestprotection.protect.types.lock;

import fbanna.chestprotection.protect.Authorised;
import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.ui.lock.EditAuthorised;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class LockBook extends CheckProtected {


    public LockBook(ItemStack stack, CPdata cpdata, ProtectedStatus status) {
        super(stack, cpdata, status);
    }


    @Override
    public boolean open(Player player, MinecraftServer server) {

        Authorised authorised = this.cpdata.getAuthorised();

        if(!authorised.isAuthorised(player.getUUID())) {

            Optional<NameAndId> authorProfileOption = server.services().nameToIdCache().get(authorised.getAuthor());

            if (authorProfileOption.isEmpty()) {
                return true;
            }

            player.sendOverlayMessage(Component.literal("Locked by %s!".formatted(authorProfileOption.get().name()))
                    .withStyle(ChatFormatting.RED));

            return false;

        }

        EditAuthorised gui = new EditAuthorised((ServerPlayer) player, this);
        gui.open();
        return false;
    }





}
