package fbanna.chestprotection.protect.types.sell;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.data.Authorised;
import fbanna.chestprotection.protect.data.CPdata;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.ui.sellsetup.SellSetupUI;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SellBook extends CheckProtected {

    private final Optional<GlobalPos> oldPosition;

    public SellBook(ItemStack stack, CPdata cpdata) {
        super(stack, cpdata, ProtectedStatus.SELL);
        this.oldPosition = Optional.empty();

        this.GenerateSellCPdata();
    }

    public SellBook(ItemStack stack, CPdata cpdata, GlobalPos pos) {
        this.oldPosition = Optional.of(pos);
        super(stack, cpdata, ProtectedStatus.SELL);

        this.GenerateSellCPdata();
    }

    @Override
    public boolean open(Player player, MinecraftServer server) {

        Authorised authorised = this.cpdata.getAuthorised();

        if(!authorised.isAuthorised(player.getUUID()) && !isAlwaysAllowed(player)) {

            String name = authorised.getAuthorName(server);

            if (this.cpdata.isSellDataCorrect()) {

                player.sendOverlayMessage(Component.literal("You are not authorised by %s!".formatted(name))
                        .withStyle(ChatFormatting.RED));

            } else {
                player.sendOverlayMessage(Component.literal("Shop is broken, please contact %s!".formatted(name))
                        .withStyle(ChatFormatting.RED));
            }

            return false;

        }


        SimpleGui setupUI = new SellSetupUI((ServerPlayer) player, this);
        setupUI.open();

        return false;
    }

    /// returns true if allowed, false if not
    public boolean openProtectedInventory(ServerPlayer player) {

        if (this.oldPosition.isPresent()) {
            Level dimension =  player.level().getServer().getLevel(this.oldPosition.get().dimension());
            BlockState blockState = dimension.getBlockState(this.oldPosition.get().pos());
            player.openMenu(blockState.getMenuProvider(dimension, this.oldPosition.get().pos()));
            return true;
        }

        return false;
    }


}
