package fbanna.chestprotection.protect.types.Sell;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.Authorised;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.ui.sellsetup.SellSetupUI;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class SellBook extends CheckProtected {


    public SellBook(ItemStack stack, CPdata cpdata, ProtectedStatus status) {
        super(stack, cpdata, status);

        this.GenerateSellCPdata();
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        Authorised authorised = this.cpdata.getAuthorised();

        if(!authorised.isAuthorised(player.getUUID())) {

            Optional<NameAndId> authorProfileOption = server.services().nameToIdCache().get(authorised.getAuthor());

            if (authorProfileOption.isEmpty()) {
                return true;
            }

            if (this.cpdata.isSellDataCorrect()) {

                player.sendOverlayMessage(Component.literal("You are not authorised by %s!".formatted(authorProfileOption.get().name()))
                        .withStyle(ChatFormatting.RED));

            } else {
                player.sendOverlayMessage(Component.literal("Shop is broken, please contact %s!".formatted(authorProfileOption.get().name()))
                        .withStyle(ChatFormatting.RED));
            }



            return false;

        }


        SimpleGui setupUI = new SellSetupUI((ServerPlayer) player, this);
        setupUI.open();

        return false;
    }
}
